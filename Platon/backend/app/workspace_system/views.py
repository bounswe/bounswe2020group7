from flask import make_response, jsonify, request
from flask_restplus import Resource, Namespace, fields
from flask import current_app as app
import math

from app import api, db

from app.auth_system.models import User
from app.follow_system.models import Follow, FollowRequests
from app.profile_management.models import Jobs, Skills
from app.workspace_system.models import *

from app.workspace_system.forms import *

from app.workspace_system.helpers import *
from app.follow_system.helpers import follow_required
from app.auth_system.helpers import login_required, profile_photo_link
from app.file_system.helpers import FileSystem
from app.activity_stream.models import ActivityStreamItem

workspace_system_ns = Namespace("Workspace System",
                            description="Workspace System Endpoints",
                            path = "/workspaces")


contributor_model = api.model("Contributor", {
    "id": fields.Integer,
    "name": fields.String,
    "surname": fields.String,
    }
)

workspace_model = api.model('Workspace', {
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
	"creator_e_mail": fields.String,
	"creator_rate": fields.Float,
	"creator_job_name": fields.String,
	"creator_institution": fields.String,
	"creator_is_private": fields.Boolean,
    "creator_photo": fields.String
    })

issue_list_model = api.model('Issues List', {
    'number_of_pages': fields.Integer,
    'result': fields.List(
        fields.Nested(issue_model)
    )
})

issue_post_model = api.model('Issue Post', {
    'msg': fields.String,
	"issue_id": fields.Integer, 
	"workspace_id": fields.Integer, 
	"title": fields.String,
	"description": fields.String,
	"deadline": fields.DateTime,
	"is_open": fields.Boolean,
	"creator_id": fields.Integer,
    })

milestone_model = api.model('Milestone', {
    "milestone_id": fields.Integer, 
    "workspace_id": fields.Integer,
    "title": fields.String,
    "description": fields.String,
    "deadline": fields.DateTime,  
    "creator_id": fields.Integer,
	"creator_name": fields.String,
	"creator_surname": fields.String,
	"creator_e_mail": fields.String,
	"creator_rate": fields.Float,
	"creator_job_name": fields.String,
	"creator_institution": fields.String,
	"creator_is_private": fields.Boolean
    })

milestone_list_model = api.model('Milestones List', {
    'number_of_pages': fields.Integer,
    'result': fields.List(
        fields.Nested(milestone_model)
    )
})

issue_assignee_model = api.model('Issue Assignee', {
    "assignee_id": fields.Integer,
	"assignee_name": fields.String,
	"assignee_surname": fields.String,
	"assignee_e_mail": fields.String,
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
	"owner_e_mail": fields.String,
	"owner_rate": fields.Float,
    "owner_photo": fields.String
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
            Get Issues.
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
                issueSearch = Issue.query.filter(Issue.workspace_id == workspace_id).all()
            except:
                return make_response(jsonify({'error': 'Database Connection Error'}), 500)

            # If there is no issue in corresponding issue, I'll return an empty list with 200 code.
            # Since it is not some kind of an error, I thought 200 code is more appropriate.
            if len(issueSearch) == 0:
                return make_response(jsonify({'result': []}), 200)

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
                    'creator_e_mail': creator_user.e_mail, 
                    'creator_rate': creator_user.rate, 
                    'creator_job_name': job_name.name,
                    'creator_is_private': creator_user.is_private,
                    'creator_photo': profile_photo_link(creator_user.profile_photo,creator_user.id)
                    })
            
            # Pagination functionality
            number_of_pages = 1
            if form.page.data is not None and form.per_page.data is not None:
                per_page = form.per_page.data
                number_of_pages = math.ceil(len(return_list) / per_page)
                # Assign the page index to the maximum if it exceeds the max index
                page = form.page.data if form.page.data < number_of_pages else number_of_pages-1
                return_list = return_list[page*per_page:(page+1)*per_page]
            return make_response(jsonify({'number_of_pages': number_of_pages ,'result': return_list}),200)
            
            
        else:
            return make_response(jsonify({'error': 'Input Format Error'}), 400)

    @api.doc(responses={401 : 'Account Problems', 400 : 'Input Format Error' ,500 : ' Database Connection Error', 404: 'Not found'})
    @api.response(200, 'Success', issue_post_model)
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

            return make_response(jsonify({
                'msg': 'Issue is successfully created',
                "issue_id": issue.id, 
	            "workspace_id": issue.workspace_id, 
	            "title": issue.title,
	            "description": issue.description,
	            "deadline": issue.deadline,
	            "is_open": issue.is_open,
	            "creator_id": issue.creator_id
                }), 200)

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
                issueAssigneeSearch = IssueAssignee.query.filter(IssueAssignee.issue_id == form.issue_id.data).all()
            except:
                return make_response(jsonify({'error': 'Database Connection Error'}), 500)

            # If there is no assignee in corresponding issue, I'll return an empty list with 200 code.
            # Since it is not some kind of an error, I thought 200 code is more appropriate.
            if len(issueAssigneeSearch) == 0:
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
                    "assignee_e_mail": assignee.e_mail,
                    "assignee_rate": assignee.rate,
                    "assignee_job_name": job.name,
                    "assignee_institution": assignee.institution,
                    })
            
            # Pagination functionality
            number_of_pages = 1
            if form.page.data is not None and form.per_page.data is not None:
                per_page = form.per_page.data
                number_of_pages = math.ceil(len(return_list) / per_page)
                # Assign the page index to the maximum if it exceeds the max index
                page = form.page.data if form.page.data < number_of_pages else number_of_pages-1
                return_list = return_list[page*per_page:(page+1)*per_page]
            return make_response(jsonify({'number_of_pages': number_of_pages ,'result': return_list}),200)
            
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
                issueCommentSearch = IssueComment.query.filter(IssueComment.issue_id == form.issue_id.data).all()
            except:
                return make_response(jsonify({'error': 'Database Connection Error'}), 500)

            # If there is no comment in corresponding issue, I'll return an empty list with 200 code.
            # Since it is not some kind of an error, I thought 200 code is more appropriate.
            if len(issueCommentSearch)==0:
                return make_response(jsonify({'result': []}), 200)

            return_list = []
            for issue_comment in issueCommentSearch:

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
                    "owner_e_mail": commenter.e_mail,
                    "owner_rate": commenter.rate,
                    "owner_photo": profile_photo_link(commenter.profile_photo,commenter.id)
                    })
            
            # Pagination functionality
            number_of_pages = 1
            if form.page.data is not None and form.per_page.data is not None:
                per_page = form.per_page.data
                number_of_pages = math.ceil(len(return_list) / per_page)
                # Assign the page index to the maximum if it exceeds the max index
                page = form.page.data if form.page.data < number_of_pages else number_of_pages-1
                return_list = return_list[page*per_page:(page+1)*per_page]
            return make_response(jsonify({'number_of_pages': number_of_pages ,'result': return_list}),200)
            
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

