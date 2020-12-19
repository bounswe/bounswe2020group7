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