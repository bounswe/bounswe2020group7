from flask import make_response,jsonify,request
from flask_restplus import Resource,Namespace, fields
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
from app.auth_system.forms import GetUserSkillsForm, get_userskill_parser
from app.auth_system.forms import PostUserSkillsForm, post_userskill_parser
from app.auth_system.forms import DeleteUserSkillsForm, delete_userskill_parser
from app.auth_system.models import User
from app.profile_management.models import Jobs, Skills, UserSkills
from app.follow_system.models import Follow, FollowRequests
from app.follow_system.helpers import follow_required
from app.auth_system.helpers import generate_token,send_email,login_required, hashed
from app.profile_management.helpers import ResearchInfoFetch
from app.follow_system.helpers import follow_required

from hashlib import sha256
import datetime

auth_system_ns = Namespace("Authentication System",
                            description="Authentication System Endpoints",
                            path = "/auth_system")

success_msg_model = api.model('Success Message', {
    "mgs": fields.String
})

account_information_model = api.model('User Data', {
    "id": fields.Integer,
    "name": fields.String,
    "surname": fields.String,
    "rate": fields.Float,
    "profile_photo": fields.String,
    "e_mail": fields.String,
    "google_scholar_name": fields.String,
    "researchgate_name": fields.String,
    "job": fields.String
})

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
            return make_response(jsonify({ 'user_id': user.id, 'token':generate_token(user.id,app.config['SESSION_DURATION'])}),200)
        else:
            return make_response(jsonify({'error' : 'Write your e-mail and password'}),400)

@auth_system_ns.route("/reset_password")
class ResetPasswordAPI(Resource):
    """
        Login Functionality is implemented in this class
    """
    @api.doc(responses={404 : 'E-mail not found',400 : 'Input Format Error' ,401 : 'Account Problems' ,500 : ' Database Connection/E-mail Server Error'})
    @api.response(200, 'Valid e-mail', success_msg_model)
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
            if send_email(user.e_mail,"Password Reset Link","Click the following link to change tour password\nDo not forget to activate your account today, the link expires in 20 minutes!\nToken: {}".format(token),"{}/resetpassword/{}".format(app.config["FRONTEND_HOSTNAME"],token)):
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

@auth_system_ns.route("/self")
class GetSelfAPI(Resource):

    # GET request (for the logged-in user to get their own user information)
    @api.expect(get_self_parser)
    @api.doc(responses={
                404: "The user is not found.",
                500: "The server is not connected to the database."
            })
    @api.response(200, "User has been found.", account_information_model)
    @login_required
    def get(requester_id, self):
        # Tries to connect to the database.
        # If it fails, an error is raised.
        try:
            logged_in_user = User.query.filter_by(id=requester_id).first()
            user_job = Jobs.query.filter(Jobs.id == logged_in_user.job_id).first()
        except:
            return make_response(jsonify({"error" : "The server is not connected to the database."}), 500)
        else:
            # Checks whether there is an already existing user in the database with the given user ID.
            # If yes, user information is returned.
            # If not, an error is raised.
            if logged_in_user is not None:
                account_information = {
                                        "id": logged_in_user.id,
                                        "name": logged_in_user.name,
                                        "surname": logged_in_user.surname,
                                        "is_private": logged_in_user.is_private,
                                        "rate": logged_in_user.rate,
                                        "profile_photo": logged_in_user.profile_photo,
                                        "e_mail": logged_in_user.e_mail,
                                        "google_scholar_name": logged_in_user.google_scholar_name,
                                        "researchgate_name": logged_in_user.researchgate_name,
                                        "job": user_job.name,
                                        "institution": logged_in_user.institution
                                        }
                return make_response(jsonify(account_information), 200)
            else:
                return make_response(jsonify({"error" : "The user is not found."}), 404)

