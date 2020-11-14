from flask import make_response,jsonify,request
from flask_restful import Resource
from flask import current_app as app

from app import api
from app.auth_system.forms import LoginForm
from app.auth_system.models import User

import jwt
import datetime
from hashlib import sha256
import json

class LoginAPI(Resource):

    def post(self):
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
            token = jwt.encode({'id':user.id,'expire_time':(datetime.datetime.now()+app.config['SESSION_DURATION']).isoformat()},app.config['JWT_SESSION_KEY'],app.config['JWT_ALGORITHM'])
            return make_response(jsonify({'token':token}),200)
        else:
            return make_response(jsonify({'error' : 'Write your e-mail and password'}),401)




def register_resources(api):
    api.add_resource(LoginAPI,"/login")
 