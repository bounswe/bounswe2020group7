from app import app, db
from functools import wraps
from flask import make_response,jsonify,request
import atexit, json
from apscheduler.schedulers.background import BackgroundScheduler

from app.workspace_system.models import Workspace,WorkspaceSkill, WorkspaceRequirement, Contribution, Requirement
from app.profile_management.models import Skills
from app.auth_system.models import User
from enum import IntEnum

class WorkspaceState(IntEnum):
    search_for_collaborator = 0
    ongoing = 1
    published = 2

def add_workspace_skills(workspace_id, workspace_skills_list):
	'''
	This method adds the skills in the "workspace_skills_list" to the workspace with the given ID.
	'''
	for skill_name in (workspace_skills_list or []):
		# Checks whether the skill with the given name already exists in the database or not.
		skill = Skills.query.filter_by(name=skill_name).first()
		# If not, first, the skill gets created in the database.
		if skill is None:
			skill = Skills(name=skill_name)
			db.session.add(skill)
			db.session.commit()
		# After the skill is retrieved from the database, it gets added to the workspace.
		workspace_skill = WorkspaceSkill(workspace_id=workspace_id, skill_id=skill.id)
		db.session.add(workspace_skill)
		db.session.commit()


def add_workspace_requirements(workspace_id, workspace_requirements_list):
	'''
	This method adds the requirements in the "workspace_requirements_list" to the workspace with the given ID.
	'''
	for requirement_text in (workspace_requirements_list or []):
		requirement = Requirement(text=requirement_text)
		db.session.add(requirement)
		db.session.commit()
		# After the requirement is retrieved from the database, it gets added to the workspace.
		workspace_requirement = WorkspaceRequirement(workspace_id=workspace_id, requirement_id=requirement.id)
		db.session.add(workspace_requirement)
		db.session.commit()	


def add_workspace_contribution(workspace_id, user_id):
	'''
	This method adds the contribution of the user with the ID "user_id"
	to the workspace with the given ID.
	'''
	workspace_contribution = Contribution(workspace_id=workspace_id, user_id=user_id, is_active=True)
	db.session.add(workspace_contribution)
	db.session.commit()


def get_workspace_skills_list(workspace_id):
    # Skill list of the requested workspace is retrieved from the database.
    requested_workspace_skills = WorkspaceSkill.query.filter_by(workspace_id=workspace_id)

    # After the list is retrieved, the name of each skill of that workspace is retrieved from the database
    # and gets appended to "workspace_skill_names"
    workspace_skill_names = list()
    for workspace_skill in requested_workspace_skills:
        skill_name = (Skills.query.filter_by(id=workspace_skill.skill_id).first()).name
        workspace_skill_names.append(skill_name)
    # The list of the names of the skills are converted to a JSON string and returned.
    return workspace_skill_names


def get_workspace_requirements_list(workspace_id):
    # Requirement list of the requested workspace is retrieved from the database.
    requested_workspace_requirements = WorkspaceRequirement.query.filter_by(workspace_id=workspace_id)
    
    # After the list is retrieved, the text of each requirement of that workspace is retrieved from the database
    # and gets appended to "workspace_requirement_texts"
    workspace_requirement_texts = list()
    for workspace_requirement in requested_workspace_requirements:
        requirement_text = (Requirement.query.filter_by(id=workspace_requirement.requirement_id).first()).text
        workspace_requirement_texts.append(requirement_text)
    return workspace_requirement_texts


def get_workspace_active_contributors_list(workspace_id):
    # Contributor list of the requested workspace is retrieved from the database.
    requested_workspace_contributors = Contribution.query.filter_by(workspace_id=workspace_id, is_active=True)
    
    # After the list is retrieved, the data of each contributor of that workspace is retrieved from the database
    # and gets appended to "workspace_contributors_data"
    workspace_contributors_data = list()
    for workspace_contributor in requested_workspace_contributors:
        contributor_data = User.query.filter_by(id=workspace_contributor.user_id).first()
        workspace_contributors_data.append({
                                                "id": contributor_data.id,
                                                "name": contributor_data.name,
                                                "surname": contributor_data.surname
                                            })
    return workspace_contributors_data


def update_workspace_skills(workspace_id, workspace_updated_skills_list):
    '''
    This method updates the required skills of a workspace.
    '''
    if workspace_updated_skills_list is None:
        return
    else:
        workspace_skills_list = WorkspaceSkill.query.filter_by(workspace_id=workspace_id)
        workspace_skills_list.delete()
        db.session.commit()
        add_workspace_skills(workspace_id, workspace_updated_skills_list)


def update_workspace_requirements(workspace_id, workspace_updated_requirements_list):
    '''
    This method updates the requirements of a workspace.
    '''
    if workspace_updated_requirements_list is None:
        return
    else:
        workspace_requirements_list = WorkspaceRequirement.query.filter_by(workspace_id=workspace_id)
        workspace_requirements_list.delete()
        db.session.commit()
        add_workspace_requirements(workspace_id, workspace_updated_requirements_list)


