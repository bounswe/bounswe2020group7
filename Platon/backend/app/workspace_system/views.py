from flask import make_response, jsonify, request
from flask_restplus import Resource, Namespace, fields
from flask import current_app as app
import math

from app import api, db

from app.auth_system.models import User
from app.follow_system.models import Follow, FollowRequests
from app.profile_management.models import Jobs, Skills
from app.workspace_system.models import Issue, IssueAssignee, IssueComment, Workspace, Contribution, Workspace, WorkspaceSkill, WorkspaceRequirement, Contribution, Requirement

from app.workspace_system.forms import *

from app.workspace_system.helpers import *
from app.follow_system.helpers import follow_required
from app.auth_system.helpers import login_required
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

                    

issue_model = api.model('Issue', {
	"issue_id": fields.Integer, 
	"workspace_id": fields.Integer, 
	"title": fields.String,
	"description": fields.String,
	"deadline": fields.DateTime,
	"is_open": fields.Boolean,
	"creator_id": fields.Integer,
	"creator_name": fields.String,
	"creator_surname": fields.String,
	"creator_e-mail": fields.String,
	"creator_rate": fields.Float,
	"creator_job_name": fields.String,
	"creator_institution": fields.String,
	"creator_is_private": fields.Boolean
    })

issue_list_model = api.model('Issues List', {
    'number_of_pages': fields.Integer,
    'result': fields.List(
        fields.Nested(issue_model)
    )
})

issue_assignee_model = api.model('Issue Assignee', {
    "assignee_id": fields.Integer,
	"assignee_name": fields.String,
	"assignee_surname": fields.String,
	"assignee_e-mail": fields.String,
	"assignee_rate": fields.Float,
	"assignee_job_name": fields.String,
	"assignee_institution": fields.String
})

issue_assignee_list_model = api.model('Issue Assignees List', {
    'number_of_pages': fields.Integer,
    'result': fields.List(
        fields.Nested(issue_assignee_model)
    )
})

issue_comment_model = api.model('Issue Assignee', {
    "comment_id": fields.Integer,
	"comment": fields.String,
	"owner_id": fields.Integer,
	"owner_name": fields.String,
	"owner_surname": fields.String,
	"owner_e-mail": fields.String,
	"owner_rate": fields.Float
})

issue_comment_list_model = api.model('Issue Comments List', {
    'number_of_pages': fields.Integer,
    'result': fields.List(
        fields.Nested(issue_comment_model)
    )
})

