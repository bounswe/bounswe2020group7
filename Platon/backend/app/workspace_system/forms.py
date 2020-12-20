from wtforms import Form, StringField, IntegerField, FieldList, validators, DateTimeField, BooleanField, ValidationError
from flask_restplus import reqparse
import datetime, json

date_regex = "^(20|21)\\d\\d[- /.](0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])$"


def validate_json(form, field):
	try:
		json.loads(field.data)
	except ValueError as e:
		raise ValidationError("This is not a valid JSON string.")


class CreateWorkspaceForm(Form):
	title = StringField("Title of the new workspace", validators=[validators.DataRequired(), validators.Length(max=256)])
	description = StringField("Description of the new workspace", validators=[validators.DataRequired(), validators.Length(max=2000)])
	is_private = IntegerField("Privacy status of the new workspace", validators=[validators.optional(), validators.NumberRange(min=0, max=1)])
	max_collaborators = IntegerField("Maximum number of collaborators of the new workspace", validators=[validators.optional(), validators.NumberRange(min=1)])
	deadline = StringField("Deadline of the new workspace", validators=[validators.optional(), validators.Regexp(regex=date_regex)], filters = [lambda x: x or None])
	requirements = StringField("The list of the requirements to be able to join the new workspace", validators=[validators.optional(), validate_json], default="null")
	skills = StringField("The list of the skills required to be able to join the new workspace", validators=[validators.optional(), validate_json], default="null")
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
	workspace_id = IntegerField("ID of the requested workspace", validators=[validators.DataRequired()])
read_workspace_parser = reqparse.RequestParser()
read_workspace_parser.add_argument("workspace_id",required=True,type=int,help="ID of the requested workspace", location="args")
read_workspace_parser.add_argument("auth_token",required=True, type=str, help="Authentication token", location="headers")


class UpdateWorkspaceForm(Form):
	workspace_id = IntegerField("ID of the workspace to be updated", validators=[validators.DataRequired()])
	title = StringField("Updated title of the workspace", validators=[validators.optional(), validators.Length(max=256)])
	description = StringField("Updated description of the workspace", validators=[validators.optional(), validators.Length(max=2000)])
	is_private = IntegerField("Updated privacy status of the workspace", validators=[validators.optional(), validators.NumberRange(min=0, max=1)])
	max_collaborators = IntegerField("Updated maximum number of collaborators of the workspace", validators=[validators.optional(), validators.NumberRange(min=1)])
	deadline = StringField("Updated deadline of the workspace", validators=[validators.optional(), validators.Regexp(regex=date_regex)])
	requirements = StringField("Updated list of the requirements to be able to join the workspace", validators=[validators.optional(), validate_json], default="null")
	skills = StringField("Updated list of the skills required to be able to join the workspace", validators=[validators.optional(), validate_json], default="null")
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
delete_workspace_parser.add_argument("workspace_id",required=True,type=int,help="ID of the requested workspace", location="form")
delete_workspace_parser.add_argument("auth_token",required=True, type=str, help="Authentication token", location="headers")


class GetIssuesForm(Form):
	workspace_id = IntegerField('workspace_id', validators=[validators.DataRequired()])
	page = IntegerField("page")
	per_page = IntegerField("per_page")

get_issue_parser = reqparse.RequestParser()
get_issue_parser.add_argument("workspace_id",required=True,type=int,help="ID of the requested workspace", location="args")
get_issue_parser.add_argument('page',type=int,help="Page index that you want(Starts from 0)",location='args')
get_issue_parser.add_argument('per_page',type=int,help="Number of items in a page",location='args')
get_issue_parser.add_argument("auth_token",required=True, type=str, help="Authentication token", location="headers")

class PostIssuesForm(Form):
	workspace_id = IntegerField('workspace_id', validators=[validators.DataRequired()])
	title = StringField("Title of the issue", validators=[validators.DataRequired(), validators.Length(max=256)])
	description = StringField("Description of the issue", validators=[validators.DataRequired(), validators.Length(max=2000)])
	deadline = DateTimeField("Deadline of the issue", validators=[validators.optional()])
