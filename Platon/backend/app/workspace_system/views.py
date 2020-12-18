from flask import make_response,jsonify,request
from flask_restplus import Resource,Namespace, fields

from app import api, db

workspace_system_ns = Namespace("Workspace System",
                                description="Workspace System Endpoints",
                                path = "/worksapce_system")

workspace_model = api.model('Workspace', {
    "id": fields.Integer,
    "name": fields.String,
    "surname": fields.String,
    "profile_photo": fields.String,
    "e_mail": fields.String,
    "job_id": fields.Integer
})

search_user_list_model = api.model('User List(Search)', {
    'number_of_pages': fields.Integer,
    'result_list': fields.List(
        fields.Nested(search_user_model)
    )
})