from flask import make_response, jsonify, request
from flask_restplus import Resource
from flask import current_app as app

from app import api, db
from app.follow_system.forms import GetFollowingsForm, GetFollowersForm, GetFollowRequestsForm, \
    SendFollowRequestsForm, AcceptFollowRequestsForm, RejectFollowRequestsForm
from app.follow_system.models import Follow, FollowRequests

import datetime
from dateutil import parser
from hashlib import sha256
from functools import wraps


# Private Account Restrictions not implemented.
class GetFollowingsAPI(Resource):

    def post(self):
        form = GetFollowingsForm(request.form)
        if form.validate():
            try:
                followSearch = Follow.query.filter(Follow.follower_id == form.follower_id.data).all()
            except:
                return make_response(jsonify({'error': 'Database Connection Problem'}), 500)

            if followSearch is []:
                return make_response(jsonify({'error': 'No User Found'}), 404)

            mylist = []
            for follow in followSearch:
                mylist.append(follow.following_id)

            return make_response(jsonify(mylist), 200)

        else:
            return make_response(jsonify({'error': 'Bad Request'}), 400)


# Private Account Restrictions not implemented.
class GetFollowersAPI(Resource):

    def post(self):
        form = GetFollowersForm(request.form)
        if form.validate():
            try:
                followSearch = Follow.query.filter(Follow.following_id == form.following_id.data).all()
            except:
                return make_response(jsonify({'error': 'Database Connection Problem'}), 500)

            if followSearch is []:
                return make_response(jsonify({'error': 'No User Found'}), 404)

            mylist = []
            for follow in followSearch:
                mylist.append(follow.follower_id)

            return make_response(jsonify(mylist), 200)

        else:
            return make_response(jsonify({'error': 'Bad Request'}), 400)

# login_required decorator will be added.
class GetFollowRequestsAPI(Resource):

    def post(self):
        form = GetFollowRequestsForm(request.form)
        if form.validate():
            try:
                followSearch = FollowRequests.query.filter(FollowRequests.following_id == form.following_id.data).all()
            except:
                return make_response(jsonify({'error': 'Database Connection Problem'}), 500)

            if followSearch is []:
                return make_response(jsonify({'error': 'No Follow Request Found'}), 404)

            mylist = []
            for follow in followSearch:
                mylist.append(follow.follower_id)

            return make_response(jsonify(mylist), 200)

        else:
            return make_response(jsonify({'error': 'Bad Request'}), 400)


# login_required decorator will be added.
class SendFollowRequestAPI(Resource):

    def post(self):
        form = SendFollowRequestsForm(request.form)
        if form.validate():
            fr = FollowRequests(form.follower_id.data, form.following_id.data)
            try:
                db.session.add(fr)  # Creating a new database entry.
                db.session.commit()
            except:
                return make_response(jsonify({'error': 'Database Connection Problem'}), 500)

            return make_response(jsonify({'msg': 'Follow Request is succesfully added'}), 200)

        else:
            return make_response(jsonify({'error': 'Bad Request'}), 400)


# login_required decorator will be added.
class AcceptFollowRequestAPI(Resource):

    def post(self):
        form = AcceptFollowRequestsForm(request.form)
        if form.validate():

            # Check if the given FollowRequest instance exists.
            try:
                followSearch = FollowRequests.query.filter(FollowRequests.following_id == form.following_id.data,
                                                           FollowRequests.follower_id == form.follower_id.data).all()
            except:
                return make_response(jsonify({'error': 'Database Connection Problem'}), 500)

            if len(followSearch)==0:
                return make_response(jsonify({'error': 'No FollowRequest found'}), 404)

            # If it exists: Create a Follow instance. Delete FollowRequest instance.
            follow_request = followSearch[0]
            new_follow_entry = Follow(follow_request.follower_id, follow_request.following_id)
            try:
                db.session.add(new_follow_entry)
                db.session.delete(follow_request)
                db.session.commit()
            except:
                return make_response(jsonify({'error': 'Database Connection Problem'}), 500)

            return make_response(jsonify({'msg': 'Follow Request is succesfully accepted'}), 200)

        else:
            return make_response(jsonify({'error': 'Bad Request'}), 400)


# login_required decorator will be added.
class RejectFollowRequestAPI(Resource):

    def post(self):
        form = AcceptFollowRequestsForm(request.form)
        if form.validate():

            # Check if the given FollowRequest instance exists.
            try:
                followSearch = FollowRequests.query.filter(FollowRequests.following_id == form.following_id.data,
                                                           FollowRequests.follower_id == form.follower_id.data).all()
            except:
                return make_response(jsonify({'error': 'Database Connection Problem'}), 500)

            if len(followSearch) == 0:
                return make_response(jsonify({'error': 'No FollowRequest found'}), 404)

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
