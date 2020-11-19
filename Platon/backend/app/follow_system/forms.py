from wtforms import Form, StringField, IntegerField, validators
from app import api
from flask_restplus import reqparse

# follower: user that follows someone
# following: user that is followed by someone
class GetFollowingsForm(Form):
    follower_id = IntegerField('follower_id', validators=[validators.DataRequired()])

get_followings_parser = reqparse.RequestParser()
get_followings_parser.add_argument('follower_id', required=True, type=int,
                                   help="ID of the follower. Follower is the one who follows someone.",
                                   location='form')

class GetFollowersForm(Form):
    following_id = IntegerField('following_id', validators=[validators.DataRequired()])

get_followers_parser = reqparse.RequestParser()
get_followers_parser.add_argument('following_id', required=True, type=int,
                                  help="ID of the following user. Following user is the one who is followed by someone.",
                                  location='form')

class GetFollowRequestsForm(Form):
    following_id = IntegerField('following_id', validators=[validators.DataRequired()])

get_follow_requests_parser = reqparse.RequestParser()
get_follow_requests_parser.add_argument('following_id', required=True, type=int,
                                        help="ID of the following user. Following user is the one who is followed by someone.",
                                        location='form')

class SendFollowRequestsForm(Form):
    follower_id = IntegerField('follower_id', validators=[validators.DataRequired()])
    following_id = IntegerField('following_id', validators=[validators.DataRequired()])

send_follow_requests_parser = reqparse.RequestParser()
send_follow_requests_parser.add_argument('follower_id',required=True,type=int,
                                         help="ID of the follower. Follower is the one who follows someone.",
                                         location='form')
send_follow_requests_parser.add_argument('following_id', required=True, type=int,
                                         help="ID of the following user. Following user is the one who is followed by someone.",
                                         location='form')

class AcceptFollowRequestsForm(Form):
    follower_id = IntegerField('follower_id', validators=[validators.DataRequired()])
    following_id = IntegerField('following_id', validators=[validators.DataRequired()])

accept_follow_requests_parser = reqparse.RequestParser()
accept_follow_requests_parser.add_argument('follower_id', required=True, type=int,
                                           help="ID of the follower. Follower is the one who follows someone.",
                                           location='form')
accept_follow_requests_parser.add_argument('following_id', required=True, type=int,
                                           help="ID of the following user. Following user is the one who is followed by someone.",
                                           location='form')

class RejectFollowRequestsForm(Form):
    follower_id = IntegerField('follower_id', validators=[validators.DataRequired()])
    following_id = IntegerField('following_id', validators=[validators.DataRequired()])

reject_follow_requests_parser = reqparse.RequestParser()
reject_follow_requests_parser.add_argument('follower_id', required=True, type=int,
                                           help="ID of the follower. Follower is the one who follows someone.",
                                           location='form')
reject_follow_requests_parser.add_argument('following_id', required=True, type=int,
                                           help="ID of the following user. Following user is the one who is followed by someone.",
                                           location='form')
