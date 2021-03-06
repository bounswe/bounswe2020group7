from flask import make_response,jsonify,request
from flask_mail import Message 
from app import api, mail
from flask import current_app as app

import jwt
import datetime
from dateutil import parser 
from hashlib import sha256
from functools import wraps

ALLOWED_PHOTO_EXTENSIONS = {'png', 'jpg', 'jpeg', 'gif'}

def allowed_file(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1].lower() in ALLOWED_PHOTO_EXTENSIONS

def profile_photo_link(profile_photo_field,user_id):
    if profile_photo_field is not None and allowed_file(profile_photo_field):
        return "/auth_system/profile_photo?user_id={}".format(user_id)
    else:
        return "/auth_system/logo"

def generate_token(user_id,expire_duration):
    """
        Generates new token for given user id
    """
    return jwt.encode({'id':user_id,'expire_time':(datetime.datetime.now()+expire_duration).isoformat()},app.config['JWT_SESSION_KEY'],app.config['JWT_ALGORITHM']).decode('utf-8')

def decode_token(auth_token):
    auth_info = jwt.decode(auth_token,app.config['JWT_SESSION_KEY'],algorithms=[app.config['JWT_ALGORITHM']])
    expire_time = parser.isoparse(auth_info['expire_time'])
    if expire_time < datetime.datetime.now():
        return make_response(jsonify({'error' : 'Expired Token'}),401)
    return int(auth_info["id"])

def login_required(func):
    """
        A Decorator that controls the token is valid or not and returns user id and new token.
    """
    @wraps(func)
    def auth_check(*args, **kws):
        try:
            auth_token = request.headers.get('auth_token')
        except:
            return make_response(jsonify({'error' : 'Login Required'}),401)
        try:
            id = decode_token(auth_token)
            if type(id) is not int:
                return id
        except:
            return make_response(jsonify({'error' : 'Wrong Token Format'}),401)
        response = func(id,*args,**kws)
        new_token = generate_token(id,app.config['SESSION_DURATION'])
        response.headers["auth_token"] = new_token
        return response
        
    return auth_check

def send_email(recipient_email,subject,message_body,message_link):
    msg = Message(subject=subject,body=message_body + '\n' + message_link,recipients = [recipient_email])
    try:
        mail.send(msg)
        return True
    except:
        return False

def hashed(password):
    return sha256(password.encode('utf-8')).hexdigest()