@auth_system_ns.route("/user")
class UserAPI(Resource):
    '''
    This class is a RESTful API for "User" model.
    You can find the endpoints for creating, reading, updating and deleting a user below.
    '''

    # GET request
    @api.expect(get_user_parser)
    @api.doc(responses={
                400: "Missing data fields or invalid data.",
                404: "The user is not found.",
                500: "The server is not connected to the database."
            })
    @api.response(200, "User has been found.", account_information_model)
    @login_required
    @follow_required(param_loc = 'args', requested_user_id_key='user_id')
    def get(requester_id, self):
        '''
        Returns the profile information of the requested user.
        '''
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
                user_job = Jobs.query.filter(Jobs.id == existing_user.job_id).first()
            except:
                return make_response(jsonify({"error" : "The server is not connected to the database."}), 500)
            else:
                # Checks whether there is an already existing user in the database with the given user ID.
                # If yes, user information is returned.
                # If not, an error is raised.
                if existing_user is not None:
                    # Checks whether the requester user follows the requested user or not.
                    if Follow.query.filter_by(follower_id=requester_id, following_id=form.user_id.data).first() is not None:
                        following_status = 1 # Represents that the requester user follows the requested user.
                    elif FollowRequests.query.filter_by(follower_id=requester_id, following_id=form.user_id.data).first() is not None:
                        following_status = 0 # Represents that the requester user has already sent a follow request to the requested user and it is pending.
                    else:
                        following_status = -1 # Represents that the requester user does not follow the requested user and has not yet sent a request to follow them.
                    
                    user_job = Jobs.query.filter(Jobs.id == existing_user.job_id).first()

                    account_information = { 
                                        "id": existing_user.id,
                                        "name": existing_user.name,
                                        "surname": existing_user.surname,
                                        "e_mail": existing_user.e_mail,
                                        "is_private": existing_user.is_private,
                                        "following_status": following_status,
                                        "rate": existing_user.rate,
                                        "profile_photo": existing_user.profile_photo,
                                        "google_scholar_name": existing_user.google_scholar_name,
                                        "researchgate_name": existing_user.researchgate_name,
                                        "job": user_job.name,
                                        "institution": existing_user.institution
                                        }
                    return make_response(jsonify(account_information), 200)
                else:
                    return make_response(jsonify({"error" : "The user is not found."}), 404)
        else:
            return make_response(jsonify({"error" : "Missing data fields or invalid data."}), 400)

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
                                        job_id=new_user_job.id,
                                        institution=form.institution.data
                                        )
                        db.session.add(new_user)
                        db.session.commit()
                    except:
                        return make_response(jsonify({"error" : "The server is not connected to the database."}), 500)
                    
                    # Tries to update the research information of the newly updated user.
                    # If it fails, it does not raise an error.
                    # -as research information is scheduled to be fetched everyday.-
                    try:
                        ResearchInfoFetch.update_research_info(new_user.id)
                    except:
                        pass

                    # Tries to send the activation mail to the user.
                    # If it fails, an error is raised.
                    try:
                        account_activation_token = generate_token(new_user.id, datetime.timedelta(days=1))
                        send_email(
                                    recipient_email=new_user.e_mail,
                                    subject="Activate Your Platon Account",
                                    message_body="Please activate your Platon account by clicking the activation link below.\nDo not forget to activate your account today, the link expires in one day!\nToken: {}".format(account_activation_token),
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
                202: "Server has received the request but there was no information to be updated.",
                400: "Missing data fields or invalid data.",
                404: "The user is not found.",
                500: "The server is not connected to the database."
            })
    @login_required
    def put(requester_id, self):
        # Parses the form data.
        form = UpdateUserForm(request.form)
        
        # Checks whether the data is in valid form.
        # If yes, starts processing the data.
        # If not, an error is raised.
        if form.validate():
            # Tries to connect to the database.
            # If it fails, an error is raised.
            try:
                existing_user = User.query.filter_by(id=requester_id)
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
                        # Gets the inputted parameters from the form data.
                        new_attributes = {}
                        for key, value in form.data.items():
                            if value or (value == 0):
                                new_attributes[key] = value

                        # Checks whether the form data contains "job" data.
                        if new_attributes.get("job", None):
                            # Checks whether the inputted job already exists in the database,
                            # If not, adds the job to the database.
                            # If yes, gets the ID of the job and writes it to the new user's "job_id" field.
                            job_name = new_attributes["job"].title()
                            new_user_job = Jobs.query.filter_by(name=job_name).first()
                            if new_user_job is None:
                                new_user_job = Jobs(name=job_name)
                                db.session.add(new_user_job)
                                db.session.commit()
                            # Replaces the "job" in the form data with its ID.
                            new_attributes["job_id"] = new_user_job.id
                            del new_attributes["job"]

                        if new_attributes:
                            # Updates the attributes of the user in the database.
                            existing_user.update(new_attributes)
                            db.session.commit()
                        else:
                            return make_response(jsonify({"message" : "Server has received the request but there was no information to be updated."}), 202)    
                    except:
                        return make_response(jsonify({"error" : "The server is not connected to the database."}), 500)
                    
                    # Tries to update the research information of the newly updated user.
                    # If it fails, it does not raise an error.
                    # -as research information is scheduled to be fetched everyday.-
                    try:
                        ResearchInfoFetch.update_research_info(existing_user.first().id)
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
    def delete(requester_id, self):
        # Parses the form data.
        form = DeleteUserForm(request.form)
        
        # Checks whether the data is in valid form.
        # If yes, starts processing the data.
        # If not, an error is raised.
        if form.validate():
            # Tries to connect to the database.
            # If it fails, an error is raised.
            try:
                existing_user = User.query.filter_by(id=requester_id).first()
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

