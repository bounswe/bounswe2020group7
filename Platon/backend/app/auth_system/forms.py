from wtforms import Form, StringField, IntegerField, BooleanField, validators
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
get_user_parser.add_argument("auth_token",required=True, type=str, help="Authentication token", location="headers")


get_self_parser = reqparse.RequestParser()
get_self_parser.add_argument("auth_token",required=True, type=str, help="Authentication token", location="headers")


class CreateUserForm(Form):
	e_mail = StringField("E-mail", validators=[validators.DataRequired()])
	password = StringField("Password", validators=[validators.DataRequired()])
	name = StringField("Name", validators=[validators.DataRequired()])
	surname = StringField("Surname", validators=[validators.DataRequired()])
	position = StringField("Position", validators=[validators.DataRequired()])
	profile_photo = StringField("Profile Photo", validators=[validators.optional()])
	google_scholar_name = StringField("Google Scholar Name", validators=[validators.optional()])
	researchgate_name = StringField("ResearchGate Name", validators=[validators.optional()])
	institution = StringField("Institution", validators=[validators.optional()])
create_user_parser = reqparse.RequestParser()
create_user_parser.add_argument("e_mail", required=True, type=str, help="E-mail address of the new user", location="form")
create_user_parser.add_argument("password", required=True, type=str, help="Password of the new user", location="form")
create_user_parser.add_argument("name", required=True, type=str, help="Name of the new user", location="form")
create_user_parser.add_argument("surname", required=True, type=str, help="Surname of the new user", location="form")
create_user_parser.add_argument("position", required=True, type=str, help="Position of the new user", location="form")
create_user_parser.add_argument("profile_photo", required=False, type=str, help="URL of the profile picture of the user", location="form")
create_user_parser.add_argument("google_scholar_name", required=False, type=str, help="URL of the Google Scholar page of the user", location="form")
create_user_parser.add_argument("researchgate_name", required=False, type=str, help="URL of the ResearchGate page of the user", location="form")
create_user_parser.add_argument("institution", required=False, type=str, help="Institution of the user", location="form")


class UpdateUserForm(Form):
	name = StringField("Name", validators=[validators.optional()])
	surname = StringField("Surname", validators=[validators.optional()])
	position = StringField("Position", validators=[validators.optional()])
	is_valid = BooleanField("Activation State", validators=[validators.optional()])
	is_private = BooleanField("Public/Private State", validators=[validators.optional()])
	profile_photo = StringField("Profile Photo", validators=[validators.optional()])
	google_scholar_name = StringField("Google Scholar Name", validators=[validators.optional()])
	researchgate_name = StringField("ResearchGate Name", validators=[validators.optional()])
	institution = StringField("Institution", validators=[validators.optional()])
update_user_parser = reqparse.RequestParser()
update_user_parser.add_argument("name", required=False, type=str, help="Name of the user", location="form")
update_user_parser.add_argument("surname", required=False, type=str, help="Surname of the user", location="form")
update_user_parser.add_argument("position", required=False, type=str, help="Position of the user", location="form")
update_user_parser.add_argument("is_valid", required=False, type=inputs.boolean, help="The flag that shows whether the user account is activated or not.", location="form")
update_user_parser.add_argument("is_private", required=False, type=inputs.boolean, help="The flag that shows whether the user's profile is public or private.", location="form")
update_user_parser.add_argument("profile_photo", required=False, type=str, help="URL of the profile picture of the user", location="form")
update_user_parser.add_argument("google_scholar_name", required=False, type=str, help="URL of the Google Scholar page of the user", location="form")
update_user_parser.add_argument("researchgate_name", required=False, type=str, help="URL of the ResearchGate page of the user", location="form")
create_user_parser.add_argument("institution", required=False, type=str, help="Institution of the user", location="form")
update_user_parser.add_argument("auth_token",required=True, type=str, help="Authentication token", location="headers")


class DeleteUserForm(Form):
	password = StringField("Password", validators=[validators.DataRequired()])
delete_user_parser = reqparse.RequestParser()
delete_user_parser.add_argument("password", required=True, type=str, help="Password of the user", location="form")
delete_user_parser.add_argument("auth_token",required=True, type=str, help="Authentication token", location="headers")

class GetUserSkillsForm(Form):
	user_id = IntegerField("User ID", validators=[validators.DataRequired()])
get_userskill_parser = reqparse.RequestParser()
get_userskill_parser.add_argument("user_id", required=True, type=int, help="ID of the requested user.", location="args")
get_userskill_parser.add_argument("auth_token", required=True, type=str, help="Authentication token", location="headers")

class PostUserSkillsForm(Form):
	user_id = IntegerField("User ID", validators=[validators.DataRequired()])
	skill = StringField("Skill Name", validators=[validators.DataRequired()])
post_userskill_parser = reqparse.RequestParser()
post_userskill_parser.add_argument("user_id", required=True, type=int, help="ID of the requested user.", location="args")
post_userskill_parser.add_argument("skill", required=True, type=int, help="Skill name", location="form")
post_userskill_parser.add_argument("auth_token", required=True, type=str, help="Authentication token", location="headers")

class DeleteUserSkillsForm(Form):
	user_id = IntegerField("User ID", validators=[validators.DataRequired()])
	skill = StringField("Skill Name", validators=[validators.DataRequired()])
delete_userskill_parser = reqparse.RequestParser()
delete_userskill_parser.add_argument("user_id", required=True, type=int, help="ID of the requested user.", location="args")
delete_userskill_parser.add_argument("skill", required=True, type=int, help="Skill name", location="form")
delete_userskill_parser.add_argument("auth_token", required=True, type=str, help="Authentication token", location="headers")