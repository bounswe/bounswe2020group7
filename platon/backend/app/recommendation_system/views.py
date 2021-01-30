from flask import make_response,jsonify,request
from flask_restplus import Resource,Namespace, fields
from app.auth_system.helpers import login_required,profile_photo_link
from app.auth_system.models import User
from app.workspace_system.models import Workspace
from app.workspace_system.helpers import workspace_exists,active_contribution_required
from app.profile_management.models import Jobs
from app.recommendation_system.helpers import *
from app.recommendation_system.forms import *
from app.recommendation_system.models import *

from app import api, db
import math

recommendation_system_ns = Namespace("Recommendation System",
                                     description="Recommendation System Endpoints",
                                     path = "/recommendation_system")

user_model = api.model('User Recommendation', {
    'id': fields.Integer,
    'name': fields.String,
    'surname': fields.String,
    'profile_photo': fields.String,
    "job": fields.String,
    "institution": fields.String,
    "is_private": fields.Integer
})

contributor_model = api.model("Contributor", {
    "id": fields.Integer,
    "name": fields.String,
    "surname": fields.String,
    }
)

workspace_model = api.model('Workspace', {
    "id": fields.Integer,
    "title": fields.String,
    "description": fields.String,
    "state": fields.Integer,
    "creator_id": fields.Integer,
    "contributors": fields.List(
        fields.Nested(contributor_model)
    )
})

workspace_recommendation_list = api.model('Workspace Recommendations List', {
    'recommendation_list': fields.List(
        fields.Nested(workspace_model)
    )
})

user_recommendation_list = api.model('User Recommendations List', {
    'recommendation_list': fields.List(
        fields.Nested(user_model)
    )
})

@recommendation_system_ns.route("/follow")
class FollowRecommandationsAPI(Resource):

    @api.doc(responses={401: 'Authantication Required',
                        400: 'Input Format Error',
                        500: 'Database Connection Error'})
    @api.response(200, 'Valid Response', user_recommendation_list)
    @api.expect(recommendation_parser)
    @login_required
    def get(user_id,self):
        form = UserForm(request.args)
        if form.validate():
            # Fetch recommendations from database
            try:
                all_recommendations = FollowRecommendationItem.query.filter_by(owner_id=user_id).order_by(FollowRecommendationItem.score.desc()).all()
            except:
                return make_response(jsonify({"error": "Database Connection Error"}),500)
            # Fetch the user information from database (Also reduce number of them to required number)
            try:
                all_users = [User.query.get(recommendation.recommendation_id) for recommendation in all_recommendations[:form.number_of_recommendations.data]]
            except:
                return make_response(jsonify({"error": "Database Connection Error"}),500)
            # Create the response format
            try:
                response = [
                    {
                    'id': user.id,
                    'name': user.name,
                    'surname': user.surname,
                    'profile_photo': profile_photo_link(user.profile_photo,user.id),
                    "job": Jobs.query.get(user.job_id).name,
                    "institution": user.institution,
                    "is_private": int(user.is_private)               
                    } 
                    for user in all_users]
            except:
                return make_response(jsonify({"error": "Database Connection Error"}),500)
            return make_response(jsonify({"recommendation_list": response}),200)
        else:
            return make_response(jsonify({"error": "Input Format Error"}),400)

@recommendation_system_ns.route("/workspace")
class WorkspaceRecommendationsAPI(Resource):
    
    @api.doc(responses={401: 'Authantication Required',
                        400: 'Input Format Error',
                        500: 'Database Connection Error'})
    @api.response(200, 'Valid Response', user_recommendation_list)
    @api.expect(recommendation_parser)    
    @login_required
    def get(user_id,self):
        form = UserForm(request.args)
        if form.validate():
            # Fetch recommendations from database
            try:
                all_recommendations = WorkspaceRecommendationItem.query.filter_by(owner_id=user_id).order_by(WorkspaceRecommendationItem.score.desc()).all()
            except:
                return make_response(jsonify({"error": "Database Connection Error"}),500)
            # Fetch the user information from database (Also reduce number of them to required number)
            try:
                all_ws = [Workspace.query.get(recommendation.recommendation_id) for recommendation in all_recommendations]
            except:
                return make_response(jsonify({"error": "Database Connection Error"}),500)
            # Create Response Format
            response = [
                {
                    "id": ws.id,
                    "title": ws.title,
                    "description": ws.description,
                    "state": ws.state,
                    "creator_id": ws.creator_id,
                }
                for ws in all_ws if ws.is_private == 0
            ][:form.number_of_recommendations.data]

            for index,workspace in enumerate(response):
                try:
                    contributors = Contribution.query.filter(Contribution.workspace_id == workspace["id"]).all()
                except:
                    return make_response(jsonify({"error": "Database Connection Error"}),500)
                contributor_list = []
                for contributor in contributors:
                    try:
                        user = User.query.get(contributor.user_id)
                    except:
                        return make_response(jsonify({"error": "Database Connection Error"}),500)
                    contributor_list.append({
                        "id": user.id,
                        "name": user.name,
                        "surname": user.surname
                    })
                response[index]["contributor_list"] = contributor_list
            return make_response(jsonify({"recommendation_list": response}),200)
        else:
            return make_response(jsonify({"error": "Input Format Error"}),400)

@recommendation_system_ns.route("/collaboration")
class CollaborationRecommendationsAPI(Resource):

    @api.doc(responses={401: 'Authantication Required',
                        400: 'Input Format Error',
                        500: 'Database Connection Error'})
    @api.response(200, 'Valid Response', user_recommendation_list)
    @api.expect(recommendation_ws_parser)
    @login_required
    @workspace_exists(param_loc="args",workspace_id_key="workspace_id")
    @active_contribution_required(param_loc="args",workspace_id_key="workspace_id")
    def get(user_id,self):
        form = WorkspaceForm(request.args)
        if form.validate():
            # Fetch recommendations from database
            try:
                all_recommendations = CollaboratorRecommendationItem.query.filter_by(owner_id=form.workspace_id.data).order_by(CollaboratorRecommendationItem.score.desc()).all()
            except:
                return make_response(jsonify({"error": "Database Connection Error"}),500)
            # Fetch the user information from database (Also reduce number of them to required number)
            try:
                all_users = [User.query.get(recommendation.recommendation_id) for recommendation in all_recommendations[:form.number_of_recommendations.data]]
            except:
                return make_response(jsonify({"error": "Database Connection Error"}),500)
            # Create the response format
            try:
                response = [
                    {
                    'id': user.id,
                    'name': user.name,
                    'surname': user.surname,
                    'profile_photo': profile_photo_link(user.profile_photo,user.id),
                    "job": Jobs.query.get(user.job_id).name,
                    "institution": user.institution,
                    "is_private": int(user.is_private)               
                    } 
                    for user in all_users]
            except:
                return make_response(jsonify({"error": "Database Connection Error"}),500)
            return make_response(jsonify({"recommendation_list": response}),200)
        else:
            return make_response(jsonify({"error": "Input Format Error"}),400)

def register_resources(api):
    schedule_regularly()
    api.add_namespace(recommendation_system_ns)