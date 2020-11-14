from flask import Response
from app import api
from flask_restful import Resource

class ProfileAPI(Resource):
    def get(self):
        return Response("Platon API is under development!!",200)


def register_resources():
    api.add_resource(ProfileAPI,"/profile")