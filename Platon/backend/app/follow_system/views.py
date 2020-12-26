import datetime

from flask import make_response, jsonify, request
from flask_restplus import Resource, Namespace, fields
from flask import current_app as app
import math

from app import api, db
from app.follow_system.forms import GetFollowingsForm, GetFollowersForm, GetFollowRequestsForm, \
    SendFollowRequestsForm, ReplyFollowRequestsForm, UnfollowForm
from app.follow_system.forms import get_followings_parser, get_followers_parser, get_follow_requests_parser, send_follow_requests_parser, \
    reply_follow_requests_parser, unfollow_parser
from app.follow_system.models import Follow, FollowRequests, Comments
from app.auth_system.models import User
from app.auth_system.views import login_required
from app.follow_system.helpers import follow_required, previous_collaboration_required, update_rate
from app.profile_management.helpers import NotificationManager
from app.auth_system.helpers import profile_photo_link
from app.follow_system.forms import GetCommentsForm, PostCommentForm, DeleteCommentForm
from app.follow_system.forms import get_comment_parser, post_comment_parser, delete_comment_parser

follow_system_ns = Namespace("Follow System",
                             description="Follow System Endpoints",
                             path="/follow")

user_data_model = api.model('User Data', {
        'id': fields.Integer,
        'name': fields.String,
        'surname': fields.String,
        'e_mail': fields.String,
        'rate': fields.Float,
        'is_private': fields.Boolean
    })

followers_model = api.model('Followers', {
    'number_of_pages': fields.Integer,
    'followers': fields.List(
        fields.Nested(user_data_model)
    )
})

followings_model = api.model('Followings', {
    'number_of_pages': fields.Integer,
    'followings': fields.List(
        fields.Nested(user_data_model)
    )
})

follow_requests_model = api.model('Follow Requests', {
    'number_of_pages': fields.Integer,
    'follow_requests': fields.List(
        fields.Nested(user_data_model)
    )
})

comment_model = api.model('Comments', {
    'comment_id': fields.Integer,
    'owner_id': fields.Integer,
    'commented_user_id': fields.Integer,
    'timestamp': fields.DateTime,
    'rate': fields.Integer,
    'text': fields.String
})

comment_list_model = api.model('Comments List', {
    'number_of_pages': fields.Integer,
    'result': fields.List(
        fields.Nested(comment_model)
    )
})

@follow_system_ns.route("/followings")
class GetFollowingsAPI(Resource):

    @api.doc(responses={404: 'Followings List is empty',
                        400: 'Input Format Error',
                        500: 'Database Connection Error'})
    @api.response(200, 'Followings List is successfully returned', followings_model)
    @api.expect(get_followings_parser)
    @login_required
    @follow_required(param_loc='args',requested_user_id_key='follower_id')
    def get(user_id, self):
        '''
            Returns a list of dictionaries with id, name, surname, e_mail, rate and is_private informations.
        '''
        form = GetFollowingsForm(request.args)
        if form.validate():
            try:
                followSearch = Follow.query.filter(Follow.follower_id == form.follower_id.data).all()
            except:
                return make_response(jsonify({'error': 'Database Connection Error'}), 500)

            if followSearch is []:
                return make_response(jsonify({'error': 'User not found'}), 404)

            following_users = []
            for follow in followSearch:
                following_users.append(User.query.filter(User.id == follow.following_id).first())

            followings_list = [
                {'id': following_user.id, 'name': following_user.name, 'surname': following_user.surname,
                 'e_mail': following_user.e_mail, 'rate': following_user.rate,
                 'is_private': following_user.is_private,
                 'profile_photo': profile_photo_link(following_user.profile_photo,following_user.id) 
                 } for following_user in following_users]

            # Pagination functionality
            number_of_pages = 1
            if form.page.data is not None and form.per_page.data is not None:
                per_page = form.per_page.data
                number_of_pages = math.ceil(len(followings_list) / per_page)
                # Assign the page index to the maximum if it exceeds the max index
                page = form.page.data if form.page.data < number_of_pages else number_of_pages-1
                followings_list = followings_list[page*per_page:(page+1)*per_page]
            return make_response(jsonify({'number_of_pages':number_of_pages ,'followings' :followings_list}),200)

        else:
            return make_response(jsonify({'error': 'Input Format Error'}), 400)

    @api.doc(responses={200: 'User is unfollowed successfully',
                        400: 'Input Format Error',
                        404: 'No follow information found with given authentication token and following_id',
                        500: 'Database Connection Error'})
    @api.expect(unfollow_parser)
    @login_required
    def delete(user_id, self):
        '''
            Takes the following_id and authentication token as inputs and deletes corresponding Follow entry.
        '''
        form = UnfollowForm(request.form)
        if form.validate():

            # Check if the given Follow instance exists.
            try:
                followSearch = Follow.query.filter(Follow.following_id == form.following_id.data, Follow.follower_id == user_id).all()
            except:
                return make_response(jsonify({'error': 'Database Connection Error'}), 500)

            if len(followSearch) == 0:
                return make_response(jsonify({'error': 'No follow information found with given authentication token and following_id'}), 404)

            follow_instance = followSearch[0]
            try:
                db.session.delete(follow_instance)
                db.session.commit()
            except:
                return make_response(jsonify({'error': 'Database Connection Problem'}), 500)

            return make_response(jsonify({'msg': 'User is unfollowed successfully'}), 200)

        else:
            return make_response(jsonify({'error': 'Input Format Error'}), 400)