@auth_system_ns.route("/skills")
class UserSkillAPI(Resource):
    '''
        This class is a RESTful API for "UserSkill" model.
        You can find the endpoints for creating, reading, updating and deleting a user below.
    '''

    @api.doc(
        responses={200: 'Skills are Successfully Returned', 404: 'Skills are empty', 400: 'Input Format Error',
                   500: 'Database Connection'})
    @api.expect(get_userskill_parser)
    @login_required
    @follow_required(param_loc = 'form', requested_user_id_key='user_id')
    def get(self):
        '''
            Returns a list of user's skills with id and name
        '''
        form = GetUserSkillsForm(request.form)
        if form.validate():
            try:
                user_skills = UserSkills.query.filter(UserSkills.user_id == form.user_id.data).all()
            except:
                return make_response(jsonify({'error': 'Database Connection Error'}), 500)

            if user_skills is []:
                return make_response(jsonify({'error': 'Skills not found'}), 404)

            skills = []
            for skill in user_skills:
                skills.append(Skills.query.filter(Skills.id == skill.skill_id).first())

            return make_response(jsonify({'skills': [
                {'id': skill.id, 'name': skill.name} for skill in skills]}), 200)

        else:
            return make_response(jsonify({'error': 'Input Format Error'}), 400)

    @api.doc(
        responses={200: 'Skill is Successfully Added', 400: 'Input Format Error',
                   500: 'Database Connection'})
    @api.expect(post_userskill_parser)
    @login_required
    def post(user_id):
        '''
            Adds a new skill to user's skills with name
        '''
        form = PostUserSkillsForm(request.form)
        if form.validate():
            try:
                skill_name = form.skill.data.title()
                new_skill = Skills.query.filter(Skills.name == skill_name).first()
                if new_skill is None:
                    new_skill = Skills(name=skill_name)
                    db.session.add(new_skill)
                    db.session.commit()
                new_userskill = UserSkills(user_id=user_id,
                                           skill_id=new_skill.id)
                db.session.add(new_userskill)
                db.session.commit()
            except:
                return make_response(jsonify({"error": "The server is not connected to the database."}), 500)

            return make_response(jsonify({'msg': 'Skill is successfully added'}), 200)
        else:
            return make_response(jsonify({"error": "Missing data fields or invalid data."}), 400)

    @api.doc(
        responses={200: 'Skill is Successfully Deleted', 400: 'Input Format Error',
                   500: 'Database Connection', 404: 'Skill or UserSkill is not Found'})
    @api.expect(delete_userskill_parser)
    @login_required
    def delete(user_id):
        '''
            Deletes the skill from user's skills with name
        '''
        form = DeleteUserSkillsForm(request.form)
        if form.validate():
            try:
                skill_name = form.skill.data.title()
                skill = Skills.query.filter_by(name=skill_name).first()
                if skill is None:
                    return make_response(jsonify({'error': 'Skill is not found'}), 404)
                userskill = UserSkills.query.filter_by(skill_id=skill.id).first()
                if userskill is None:
                    return make_response(jsonify({'error': 'User Skill is not found'}), 404)
                else:
                    db.session.delete(userskill)
                    db.session.commit()
            except:
                return make_response(jsonify({"error": "The server is not connected to the database."}), 500)
            return make_response(jsonify({'msg': 'Skill is successfully deleted'}), 200)
        else:
            return make_response(jsonify({"error": "Missing data fields or invalid data."}), 400)


def register_resources(api):
    api.add_namespace(auth_system_ns)

