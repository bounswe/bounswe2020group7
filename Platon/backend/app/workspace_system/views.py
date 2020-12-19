from flask import make_response, jsonify, request
from flask_restplus import Resource, Namespace, fields
from flask import current_app as app
import math

from app import api, db
from app.auth_system.models import User
from app.follow_system.models import Follow, FollowRequests
from app.auth_system.views import login_required
from app.follow_system.helpers import follow_required
from app.workspace_system.models import Issue, IssueAssignee, IssueComment, Workspace, Contribution
from app.profile_management.models import Jobs
from app.workspace_system.forms import GetIssuesForm, GetIssueCommentForm, GetIssueAssigneeForm, PostIssueAssigneeForm, PostIssueCommentForm, PostIssuesForm, PutIssuesForm, DeleteIssueAssigneeForm, DeleteIssueCommentForm, DeleteIssuesForm
from app.workspace_system.helpers import workspace_exists,active_contribution_required
from app.workspace_system.forms import get_issue_parser, get_issue_comment_parser, get_issue_assignee_parser, post_issue_assignee_parser, post_issue_comment_parser, post_issue_parser, put_issue_parser, delete_issue_assignee_parser, delete_issue_comment_parser, delete_issue_parser

workspace_system_ns = Namespace("Workspace System",
                             description="Workspace System Endpoints",
                             path="/workspace")
                    

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

def register_resources(api):
    api.add_namespace(workspace_system_ns)