@follow_system_ns.route("/followers")
class GetFollowersAPI(Resource):

    @api.doc(responses={404: 'Followers List is empty',
                        400: 'Input Format Error',
                        500: ' Database Connection Error'})
    @api.response(200, 'Followers List is successfully returned', followers_model)
    @api.expect(get_followers_parser)
    @login_required
    @follow_required(param_loc='args',requested_user_id_key='following_id')
    def get(user_id, self):
        '''
            Returns a list of dictionaries with id, name, surname, e_mail, rate and is_private informations.
        '''
        form = GetFollowersForm(request.args)
        if form.validate():
            try:
                followSearch = Follow.query.filter(Follow.following_id == form.following_id.data).all()
            except:
                return make_response(jsonify({'error': 'Database Connection Error'}), 500)

            if followSearch is []:
                return make_response(jsonify({'error': 'User not found'}), 404)

            follower_users = []
            for follow in followSearch:
                follower_users.append(User.query.filter(User.id == follow.follower_id).first())

            followers_list = [
                {'id': follower_user.id, 'name': follower_user.name, 'surname': follower_user.surname,
                 'e_mail': follower_user.e_mail, 'rate': follower_user.rate,
                 'is_private': follower_user.is_private,
                 'profile_photo': profile_photo_link(follower_user.profile_photo,follower_user.id) 
                 } for follower_user in follower_users]

            # Pagination functionality
            number_of_pages = 1
            if form.page.data is not None and form.per_page.data is not None:
                per_page = form.per_page.data
                number_of_pages = math.ceil(len(followers_list) / per_page)
                # Assign the page index to the maximum if it exceeds the max index
                page = form.page.data if form.page.data < number_of_pages else number_of_pages-1
                followers_list = followers_list[page*per_page:(page+1)*per_page]
            return make_response(jsonify({'number_of_pages':number_of_pages ,'followers' :followers_list}),200)

        else:
            return make_response(jsonify({'error': 'Input Format Error'}), 400)


