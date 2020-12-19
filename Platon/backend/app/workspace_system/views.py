from flask import current_app as app
from flask_restplus import Namespace, Resource, fields
from flask import make_response,jsonify,request

from app import api, db

from app.workspace_system.forms import *

from app.workspace_system.models import Workspace, WorkspaceSkill, WorkspaceRequirement, Contribution, Requirement
from app.profile_management.models import Skills
from app.auth_system.models import User

from app.auth_system.helpers import login_required
from app.workspace_system.helpers import *
from app.file_system.helpers import FileSystem

workspace_system_ns = Namespace("Workspace System",
                            description="Workspace System Endpoints",
                            path = "/workspaces")


contributor_model = api.model("Contributor", {
    "id": fields.Integer,
    "name": fields.String,
    "surname": fields.String,
    }
)

workspace_model = api.model('Worksapce', {
    "id": fields.Integer,
    "title": fields.String,
    "is_private": fields.Integer,
    "description": fields.String,
    "state": fields.Integer,
    "deadline": fields.String,
    "creation_time": fields.String,
    "max_contibutors": fields.Integer,
    "contributors": fields.List(
        fields.Nested(contributor_model)
    ),
})

workspace_list_model = api.model('Worksapce List', {
    'workspaces': fields.List(
        fields.Nested(workspace_model)
    )
})

workspace_small_model = api.model('Workspace', {
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
        fields.Nested(workspace_small_model)
    )
})


@workspace_system_ns.route("")
class WorkspacesAPI(Resource):
	# POST request
    @api.expect(create_workspace_parser)
    @api.doc(responses={
                201: "Workspace has been successfully created.",
                400: "Missing data fields or invalid data.",
                500: "The server is not connected to the database."
            })
    @login_required
    def post(requester_id, self):
        '''
        Creates a new workspace.
        '''

        # Parses the form data.
        form = CreateWorkspaceForm(request.form)
        if form.validate():
            new_workspace_information = {**form.data}
            new_workspace_skills = eval(new_workspace_information.pop("skills", "None"))
            new_workspace_requirements = eval(new_workspace_information.pop("requirements", "None"))

            new_workspace = Workspace(creator_id=requester_id, **new_workspace_information)
            try:
                db.session.add(new_workspace)
                db.session.commit()
            except:
                return make_response(jsonify({"error" : "The server is not connected to the database. (Workspace could not get created.)"}, 500))
            else:
                FileSystem.initialize_ws(new_workspace.id)
                try:
                    add_workspace_skills(new_workspace.id, new_workspace_skills)
                    add_workspace_requirements(new_workspace.id, new_workspace_requirements)
                    add_workspace_contribution(new_workspace.id, requester_id)
                except:
                    return make_response(jsonify({"error" : "The server is not connected to the database. (Workspace skills/requirements/contributions could not get created.)"}, 500))
                else:
                    return make_response(jsonify({"message" : "Workspace has been successfully created."}, 201))
        else:
            return make_response(jsonify({"error" : "Missing data fields or invalid data."}, 400))


    # GET request
    @api.expect(read_workspace_parser)
    @api.doc(responses={
                200: "Workspace(s) found.",
                400: "Missing data fields or invalid data.",
                401: "The user is not allowed to view this workspace.",
                404: "Requested workspace is not found.",
                500: "The server is not connected to the database."
            })
    @login_required
    def get(requester_id, self):
        '''
        Returns the requested workspace(s).
        '''

        # Parses the form data.
        form = ReadWorkspaceForm(request.args)

        # Checks whether the sent data is in valid form.
        # If yes, starts processing the data.
        # If not, an error is raised.
        if form.validate():
            # Tries to connect to the database.
            # If it fails, an error is raised.
            try:
                requested_workspace = Workspace.query.filter_by(id=form.workspace_id.data).first()
            except:
                return make_response(jsonify({"error" : "The server is not connected to the database."}), 500)
            else:
                # Checks whether the requested workspace with the given ID exists in the database.
                # If not, an error is raised.
                # If yes, workspace information is returned depending on whether the requester can see the requested workspace.
                if requested_workspace is None:
                    return make_response(jsonify({"error" : "Requested workspace is not found."}), 404)
                else:
                    # Checks whether the requester is allowed to view the requested workspace.
                    # If not, an error is raised.
                    # If yes, workspace information is returned.
                    if requested_workspace.is_private and \
                    (Contribution.query.filter_by(workspace_id=form.workspace_id.data, user_id=requester_id, is_active=True).first() is None):
                        return make_response(jsonify({"error" : "The user is not allowed to view this workspace."}), 401)
                    else:
                        workspace_information = {
                                                    "id": requested_workspace.id,
                                                    "creator_id": requested_workspace.creator_id,
                                                    "is_private": requested_workspace.is_private,
                                                    "title": requested_workspace.title,
                                                    "state": requested_workspace.state,
                                                    "timestamp": requested_workspace.timestamp,
                                                    "description": requested_workspace.description,
                                                    "deadline": requested_workspace.deadline,
                                                    "max_collaborators": requested_workspace.max_collaborators,
                                                    "skills": get_workspace_skills_text(form.workspace_id.data),
                                                    "requirements": get_workspace_requirements_text(form.workspace_id.data)
                                                }

                        # Workspace information is returned.
                        return make_response(jsonify(workspace_information), 200)
        else:
            return make_response(jsonify({"error" : "Missing data fields or invalid data."}), 400)

          
@workspace_system_ns.route("/self")
class GetSelfWorkspaces(Resource):

    @api.doc(responses={401:'Authantication Problem',500:'Database Connection Problem'})
    @api.response(200,'Valid Response',workspace_list_model)
    @api.expect(get_self_parser)
    @login_required
    def get(user_id,self):
        try:
            contributions = Contribution.query.filter(Contribution.user_id==user_id).all()
        except:
            return make_response(jsonify({"err": "Database Connection Error"}),500)
        workspaces = []
        for contribution in contributions:
            if contribution.is_active == 0:
                continue
            try:
                ws = Workspace.query.get(contribution.workspace_id)
            except:
                return make_response(jsonify({"err": "Database Connection Error"}),500)
            try:
                contributors = Contribution.query.filter(Contribution.workspace_id == ws.id).all()
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
            ws_dict = {
                "id": ws.id,
                "is_private": int(ws.is_private),
                "title": ws.title,
                "state": ws.state,
                "creation_time": ws.timestamp,
                "description": ws.description,
                "deadline": ws.deadline,
                "max_collaborators": ws.max_collaborators,
                "contributors": contributor_list
            }
            workspaces.append(ws_dict)
        return make_response(jsonify({"workspaces": workspaces}),200)

      
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
            } for workspace in all_workspaces if workspace.is_private == 0]
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
