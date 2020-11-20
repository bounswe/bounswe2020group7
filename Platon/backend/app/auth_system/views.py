from flask import make_response,jsonify,request
from flask_restplus import Resource,Namespace
from flask import current_app as app
from flask_mail import Message 

from app import api, mail, db
from app.auth_system.forms import LoginForm,login_parser
from app.auth_system.forms import ResetPasswordGetForm,reset_password_get_parser
from app.auth_system.forms import ResetPasswordPostForm,reset_password_post_parser
from app.auth_system.models import User
from app.auth_system.helpers import generate_token,send_email,login_required

from hashlib import sha256

auth_system_ns = Namespace("Authentication System",
                            description="Authentication System Endpoints",
                            path = "/auth_system")

@auth_system_ns.route("/login")
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

@auth_system_ns.route("/reset_password")
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
    def post(user_id,self):
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
    api.add_namespace(auth_system_ns)

