from flask import make_response,jsonify,request
from flask_restplus import Resource,Namespace, fields

from app.workspace_system.forms import TrendingProjectsForm,trending_project_parser
from app.workspace_system.models import Workspace,Contribution
from app.auth_system.models import User
from app.workspace_system.helpers import schedule_regularly


from app import api, db

workspace_system_ns = Namespace("Workspace System",
                                description="Workspace System Endpoints",
                                path = "/worksapce_system")

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
    "contributors": fields.List(
        fields.Nested(contributor_model)
    ),
})

trending_workspaces_model = api.model('Trending Projects', {
    'trending_projects': fields.List(
        fields.Nested(workspace_model)
    )
})

@workspace_system_ns.route("/trending_projects")
class TrendingWorkspacesAPI(Resource):

    @api.doc(responses={500 : 'Database Connection Error',400: 'Invalid Input Format'})
    @api.response(200, 'Valid Response', trending_workspaces_model)
    @api.expect(trending_project_parser)
    def get(self):
        form = TrendingProjectsForm(request.args)
        if form.validate():
            try:
                all_workspaces = Workspace.query.order_by(Workspace.trending_score.desc()).all()
            except:
                return make_response(jsonify({"err": "Database Connection Error"}),500)
            all_workspaces = all_workspaces[:form.number_of_workspaces.data]
            workspace_response = [{
                "id": workspace.id,
                "title": workspace.title,
                "description": workspace.description,
                "state": workspace.state
            } for workspace in all_workspaces]
            # Add Contributors of the workspaces
            for index,workspace in enumerate(workspace_response):
                try:
                    contributors = Contribution.query.filter(Contribution.workspace_id == workspace["id"]).all()
                except:
                    return make_response(jsonify({"err": "Database Connection Error"}),500)
                contributor_list = []
                for contributor in contributors:
                    try:
                        user = User.query.get(contributor.user_id)
                    except:
                        return make_response(jsonify({"err": "Database Connection Error"}),500)
                    contributor_list.append({
                        "id": user.id,
                        "name": user.name,
                        "surname": user.surname
                    })
                workspace_response[index]["contributor_list"] = contributor_list
            return make_response(jsonify({"trending_projects": workspace_response}),200)
        else:
            return make_response(jsonify({"err": "Invalid Input Format"}),400)


def register_resources(api):
    schedule_regularly()
    api.add_namespace(workspace_system_ns)