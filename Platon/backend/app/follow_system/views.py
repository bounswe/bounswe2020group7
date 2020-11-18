from flask import make_response,jsonify,request
from flask_restplus import Resource
from flask import current_app as app

from app import api, db
from app.follow_system.forms import GetFollowingsForm, GetFollowersForm, GetFollowRequestsForm, SendFollowRequestsForm, AcceptFollowRequestsForm, RejectFollowRequestsForm
from app.follow_system.models import Follow, FollowRequests


#import jwt
import datetime
from dateutil import parser
from hashlib import sha256
from functools import wraps


#Input: follower_id
#Output: list containing following_id values.
class GetFollowingsAPI(Resource):

    def post(self):
        print("GetFollowingsAPI's post")
        form = GetFollowingsForm(request.form)
        if form.validate():
            try:
                followSearch = Follow.query.filter(Follow.follower_id == form.follower_id.data).all()
            except:
                return make_response(jsonify({'error' : 'Database Connection Problem'}),500)
            '''
            if followSearch is []:
                return make_response(jsonify({'error' : 'Followings Not Found'}),404)
            '''
            #print(followSearch)
            mylist = []
            for follow in followSearch:
                mylist.append(follow.following_id)

            return make_response(jsonify(mylist), 200)

        else:
            return make_response(jsonify({'error': ''}), 401)

class GetFollowersAPI(Resource):

    def post(self):
        print("GetFollowersAPI's post")
        form = GetFollowersForm(request.form)
        if form.validate():
            try:
                followSearch = Follow.query.filter(Follow.following_id == form.following_id.data).all()
            except:
                return make_response(jsonify({'error': 'Database Connection Problem'}), 500)
            '''
            if followSearch is []:
                return make_response(jsonify({'error' : 'Followings Not Found'}),404)
            '''
            # print(followSearch)
            mylist = []
            for follow in followSearch:
                mylist.append(follow.follower_id)

            return make_response(jsonify(mylist), 200)

        else:
            return make_response(jsonify({'error': ''}), 401)

class GetFollowRequestsAPI(Resource):

    def post(self):
        print("GetFollowRequestsAPI's post")
        form = GetFollowRequestsForm(request.form)
        if form.validate():
            try:
                followSearch = FollowRequests.query.filter(FollowRequests.following_id == form.following_id.data).all()
            except:
                return make_response(jsonify({'error': 'Database Connection Problem'}), 500)
            '''
            if followSearch is []:
                return make_response(jsonify({'error' : 'Followings Not Found'}),404)
            '''
            # print(followSearch)
            mylist = []
            for follow in followSearch:
                mylist.append(follow.follower_id)

            return make_response(jsonify(mylist), 200)

        else:
            return make_response(jsonify({'error': ''}), 401)

class SendFollowRequestAPI(Resource):

    def post(self):
        print("SendFollowRequestAPI's post")
        form = SendFollowRequestsForm(request.form)
        if form.validate():
            try:
                fr = FollowRequests(form.follower_id.data, form.following_id.data)
                db.session.add(fr)  # Creating a new database entry.
                db.session.commit()
            except:
                return make_response(jsonify({'error': 'Database Connection Problem'}), 500)

            return make_response(jsonify({'msg' : 'Follow Request is succesfully added'}),200)

        else:
            return make_response(jsonify({'error': ''}), 401)

'''
class AcceptFollowRequestAPI(Resource):

    def post(self):
        print("AcceptFollowRequestAPI's post")
        form = AcceptFollowRequestsForm(request.form)
        if form.validate():

            # Check if the given FollowRequest instance exists.
            try:
                followSearch = FollowRequests.query.filter(FollowRequests.following_id == form.following_id.data and ).all()
            except:
                return make_response(jsonify({'error': 'Database Connection Problem'}), 500)
            # If not, return error.
            # If it exists: Create a Follow instance. Delete FollowRequest instance.



            try:
                fr = FollowRequests(form.follower_id.data, form.following_id.data)
                db.session.add(fr)  # Creating a new database entry.
                db.session.commit()
            except:
                return make_response(jsonify({'error': 'Database Connection Problem'}), 500)

            return make_response(jsonify({'msg' : 'Follow Request is succesfully accepted'}),200)

        else:
            return make_response(jsonify({'error': ''}), 401)


class RejectFollowRequestAPI(Resource):

    def post(self):
        form = RejectFollowRequestsForm(request.form)
        print("hi")
        
'''


def register_resources(api):
    api.add_resource(GetFollowingsAPI,"/follow/followings")
    api.add_resource(GetFollowersAPI, "/follow/followers")
    api.add_resource(GetFollowRequestsAPI, "/follow/get_follow_requests")
    api.add_resource(SendFollowRequestAPI, "/follow/send_follow_requests")
    #api.add_resource(AcceptFollowRequestAPI,"/follow/accept_follow_requests")
    #api.add_resource(RejectFollowRequestAPI, "/follow/reject_follow_requests")