@workspace_system_ns.route("/issue")
class IssueAPI(Resource):

    # pagination will be added later.
    @api.doc(responses={401 : 'Account Problems', 400 : 'Input Format Error' ,500 : ' Database Connection Error', 404: 'Not found'})
    @api.response(200, 'Success', issue_list_model)
    @api.expect(get_issue_parser)
    @login_required
    @workspace_exists(param_loc='args',workspace_id_key='workspace_id')
    def get(user_id, self):
        '''
            Let's return Issues.
        '''
        form = GetIssuesForm(request.args)
        if form.validate():
            workspace_id = form.workspace_id.data

            # check if the workspace is public or not.
            try:
                workspaceSearch = Workspace.query.filter(Workspace.id == workspace_id).first()
            except:
                return make_response(jsonify({'error': 'Database Connection Error'}), 500)

            if workspaceSearch.is_private:
                # check if current user is an active contributor.
                try:
                    contributionSearch = Contribution.query.filter((Contribution.workspace_id == workspace_id)&(Contribution.user_id == user_id)).first()
                except:
                    return make_response(jsonify({'error': 'Database Connection Error'}), 500)

                if contributionSearch is None:
                    return make_response(jsonify({'error': 'User is not a contributor'}), 401)

                if not contributionSearch.is_active:
                    return make_response(jsonify({'error': 'User is not an active contributor currently'}), 401)
            
            try:
                issueSearch = Issue.query.filter(Issue.workspace_id == workspace_id)
            except:
                return make_response(jsonify({'error': 'Database Connection Error'}), 500)

            if issueSearch is []:
                return make_response(jsonify({'error': 'Issues not found'}), 404)

            return_list = []
            for issue in issueSearch:

                try:
                    creator_user = User.query.filter(User.id == issue.creator_id).first()
                except:
                    return make_response(jsonify({'error': 'Database Connection Error'}), 500)

                if creator_user is None:
                    return make_response(jsonify({'error': 'Creator user not found'}), 404)

                try:
                    job_name = Jobs.query.filter(Jobs.id == creator_user.job_id).first()
                except:
                    return make_response(jsonify({'error': 'Database Connection Error'}), 500)

                if job_name is None:
                    return make_response(jsonify({'error': 'Corresponding job name not found'}), 404)

                return_list.append({
                    'issue_id': issue.id,
                    'workspace_id': issue.workspace_id,
                    'title': issue.title,
                    'description': issue.description,
                    'deadline': issue.deadline,
                    'is_open': issue.is_open,
                    'creator_id': creator_user.id, 
                    'creator_name': creator_user.name, 
                    'creator_surname': creator_user.surname, 
                    'creator_e-mail': creator_user.e_mail, 
                    'creator_rate': creator_user.rate, 
                    'creator_job_name': job_name.name,
                    'creator_is_private': creator_user.is_private
                    })
            
            return make_response(jsonify({'result': return_list}),200)
            
        else:
            return make_response(jsonify({'error': 'Input Format Error'}), 400)

    @api.doc(responses={401 : 'Account Problems', 400 : 'Input Format Error' ,500 : ' Database Connection Error', 404: 'Not found'})
    @api.expect(post_issue_parser)
    @login_required
    @workspace_exists(param_loc='form',workspace_id_key='workspace_id')
    @active_contribution_required(param_loc='form',workspace_id_key='workspace_id')
    def post(user_id, self):
        '''
            Creates issue
        '''
        form = PostIssuesForm(request.form)
        if form.validate():
            
            try: 
                # Create issue record
                issue = Issue(user_id, form.workspace_id.data, form.title.data, form.description.data, form.deadline.data)
            except:
                return make_response(jsonify({'error': 'Issue record is not created'}), 500)

            try:
                db.session.add(issue)
                db.session.commit()
            except:
                return make_response(jsonify({'error': 'Database Connection Error'}), 500)

            return make_response(jsonify({'msg': 'Issue is successfully created'}), 200)

        else:
            return make_response(jsonify({'error': 'Input Format Error'}), 400)

    @api.doc(responses={401 : 'Account Problems', 400 : 'Input Format Error' ,500 : ' Database Connection Error', 404: 'Not found'})
    @api.expect(put_issue_parser)
    @login_required
    @workspace_exists(param_loc='form',workspace_id_key='workspace_id')
    @active_contribution_required(param_loc='form',workspace_id_key='workspace_id')
    def put(user_id, self):
        '''
            Updates an issue
        '''
        form = PutIssuesForm(request.form)
        if form.validate():
            
            try:
                # Gets the inputted parameters from the form data.
                new_attributes = {}
                for key, value in form.data.items():
                    if value or (value == 0):
                        if key == 'issue_id' or key == 'workspace_id':
                            continue
                        new_attributes[key] = value
            except:
                return make_response(jsonify({'error': 'Form data usage is incorrect'}), 500)

            try:
                issue = Issue.query.filter(Issue.id == form.issue_id.data).first()

                if issue is None:
                    return make_response(jsonify({'error': 'Issue not found'}), 404)

                if new_attributes:
                    # Updates the attributes of the issue in the database.
                    for key, value in new_attributes.items():
                        if key == 'title':
                            issue.title = value
                        elif key == 'description':
                            issue.description = value
                        elif key == 'deadline':
                            issue.deadline = value
                        elif key == 'is_open': 
                            issue.is_open = value
                        else:
                            return make_response(jsonify({'error': 'Attribute name {} is incorrect'.format(key)}), 500)
                    db.session.commit()
            except:
                return make_response(jsonify({'error': 'Database Connection Error'}), 500)

            return make_response(jsonify({'msg': 'Issue is successfully updated'}), 200)

        else:
            return make_response(jsonify({'error': 'Input Format Error'}), 400)

    @api.doc(responses={401 : 'Account Problems', 400 : 'Input Format Error' ,500 : ' Database Connection Error', 404: 'Not found'})
    @api.expect(delete_issue_parser)
    @login_required
    @workspace_exists(param_loc='form',workspace_id_key='workspace_id')
    @active_contribution_required(param_loc='form',workspace_id_key='workspace_id')
    def delete(user_id, self):
        '''
            Deletes an issue
        '''
        form = DeleteIssuesForm(request.form)
        if form.validate():
            
            try: 
                # Find issue record
                issue = Issue.query.filter(Issue.id == form.issue_id.data).first()

                if issue is None:
                    return make_response(jsonify({'error': 'Issue not found'}), 404)

                db.session.delete(issue)
                db.session.commit()
            except:
                return make_response(jsonify({'error': 'Database Connection Error'}), 500)

            return make_response(jsonify({'msg': 'Issue is successfully deleted'}), 200)

        else:
            return make_response(jsonify({'error': 'Input Format Error'}), 400)

