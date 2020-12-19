from flask import current_app as app
from flask_restplus import Namespace, Resource
from flask import make_response,jsonify,request

from app import api, db

from app.workspace_system.forms import CreateWorkspaceForm, create_workspace_parser
from app.workspace_system.forms import ReadWorkspaceForm, read_workspace_parser
from app.workspace_system.forms import UpdateWorkspaceForm, update_workspace_parser
from app.workspace_system.forms import DeleteWorkspaceForm, delete_workspace_parser

from app.workspace_system.models import Workspace, WorkspaceSkill, WorkspaceRequirement, Contribution, Requirement
from app.profile_management.models import Skills

from app.auth_system.helpers import login_required
from app.workspace_system.helpers import *

workspace_system_ns = Namespace("Workspace System",
                            description="Workspace System Endpoints",
                            path = "/workspaces")


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
                    (Contribution.query.filter_by(workspace_id=form.workspace_id.data, contributor_id=requester_id, is_active=True).first() is None):
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


    # PUT request
    @api.expect(update_workspace_parser)
    @api.doc(responses={
                200: "Workspace information has been successfully updated.",
                202: "Server has received the request but there was no information to be updated.",
                400: "Missing data fields or invalid data.",
                404: "The workspace is not found.",
                500: "The server is not connected to the database."
            })
    def put(self):
        '''
        Updates the requested workspace.
        '''

        # Parses the form data.
        form = UpdateWorkspaceForm(request.form)

        if form.validate():
            return make_response(jsonify({"message" : form.data}), 200)
        else:
            return make_response(jsonify({"error" : "Missing data fields or invalid data."}, 400))


    # DELETE request
    @api.expect(delete_workspace_parser)
    @api.doc(responses={
                200: "Workspace has been successfully deleted.",
                400: "Missing data fields or invalid data.",
                404: "The workspace is not found.",
                500: "The server is not connected to the database."
            })
    def delete(self):
        '''
        Deletes the requested workspace.
        '''

        # Parses the form data.
        form = DeleteWorkspaceForm(request.args)

        if form.validate():
            return make_response(jsonify({"message" : form.data}), 200)
        else:
            return make_response(jsonify({"error" : "Missing data fields or invalid data."}, 400))


def register_resources(api):
    api.add_namespace(workspace_system_ns)