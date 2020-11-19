from flask import make_response, jsonify, request
from flask_restplus import Resource
from flask import current_app as app

from app import api, db
from app.follow_system.forms import GetFollowingsForm, GetFollowersForm, GetFollowRequestsForm, \
    SendFollowRequestsForm, AcceptFollowRequestsForm, RejectFollowRequestsForm
from app.follow_system.forms import get_followings_parser, get_followers_parser, get_follow_requests_parser, send_follow_requests_parser, \
    accept_follow_requests_parser, reject_follow_requests_parser
from app.follow_system.models import Follow, FollowRequests
from app.auth_system.views import login_required, generate_token

import datetime
from dateutil import parser
from hashlib import sha256
from functools import wraps


# Private Account Restrictions not implemented.
class GetFollowingsAPI(Resource):

    @api.doc(responses={200: 'Followings List is successfully returned',
                        404: 'Followings List is empty',
                        400: 'Input Format Error',
                        500: 'Database Connection Error'})
    @api.expect(get_followings_parser)
    def post(self):
        '''
            Takes follower_id as input and returns the followings as a list.
        '''
        form = GetFollowingsForm(request.form)
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


# Private Account Restrictions not implemented.
class GetFollowersAPI(Resource):

    @api.doc(responses={200: 'Followers List is successfully returned',
                        404: 'Followers List is empty',
                        400: 'Input Format Error',
                        500: ' Database Connection Error'})
    @api.expect(get_followers_parser)
    def post(self):
        '''
            Takes following_id as input and returns the followers as a list.
        '''
        form = GetFollowersForm(request.form)
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

class GetFollowRequestsAPI(Resource):

    @api.doc(responses={200: 'Follow Requests List is successfully returned',
                        404: 'Follow Requests List is empty',
                        400: 'Input Format Error',
                        401: 'Current user is unauthorized to see the Follow Requests',
                        500: 'Database Connection Error'})
    @api.expect(get_follow_requests_parser)
    @login_required
    def post(user_id, new_token, self):
        '''
            Takes following_id as input and returns the follow requests as a list.
        '''
        form = GetFollowRequestsForm(request.form)
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

class SendFollowRequestAPI(Resource):

    @api.doc(responses={200: 'Follow Request is sent successfully',
                        400: 'Input Format Error',
                        401: 'Current user is unauthorized to send Follow Request',
                        500: 'Database Connection Error'})
    @api.expect(send_follow_requests_parser)
    @login_required
    def post(user_id, new_token, self):
        '''
            Takes follower_id and following_id as inputs and creates the corresponding Follow Request.
        '''
        form = SendFollowRequestsForm(request.form)
        if form.validate():

            # Current user should be the follower.
            if user_id != form.follower_id.data:
                return make_response(jsonify({'error': 'Unauthorized'}), 401)

            fr = FollowRequests(form.follower_id.data, form.following_id.data)
            try:
                db.session.add(fr)  # Creating a new database entry.
                db.session.commit()
            except:
                return make_response(jsonify({'error': 'Database Connection Error'}), 500)

            return make_response(jsonify({'msg': 'Follow Request is succesfully added'}), 200)

        else:
            return make_response(jsonify({'error': 'Input Format Error'}), 400)


# login_required decorator will be added.
class AcceptFollowRequestAPI(Resource):

    @api.doc(responses={200: 'Follow Request is accepted successfully',
                        400: 'Input Format Error',
                        401: 'Current user is unauthorized to accept the Follow Request',
                        404: 'Follow Request not found',
                        500: 'Database Connection Error'})
    @api.expect(accept_follow_requests_parser)
    @login_required
    def post(user_id, new_token, self):
        '''
            Takes follower_id and following_id as inputs and accepts the corresponding Follow Request.
        '''
        form = AcceptFollowRequestsForm(request.form)
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

            if len(followSearch)==0:
                return make_response(jsonify({'error': 'Follow Request not found'}), 404)

            # If it exists: Create a Follow instance. Delete FollowRequest instance.
            follow_request = followSearch[0]
            new_follow_entry = Follow(follow_request.follower_id, follow_request.following_id)
            try:
                db.session.add(new_follow_entry)
                db.session.delete(follow_request)
                db.session.commit()
            except:
                return make_response(jsonify({'error': 'Database Connection Error'}), 500)

            return make_response(jsonify({'msg': 'Follow Request is succesfully accepted'}), 200)

        else:
            return make_response(jsonify({'error': 'Input Format Error'}), 400)


# login_required decorator will be added.
class RejectFollowRequestAPI(Resource):

    @api.doc(responses={200: 'Follow Request is rejected successfully',
                        400: 'Input Format Error',
                        401: 'Current user is unauthorized to reject the Follow Request',
                        404: 'Follow Request not found',
                        500: 'Database Connection Error'})
    @api.expect(reject_follow_requests_parser)
    @login_required
    def post(user_id, new_token, self):
        '''
            Takes the follower_id and following_id as inputs and rejects the corresponding Follow Request.
        '''
        form = AcceptFollowRequestsForm(request.form)
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

            # If it exists: Delete FollowRequest instance.
            follow_request = followSearch[0]
            try:
                db.session.delete(follow_request)
                db.session.commit()
            except:
                return make_response(jsonify({'error': 'Database Connection Problem'}), 500)

            return make_response(jsonify({'msg': 'Follow Request is succesfully accepted'}), 200)

        else:
            return make_response(jsonify({'error': 'Bad Request'}), 400)


def register_resources(api):
    api.add_resource(GetFollowingsAPI, "/follow/followings")
    api.add_resource(GetFollowersAPI, "/follow/followers")
    api.add_resource(GetFollowRequestsAPI, "/follow/get_follow_requests")
    api.add_resource(SendFollowRequestAPI, "/follow/send_follow_requests")
    api.add_resource(AcceptFollowRequestAPI, "/follow/accept_follow_requests")
    api.add_resource(RejectFollowRequestAPI, "/follow/reject_follow_requests")