@workspace_system_ns.route("/issue/assignee")
class IssueAssigneeAPI(Resource):

    # pagination will be added later.
    @api.doc(responses={401 : 'Account Problems', 400 : 'Input Format Error' ,500 : ' Database Connection Error', 404: 'Not found'})
    @api.response(200, 'Success', issue_assignee_list_model)
    @api.expect(get_issue_assignee_parser)
    @login_required
    @workspace_exists(param_loc='args',workspace_id_key='workspace_id')
    def get(user_id, self):
        '''
            Get Issue Assignees.
        '''
        form = GetIssueAssigneeForm(request.args)
        if form.validate():
            workspace_id = form.workspace_id.data

            # check if the workspace is public or not.
            try:
                workspaceSearch = Workspace.query.filter(Workspace.id == workspace_id).first()
            except:
                return make_response(jsonify({'error': 'Database Connection Error'}), 500)

            if workspaceSearch.is_private:
                # check if current user is an active contributor.
                try:
                    contributionSearch = Contribution.query.filter((Contribution.workspace_id == workspace_id)&(Contribution.user_id == user_id)).first()
                except:
                    return make_response(jsonify({'error': 'Database Connection Error'}), 500)

                if contributionSearch is None:
                    return make_response(jsonify({'error': 'User is not a contributor'}), 401)

                if not contributionSearch.is_active:
                    return make_response(jsonify({'error': 'User is not an active contributor currently'}), 401)
            
            try:
                issueAssigneeSearch = IssueAssignee.query.filter(IssueAssignee.issue_id == form.issue_id.data)
            except:
                return make_response(jsonify({'error': 'Database Connection Error'}), 500)

            # If there is no assignee in corresponding issue, I'll return an empty list with 200 code.
            # Since it is not some kind of an error, I thought 200 code is more appropriate.
            if issueAssigneeSearch is []:
                return make_response(jsonify({'result': []}), 200)

            return_list = []
            for issue_assignee in issueAssigneeSearch:

                try:
                    assignee = User.query.filter(User.id == issue_assignee.assignee_id).first()
                except:
                    return make_response(jsonify({'error': 'Database Connection Error'}), 500)

                if assignee is None:
                    return make_response(jsonify({'error': 'Assignee is None, smth wrong'}), 500)

                try:
                    job = Jobs.query.filter(Jobs.id == assignee.job_id).first()
                except:
                    return make_response(jsonify({'error': 'Database Connection Error'}), 500)

                if job is None:
                    return make_response(jsonify({'error': 'Corresponding job id not found, smth wrong'}), 500)

                return_list.append({
                    "assignee_id": assignee.id,
	                "assignee_name": assignee.name,
                    "assignee_surname": assignee.surname,
                    "assignee_e-mail": assignee.e_mail,
                    "assignee_rate": assignee.rate,
                    "assignee_job_name": job.name,
                    "assignee_institution": assignee.institution,
                    })
            
            return make_response(jsonify({'result': return_list}),200)
            
        else:
            return make_response(jsonify({'error': 'Input Format Error'}), 400)

    @api.doc(responses={401 : 'Account Problems', 400 : 'Input Format Error' ,500 : ' Database Connection Error', 404: 'Not found'})
    @api.expect(post_issue_assignee_parser)
    @login_required
    @workspace_exists(param_loc='form',workspace_id_key='workspace_id')
    @active_contribution_required(param_loc='form',workspace_id_key='workspace_id')
    def post(user_id, self):
        '''
            Create issue assignee record
        '''
        form = PostIssueAssigneeForm(request.form)
        if form.validate():

            # Check if the requested assignee is an active contributor also. 
            try:
                contribution = Contribution.query.filter((Contribution.workspace_id == form.workspace_id.data) & (Contribution.user_id == form.assignee_id.data)).first()
            except:
                return make_response(jsonify({'error' : 'Database Connection Problem'}),500)
            if contribution is None:
                return make_response(jsonify({'error': 'Assignee has to contribute the Workspace'}),404)
            if int(contribution.is_active) != 1:
                return make_response(jsonify({'error': 'Assignee has to contribute the Workspace actively'}),404)
            
            # Check if the requested assignee is already assigned to the issue.
            search = IssueAssignee.query.filter((IssueAssignee.assignee_id == form.assignee_id.data) & (IssueAssignee.issue_id == form.issue_id.data)).first()

            if search is not None:
                # this means he/she is already assigned to that issue.
                return make_response(jsonify({'error': 'Assignee is already assigned to that issue'}), 403)

            try: 
                # Create issue assignee record
                issue_assignee = IssueAssignee(form.issue_id.data, form.assignee_id.data)
            except:
                return make_response(jsonify({'error': 'DB connection error'}), 500)

            try:
                db.session.add(issue_assignee)
                db.session.commit()
            except:
                return make_response(jsonify({'error': 'Database Connection Error'}), 500)

            return make_response(jsonify({'msg': 'Issue Assignee Record is successfully created'}), 200)

        else:
            return make_response(jsonify({'error': 'Input Format Error'}), 400)


    @api.doc(responses={401 : 'Account Problems', 400 : 'Input Format Error' ,500 : ' Database Connection Error', 404: 'Not found'})
    @api.expect(delete_issue_assignee_parser)
    @login_required
    @workspace_exists(param_loc='form',workspace_id_key='workspace_id')
    @active_contribution_required(param_loc='form',workspace_id_key='workspace_id')
    def delete(user_id, self):
        '''
            Deletes an issue assignee
        '''
        form = DeleteIssueAssigneeForm(request.form)
        if form.validate():
            
            try: 
                # Find issue assignee record
                issue_assignee = IssueAssignee.query.filter((IssueAssignee.assignee_id == form.assignee_id.data) & (IssueAssignee.issue_id == form.issue_id.data)).first()

                if issue_assignee is None:
                    return make_response(jsonify({'error': 'Issue Assignee not found'}), 404)

                db.session.delete(issue_assignee)
                db.session.commit()
            except:
                return make_response(jsonify({'error': 'Database Connection Error'}), 500)

            return make_response(jsonify({'msg': 'Issue Assignee Record is successfully deleted'}), 200)

        else:
            return make_response(jsonify({'error': 'Input Format Error'}), 400)