@workspace_system_ns.route("/milestone")
class MilestoneAPI(Resource):
    
    @api.doc(responses={401 : 'Account Problems', 400 : 'Input Format Error' ,500 : ' Database Connection Error', 404: 'Not found'})
    @api.response(200, 'Success', milestone_list_model)
    @api.expect(get_milestone_parser)
    @login_required
    @workspace_exists(param_loc='args',workspace_id_key='workspace_id')
    def get(user_id, self):
        '''
            Get Milestones.
        '''
        form = GetMilestoneForm(request.args)
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
                    contributionSearch = Contribution.query.filter((Contribution.workspace_id == workspace_id)&(Contribution.user_id == user_id)&(Contribution.is_active == True)).first()
                except:
                    return make_response(jsonify({'error': 'Database Connection Error'}), 500)

                if contributionSearch is None:
                    return make_response(jsonify({'error': 'User is not a contributor'}), 401)
            
            try:
                milestoneSearch = Milestone.query.filter(Milestone.workspace_id == workspace_id).all()
            except:
                return make_response(jsonify({'error': 'Database Connection Error'}), 500)

            # If there is no milestone in corresponding workspace, I'll return an empty list with 200 code.
            # Since it is not some kind of an error, I thought 200 code is more appropriate.
            if len(milestoneSearch) == 0:
                return make_response(jsonify({'result': []}), 200)

            return_list = []
            for milestone in milestoneSearch:

                try:
                    creator_user = User.query.filter(User.id == milestone.creator_id).first()
                except:
                    return make_response(jsonify({'error': 'Database Connection Error'}), 500)

                if creator_user is None:
                    # I decided to return None to the creator user infos.
                    # This may cause some problems. However it is rare situation.
                    return_list.append({
                    'milestone_id': milestone.id,
                    'workspace_id': milestone.workspace_id,
                    'title': milestone.title,
                    'description': milestone.description,
                    'deadline': milestone.deadline,
                    'creator_id': None, 
                    'creator_name': None, 
                    'creator_surname': None, 
                    'creator_e_mail': None, 
                    'creator_rate': None, 
                    'creator_job_name': None,
                    'creator_institution': None,
                    'creator_is_private': None
                    })
                    continue
                    

                try:
                    job_name = Jobs.query.filter(Jobs.id == creator_user.job_id).first()
                except:
                    return make_response(jsonify({'error': 'Database Connection Error'}), 500)

                if job_name is None:
                    # I decided to return None for job_name.
                    # This may cause some problems. However it is rare situation.
                    return_list.append({
                    'milestone_id': milestone.id,
                    'workspace_id': milestone.workspace_id,
                    'title': milestone.title,
                    'description': milestone.description,
                    'deadline': milestone.deadline,
                    'creator_id': creator_user.id, 
                    'creator_name': creator_user.name, 
                    'creator_surname': creator_user.surname, 
                    'creator_e_mail': creator_user.e_mail, 
                    'creator_rate': creator_user.rate, 
                    'creator_job_name': None,
                    'creator_institution': creator_user.institution,
                    'creator_is_private': creator_user.is_private
                    })
                    continue

                return_list.append({
                    'milestone_id': milestone.id,
                    'workspace_id': milestone.workspace_id,
                    'title': milestone.title,
                    'description': milestone.description,
                    'deadline': milestone.deadline,
                    'creator_id': creator_user.id, 
                    'creator_name': creator_user.name, 
                    'creator_surname': creator_user.surname, 
                    'creator_e_mail': creator_user.e_mail, 
                    'creator_rate': creator_user.rate, 
                    'creator_job_name': job_name.name,
                    'creator_institution': creator_user.institution,
                    'creator_is_private': creator_user.is_private
                    })
            
            # Pagination functionality
            number_of_pages = 1
            if form.page.data is not None and form.per_page.data is not None:
                per_page = form.per_page.data
                number_of_pages = math.ceil(len(return_list) / per_page)
                # Assign the page index to the maximum if it exceeds the max index
                page = form.page.data if form.page.data < number_of_pages else number_of_pages-1
                return_list = return_list[page*per_page:(page+1)*per_page]
            return make_response(jsonify({'number_of_pages': number_of_pages ,'result': return_list}),200)
            
            
        else:
            return make_response(jsonify({'error': 'Input Format Error'}), 400)

    @api.doc(responses={401 : 'Account Problems', 400 : 'Input Format Error' ,500 : ' Database Connection Error', 404: 'Not found'})
    @api.expect(post_milestone_parser)
    @login_required
    @workspace_exists(param_loc='form',workspace_id_key='workspace_id')
    @active_contribution_required(param_loc='form',workspace_id_key='workspace_id')
    def post(user_id, self):
        '''
            Creates milestone
        '''
        form = PostMilestoneForm(request.form)
        if form.validate():
            
            try: 
                # Create milestone record
                milestone = Milestone(user_id, form.workspace_id.data, form.title.data, form.description.data, form.deadline.data)
            except:
                return make_response(jsonify({'error': 'milestone record is not created'}), 500)

            try:
                db.session.add(milestone)
                db.session.commit()
            except:
                return make_response(jsonify({'error': 'Database Connection Error'}), 500)

            return make_response(jsonify({'msg': 'milestone is successfully created'}), 200)

        else:
            return make_response(jsonify({'error': 'Input Format Error'}), 400)

    @api.doc(responses={401 : 'Account Problems', 400 : 'Input Format Error' ,500 : ' Database Connection Error', 404: 'Not found'})
    @api.expect(put_milestone_parser)
    @login_required
    @workspace_exists(param_loc='form',workspace_id_key='workspace_id')
    @active_contribution_required(param_loc='form',workspace_id_key='workspace_id')
    def put(user_id, self):
        '''
            Updates a milestone
        '''
        form = PutMilestoneForm(request.form)
        if form.validate():
            
            try:
                # Gets the parameters from the form data.
                new_attributes = {}
                for key, value in form.data.items():
                    if value or (value == 0):
                        # these attributes shouldn't be changed
                        if key == 'milestone_id' or key == 'workspace_id':
                            continue
                        new_attributes[key] = value
            except:
                return make_response(jsonify({'error': 'Form data usage is incorrect'}), 500)

            try:
                milestone = Milestone.query.filter((Milestone.id == form.milestone_id.data)&(Milestone.workspace_id == form.workspace_id.data)).first()

                if milestone is None:
                    return make_response(jsonify({'error': 'milestone not found'}), 404)

                if new_attributes:
                    # Updates the attributes of the milestone in the database.
                    for key, value in new_attributes.items():
                        if key == 'title':
                            milestone.title = value
                        elif key == 'description':
                            milestone.description = value
                        elif key == 'deadline':
                            milestone.deadline = value
                    db.session.commit()
            except:
                return make_response(jsonify({'error': 'Database Connection Error'}), 500)

            return make_response(jsonify({'msg': 'milestone is successfully updated'}), 200)

        else:
            return make_response(jsonify({'error': 'Input Format Error'}), 400)

    @api.doc(responses={401 : 'Account Problems', 400 : 'Input Format Error' ,500 : ' Database Connection Error', 404: 'Not found'})
    @api.expect(delete_milestone_parser)
    @login_required
    @workspace_exists(param_loc='form',workspace_id_key='workspace_id')
    @active_contribution_required(param_loc='form',workspace_id_key='workspace_id')
    def delete(user_id, self):
        '''
            Deletes a milestone
        '''
        form = DeleteMilestoneForm(request.form)
        if form.validate():
            
            try: 
                # Find milestone record
                milestone = Milestone.query.filter(Milestone.id == form.milestone_id.data).first()

                if milestone is None:
                    return make_response(jsonify({'error': 'milestone not found'}), 404)

                db.session.delete(milestone)
                db.session.commit()
            except:
                return make_response(jsonify({'error': 'Database Connection Error'}), 500)

            return make_response(jsonify({'msg': 'milestone is successfully deleted'}), 200)

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
        # Checks whether the data is in valid form.
        # If yes, starts processing the data.
        # If not, an error is raised.
        if form.validate():
            # Form data gets converted to a dictionary, to create a workspace.
            new_workspace_information = {**form.data}
            # The list of skills is popped from the dictionary, as the skills will be added to the database through a separate operation.
            new_workspace_skills = json.loads(new_workspace_information.pop("skills"))
            # The list of requirements is popped from the dictionary, as the requirements will be added to the database through a separate operation.
            new_workspace_requirements = json.loads(new_workspace_information.pop("requirements"))
            # The list of upcoming events is popped from the dictionary, as the upcoming events will be added to the database through a separate operation.
            new_workspace_upcoming_events = json.loads(new_workspace_information.pop("upcoming_events"))

            # A new workspace with the given fields gets created.
            new_workspace = Workspace(creator_id=requester_id, **new_workspace_information)
            # Attempts to add the newly created workspace to the database.
            # If fails, an error gets raised.
            # If it does not fail, the skills and the requirements of the new workspace and the contribution of the creator user gets added to the database.
            # Also, the folder for the workspace gets created.
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
                    add_workspace_upcoming_events(new_workspace.id, new_workspace_upcoming_events)
                    add_workspace_contribution(new_workspace.id, requester_id)
                except:
                    return make_response(jsonify({"error" : "The server is not connected to the database. (Workspace skills/requirements/contributions could not get created.)"}), 500)
                else:
                    
                    #Add this activity into Activity Stream if it is public
                    if not new_workspace.is_private:
                        try:
                            current_user = User.query.filter(User.id == requester_id).first()
                        except:
                            return make_response(jsonify({"error" : "The server is not connected to the database."}), 500)

                        # Activity is added into Activity Stream
                        activity_stream_entry = ActivityStreamItem(
                            activity_context_vocab = "https://www.w3.org/ns/activitystreams",
                            activity_summary = "{} {} created a workspace named {}".format(current_user.name, current_user.surname, new_workspace.title),
                            activity_type = "Create",
                            activity_actor_type = "Person",
                            activity_actor_id = current_user.id,
                            activity_actor_name = (current_user.name + " " + current_user.surname),
                            activity_actor_image_url = profile_photo_link(current_user.profile_photo,current_user.id),
                            activity_object_type = "Group",
                            activity_object_name = new_workspace.title,
                            activity_object_id = new_workspace.id,
                        )
                        try:
                            db.session.add(activity_stream_entry)
                            db.session.commit()
                        except:
                            return make_response(jsonify({'error': 'Database Connection Error'}), 500)

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
                                                    "deadline": requested_workspace.deadline.strftime("%Y.%m.%d") if requested_workspace.deadline is not None else None,
                                                    "max_collaborators": requested_workspace.max_collaborators,
                                                    "skills": get_workspace_skills_list(form.workspace_id.data),
                                                    "requirements": get_workspace_requirements_list(form.workspace_id.data),
                                                    "upcoming_events": get_workspace_upcoming_events_list(form.workspace_id.data),
                                                    "active_contributors": get_workspace_active_contributors_list(form.workspace_id.data)
                                                }
                        requested_workspace.view_count = requested_workspace.view_count + 1
                        db.session.commit()
                        # Workspace information is returned.
                        return make_response(jsonify(workspace_information), 200)
        else:
            return make_response(jsonify({"error" : "Missing data fields or invalid data."}), 400)

    # PUT request
    @api.expect(update_workspace_parser)
    @api.doc(responses={
                200: "Workspace has been successfully updated.",
                202: "Server has received the request but there was no information to be updated.",
                400: "Missing data fields or invalid data.",
                401: "You are not among the active contributors of this workspace, you cannot modify it.",
                404: "The workspace is not found.",
                500: "The server is not connected to the database."
            })
    @login_required
    def put(requester_id, self):
        '''
        Updates the requested workspace.
        '''

        # Parses the form data.
        form = UpdateWorkspaceForm(request.form)

        # Checks whether the sent data is in valid form.
        # If yes, starts processing the data.
        # If not, an error is raised.
        if form.validate():
            # Tries to connect to the database.
            # If it fails, an error is raised.
            try:
                requested_workspace = Workspace.query.filter_by(id=form.workspace_id.data)
            except:
                return make_response(jsonify({"error" : "The server is not connected to the database."}), 500)
            else:
                # Checks whether the requested workspace with the given ID exists in the database.
                # If not, an error is raised.
                # If yes, workspace information is updated depending on whether the requester can update the requested workspace.
                if requested_workspace.first() is None:
                    return make_response(jsonify({"error" : "Requested workspace is not found."}), 404)
                else:
                    if Contribution.query.filter_by(workspace_id=form.workspace_id.data, user_id=requester_id, is_active=True).first() is None:
                        return make_response(jsonify({"error" : "You are not among the active contributors of this workspace, you cannot modify it."}), 401)
                    else:
                        # Empty parameters get filtered out.
                        new_attributes = {}
                        for key, value in form.data.items():
                            if value or (value == 0):
                                new_attributes[key] = value
                        # As the ID of the workspace will not be updated, it gets deleted from the dictionary of the fields.
                        new_attributes.pop("workspace_id")

                        # Checks whether the dictionary of the new attributes is empty or not.
                        # If empty, skips the database operations.
                        # If not, the workspace gets updated as requested.
                        is_updated = False
                        if new_attributes["skills"] != 'null' or new_attributes["requirements"] != 'null' or new_attributes["upcoming_events"] != 'null':
                            is_updated = True
                            update_workspace_skills(form.workspace_id.data, json.loads(new_attributes.pop("skills")))
                            update_workspace_requirements(form.workspace_id.data, json.loads(new_attributes.pop("requirements")))
                            update_workspace_upcoming_events(form.workspace_id.data, json.loads(new_attributes.pop("upcoming_events")))
                        else:
                            new_attributes.pop("skills")
                            new_attributes.pop("requirements")
                            new_attributes.pop("upcoming_events")
                        if new_attributes:
                            requested_workspace.update(new_attributes)
                            db.session.commit()
                            return make_response(jsonify({"message" : "Workspace has been successfully updated."}), 200)
                        elif is_updated:
                            return make_response(jsonify({"message" : "Workspace has been successfully updated."}), 200)
                        else:
                            return make_response(jsonify({"message" : "Server has received the request but there was no information to be updated."}), 202)
        else:
            return make_response(jsonify({"error" : "Missing data fields or invalid data."}), 400)


    # DELETE request
    @api.expect(delete_workspace_parser)
    @api.doc(responses={
                200: "Workspace has been successfully deleted.",
                400: "Missing data fields or invalid data.",
                401: "You are not the creator of this workspace, you cannot delete it.",
                404: "The workspace is not found.",
                500: "The server is not connected to the database."
            })
    @login_required
    def delete(requester_id, self):
        '''
        Deletes the requested workspace.
        '''

        # Parses the form data.
        form = DeleteWorkspaceForm(request.form)

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
                # Checks whether there is an existing workspace with the given ID in the database.
                # If yes, processing continues.
                # If not, an error gets raised.
                if requested_workspace is not None:
                    # Checks whether the requester user is the creator of the requested workspace.
                    # If yes, the workspace gets deleted.
                    # If not, an error gets raised.
                    worksapce_id = requested_workspace.id
                    if requested_workspace.creator_id == requester_id:
                        try:
                            db.session.delete(requested_workspace)
                            db.session.commit()
                            FileSystem.delete_ws_files(worksapce_id)
                        except:
                            return make_response(jsonify({"error" : "The server is not connected to the database."}), 500)
                        else:
                            return make_response(jsonify({"message" : "Workspace has been successfully deleted."}), 200)
                    else:
                        return make_response(jsonify({"error" : "You are not the creator of this workspace, you cannot delete it."}), 401)
                else:
                    return make_response(jsonify({"error" : "The workspace is not found."}), 404)
        else:
            return make_response(jsonify({"error" : "Missing data fields or invalid data."}), 400)

