from flask import make_response,jsonify,request
from flask_mail import Message 
from app import api, mail
from flask import current_app as app

import jwt
import datetime
from dateutil import parser 
from hashlib import sha256
from functools import wraps

def generate_token(user_id,expire_duration):
    """
        Generates new token for given user id
    """
    return jwt.encode({'id':user_id,'expire_time':(datetime.datetime.now()+expire_duration).isoformat()},app.config['JWT_SESSION_KEY'],app.config['JWT_ALGORITHM']).decode('utf-8')

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
            auth_info = jwt.decode(auth_token,app.config['JWT_SESSION_KEY'],algorithms=[app.config['JWT_ALGORITHM']])
            expire_time = parser.isoparse(auth_info['expire_time'])
            if expire_time < datetime.datetime.now():
                return make_response(jsonify({'error' : 'Expired Token'}),401)
        except:
            return make_response(jsonify({'error' : 'Wrong Token Format'}),401)
        response = func(auth_info['id'],*args,**kws)
        new_token = generate_token(auth_info['id'],app.config['SESSION_DURATION'])
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