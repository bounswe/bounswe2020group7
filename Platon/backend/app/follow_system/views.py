from flask import Response
from app import api
from flask_restful import Resource

class FollowAPI(Resource):
    def get(self):
        return Response("Platon API is under development!!",200)


def register_resources():
    api.add_resource(FollowAPI,"/follow")