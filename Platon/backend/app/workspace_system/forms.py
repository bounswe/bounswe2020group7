from wtforms import Form, StringField, IntegerField, FieldList, validators
from flask_restplus import reqparse

date_regex = "^(20|21)\\d\\d[- /.](0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])$"
list_regex = "^\\[((\"|')?\\w+(\"|')?)(,\\s*(\"|')?\\w+(\"|')?)*\\]$"

class CreateWorkspaceForm(Form):
	title = StringField("Title of the new workspace", validators=[validators.DataRequired(), validators.Length(max=256)])
	description = StringField("Description of the new workspace", validators=[validators.DataRequired(), validators.Length(max=2000)])
	is_private = IntegerField("Privacy status of the new workspace", validators=[validators.optional(), validators.NumberRange(min=0, max=1)])
	max_collaborators = IntegerField("Maximum number of collaborators of the new workspace", validators=[validators.optional(), validators.NumberRange(min=1)])
	deadline = StringField("Deadline of the new workspace", validators=[validators.optional(), validators.Regexp(regex=date_regex)])
	requirements = StringField("The list of the requirements to be able to join the new workspace", validators=[validators.optional(), validators.Regexp(regex=list_regex)])
	skills = StringField("The list of the skills required to be able to join the new workspace", validators=[validators.optional(), validators.Regexp(regex=list_regex)])
create_workspace_parser = reqparse.RequestParser()
create_workspace_parser.add_argument("title", required=True, type=str, help="Title of the new workspace", location="form")
create_workspace_parser.add_argument("description", required=True, type=str, help="Description of the new workspace", location="form")
create_workspace_parser.add_argument("is_private", required=False, type=int, help="Privacy status of the new workspace", location="form")
create_workspace_parser.add_argument("max_collaborators", required=False, type=int, help="Maximum number of collaborators of the new workspace", location="form")
create_workspace_parser.add_argument("deadline", required=False, type=str, help="Deadline of the new workspace", location="form")
create_workspace_parser.add_argument("requirements", required=False, type=str, help="The list of the requirements to be able to join the new workspace", location="form")
create_workspace_parser.add_argument("skills", required=False, type=str, help="The list of the skills required to be able to join the new workspace", location="form")
create_workspace_parser.add_argument("auth_token",required=True, type=str, help="Authentication token", location="headers")


class ReadWorkspaceForm(Form):
	workspace_id = IntegerField("ID of the requested workspace", validators=[validators.optional()])
read_workspace_parser = reqparse.RequestParser()
read_workspace_parser.add_argument("workspace_id",required=False,type=int,help="ID of the requested workspace", location="args")
read_workspace_parser.add_argument("auth_token",required=True, type=str, help="Authentication token", location="headers")


class UpdateWorkspaceForm(Form):
	workspace_id = IntegerField("ID of the workspace to be updated", validators=[validators.DataRequired()])
	title = StringField("Updated title of the workspace", validators=[validators.optional(), validators.Length(max=256)])
	description = StringField("Updated description of the workspace", validators=[validators.optional(), validators.Length(max=2000)])
	is_private = IntegerField("Updated privacy status of the workspace", validators=[validators.optional(), validators.NumberRange(min=0, max=1)])
	max_collaborators = IntegerField("Updated maximum number of collaborators of the workspace", validators=[validators.optional(), validators.NumberRange(min=1)])
	deadline = StringField("Updated deadline of the workspace", validators=[validators.optional(), validators.Regexp(regex=date_regex)])
	requirements = StringField("Updated list of the requirements to be able to join the workspace", validators=[validators.optional(), validators.Regexp(regex=list_regex)])
	skills = StringField("Updated list of the skills required to be able to join the workspace", validators=[validators.optional(), validators.Regexp(regex=list_regex)])
	state = IntegerField("Updated state of the workspace", validators=[validators.optional(), validators.NumberRange(min=0, max=2)])
update_workspace_parser = reqparse.RequestParser()
update_workspace_parser.add_argument("workspace_id", required=True, type=int, help="ID of the workspace to be updated", location="form")
update_workspace_parser.add_argument("title", required=False, type=str, help="Updated title of the new workspace", location="form")
update_workspace_parser.add_argument("description", required=False, type=str, help="Updated description of the new workspace", location="form")
update_workspace_parser.add_argument("is_private", required=False, type=int, help="Updated privacy status of the new workspace", location="form")
update_workspace_parser.add_argument("max_collaborators", required=False, type=int, help="Updated maximum number of collaborators of the new workspace", location="form")
update_workspace_parser.add_argument("deadline", required=False, type=str, help="Updated deadline of the new workspace", location="form")
update_workspace_parser.add_argument("requirements", required=False, type=str, help="Updated list of the requirements to be able to join the new workspace", location="form")
update_workspace_parser.add_argument("skills", required=False, type=str, help="Updated list of the skills required to be able to join the new workspace", location="form")
update_workspace_parser.add_argument("state", required=False, type=int, help="Updated state of the workspace", location="form")
update_workspace_parser.add_argument("auth_token",required=True, type=str, help="Authentication token", location="headers")


class DeleteWorkspaceForm(Form):
	workspace_id = IntegerField("ID of the workspace to be deleted", validators=[validators.DataRequired()])
delete_workspace_parser = reqparse.RequestParser()
delete_workspace_parser.add_argument("workspace_id",required=True,type=int,help="ID of the requested workspace", location="args")
delete_workspace_parser.add_argument("auth_token",required=True, type=str, help="Authentication token", location="headers")

class TrendingProjectsForm(Form):
	number_of_workspaces = IntegerField("number_of_workspaces",validators=[validators.DataRequired()])

trending_project_parser = reqparse.RequestParser()
trending_project_parser.add_argument("number_of_workspaces",required=True,type=int,help="Number of Workspaces", location="args")