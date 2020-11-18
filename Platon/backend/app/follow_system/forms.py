from wtforms import Form, StringField, IntegerField, validators
from app import api
from flask_restplus import reqparse

# follower: user that follows someone
# following: user that is followed by someone
class GetFollowingsForm(Form):
    follower_id = IntegerField('follower_id', validators=[validators.DataRequired()])

class GetFollowersForm(Form):
    following_id = IntegerField('following_id', validators=[validators.DataRequired()])

class GetFollowRequestsForm(Form):
    following_id = IntegerField('following_id', validators=[validators.DataRequired()])

class SendFollowRequestsForm(Form):
    follower_id = IntegerField('follower_id', validators=[validators.DataRequired()])
    following_id = IntegerField('following_id', validators=[validators.DataRequired()])

class AcceptFollowRequestsForm(Form):
    follower_id = IntegerField('follower_id', validators=[validators.DataRequired()])
    following_id = IntegerField('following_id', validators=[validators.DataRequired()])

class RejectFollowRequestsForm(Form):
    follower_id = IntegerField('follower_id', validators=[validators.DataRequired()])
    following_id = IntegerField('following_id', validators=[validators.DataRequired()])

'''
class LoginForm(Form):
    e_mail = StringField("e_mail",validators=[validators.DataRequired()])
    password = StringField("password",validators=[validators.DataRequired()])

login_parser = reqparse.RequestParser()
login_parser.add_argument('e_mail',required=True,type=str,help="E-mail of the person",location='form')
login_parser.add_argument('password',required=True,type=str,help="Password of the person",location='form')
'''