@follow_system_ns.route("/follow_requests")
class FollowRequestAPI(Resource):

    @api.doc(responses={404: 'Follow Requests List is empty',
                        400: 'Input Format Error',
                        401: 'Current user is unauthorized to see the Follow Requests',
                        500: 'Database Connection Error'})
    @api.response(200, 'Follow Requests List is successfully returned', follow_requests_model)
    @api.expect(get_follow_requests_parser)
    @login_required
    def get(user_id, self):
        '''
            Returns a list of dictionaries that contains id, name, surname, e_mail, rate and is_private.
        '''
        form = GetFollowRequestsForm(request.args)
        if form.validate():

            # user_id should be equal to form.following_id.data
            if user_id != form.following_id.data:
                return make_response(jsonify({'error': 'Unauthorized'}), 401)

            try:
                followSearch = FollowRequests.query.filter(FollowRequests.following_id == form.following_id.data).all()
            except:
                return make_response(jsonify({'error': 'Database Connection Error'}), 500)

            if followSearch is []:
                return make_response(jsonify({'error': 'No Follow Request Found'}), 404)

            follower_users = []
            for follow in followSearch:
                follower_users.append(User.query.filter(User.id == follow.follower_id).first())

            follow_request_senders_list = [
                {'id': follower_user.id, 'name': follower_user.name, 'surname': follower_user.surname,
                 'e_mail': follower_user.e_mail, 'rate': follower_user.rate,
                 'is_private': follower_user.is_private,
                 'profile_photo': profile_photo_link(follower_user.profile_photo,follower_user.id)} 
                 for follower_user in follower_users]

            # Pagination functionality
            number_of_pages = 1
            if form.page.data is not None and form.per_page.data is not None:
                per_page = form.per_page.data
                number_of_pages = math.ceil(len(follow_request_senders_list) / per_page)
                # Assign the page index to the maximum if it exceeds the max index
                page = form.page.data if form.page.data < number_of_pages else number_of_pages-1
                follow_request_senders_list = follow_request_senders_list[page*per_page:(page+1)*per_page]
            return make_response(jsonify({'number_of_pages':number_of_pages ,'follow_requests' :follow_request_senders_list}),200)

        else:
            return make_response(jsonify({'error': 'Input Format Error'}), 400)

    @api.doc(responses={200: 'Follow Request is sent successfully',
                        400: 'Input Format Error',
                        401: 'Current user is unauthorized to send Follow Request',
                        500: 'Database Connection Error'})
    @api.expect(send_follow_requests_parser)
    @login_required
    def post(user_id, self):
        '''
            Creates FollowRequest record if profile is private, creates Follow record if profile is public.
        '''
        form = SendFollowRequestsForm(request.form)
        if form.validate():

            # Current user should be the follower.
            if user_id != form.follower_id.data:
                return make_response(jsonify({'error': 'Unauthorized'}), 401)

            following_user = ""
            try:
                following_user = User.query.filter((User.id == form.following_id.data)&(User.is_valid == True)).first()
            except:
                return make_response(jsonify({'error': 'Database Connection Error'}), 500)

            if following_user is None:
                return make_response(jsonify({'error': 'User not found'}), 400)
                
            follow_record = ""
            if following_user.is_private:
                # Create FollowRequest record if the following user has private profile.
                follow_record = FollowRequests(form.follower_id.data, form.following_id.data)
                logged_in_user = User.query.filter(User.id == user_id).first()
                text = "{} sent you a follow request".format(logged_in_user.name + " " + logged_in_user.surname)
                NotificationManager.add_notification(form.following_id.data,[logged_in_user.id],text)
            else:
                # Create Follow record if the following user has public profile.
                follow_record = Follow(form.follower_id.data, form.following_id.data)
                try:
                    logged_in_user = User.query.filter(User.id == user_id).first()
                    text = "{} started following you".format(logged_in_user.name + " " + logged_in_user.surname)
                    NotificationManager.add_notification(form.following_id.data,[logged_in_user.id],text)
                except:
                    return make_response(jsonify({'error': 'Database Connection Error'}), 500)

            try:
                db.session.add(follow_record)  # Creating a new database entry.
                db.session.commit()
            except:
                return make_response(jsonify({'error': 'Database Connection Error'}), 500)

            return make_response(jsonify({'msg': 'Follow Request is successfully added'}), 200)

        else:
            return make_response(jsonify({'error': 'Input Format Error'}), 400)

    @api.doc(responses={200: 'Follow Request is replied successfully',
                        400: 'Input Format Error',
                        401: 'Current user is unauthorized to reply the Follow Request',
                        404: 'Follow Request not found',
                        500: 'Database Connection Error'})
    @api.expect(reply_follow_requests_parser)
    @login_required
    def delete(user_id, self):
        '''
            Takes the follower_id and following_id as inputs and replies the corresponding Follow Request.
        '''
        form = ReplyFollowRequestsForm(request.form)
        if form.validate():

            # Current user should be the following.
            if user_id != form.following_id.data:
                return make_response(jsonify({'error': 'Unauthorized'}), 401)

            # Check if the given FollowRequest instance exists.
            try:
                followSearch = FollowRequests.query.filter(FollowRequests.following_id == form.following_id.data,
                                                           FollowRequests.follower_id == form.follower_id.data).all()
            except:
                return make_response(jsonify({'error': 'Database Connection Error'}), 500)

            if len(followSearch) == 0:
                return make_response(jsonify({'error': 'Follow Request not found'}), 404)

            follow_request = followSearch[0]

            # Accept if the state of the reply is 1.
            if form.state.data == 1:
                new_follow_entry = Follow(follow_request.follower_id, follow_request.following_id)
                try:
                    db.session.add(new_follow_entry)
                    db.session.delete(follow_request)
                    db.session.commit()
                except:
                    return make_response(jsonify({'error': 'Database Connection Error'}), 500)
                # !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                # Add notification to follower that his/her follow request is accepted.
                try:
                    follower_user = User.query.filter(User.id == follow_request.follower_id).first()
                    following_user = User.query.filter(User.id == follow_request.following_id).first()
                    text = "{} accepted your follow request".format(following_user.name + " " + following_user.surname)
                    NotificationManager.add_notification(follower_user.id, [following_user.id], text)
                except:
                    return make_response(jsonify({'error': 'Database Connection Error'}), 500)
                # !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            # Reject if the state of the reply is 2.
            elif form.state.data == 2:
                try:
                    db.session.delete(follow_request)
                    db.session.commit()
                except:
                    return make_response(jsonify({'error': 'Database Connection Problem'}), 500)

            # Return error if the state of reply is other than 1 or 2.
            else:
                return make_response(jsonify({'error': 'State of FollowRequest answer can only be 1 or 2!'}), 400)

            return make_response(jsonify({'msg': 'Follow Request is successfully replied'}), 200)

        else:
            return make_response(jsonify({'error': 'Input Format Error'}), 400)

