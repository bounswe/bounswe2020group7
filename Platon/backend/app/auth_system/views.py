from flask import make_response,jsonify,request
from flask_restplus import Resource
from flask import current_app as app
from flask_mail import Message 

from app import api, mail, db
from app.auth_system.forms import LoginForm,login_parser
from app.auth_system.forms import ResetPasswordGetForm,reset_password_get_parser
from app.auth_system.forms import ResetPasswordPostForm,reset_password_post_parser

from app.auth_system.models import User

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
            new_token = generate_token(auth_info['id'],app.config['SESSION_DURATION'])
        except:
            return make_response(jsonify({'error' : 'Wrong Token Format'}),401)
        return func(auth_info['id'],new_token,*args,**kws)
        
    return auth_check

def send_email(recepient_email,subject,message_body,message_link):
    msg = Message(subject=subject,body=message_body + '\n' + message_link,recipients = [recepient_email])
    try:
        mail.send(msg)
        return True
    except:
        return False

class LoginAPI(Resource):
    """
        Login Functionality is implemented in this class
    """
    @api.doc(responses={200 : 'Valid token', 404 : 'User not found', 401 : 'Account Problems', 400 : 'Input Format Error' ,500 : ' Database Connection Error'})
    @api.expect(login_parser)
    def post(self):
        """
         Takes Login Credentials as argument and returns a valid token
        """
        form = LoginForm(request.form)
        if form.validate():
            try:
                user = User.query.filter(User.e_mail == form.e_mail.data).first()
            except:
                return make_response(jsonify({'error' : 'Database Connection Problem'}),500)
            if user is None:
                return make_response(jsonify({'error' : 'User Not Found'}),404)
            if user.password_hashed != sha256(form.password.data.encode('utf-8')).hexdigest():
                return make_response(jsonify({'error' : 'Wrong e-mail or password'}),401)
            if not user.is_valid:
                return make_response(jsonify({'error' : 'Please activate your account'}),401)
            return make_response(jsonify({'token':generate_token(user.id,app.config['SESSION_DURATION'])}),200)
        else:
            return make_response(jsonify({'error' : 'Write your e-mail and password'}),400)
    
class ResetPasswordAPI(Resource):
    """
        Login Functionality is implemented in this class
    """
    @api.doc(responses={200 : 'Valid e-mail', 404 : 'E-mail not found',400 : 'Input Format Error' ,401 : 'Account Problems' ,500 : ' Database Connection/E-mail Server Error'})
    @api.expect(reset_password_get_parser)
    def get(self):
        """
            Sends a reset password email to the user
        """
        form = ResetPasswordGetForm(request.args)
        if form.validate():
            try:
                user = User.query.filter(User.e_mail == form.e_mail.data).first()
            except:
                return make_response(jsonify({'error' : 'Database Connection Problem'}),500)
            if user is None:
                return make_response(jsonify({'error' : 'E-mail not found'}),404)
            if not user.is_valid:
                return make_response(jsonify({'error' : 'Please activate your account'}),401)
            token = generate_token(user.id,app.config['LINK_DURATION'])
            if send_email(user.e_mail,"Password Reset Link","Click the following link to change tour password","{}".format(token)):
                return make_response(jsonify({'mgs' : 'E-mail is successfully sent'}),200)
            else:
                return make_response(jsonify({'error' : 'E-mail Server Error'}),500)
        else:
            return make_response(jsonify({'error' : 'Write your e-mail'}),400)

    @api.doc(responses={200 : 'Password Successfully Changed',400 : 'Passwords are not matched' ,401 : 'Authorization Error',500 : 'Database Connection/E-mail Server Error'})
    @api.expect(reset_password_post_parser)
    @login_required
    def post(user_id,new_token,self):
        """
            Changes password of the user
        """
        form = ResetPasswordPostForm(request.form)
        if form.validate():
            if form.new_password.data != form.new_password_repeat.data:
                return make_response(jsonify({'error' : 'Passwords are not matched' }),400)
            try:
                user = User.query.filter(User.id == user_id).first()
            except:
                return make_response(jsonify({'error' : 'Database Connection Problem'}),500)
            if user is None:
                return make_response(jsonify({'error' : 'Authorization Error'}),401)
            if not user.is_valid:
                return make_response(jsonify({'error' : 'Please activate your account'}),401)
            user.password_hashed = sha256(form.new_password.data.encode('utf-8')).hexdigest()
            try:
                db.session.commit()
            except:
                return make_response(jsonify({'error' : 'Database Connection Problem'}),500)
            return make_response(jsonify({'mgs' : 'Password successfully changed'}),200)
        else:
            return make_response(jsonify({'error' : 'Write new password twice'}),400)

def register_resources(api):
    api.add_resource(LoginAPI,"/login")
    api.add_resource(ResetPasswordAPI,"/reset_password")