post_issue_parser = reqparse.RequestParser()
post_issue_parser.add_argument("workspace_id",required=True,type=int,help="ID of the requested workspace", location="form")
post_issue_parser.add_argument("title", required=True, type=str, help="Title of the new workspace", location="form")
post_issue_parser.add_argument("description", required=True, type=str, help="Description of the new workspace", location="form")
post_issue_parser.add_argument("deadline", required=False, type=datetime.datetime, help="Deadline of the new workspace", location="form")
post_issue_parser.add_argument("auth_token",required=True, type=str, help="Authentication token", location="headers")

class PutIssuesForm(Form):
	workspace_id = IntegerField('workspace_id', validators=[validators.DataRequired()])
	issue_id = IntegerField('issue_id', validators=[validators.DataRequired()])
	title = StringField('Updated title', validators = [validators.optional()])
	description = StringField("Updated description of the issue", validators=[validators.optional(), validators.Length(max=2000)])
	deadline = DateTimeField("Updated deadline of the workspace", validators=[validators.optional()])
	is_open = BooleanField("Represents if an issue is open or not", validators=[validators.optional()])
put_issue_parser = reqparse.RequestParser()
put_issue_parser.add_argument("workspace_id",required=True,type=int,help="ID of the requested workspace", location="form")
put_issue_parser.add_argument("issue_id",required=True,type=int,help="ID of the issue", location="form")
put_issue_parser.add_argument("title", required=False, type=str, help="Title of the new workspace", location="form")
put_issue_parser.add_argument("description", required=False, type=str, help="Description of the new workspace", location="form")
put_issue_parser.add_argument("deadline", required=False, type=datetime.datetime, help="Updated deadline of the new workspace", location="form")
put_issue_parser.add_argument("auth_token",required=True, type=str, help="Authentication token", location="headers")

class DeleteIssuesForm(Form):
	workspace_id = IntegerField('workspace_id', validators=[validators.DataRequired()])
	issue_id = IntegerField('issue_id', validators=[validators.DataRequired()])
delete_issue_parser = reqparse.RequestParser()
delete_issue_parser.add_argument("workspace_id",required=True,type=int,help="ID of the requested workspace", location="form")
delete_issue_parser.add_argument("issue_id",required=True,type=int,help="ID of the issue", location="form")
delete_issue_parser.add_argument("auth_token",required=True, type=str, help="Authentication token", location="headers")

class GetIssueAssigneeForm(Form):
	workspace_id = IntegerField('workspace_id', validators=[validators.DataRequired()])
	issue_id = IntegerField('issue_id', validators=[validators.DataRequired()])
	page = IntegerField("page")
	per_page = IntegerField("per_page")

get_issue_assignee_parser = reqparse.RequestParser()
get_issue_assignee_parser.add_argument("workspace_id",required=True,type=int,help="ID of the requested workspace", location="args")
get_issue_assignee_parser.add_argument("issue_id",required=True,type=int,help="ID of the issue", location="args")
get_issue_assignee_parser.add_argument('page',type=int,help="Page index that you want(Starts from 0)",location='args')
get_issue_assignee_parser.add_argument('per_page',type=int,help="Number of items in a page",location='args')
get_issue_assignee_parser.add_argument("auth_token",required=True, type=str, help="Authentication token", location="headers")

class PostIssueAssigneeForm(Form):
	workspace_id = IntegerField('workspace_id', validators=[validators.DataRequired()])
	issue_id = IntegerField('issue_id', validators=[validators.DataRequired()])
	assignee_id = IntegerField('assignee_id', validators=[validators.DataRequired()])
post_issue_assignee_parser = reqparse.RequestParser()
post_issue_assignee_parser.add_argument("workspace_id",required=True,type=int,help="ID of the requested workspace", location="form")
post_issue_assignee_parser.add_argument("issue_id",required=True,type=int,help="ID of the issue", location="form")
post_issue_assignee_parser.add_argument("assignee_id", required=True, type=int, help="ID of the assignee", location="form")
post_issue_assignee_parser.add_argument("auth_token",required=True, type=str, help="Authentication token", location="headers")