@workspace_system_ns.route("/user")
class GetUserWorkspaces(Resource):
    
    @api.doc(responses={401:'Authantication Problem',500:'Database Connection Problem',400:'Invalid Input',403: "Private Profile"})
    @api.response(200,'Valid Response',workspace_list_model)
    @api.expect(get_users_workspaces)
    @login_required
    @follow_required(param_loc = 'args', requested_user_id_key='user_id')
    def get(user_id,self):
        """
            Only returns public workspaces of the user with given ID
        """
        form = GetUserWorkspacesForm(request.args)
        if form.validate():
            try:
                contributions = Contribution.query.filter(Contribution.user_id==form.user_id.data).all()
            except:
                return make_response(jsonify({"error": "Database Connection Error"}),500)
            workspaces = []
            for contribution in contributions:
                if contribution.is_active == 0:
                    continue
                try:
                    ws = Workspace.query.filter((Workspace.id == contribution.workspace_id)&(Workspace.is_private == False)).first()
                except:
                    return make_response(jsonify({"error": "Database Connection Error"}),500)
                if ws is None:
                    continue
                try:
                    contributors = Contribution.query.filter(Contribution.workspace_id == ws.id).all()
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
        else:
            return make_response(jsonify({"error" : "Missing data fields or invalid data."}), 400)

          
