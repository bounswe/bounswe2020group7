from wtforms import Form, StringField, validators
from app import api
from flask_restplus import reqparse

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