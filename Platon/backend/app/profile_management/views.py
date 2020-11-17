from flask import Response
from flask_restplus import Resource
from enum import Enum

class ResearchType(Enum):
    HAND_WRITTEN = 0
    RESEARCHGATE = 1
    GOOGLE_SCHOLAR = 2
    BOTH_RG_AND_GS = 3
    
class ProfileAPI(Resource):
    def get(self):
        return Response("Platon API is under development!!",200)


def register_resources(api):
    api.add_resource(ProfileAPI,"/profile")