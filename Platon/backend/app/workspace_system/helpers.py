from app import db

from app.workspace_system.models import WorkspaceSkill, WorkspaceRequirement, Contribution, Requirement
from app.profile_management.models import Skills
from app.auth_system.models import User


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


def add_workspace_contribution(workspace_id, contributor_id):
	'''
	This method adds the contribution of the user with the ID "contributor_id"
	to the workspace with the given ID.
	'''
	workspace_contribution = Contribution(workspace_id=workspace_id, contributor_id=contributor_id, is_active=True)
	db.session.add(workspace_contribution)
	db.session.commit()


def get_workspace_skills_text(workspace_id):
	# Skill list of the requested workspace is retrieved from the database.
	requested_workspace_skills = WorkspaceSkill.query.filter_by(workspace_id=workspace_id)
	return str([Skills.query.filter_by(id=workspace_skill.skill_id).first().name for workspace_skill in requested_workspace_skills])


def get_workspace_requirements_text(workspace_id):
	# Requirements list of the requested workspace is retrieved from the database.
	requested_workspace_requirements = WorkspaceRequirement.query.filter_by(workspace_id=workspace_id)
	return str([Requirement.query.filter_by(id=workspace_requirement.requirement_id).first().text for workspace_requirement in requested_workspace_requirements])


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
                workspace = Workspace.query.filter((Workspace.id == workspace_id)).first()
            except:
                return make_response(jsonify({'error' : 'Database Connection Problem'}),500)
            if workspace is None:
                return make_response(jsonify({'error': 'Requested Workspace can not be found'}),404)
            return func(*args,**kwargs)
        return workspace_check
    return workspace_exists_inner

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
            try:
                contribution = Contribution.query.filter((Contribution.workspace_id == workspace_id) & (Contribution.user_id == user_id)).first()
            except:
                return make_response(jsonify({'error' : 'Database Connection Problem'}),500)
            if contribution is None:
                return make_response(jsonify({'error': 'You have to contribute the Workspace'}),404)
            if int(contribution.is_active) != 1:
                return make_response(jsonify({'error': 'You have to contribute the Workspace actively'}),404)
            return func(*args,**kwargs)
        return contribution_check
    return active_contribution_required_inner
