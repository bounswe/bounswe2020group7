from flask import make_response, jsonify, request
from flask_restplus import Resource, Namespace
from flask import current_app as app

from app import api, db
from app.follow_system.forms import GetFollowingsForm, GetFollowersForm, GetFollowRequestsForm, \
    SendFollowRequestsForm, ReplyFollowRequestsForm
from app.follow_system.forms import get_followings_parser, get_followers_parser, get_follow_requests_parser, send_follow_requests_parser, \
    reply_follow_requests_parser
from app.follow_system.models import Follow, FollowRequests
from app.auth_system.models import User
from app.auth_system.views import login_required
from app.follow_system.helpers import follow_required


follow_system_ns = Namespace("Follow System",
                             description="Follow System Endpoints",
                             path="/follow")

@follow_system_ns.route("/followings")
class GetFollowingsAPI(Resource):

    @api.doc(responses={200: 'Followings List is successfully returned',
                        404: 'Followings List is empty',
                        400: 'Input Format Error',
                        500: 'Database Connection Error'})
    @api.expect(get_followings_parser)
    @login_required
    @follow_required(param_loc='args',requested_user_id_key='follower_id')
    def get(user_id, self):
        '''
            Takes follower_id as input and returns the followings as a list.
        '''
        form = GetFollowingsForm(request.args)
        if form.validate():
            try:
                followSearch = Follow.query.filter(Follow.follower_id == form.follower_id.data).all()
            except:
                return make_response(jsonify({'error': 'Database Connection Error'}), 500)

            if followSearch is []:
                return make_response(jsonify({'error': 'User not found'}), 404)

            mylist = []
            for follow in followSearch:
                mylist.append(follow.following_id)

            return make_response(jsonify(mylist), 200)

        else:
            return make_response(jsonify({'error': 'Input Format Error'}), 400)


@follow_system_ns.route("/followers")
class GetFollowersAPI(Resource):

    @api.doc(responses={200: 'Followers List is successfully returned',
                        404: 'Followers List is empty',
                        400: 'Input Format Error',
                        500: ' Database Connection Error'})
    @api.expect(get_followers_parser)
    @login_required
    @follow_required(param_loc='args',requested_user_id_key='following_id')
    def get(user_id, self):
        '''
            Takes following_id as input and returns the followers as a list.
        '''
        form = GetFollowersForm(request.args)
        if form.validate():
            try:
                followSearch = Follow.query.filter(Follow.following_id == form.following_id.data).all()
            except:
                return make_response(jsonify({'error': 'Database Connection Error'}), 500)

            if followSearch is []:
                return make_response(jsonify({'error': 'User not found'}), 404)

            mylist = []
            for follow in followSearch:
                mylist.append(follow.follower_id)

            return make_response(jsonify(mylist), 200)

        else:
            return make_response(jsonify({'error': 'Input Format Error'}), 400)


@follow_system_ns.route("/follow_requests")
class FollowRequestAPI(Resource):

    @api.doc(responses={200: 'Follow Requests List is successfully returned',
                        404: 'Follow Requests List is empty',
                        400: 'Input Format Error',
                        401: 'Current user is unauthorized to see the Follow Requests',
                        500: 'Database Connection Error'})
    @api.expect(get_follow_requests_parser)
    @login_required
    @follow_required(param_loc='args',requested_user_id_key='following_id')
    def get(user_id, self):
        '''
            Takes following_id as input and returns the follow requests as a list.
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

            mylist = []
            for follow in followSearch:
                mylist.append(follow.follower_id)

            return make_response(jsonify(mylist), 200)

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

            if following_user in None:
                return make_response(jsonify({'error': 'User not found'}), 400)
                
            follow_record = ""
            if following_user.is_private:
                # Create FollowRequest record if the following user has private profile.
                follow_record = FollowRequests(form.follower_id.data, form.following_id.data)
            else:
                # Create Follow record if the following user has public profile.
                follow_record = Follow(form.follower_id.data, form.following_id.data)

            try:
                db.session.add(follow_record)  # Creating a new database entry.
                db.session.commit()
            except:
                return make_response(jsonify({'error': 'Database Connection Error'}), 500)

            return make_response(jsonify({'msg': 'Follow Request is succesfully added'}), 200)


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


def register_resources(api):
    api.add_namespace(follow_system_ns)
