from wtforms import Form, StringField, IntegerField, validators
from app import api
from flask_restplus import reqparse

# follower: user that follows someone
# following: user that is followed by someone
class GetFollowingsForm(Form):
    follower_id = IntegerField('follower_id', validators=[validators.DataRequired()])
    page = IntegerField("page")
    per_page = IntegerField("per_page")

get_followings_parser = reqparse.RequestParser()
get_followings_parser.add_argument('follower_id', required=True, type=int,
                                   help="ID of the follower. Follower is the one who follows someone.",
                                   location='args')
get_followings_parser.add_argument('page',type=int,help="Page index that you want(Starts from 0)",location='args')
get_followings_parser.add_argument('per_page',type=int,help="Number of items in a page",location='args')
get_followings_parser.add_argument('auth_token',required=True, type=str,help="Authentication Token",location='headers')


class GetFollowersForm(Form):
    following_id = IntegerField('following_id', validators=[validators.DataRequired()])
    page = IntegerField("page")
    per_page = IntegerField("per_page")

get_followers_parser = reqparse.RequestParser()
get_followers_parser.add_argument('following_id', required=True, type=int,
                                  help="ID of the following user. Following user is the one who is followed by someone.",
                                  location='args')
get_followers_parser.add_argument('page',type=int,help="Page index that you want(Starts from 0)",location='args')
get_followers_parser.add_argument('per_page',type=int,help="Number of items in a page",location='args')
get_followers_parser.add_argument('auth_token',required=True, type=str,help="Authentication Token",location='headers')


class GetFollowRequestsForm(Form):
    following_id = IntegerField('following_id', validators=[validators.DataRequired()])
    page = IntegerField("page")
    per_page = IntegerField("per_page")

get_follow_requests_parser = reqparse.RequestParser()
get_follow_requests_parser.add_argument('following_id', required=True, type=int,
                                        help="ID of the following user. Following user is the one who is followed by someone.",
                                        location='args')

get_follow_requests_parser.add_argument('page',type=int,help="Page index that you want(Starts from 0)",location='args')
get_follow_requests_parser.add_argument('per_page',type=int,help="Number of items in a page",location='args')
get_follow_requests_parser.add_argument('auth_token',required=True, type=str,help="Authentication Token",location='headers')

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
send_follow_requests_parser.add_argument('auth_token',required=True, type=str,help="Authentication Token",location='headers')


class ReplyFollowRequestsForm(Form):
    follower_id = IntegerField('follower_id', validators=[validators.DataRequired()])
    following_id = IntegerField('following_id', validators=[validators.DataRequired()])
    state = IntegerField('state', validators=[validators.DataRequired()])


reply_follow_requests_parser = reqparse.RequestParser()
reply_follow_requests_parser.add_argument('follower_id', required=True, type=int,
                                           help="ID of the follower. Follower is the one who follows someone.",
                                           location='form')
reply_follow_requests_parser.add_argument('following_id', required=True, type=int,
                                           help="ID of the following user. Following user is the one who is followed by someone.",
                                           location='form')
reply_follow_requests_parser.add_argument('state', required=True, type=int,
                                           help="State is 1 if the reply is accept, state is 2 if the reply is reject.",
                                           location='form')
reply_follow_requests_parser.add_argument('auth_token',required=True, type=str,help="Authentication Token",location='headers')


class UnfollowForm(Form):
    following_id = IntegerField('following_id', validators=[validators.DataRequired()])


unfollow_parser = reqparse.RequestParser()
unfollow_parser.add_argument('following_id', required=True, type=int,
                                  help="ID of the unfollowed user",
                                  location='form')
unfollow_parser.add_argument('auth_token',required=True, type=str,help="Authentication Token",location='headers')

class GetCommentsForm(Form):
    commented_user_id = IntegerField('commented_user_id', validators=[validators.DataRequired()])
    page = IntegerField("page")
    per_page = IntegerField("per_page")

get_comment_parser = reqparse.RequestParser()
get_comment_parser.add_argument('commented_user_id',required=True,type=int,help="ID of the commented user", location="args")
get_comment_parser.add_argument('page',type=int,help="Page index that you want(Starts from 0)",location='args')
get_comment_parser.add_argument('per_page',type=int,help="Number of items in a page",location='args')
get_comment_parser.add_argument("auth_token",required=True, type=str, help="Authentication token", location="headers")

class PostCommentForm(Form):
    commented_user_id = IntegerField('commented_user_id', validators=[validators.DataRequired()])
    rate = IntegerField('rate', validators=[validators.DataRequired()])
    text = StringField("text")

post_comment_parser = reqparse.RequestParser()
post_comment_parser.add_argument('commented_user_id',required=True,type=int,help="ID of the commented user", location="form")
post_comment_parser.add_argument('rate',required=True,type=int,help="Rate for the User", location="form")
post_comment_parser.add_argument('text',type=str,help="Comment for the User", location="form")
post_comment_parser.add_argument("auth_token",required=True, type=str, help="Authentication token", location="headers")

class DeleteCommentForm(Form):
    comment_id = IntegerField('comment_id', validators=[validators.DataRequired()])

delete_comment_parser = reqparse.RequestParser()
delete_comment_parser.add_argument('comment_id',required=True,type=int,help="ID of the commented user", location="args")
delete_comment_parser.add_argument("auth_token",required=True, type=str, help="Authentication token", location="headers")
