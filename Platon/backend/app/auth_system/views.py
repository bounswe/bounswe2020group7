from flask import make_response,jsonify,request
from flask_restplus import Resource,Namespace
from flask import current_app as app
from flask_mail import Message 

from app import api, mail, db
from app.auth_system.forms import LoginForm,login_parser
from app.auth_system.forms import ResetPasswordGetForm,reset_password_get_parser
from app.auth_system.forms import ResetPasswordPostForm,reset_password_post_parser
from app.auth_system.forms import GetUserForm, get_user_parser, get_self_parser
from app.auth_system.forms import CreateUserForm, create_user_parser
from app.auth_system.forms import UpdateUserForm, update_user_parser
from app.auth_system.forms import DeleteUserForm, delete_user_parser
from app.auth_system.models import User
from app.profile_management.models import Jobs
from app.auth_system.helpers import generate_token,send_email,login_required, hashed
from app.profile_management.helpers import ResearchInfoFetch

from hashlib import sha256
import datetime

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
            if user.password_hashed != hashed(form.password.data):
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
            if send_email(user.e_mail,"Password Reset Link","Click the following link to change tour password\nToken: {}".format(token),"{}/resetpassword/{}".format(app.config["FRONTEND_HOSTNAME"],token)):
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


@auth_system_ns.route("/user")
class UserAPI(Resource):
    '''
    This class is a RESTful API for "User" model.
    You can find the endpoints for creating, reading, updating and deleting a user below.
    '''

    # GET request
    @api.expect(get_user_parser)
    @api.doc(responses={
                200: "User has been found.",
                400: "Missing data fields or invalid data.",
                404: "The user is not found.",
                500: "The server is not connected to the database."
            })
    def get(self):
        # Parses the form data.
        form = GetUserForm(request.args)
        
        # Checks whether the data is in valid form.
        # If yes, starts processing the data.
        # If not, an error is raised.
        if form.validate():
            # Tries to connect to the database.
            # If it fails, an error is raised.
            try:
                existing_user = User.query.filter_by(id=form.user_id.data).first()
            except:
                return make_response(jsonify({"error" : "The server is not connected to the database."}), 500)
            else:
                # Checks whether there is an already existing user in the database with the given user ID.
                # If yes, user information is returned.
                # If not, an error is raised.
                if existing_user is not None:
                    # DO NOT FORGET TO WRITE CODE FOR PRIVATE ACCOUNTS
                    account_information = {
                                            "name": existing_user.name,
                                            "surname": existing_user.surname,
                                            "google_scholar_name": existing_user.google_scholar_name,
                                            "researchgate_name": existing_user.researchgate_name
                                            }
                    return make_response(jsonify(account_information), 200)
                else:
                    return make_response(jsonify({"error" : "The user is not found."}), 404)
        else:
            return make_response(jsonify({"error" : "Missing data fields or invalid data."}), 400)


    # GET request (for the logged-in user to get their own user information)
    @api.expect(get_self_parser)
    @api.doc(responses={
                200: "User has been found.",
                404: "The user is not found.",
                500: "The server is not connected to the database."
            })
    @login_required
    def get(user_id, self):
        # Tries to connect to the database.
        # If it fails, an error is raised.
        try:
            logged_in_user = User.query.filter_by(id=user_id).first()
        except:
            return make_response(jsonify({"error" : "The server is not connected to the database."}), 500)
        else:
            # Checks whether there is an already existing user in the database with the given user ID.
            # If yes, user information is returned.
            # If not, an error is raised.
            if logged_in_user is not None:
                account_information = {
                                        "name": logged_in_user.name,
                                        "surname": logged_in_user.surname,
                                        "google_scholar_name": logged_in_user.google_scholar_name,
                                        "researchgate_name": logged_in_user.researchgate_name
                                        }
                return make_response(jsonify(account_information), 200)
            else:
                return make_response(jsonify({"error" : "The user is not found."}), 404)


    # POST request
    @api.expect(create_user_parser)
    @api.doc(responses={
                201: "User has been successfully created.",
                400: "Missing data fields or invalid data.",
                409: "User with the given e-mail address already exists.",
                500: "The server is not connected to the database.",
                503: "The server could not send the account activation e-mail."
            })
    def post(self):
        # Parses the form data.
        form = CreateUserForm(request.form)
        
        # Checks whether the data is in valid form.
        # If yes, starts processing the data.
        # If not, an error is raised.
        if form.validate():
            # Tries to connect to the database.
            # If it fails, an error is raised.
            try:
                existing_user = User.query.filter_by(e_mail=form.e_mail.data).first()
            except:
                return make_response(jsonify({"error" : "The server is not connected to the database."}), 500)
            else:
                # Checks whether there is an already existing user in the database with the e-mail address in the form data.
                # If not, the user gets created.
                # If yes, an error is raised.
                if existing_user is None:
                    # Tries to add the user to the database.
                    # If it fails, an error is raised.
                    try:
                        # Checks whether the inputted job already exists in the database,
                        # If not, adds the job to the database.
                        # If yes, gets the ID of the job and writes it to the new user's "job_id" field.
                        job_name = form.job.data.title()
                        new_user_job = Jobs.query.filter_by(name=job_name).first()
                        if new_user_job is None:
                            new_user_job = Jobs(name=job_name)
                            db.session.add(new_user_job)
                            db.session.commit()

                        new_user = User(is_valid=False,
                                        e_mail=form.e_mail.data,
                                        password_hashed=hashed(form.password.data),
                                        name=form.name.data,
                                        surname=form.surname.data,
                                        is_private=False,
                                        rate=-1.0,
                                        profile_photo=form.profile_photo.data,
                                        google_scholar_name=form.google_scholar_name.data,
                                        researchgate_name=form.researchgate_name.data,
                                        job_id=new_user_job.id
                                        )
                        db.session.add(new_user)
                        db.session.commit()
                    except:
                        return make_response(jsonify({"error" : "The server is not connected to the database."}), 500)
                    
                    # Tries to update the research information of the newly updated user.
                    # If it fails, it does not raise an error.
                    # -as research information is scheduled to be fetched everyday.-
                    try:
                        ResearchInfoFetch.update_research_info(existing_user.id)
                    except:
                        pass

                    # Tries to send the activation mail to the user.
                    # If it fails, an error is raised.
                    try:
                        account_activation_token = generate_token(new_user.id, datetime.timedelta(days=1))
                        send_email(
                                    recipient_email=new_user.e_mail,
                                    subject="Activate Your Platon Account",
                                    message_body='''Please activate your Platon account by clicking the activation link below.
                                                    Do not forget to activate your account today, the link expires in one day!''',
                                    message_link="{}/activate_account?token={}".format(app.config["FRONTEND_HOSTNAME"],account_activation_token)
                                    )
                    except:
                        return make_response(jsonify({"error" : "The server could not send the account activation e-mail."}), 503)
                    else:
                        return make_response(jsonify({"message" : "User has been successfully created."}), 201)
                else:
                    return make_response(jsonify({"error" : "User with the given e-mail address already exists."}), 409)
        else:
            return make_response(jsonify({"error" : "Missing data fields or invalid data."}), 400)


    # PUT request
    @api.expect(update_user_parser)
    @api.doc(responses={
                200: "Account information has been successfully updated.",
                400: "Missing data fields or invalid data.",
                404: "The user is not found.",
                500: "The server is not connected to the database."
            })
    @login_required
    def put(user_id, self):
        # Parses the form data.
        form = UpdateUserForm(request.form)
        
        # Checks whether the data is in valid form.
        # If yes, starts processing the data.
        # If not, an error is raised.
        if form.validate():
            # Tries to connect to the database.
            # If it fails, an error is raised.
            try:
                existing_user = User.query.filter_by(id=user_id).first()
            except:
                return make_response(jsonify({"error" : "The server is not connected to the database."}), 500)
            else:
                # Checks whether there is an existing user in the database with the given user ID.
                # If yes, starts processing the data.
                # If not, an error is raised.
                if existing_user is not None:
                    # Tries to update account information of the user.
                    # If it fails, an error is raised.
                    try:
                        # Checks whether the inputted job already exists in the database,
                        # If not, adds the job to the database.
                        # If yes, gets the ID of the job and writes it to the new user's "job_id" field.
                        job_name = form.job.data.title()
                        new_user_job = Jobs.query.filter_by(name=job_name).first()
                        if new_user_job is None:
                            new_user_job = Jobs(name=job_name)
                            db.session.add(new_user_job)
                            db.session.commit()


                        # Replaces the "job" in the form data with its ID.
                        form.data["job"] = new_user_job.id
                        for key, value in form.data.items():
                            if value:
                                setattr(existing_user, key, value)
                        db.session.add(existing_user)
                        db.session.commit()
                    except:
                        return make_response(jsonify({"error" : "The server is not connected to the database."}), 500)
                
                    # Tries to update the research information of the newly updated user.
                    # If it fails, it does not raise an error.
                    # -as research information is scheduled to be fetched everyday.-
                    try:
                        ResearchInfoFetch.update_research_info(existing_user.id)
                    except:
                        pass
                    
                    return make_response(jsonify({"message" : "Account information has been successfully updated."}), 200)
                else:
                    return make_response(jsonify({"error" : "The user is not found."}), 404)
        else:
            return make_response(jsonify({"error" : "Missing data fields or invalid data."}), 400)


    # DELETE request
    @api.expect(delete_user_parser)
    @api.doc(responses={
                200: "User account has been successfully deleted.",
                400: "Missing data fields or invalid data.",
                401: "Wrong password.",
                404: "The user is not found.",
                500: "The server is not connected to the database."
            })
    @login_required
    def delete(user_id, self):
        # Parses the form data.
        form = DeleteUserForm(request.form)
        
        # Checks whether the data is in valid form.
        # If yes, starts processing the data.
        # If not, an error is raised.
        if form.validate():
            # Tries to connect to the database.
            # If it fails, an error is raised.
            try:
                existing_user = User.query.filter_by(id=user_id).first()
            except:
                return make_response(jsonify({"error" : "The server is not connected to the database."}), 500)
            else:
                # Checks whether there is an existing user in the database with the given user ID.
                # If yes, starts processing the data.
                # If not, an error is raised.
                if existing_user is not None:
                    # Checks whether the inputted password matches the password of the user.
                    # If yes, the user account gets deleted.
                    # If not, an error is raised.
                    if existing_user.password_hashed == hashed(form.password.data):
                        # Tries to delete the user account.
                        # If it fails, an error is raised.
                        try:
                            db.session.delete(existing_user)
                            db.session.commit()
                        except:
                            return make_response(jsonify({"error" : "The server is not connected to the database."}), 500)
                        else:
                            return make_response(jsonify({"message" : "User account has been successfully deleted."}), 200)
                    else:
                        return make_response(jsonify({"error" : "Wrong password."}), 401)    
                else:
                    return make_response(jsonify({"error" : "The user is not found."}), 404)
        else:
            return make_response(jsonify({"error" : "Missing data fields or invalid data."}), 400)


def register_resources(api):
    api.add_namespace(auth_system_ns)

