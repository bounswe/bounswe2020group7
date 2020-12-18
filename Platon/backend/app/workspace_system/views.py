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

workspace_system_ns = Namespace("Workspace System",
                             description="Workspace System Endpoints",
                             path="/workspace")
                    

@workspace_system_ns.route("/issue")
class IssueAPI(Resource):

    # pagination will be added later.
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

def register_resources(api):
    api.add_namespace(workspace_system_ns)