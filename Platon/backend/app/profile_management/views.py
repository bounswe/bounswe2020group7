from flask import Response
from flask_restful import Resource

class ProfileAPI(Resource):
    def get(self):
        return Response("Platon API is under development!!",200)


def register_resources(api):
    api.add_resource(ProfileAPI,"/profile")