from flask import make_response,jsonify,request
from flask_restplus import Resource
from flask import current_app as app

from app import api
from app.auth_system.forms import LoginForm,login_parser
from app.auth_system.models import User

import jwt
import datetime
from dateutil import parser 
from hashlib import sha256
from functools import wraps


def generate_token(user_id):
    """
        Generates new token for given user id
    """
    return jwt.encode({'id':user_id,'expire_time':(datetime.datetime.now()+app.config['SESSION_DURATION']).isoformat()},app.config['JWT_SESSION_KEY'],app.config['JWT_ALGORITHM']).decode('utf-8')

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
            new_token = generate_token(auth_info['id'])
        except:
            return make_response(jsonify({'error' : 'Wrong Token Format'}),401)
        return func(auth_info['id'],new_token,*args,**kws)
        
    return auth_check

class LoginAPI(Resource):
    """
        Login Functionality is implemented in this class
    """
    @api.doc(responses={200 : 'Valid token', 404 : 'User not found', 401 : 'Account Problems', 500 : ' Database Connection Error'})
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
            return make_response(jsonify({'token':generate_token(user.id)}),200)
        else:
            return make_response(jsonify({'error' : 'Write your e-mail and password'}),401)
    
def register_resources(api):
    api.add_resource(LoginAPI,"/login")
 