@workspace_system_ns.route("/self")
class GetSelfWorkspaces(Resource):

    @api.doc(responses={401:'Authantication Problem',500:'Database Connection Problem'})
    @api.response(200,'Valid Response',workspace_list_model)
    @api.expect(get_self_parser)
    @login_required
    def get(user_id,self):
        """
            Returns all workspaces of the user(Public and Private)
        """
        try:
            contributions = Contribution.query.filter(Contribution.user_id==user_id).all()
        except:
            return make_response(jsonify({"error": "Database Connection Error"}),500)
        workspaces = []
        for contribution in contributions:
            if contribution.is_active == 0:
                continue
            try:
                ws = Workspace.query.get(contribution.workspace_id)
            except:
                return make_response(jsonify({"error": "Database Connection Error"}),500)
            try:
                contributors = Contribution.query.filter(Contribution.workspace_id == ws.id).all()
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
            ws_dict = {
                "id": ws.id,
                "is_private": int(ws.is_private),
                "title": ws.title,
                "state": ws.state,
                "creation_time": ws.timestamp,
                "description": ws.description,
                "deadline": ws.deadline,
                "max_collaborators": ws.max_collaborators,
                "contributors": contributor_list,
                "creator_id": ws.creator_id
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
                return make_response(jsonify({"error": "Database Connection Error"}),500)
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
                workspace_response[index]["contributor_list"] = contributor_list
            return make_response(jsonify({"trending_projects": workspace_response}),200)
        else:
            return make_response(jsonify({"error": "Invalid Input Format"}),400)


@workspace_system_ns.route("/invitations")
class CollaborationInvitationsAPI(Resource):
    # POST request
    @api.expect(create_collaboration_invitation_parser)
    @api.doc(responses={
                201: "Invitation has been successfully created.",
                400: "Missing data fields or invalid data.",
                401: "You are not the creator of this workspace, you cannot invite users to this workspace.",
                404: "The workspace or the invitee user does not exist.",
                409: "Invitee user is already an active contributor of this workspace OR \
                there is already an invitation sent to the invitee user for this workspace. OR \
                the workspace has already reached its maximum number of collaborators OR \
                this workspace is not in 'search for collaborators' state.",
                500: "The server is not connected to the database."
            })
    @login_required
    @active_contribution_required(param_loc='form',workspace_id_key='workspace_id')
    def post(invitor_id, self):
        '''
        Creates an invitation for a workspace. (i.e. an invitation for a workspace gets sent to the invitee user)
        '''
        
        # Parses the form data.
        form = CreateCollaborationInvitationForm(request.form)

        # Checks whether the sent data is in valid form.
        # If yes, starts processing the data.
        # If not, an error is raised.
        if form.validate():
            # Tries to connect to the database.
            # If it fails, an error is raised.
            try:
                requested_workspace = Workspace.query.filter_by(id=form.workspace_id.data).first()
                invitee_user = User.query.filter_by(id=form.invitee_id.data).first()
            except:
                return make_response(jsonify({"error" : "The server is not connected to the database."}), 500)
            else:
                # Checks whether there is an existing workspace and a user with the given IDs in the database.
                # If yes, processing continues.
                # If not, an error gets raised.
                if (requested_workspace is not None) and (invitee_user is not None):
                    # Checks whether the user who tries to sent this invitation is the creator of this workspace or not.
                    # If yes the processing continues.
                    # (or when the user who created this workspace has deleted their account. In that case, any of the active contributors of a workspace can invite users to that workspace.)
                    # If not, an error gets raised.
                    if (requested_workspace.creator_id == invitor_id) or (requested_workspace.creator_id is None):
                        # Checks whether the invitee user is an active contributor of the requested workspace or
                        # whether there is an already sent invitation to the invitee user for the requested workspace or
                        # whether the workspace has already reached its maximum number of collaborators or
                        # whether the workspace is not in "search for collaborators" state. 
                        # If not, the invitation gets created.
                        # If yes, an error gets raised.
                        active_contribution = Contribution.query.filter_by(workspace_id=requested_workspace.id, user_id=invitee_user.id, is_active=True).first()
                        invitation = CollaborationInvitation.query.filter_by(workspace_id=requested_workspace.id, invitee_id=invitee_user.id).first()
                        contributions = Contribution.query.filter_by(workspace_id=requested_workspace.id, is_active=True).all()
                        if active_contribution:
                            return make_response(jsonify({"error" : "Invitee user is already an active contributor of this workspace."}), 409)
                        elif invitation:
                            return make_response(jsonify({"error" : "There is already an invitation sent to this workspace."}), 409)
                        elif not (len(contributions) < requested_workspace.max_collaborators):
                            return make_response(jsonify({"error" : "The workspace has already reached its maximum number of collaborators."}), 409)
                        elif requested_workspace.state != 0:
                            return make_response(jsonify({"error" : "This workspace is not in 'search for collaborators' state."}), 409)
                        else:
                            new_invitation = CollaborationInvitation(workspace_id=requested_workspace.id, invitee_id=invitee_user.id, invitor_id=invitor_id)
                            try:
                                db.session.add(new_invitation)
                                db.session.commit()
                            except:
                                return make_response(jsonify({"error" : "The server is not connected to the database."}), 500)
                            else:
                                return make_response(jsonify({"message" : "Invitation has been successfully created."}), 201)
                    else:
                        return make_response(jsonify({"error" : "You are not the creator of this workspace, you cannot invite users to this workspace."}), 401)
                else:
                    return make_response(jsonify({"error" : "The workspace or the invitee user does not exist."}), 404)
        else:
            return make_response(jsonify({"error" : "Missing data fields or invalid data."}), 400)


    # GET request
    @api.expect(read_collaboration_invitation_parser)
    @api.doc(responses={
                200: "Invitation(s) found.",
                400: "Missing data fields or invalid data.",
                404: "Invitation(s) not found.",
                500: "The server is not connected to the database."
            })
    @login_required
    def get(invitee_id, self):
        '''
        From the database, retrieves the requested pending invitation(s) sent to the user and returns them.
        '''

        # Parses the form data.
        form = ReadCollaborationInvitationForm(request.args)

        # Checks whether the sent data is in valid form.
        # If yes, starts processing the data.
        # If not, an error is raised.
        if form.validate():
            # Tries to connect to the database.
            # If it fails, an error is raised.
            try:
                # Checks whether the requester want to view a specific single invitation or all the invitations sent to them.
                # Depending on the case, invitation(s) is retrieved from the database.
                if form.invitation_id.data is not None:
                    invitations_list = CollaborationInvitation.query.filter_by(id=form.invitation_id.data, invitee_id=invitee_id).all()
                else:
                    invitations_list = CollaborationInvitation.query.filter_by(invitee_id=invitee_id).all()
            except:
                return make_response(jsonify({"error" : "The server is not connected to the database."}), 500)
            else:
                # If no invitations found with the given input, an error gets raised.
                # If not, found invitations are returned.
                if len(invitations_list) == 0:
                    return make_response(jsonify([]), 200)
                else:
                    response = []
                    for invitation in invitations_list:
                        invitation_details = {}
                        invitation_details["invitation_id"] = invitation.id
                        invitation_details["workspace_id"] = invitation.workspace_id
                        invitation_details["invitor_id"] = invitation.invitor_id
                        invitation_details["invitee_id"] = invitation.invitee_id

                        invited_workspace = Workspace.query.filter_by(id=invitation.workspace_id).first()
                        invitation_details["workspace_title"] =  invited_workspace.title
                        invitation_details["workspace_description"] = invited_workspace.description

                        invitor_user = User.query.filter_by(id=invitation.invitor_id).first()
                        invitation_details["invitor_fullname"] = "{} {}".format(invitor_user.name, invitor_user.surname)
                    
                        response.append(invitation_details)
                    return make_response(jsonify(response), 200)
        else:
            return make_response(jsonify({"error" : "Missing data fields or invalid data."}), 400)


    # DELETE request
    @api.expect(delete_collaboration_invitation_parser)
    @api.doc(responses={
                200: "Invitation has been successfully responded.",
                400: "Missing data fields or invalid data.",
                401: "You are not the invitee of this invitation, you cannot respond to it.",
                404: "The invitation is not found.",
                500: "The server is not connected to the database."
            })
    @login_required
    def delete(invitee_id, self):
        '''
        Responds to the invitation with the given ID.
        '''

        # Parses the form data.
        form = DeleteCollaborationInvitationForm(request.form)

        # Checks whether the sent data is in valid form.
        # If yes, starts processing the data.
        # If not, an error is raised.
        if form.validate():
            # Tries to connect to the database.
            # If it fails, an error is raised.
            try:
                invitation = CollaborationInvitation.query.filter_by(id=form.invitation_id.data).first()
            except:
                return make_response(jsonify({"error" : "The server is not connected to the database."}), 500)
            else:
                # Checks whether there is an existing invitation with the given ID in the database.
                # If yes, processing continues.
                # If not, an error gets raised.
                if invitation is not None:
                    # Checks whether the given invitation is for the requester user or not.
                    # If yes, processing continues.
                    # If not, an error gets raised.
                    if invitation.invitee_id == invitee_id:
                        # If the invitation is accepted, the user becomes an active contributor of the workspace they were invited to.
                        if form.is_accepted.data == 1:
                            contribution = Contribution(workspace_id=invitation.workspace_id,user_id=invitee_id,is_active=True)
                            db.session.add(contribution)
                            add_new_collaboration(invitation.workspace_id, invitee_id)
    
                            try:
                                workspace = Workspace.query.filter(Workspace.id == invitation.workspace_id).first()
                                current_user = User.query.filter(User.id ==invitee_id).first()
                            except:
                                return make_response(jsonify({"error" : "The server is not connected to the database."}), 500)

                            # Activity is added into Activity Stream
                            activity_stream_entry = ActivityStreamItem(
                                activity_context_vocab = "https://www.w3.org/ns/activitystreams",
                                activity_summary = "{} {} is now a contributor in {} workspace".format(current_user.name, current_user.surname, workspace.title),
                                activity_type = "Join",
                                activity_actor_type = "Person",
                                activity_actor_id = current_user.id,
                                activity_actor_name = (current_user.name + " " + current_user.surname),
                                activity_actor_image_url = profile_photo_link(current_user.profile_photo,current_user.id),
                                activity_object_type = "Group",
                                activity_object_name = workspace.title,
                                activity_object_id = workspace.id,
                            )
                            try:
                                db.session.add(activity_stream_entry)
                                # commit is done 5 lines below.
                            except:
                                return make_response(jsonify({'error': 'Database Connection Error'}), 500)
                            
                        # Invitation gets deleted no matter how it was responded.
                        db.session.delete(invitation)
                        try:
                            db.session.commit()
                        except:
                            return make_response(jsonify({"error" : "The server is not connected to the database."}), 500)
                        else:
                            return make_response(jsonify({"message" : "Invitation has been successfully responded."}), 200)
                    else:
                        return make_response(jsonify({"error" : "You are not the invitee of this invitation, you cannot respond to it."}), 401)
                else:
                    return make_response(jsonify({"error" : "The invitation is not found."}), 404)
        else:
            return make_response(jsonify({"error" : "Missing data fields or invalid data."}), 400)


@workspace_system_ns.route("/applications")
class CollaborationApplicationsAPI(Resource):
    # POST request
    @api.expect(create_collaboration_application_parser)
    @api.doc(responses={
                201: "Application has been successfully created.",
                400: "Missing data fields or invalid data.",
                404: "The workspace does not exist.",
                409: "The user is already an active contributor of this workspace OR \
                there is already an application sent to this workspace. OR \
                the workspace has already reached its maximum number of collaborators OR \
                this workspace is not in 'search for collaborators' state.",
                500: "The server is not connected to the database."
            })
    @login_required
    def post(applicant_id, self):
        '''
        Creates an application for a workspace.
        '''
        
        # Parses the form data.
        form = CreateCollaborationApplicationForm(request.form)

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
                # Checks whether there is an existing workspace with the given ID in the database.
                # If yes, processing continues.
                # If not, an error gets raised.
                if requested_workspace is not None:
                    # Checks whether the applicant user is an active contributor of the requested workspace or
                    # whether there is an already sent application to the requested workspace or
                    # whether the workspace has already reached its maximum number of collaborators or
                    # whether the workspace is not in "search for collaborators" state. 
                    # If not, the application gets created.
                    # If yes, an error gets raised.
                    active_contribution = Contribution.query.filter_by(workspace_id=requested_workspace.id, user_id=applicant_id, is_active=True).first()
                    application = CollaborationApplication.query.filter_by(workspace_id=requested_workspace.id, applicant_id=applicant_id).first()
                    contributions = Contribution.query.filter_by(workspace_id=requested_workspace.id, is_active=True).all()
                    if active_contribution:
                        return make_response(jsonify({"error" : "Applicant user is already an active contributor of this workspace."}), 409)
                    elif application:
                        return make_response(jsonify({"error" : "There is already an application sent to this workspace."}), 409)
                    elif not (len(contributions) < requested_workspace.max_collaborators):
                        return make_response(jsonify({"error" : "The workspace has already reached its maximum number of collaborators."}), 409)
                    elif requested_workspace.state != 0:
                        return make_response(jsonify({"error" : "This workspace is not in 'search for collaborators' state."}), 409)
                    else:
                        new_application = CollaborationApplication(workspace_id=requested_workspace.id, applicant_id=applicant_id)
                        try:
                            db.session.add(new_application)
                            db.session.commit()
                        except:
                            return make_response(jsonify({"error" : "The server is not connected to the database."}), 500)
                        else:
                            return make_response(jsonify({"message" : "Application has been successfully created."}), 201)
                else:
                    return make_response(jsonify({"error" : "The workspace does not exist."}), 404)
        else:
            return make_response(jsonify({"error" : "Missing data fields or invalid data."}), 400)


    # GET request
    @api.expect(read_collaboration_application_parser)
    @api.doc(responses={
                200: "Application(s) found.",
                400: "Missing data fields or invalid data.",
                403: "The user is not an active contributor of this workspace.",
                404: "Application(s) not found.",
                500: "The server is not connected to the database."
            })
    @login_required
    def get(requester_id, self):
        '''
        From the database, retrieves the requested pending application(s) sent to the workspace and returns them.
        '''

        # Parses the form data.
        form = ReadCollaborationApplicationForm(request.args)

        # Checks whether the sent data is in valid form.
        # If yes, starts processing the data.
        # If not, an error is raised.
        if form.validate():
            # Tries to connect to the database.
            # If it fails, an error is raised.
            try:
                active_contributors = Contribution.query.filter_by(workspace_id=form.workspace_id.data, is_active=True).all()
            except:
                return make_response(jsonify({"error" : "The server is not connected to the database."}), 500)
            else:
                # Checks whether the requester user is among the active contributors of the workspace the application is sent for.
                # If yes, the processing continues.
                # If no, an error gets raised.
                if requester_id in [contribution.user_id for contribution in active_contributors]:
                    try:
                        # Checks whether the requester want to view a specific single application or all the applications sent to the given workspace.
                        # Depending on the case, application(s) is retrieved from the database.
                        if form.application_id.data is not None:
                            applications_list = CollaborationApplication.query.filter_by(id=form.application_id.data, workspace_id=form.workspace_id.data).all()
                        else:
                            applications_list = CollaborationApplication.query.filter_by(workspace_id=form.workspace_id.data).all()
                    except:
                        return make_response(jsonify({"error" : "The server is not connected to the database."}), 500)
                    else:
                        # If no applications found with the given input, an error gets raised.
                        # If not, found applications are returned. 
                        if len(applications_list) == 0:
                            return make_response(jsonify([]), 200)
                        else:
                            response = []
                            for application in applications_list:
                                application_details = {}
                                application_details["application_id"] = application.id

                                applicant_user = User.query.filter_by(id=application.applicant_id).first()
                                application_details["applicant_id"] = application.applicant_id
                                application_details["applicant_fullname"] = "{} {}".format(applicant_user.name, applicant_user.surname)
                            
                                response.append(application_details)
                            return make_response(jsonify(response), 200)
                else:
                    return make_response(jsonify({"error" : "The user is not an active contributor of this workspace."}), 403)
        else:
            return make_response(jsonify({"error" : "Missing data fields or invalid data."}), 400)


    # DELETE request
    @api.expect(delete_collaboration_application_parser)
    @api.doc(responses={
                200: "Application has been successfully responded.",
                400: "Missing data fields or invalid data.",
                403: "The user is not an active contributor of this workspace.",
                404: "The application is not found.",
                500: "The server is not connected to the database."
            })
    @login_required
    def delete(requester_id, self):
        '''
        Responds to the application with the given ID.
        '''

        # Parses the form data.
        form = DeleteCollaborationApplicationForm(request.form)

        # Checks whether the sent data is in valid form.
        # If yes, starts processing the data.
        # If not, an error is raised.
        if form.validate():
            # Tries to connect to the database.
            # If it fails, an error is raised.
            try:
                application = CollaborationApplication.query.filter_by(id=form.application_id.data).first()
            except:
                return make_response(jsonify({"error" : "The server is not connected to the database."}), 500)
            else:
                # Checks whether there is an existing application with the given ID in the database.
                # If yes, processing continues.
                # If not, an error gets raised.
                if application is not None:
                    try:
                        active_contributors = Contribution.query.filter_by(workspace_id=application.workspace_id, is_active=True).all()
                    except:
                        return make_response(jsonify({"error" : "The server is not connected to the database."}), 500)
                    else:
                        # Checks whether the requester user is among the active contributors of the workspace the application is sent for.
                        # If yes, the processing continues.
                        # If no, an error gets raised.
                        if requester_id in [contribution.user_id for contribution in active_contributors]:
                            # If the application is accepted, the applicant becomes an active contributor of the workspace they applied to.
                            if form.is_accepted.data == 1:
                                contribution = Contribution(workspace_id=application.workspace_id,user_id=application.applicant_id,is_active=True)
                                db.session.add(contribution)
                                add_new_collaboration(application.workspace_id, application.applicant_id)
                            
                                try:
                                    workspace = Workspace.query.filter(Workspace.id == application.workspace_id).first()
                                    current_user = User.query.filter(User.id == application.applicant_id).first()
                                except:
                                    return make_response(jsonify({"error" : "The server is not connected to the database."}), 500)

                                # Activity is added into Activity Stream
                                activity_stream_entry = ActivityStreamItem(
                                    activity_context_vocab = "https://www.w3.org/ns/activitystreams",
                                    activity_summary = "{} {} is now a contributor in {} workspace".format(current_user.name, current_user.surname, workspace.title),
                                    activity_type = "Join",
                                    activity_actor_type = "Person",
                                    activity_actor_id = current_user.id,
                                    activity_actor_name = (current_user.name + " " + current_user.surname),
                                    activity_actor_image_url = profile_photo_link(current_user.profile_photo,current_user.id),
                                    activity_object_type = "Group",
                                    activity_object_name = workspace.title,
                                    activity_object_id = workspace.id,
                                )
                                try:
                                    db.session.add(activity_stream_entry)
                                    # commit is done 5 lines below.
                                except:
                                    return make_response(jsonify({'error': 'Database Connection Error'}), 500)
                            
                            # Application gets deleted no matter how it was responded.
                            db.session.delete(application)
                            try:
                                db.session.commit()
                            except:
                                return make_response(jsonify({"error" : "The server is not connected to the database."}), 500)
                            else:
                                return make_response(jsonify({"message" : "Application has been successfully responded."}), 200)
                        else:
                            return make_response(jsonify({"error" : "The user is not an active contributor of this workspace."}), 403)
                else:
                    return make_response(jsonify({"error" : "The application is not found."}), 404)
        else:
            return make_response(jsonify({"error" : "Missing data fields or invalid data."}), 400)

@workspace_system_ns.route("/quit")
class QuitWorkspaceAPI(Resource):

    @api.expect(quit_workspace_parser)
    @api.doc(responses={
                201: "You successfully quited from workspace",
                400: "Missing or invalid data",
                401: "Login Required"
            })
    @login_required
    @workspace_exists(param_loc='form',workspace_id_key='workspace_id')
    @active_contribution_required(param_loc='form',workspace_id_key='workspace_id')
    def delete(user_id,self):
        """
            Use it to quit from a workspace
        """
        form = QuitWorkspaceForm(request.form)
        if form.validate():
            try:
                contribution = Contribution.query.filter_by(workspace_id=form.workspace_id.data, user_id = user_id , is_active=True).first()
                db.session.delete(contribution)

                # Add this activity into Activity Stream
                try:
                    current_user = User.query.filter(User.id == user_id).first()
                    workspace = Workspace.query.filter(Workspace.id == form.workspace_id.data).first()
                except:
                    return make_response(jsonify({'error': 'DB connection error'}), 500)

                activity_stream_entry = ActivityStreamItem(
                    activity_context_vocab = "https://www.w3.org/ns/activitystreams",
                    activity_summary = "{} {} left {} workspace".format(current_user.name, current_user.surname, workspace.title),
                    activity_type = "Leave",
                    activity_actor_type = "Person",
                    activity_actor_id = current_user.id,
                    activity_actor_name = (current_user.name + " " + current_user.surname),
                    activity_actor_image_url = profile_photo_link(current_user.profile_photo,current_user.id),
                    activity_object_type = "Group",
                    activity_object_name = workspace.title,
                    activity_object_id = workspace.id
                )
                try:
                    db.session.add(activity_stream_entry)
                except:
                    return make_response(jsonify({'error': 'Database Connection Error'}), 500)

                db.session.commit()
                return make_response(jsonify({"msg" : "You successfully quited from workspace."}), 201)
            except:
                return make_response(jsonify({"error" : "The server is not connected to the database."}), 500)
        else:
            return make_response(jsonify({"error" : "Missing data fields or invalid data."}), 400)

        

def register_resources(api):
    schedule_regularly()
    api.add_namespace(workspace_system_ns)