@follow_system_ns.route("/comment")
class CommentRateAPI(Resource):
    '''
        Endpoints for User Comment Model.
    '''
    @api.doc(responses={401: 'Account Problems', 400: 'Input Format Error', 500: ' Database Connection Error',
                        404: 'Not found'})
    @api.response(200, 'Success', comment_list_model)
    @api.expect(get_comment_parser)
    @login_required
    @follow_required(param_loc='args',requested_user_id_key='commented_user_id')
    def get(user_id,self):
        '''
            Get Comments.
        '''
        form = GetCommentsForm(request.args)
        if form.validate():
            try:
                user_comments = Comments.query.filter(Comments.commented_user_id == form.commented_user_id.data).order_by(Comments.timestamp.desc()).all()
            except:
                return make_response(jsonify({'error': 'Database Connection Error'}), 500)
            if len(user_comments)==0:
                return make_response(jsonify({'result': []}), 200)
            return_list = []
            for comment in user_comments:
                try:
                    commenter = User.query.filter(User.id == comment.owner_id).first()
                except:
                    return make_response(jsonify({'error': 'Database Connection Error'}), 500)
                if commenter is None:
                    return make_response(jsonify({'error': 'Commenter is None, smth wrong'}), 500)
                return_list.append({
                    "comment_id": comment.id,
                    "owner_id": comment.owner_id,
                    "commented_user_id": comment.commented_user_id,
                    "timestamp": comment.timestamp,
                    "rate": comment.rate,
                    "text": comment.text
                })
            # Pagination functionality
            number_of_pages = 1
            if form.page.data is not None and form.per_page.data is not None:
                per_page = form.per_page.data
                number_of_pages = math.ceil(len(return_list) / per_page)
                # Assign the page index to the maximum if it exceeds the max index
                page = form.page.data if form.page.data < number_of_pages else number_of_pages - 1
                return_list = return_list[page * per_page:(page + 1) * per_page]
            return make_response(jsonify({'number_of_pages': number_of_pages, 'result': return_list}), 200)
        else:
            return make_response(jsonify({'error': 'Input Format Error'}), 400)

    @api.doc(responses={401: 'Account Problems', 400: 'Input Format Error', 500: ' Database Connection Error',
                        404: 'Not found'})
    @api.expect(post_comment_parser)
    @login_required
    @previous_collaboration_required(param_loc="form",requested_user_id_key="commented_user_id")
    def post(user_id, self):
        '''
            Create user comment
        '''
        form = PostCommentForm(request.form)
        if form.validate():
            try:
                prev_comment = Comments.query.filter_by(owner_id=user_id,commented_user_id=form.commented_user_id.data).first()
            except:
                return make_response(jsonify({'error': 'DB connection error'}), 500)

            if prev_comment is not None:
                return make_response(jsonify({'error': 'You are not allowed to send more than 1 comment to a user'}), 403)

            try:
                comment = Comments(user_id, form.commented_user_id.data, form.rate.data, form.text.data)
            except:
                return make_response(jsonify({'error': 'DB connection error'}), 500)
            try:
                db.session.add(comment)
                db.session.commit()
            except:
                return make_response(jsonify({'error': 'Database Connection Error'}), 500)
            if update_rate(form.commented_user_id.data):
                return make_response(jsonify({'msg': 'Comment is successfully created'}), 201)
            else:
                return make_response(jsonify({'error': 'Database Connection Error'}), 500)
        else:
            return make_response(jsonify({'error': 'Input Format Error'}), 400)

    @api.doc(responses={401: 'Account Problems', 400: 'Input Format Error', 500: ' Database Connection Error',
                        404: 'Not found'})
    @api.expect(delete_comment_parser)
    @login_required
    def delete(user_id, self):
        '''
            Deletes an user comment
        '''
        form = DeleteCommentForm(request.args)
        if form.validate():
            try:
                # Find comment record
                comment = Comments.query.filter((Comments.id == form.comment_id.data)).first()

                if comment.owner_id != user_id:
                    return make_response(jsonify({'error': 'Unauthorized'}), 401)

                if comment is None:
                    return make_response(jsonify({'error': 'Comment not found'}), 404)

                db.session.delete(comment)
                db.session.commit()
            except:
                return make_response(jsonify({'error': 'Database Connection Error'}), 500)
            
            if update_rate(comment.commented_user_id):
                return make_response(jsonify({'msg': 'Issue Comment is successfully deleted'}), 201)
            else:
                return make_response(jsonify({'error': 'Database Connection Error'}), 500)

        else:
            return make_response(jsonify({'error': 'Input Format Error'}), 400)

def register_resources(api):
    api.add_namespace(follow_system_ns)