class DeleteIssueAssigneeForm(Form):
	workspace_id = IntegerField('workspace_id', validators=[validators.DataRequired()])
	issue_id = IntegerField('issue_id', validators=[validators.DataRequired()])
	assignee_id = IntegerField('assignee_id', validators=[validators.DataRequired()])
delete_issue_assignee_parser = reqparse.RequestParser()
delete_issue_assignee_parser.add_argument("workspace_id",required=True,type=int,help="ID of the requested workspace", location="form")
delete_issue_assignee_parser.add_argument("issue_id",required=True,type=int,help="ID of the issue", location="form")
delete_issue_assignee_parser.add_argument("assignee_id", required=True, type=int, help="ID of the assignee", location="form")
delete_issue_assignee_parser.add_argument("auth_token",required=True, type=str, help="Authentication token", location="headers")

class GetIssueCommentForm(Form):
	workspace_id = IntegerField('workspace_id', validators=[validators.DataRequired()])
	issue_id = IntegerField('issue_id', validators=[validators.DataRequired()])
	page = IntegerField("page")
	per_page = IntegerField("per_page")

get_issue_comment_parser = reqparse.RequestParser()
get_issue_comment_parser.add_argument("workspace_id",required=True,type=int,help="ID of the requested workspace", location="args")
get_issue_comment_parser.add_argument("issue_id",required=True,type=int,help="ID of the issue", location="args")
get_issue_comment_parser.add_argument('page',type=int,help="Page index that you want(Starts from 0)",location='args')
get_issue_comment_parser.add_argument('per_page',type=int,help="Number of items in a page",location='args')
get_issue_comment_parser.add_argument("auth_token",required=True, type=str, help="Authentication token", location="headers")

class PostIssueCommentForm(Form):
	workspace_id = IntegerField('workspace_id', validators=[validators.DataRequired()])
	issue_id = IntegerField('issue_id', validators=[validators.DataRequired()])
	comment = StringField('comment', validators=[validators.DataRequired()])
post_issue_comment_parser = reqparse.RequestParser()
post_issue_comment_parser.add_argument("workspace_id",required=True,type=int,help="ID of the requested workspace", location="form")
post_issue_comment_parser.add_argument("issue_id",required=True,type=int,help="ID of the issue", location="form")
post_issue_comment_parser.add_argument("comment", required=True, type=str, help="Comment", location="form")
post_issue_comment_parser.add_argument("auth_token",required=True, type=str, help="Authentication token", location="headers")

class DeleteIssueCommentForm(Form):
	workspace_id = IntegerField('workspace_id', validators=[validators.DataRequired()])
	issue_id = IntegerField('issue_id', validators=[validators.DataRequired()])
	comment_id = IntegerField('comment_id', validators=[validators.DataRequired()])
delete_issue_comment_parser = reqparse.RequestParser()
delete_issue_comment_parser.add_argument("workspace_id",required=True,type=int,help="ID of the requested workspace", location="form")
delete_issue_comment_parser.add_argument("issue_id",required=True,type=int,help="ID of the issue", location="form")
delete_issue_comment_parser.add_argument("comment_id", required=True, type=int, help="ID of the comment", location="form")
delete_issue_comment_parser.add_argument("auth_token",required=True, type=str, help="Authentication token", location="headers")

class TrendingProjectsForm(Form):
	number_of_workspaces = IntegerField("number_of_workspaces",validators=[validators.DataRequired()])

trending_project_parser = reqparse.RequestParser()
trending_project_parser.add_argument("number_of_workspaces",required=True,type=int,help="Number of Workspaces", location="args")

get_self_parser = reqparse.RequestParser()
get_self_parser.add_argument("auth_token",required=True, type=str, help="Authentication token", location="headers")