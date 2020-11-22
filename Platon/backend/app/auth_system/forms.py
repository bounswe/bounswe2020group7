from wtforms import Form, StringField, validators
from app import api
from flask_restplus import reqparse, inputs

class LoginForm(Form):
    e_mail = StringField("e_mail",validators=[validators.DataRequired()])
    password = StringField("password",validators=[validators.DataRequired()])

login_parser = reqparse.RequestParser()
login_parser.add_argument('e_mail',required=True,type=str,help="E-mail of the person",location='form')
login_parser.add_argument('password',required=True,type=str,help="Password of the person",location='form')

class ResetPasswordGetForm(Form):
    e_mail = StringField("e_mail",validators=[validators.DataRequired()])

reset_password_get_parser = reqparse.RequestParser()
reset_password_get_parser.add_argument('e_mail',required=True,type=str,help="E-mail of the person",location='args')

class ResetPasswordPostForm(Form):
    new_password = StringField("password",validators=[validators.DataRequired()])
    new_password_repeat = StringField("password",validators=[validators.DataRequired()])


reset_password_post_parser = reqparse.RequestParser()
reset_password_post_parser.add_argument('new_password',required=True,type=str,help="Enter new password",location='form')
reset_password_post_parser.add_argument('new_password_repeat',required=True,type=str,help="Enter new password(Again)",location='form')
reset_password_post_parser.add_argument('auth_token',required=True, type=str,help="Token that sent via email as a URL link",location='headers')


class GetUserForm(Form):
	user_id = IntegerField("User ID", validators=[validators.DataRequired()])
get_user_parser = reqparse.RequestParser()
get_user_parser.add_argument("user_id", required=True, type=int, help="ID of the requested user.", location="args")


class CreateUserForm(Form):
	e_mail = StringField("E-mail", validators=[validators.DataRequired(), validators.Email()])
	password = StringField("Password", validators=[validators.DataRequired()])
	name = StringField("Name", validators=[validators.DataRequired()])
	surname = StringField("Surname", validators=[validators.DataRequired()])
	job = StringField("Job", validators=[validators.DataRequired()])
create_user_parser = reqparse.RequestParser()
create_user_parser.add_argument("e_mail", required=True, type=str, help="E-mail address of the new user", location="form")
create_user_parser.add_argument("password", required=True, type=str, help="Password of the new user", location="form")
create_user_parser.add_argument("name", required=True, type=str, help="Name of the new user", location="form")
create_user_parser.add_argument("surname", required=True, type=str, help="Surname of the new user", location="form")
create_user_parser.add_argument("job", required=True, type=str, help="Job of the new user", location="form")


class UpdateUserForm(Form):
	name = StringField("Name", validators=[validators.optional()])
	surname = StringField("Surname", validators=[validators.optional()])
	job = StringField("Job", validators=[validators.optional()])
	is_valid = BooleanField("Activation State", validators=[validators.optional()])
	is_private = BooleanField("Public/Private State", validators=[validators.optional()])
	profile_photo = StringField("Profile Photo", validators=[validators.optional()])
	google_scholar_name = StringField("Google Scholar Name", validators=[validators.optional()])
	researchgate_name = StringField("ResearchGate Name", validators=[validators.optional()])
update_user_parser = reqparse.RequestParser()
update_user_parser.add_argument("name", required=False, type=str, help="Name of the user", location="form")
update_user_parser.add_argument("surname", required=False, type=str, help="Surname of the user", location="form")
update_user_parser.add_argument("job", required=False, type=str, help="Job of the user", location="form")
update_user_parser.add_argument("is_valid", required=False, type=inputs.boolean, help="The flag that shows whether the user account is activated or not.", location="form")
update_user_parser.add_argument("is_private", required=False, type=type=inputs.boolean, help="The flag that shows whether the user's profile is public or private.", location="form")
update_user_parser.add_argument("profile_photo", required=False, type=str, help="Profile photo of the user", location="form")
update_user_parser.add_argument("google_scholar_name", required=False, type=str, help="Google Scholar user name of the user", location="form")
update_user_parser.add_argument("researchgate_name", required=False, type=str, help="ResearchGate user name of the user", location="form")

class DeleteUserForm(Form):
	password = StringField("Password", validators=[validators.DataRequired()])
delete_user_parser = reqparse.RequestParser()
delete_user_parser.add_argument("password", required=True, type=str, help="Password of the user", location="form")