def workspace_exists(param_loc,workspace_id_key):
    """
        param_loc: it only can be "args" or "form" which specifies the location of the requested wokspace id in the request
        workspace_id_key: key of the parameter that represents the requested wokspace id in the request
    """
    def workspace_exists_inner(func):
        """
            Checks the privacy of the user before giving the user data
        """
        @wraps(func)
        def workspace_check(*args,**kwargs):
            if param_loc == 'args':
                try:
                    workspace_id = int(request.args.get(workspace_id_key))
                except:
                    return make_response(jsonify({'error':'Wrong input format'}),400) 
            elif param_loc == 'form':
                try:
                    workspace_id = int(request.form.get(workspace_id_key))
                except:
                    return make_response(jsonify({'error':'Wrong input format'}),400)
            try:
                workspace = Workspace.query.get(workspace_id)
            except:
                return make_response(jsonify({'error' : 'Database Connection Problem'}),500)
            if workspace is None:
                return make_response(jsonify({'error': 'Requested Workspace can not be found'}),404)
            return func(*args,**kwargs)
        return workspace_check
    return workspace_exists_inner

def active_contribution_check(workspace_id,user_id,func,*args,**kwargs):
    try:
        contribution = Contribution.query.filter((Contribution.workspace_id == workspace_id) & (Contribution.user_id == user_id)).first()
    except:
        return make_response(jsonify({'error' : 'Database Connection Problem'}),500)
    if contribution is None:
        return make_response(jsonify({'error': 'You have to contribute the Workspace'}),404)
    if int(contribution.is_active) != 1:
        return make_response(jsonify({'error': 'You have to contribute the Workspace actively'}),404)
    return func(*args,**kwargs)

def active_contribution_required(param_loc,workspace_id_key):
    """
        param_loc: it only can be "args" or "form" which specifies the location of the requested wokspace id in the request
        workspace_id_key: key of the parameter that represents the requested wokspace id in the request
    """
    def active_contribution_required_inner(func):
        """
            Checks the privacy of the user before giving the user data
        """
        @wraps(func)
        def contribution_check(*args,**kwargs):
            if param_loc == 'args':
                try:
                    workspace_id = int(request.args.get(workspace_id_key))
                except:
                    return make_response(jsonify({'error':'Wrong input format'}),400) 
            elif param_loc == 'form':
                try:
                    workspace_id = int(request.form.get(workspace_id_key))
                except:
                    return make_response(jsonify({'error':'Wrong input format'}),400)
            user_id = args[0]
            return active_contribution_check(workspace_id,user_id,func,*args,**kwargs)
        return contribution_check
    return active_contribution_required_inner

def visibility_required(param_loc,workspace_id_key):
    """
        param_loc: it only can be "args" or "form" which specifies the location of the requested wokspace id in the request
        workspace_id_key: key of the parameter that represents the requested wokspace id in the request
        If a Workspace is visible, it must be public and in SFC or PS
    """
    def visibility_required_inner(func):
        """
            Checks the privacy of the user before giving the user data
        """
        @wraps(func)
        def visibilitiy_check(*args,**kwargs):
            if param_loc == 'args':
                try:
                    workspace_id = int(request.args.get(workspace_id_key))
                except:
                    return make_response(jsonify({'error':'Wrong input format'}),400) 
            elif param_loc == 'form':
                try:
                    workspace_id = int(request.form.get(workspace_id_key))
                except:
                    return make_response(jsonify({'error':'Wrong input format'}),400)
            # Take Workspace record from database
            try:
                workspace = Workspace.query.get(workspace_id)
            except:
                return make_response(jsonify({'error' : 'Database Connection Problem'}),500)
            if workspace.is_private:
                user_id = args[0]
                return active_contribution_check(workspace.id,user_id,func,*args,**kwargs)
            else:
                if workspace.state == int(WorkspaceState.ongoing):
                    return make_response(jsonify({"err": "You are note allowed to see the content of this workspace"}),403)
            return func(*args,**kwargs)
        return visibilitiy_check
    return visibility_required_inner


class TredingProjectManager():
    """
        It is used to calculate nex tranding score
        trending_score[t+1] = trnding_score[t]*aging_factor + view_count[t]    
        It must be between 0 and 1
    """
    aging_factor = 0.8

    @staticmethod
    def update_trending_point(workspace):
        """
            Updates the trending score according to the aging factor
            `workspace`: Workspace instance
        """
        prev_score = workspace.trending_score
        prev_count = workspace.view_count
        workspace.trending_score = prev_score * TredingProjectManager.aging_factor + prev_count
        workspace.view_count = 0

    @staticmethod
    def update_all_trending_points():
        with app.app_context():
            try:
               all_workspaces = Workspace.query.all()
            except:
                return
            for workspace in all_workspaces:
                TredingProjectManager.update_trending_point(workspace)
            db.session.commit()

def schedule_regularly():
    scheduler = BackgroundScheduler()
    scheduler.add_job(func=TredingProjectManager.update_all_trending_points, trigger="interval",seconds=60*60)
    scheduler.start()
    # Shut down the scheduler when exiting the app
    atexit.register(lambda: scheduler.shutdown())    
    
