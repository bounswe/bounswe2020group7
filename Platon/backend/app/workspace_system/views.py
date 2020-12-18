from flask import make_response, jsonify, request
from flask_restplus import Resource, Namespace, fields
from flask import current_app as app
import math

from app import api, db
from app.auth_system.models import User
from app.follow_system.models import Follow, FollowRequests
from app.auth_system.views import login_required
from app.follow_system.helpers import follow_required
from app.workspace_system.models import Issue, IssueAssignee, IssueComment
from app.profile_management.models import Jobs
from app.workspace_system.forms import GetIssuesForm, GetIssueCommentForm, GetIssueAssigneeForm, PostIssueAssigneeForm, PostIssueCommentForm, PostIssuesForm, PutIssuesForm, DeleteIssueAssigneeForm, DeleteIssueCommentForm, DeleteIssuesForm

workspace_system_ns = Namespace("Workspace System",
                             description="Workspace System Endpoints",
                             path="/workspace")
                    

@workspace_system_ns.route("/issue")
class GetIssuesAPI(Resource):

    # pagination will be added later.
    @login_required
    def get(user_id, self):
        '''
            Let's return Issues.
        '''
        form = GetIssuesForm(request.args)
        if form.validate():
            workspace_id = form.workspace_id.data

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
                    'creator_job_name': job_name,
                    'is_private': creator.is_private
                    })
            
            return make_response(jsonify({'result': return_list}),200)
            
        else:
            return make_response(jsonify({'error': 'Input Format Error'}), 400)

def register_resources(api):
    api.add_namespace(workspace_system_ns)