@workspace_system_ns.route("/issue/comment")
class IssueCommentAPI(Resource):

    # pagination will be added later.
    @api.doc(responses={401 : 'Account Problems', 400 : 'Input Format Error' ,500 : ' Database Connection Error', 404: 'Not found'})
    @api.response(200, 'Success', issue_comment_list_model)
    @api.expect(get_issue_comment_parser)
    @login_required
    @workspace_exists(param_loc='args',workspace_id_key='workspace_id')
    def get(user_id, self):
        '''
            Get Issue Comments.
        '''
        form = GetIssueCommentForm(request.args)
        if form.validate():
            workspace_id = form.workspace_id.data

            # check if the workspace is public or not.
            try:
                workspaceSearch = Workspace.query.filter(Workspace.id == workspace_id).first()
            except:
                return make_response(jsonify({'error': 'Database Connection Error'}), 500)

            if workspaceSearch.is_private:
                # check if current user is an active contributor.
                try:
                    contributionSearch = Contribution.query.filter((Contribution.workspace_id == workspace_id)&(Contribution.user_id == user_id)).first()
                except:
                    return make_response(jsonify({'error': 'Database Connection Error'}), 500)

                if contributionSearch is None:
                    return make_response(jsonify({'error': 'User is not a contributor'}), 401)

                if not contributionSearch.is_active:
                    return make_response(jsonify({'error': 'User is not an active contributor currently'}), 401)
            
            try:
                issueCommentSearch = IssueComment.query.filter(IssueComment.issue_id == form.issue_id.data)
            except:
                return make_response(jsonify({'error': 'Database Connection Error'}), 500)


            return_list = []
            for issue_comment in issueCommentSearch:

                # If there is no comment in corresponding issue, I'll return an empty list with 206 code.
                # Since it is not some kind of an error, I thought 206 code is more appropriate.
                if issue_comment is None:
                    return make_response(jsonify({'result': []}), 206)

                try:
                    commenter = User.query.filter(User.id == issue_comment.commenter_id).first()
                except:
                    return make_response(jsonify({'error': 'Database Connection Error'}), 500)

                if commenter is None:
                    return make_response(jsonify({'error': 'Commenter is None, smth wrong'}), 500)

                try:
                    job = Jobs.query.filter(Jobs.id == commenter.job_id).first()
                except:
                    return make_response(jsonify({'error': 'Database Connection Error'}), 500)

                if job is None:
                    return make_response(jsonify({'error': 'Corresponding job id not found, smth wrong'}), 500)

                return_list.append({
                    "comment_id": issue_comment.id,
                    "comment": issue_comment.comment,
                    "owner_id": issue_comment.commenter_id,
                    "owner_name": commenter.name,
                    "owner_surname": commenter.surname,
                    "owner_e-mail": commenter.e_mail,
                    "owner_rate": commenter.rate,
                    })
            
            # If there is no comment in corresponding issue, I'll return an empty list with 200 code.
            # Since it is not some kind of an error, I thought 200 code is more appropriate.
            return make_response(jsonify({'result': return_list}),200)
            
        else:
            return make_response(jsonify({'error': 'Input Format Error'}), 400)


    @api.doc(responses={401 : 'Account Problems', 400 : 'Input Format Error' ,500 : ' Database Connection Error', 404: 'Not found'})
    @api.expect(post_issue_comment_parser)
    @login_required
    @workspace_exists(param_loc='form',workspace_id_key='workspace_id')
    @active_contribution_required(param_loc='form',workspace_id_key='workspace_id')
    def post(user_id, self):
        '''
            Create issue comment
        '''
        form = PostIssueCommentForm(request.form)
        if form.validate():
            
            try: 
                # Create issue comment record
                issue_comment = IssueComment(form.issue_id.data, user_id, form.comment.data)
            except:
                return make_response(jsonify({'error': 'DB connection error'}), 500)

            try:
                db.session.add(issue_comment)
                db.session.commit()
            except:
                return make_response(jsonify({'error': 'Database Connection Error'}), 500)

            return make_response(jsonify({'msg': 'Issue Comment is successfully created'}), 200)

        else:
            return make_response(jsonify({'error': 'Input Format Error'}), 400)

    @api.doc(responses={401 : 'Account Problems', 400 : 'Input Format Error' ,500 : ' Database Connection Error', 404: 'Not found'})
    @api.expect(delete_issue_comment_parser)
    @login_required
    @workspace_exists(param_loc='form',workspace_id_key='workspace_id')
    @active_contribution_required(param_loc='form',workspace_id_key='workspace_id')
    def delete(user_id, self):
        '''
            Deletes an issue comment
        '''
        form = DeleteIssueCommentForm(request.form)
        if form.validate():
            
            try: 
                # Find issue comment record
                issue_comment = IssueComment.query.filter((IssueComment.id == form.comment_id.data)).first()

                # Check if comment is his/her comment.
                if issue_comment.commenter_id != user_id:
                    return make_response(jsonify({'error': 'Unauthorized'}), 401)

                if issue_comment is None:
                    return make_response(jsonify({'error': 'Issue Comment not found'}), 404)

                db.session.delete(issue_comment)
                db.session.commit()
            except:
                return make_response(jsonify({'error': 'Database Connection Error'}), 500)

            return make_response(jsonify({'msg': 'Issue Comment is successfully deleted'}), 200)

        else:
            return make_response(jsonify({'error': 'Input Format Error'}), 400)


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
                    return make_response(jsonify({"error" : "The server is not connected to the database. (Workspace skills/requirements/contributions could not get created.)"}), 500)
                else:
                    return make_response(jsonify({"message" : "Workspace has been successfully created."}), 201)
        else:
            return make_response(jsonify({"error" : "Missing data fields or invalid data."}), 400)


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
