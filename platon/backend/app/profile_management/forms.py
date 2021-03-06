from wtforms import Form, IntegerField, StringField, validators
from app import api
from flask_restplus import reqparse

class ResearchInfoGetForm(Form):
    user_id = IntegerField("user_id",validators=[validators.DataRequired()])
    page = IntegerField("page")
    per_page = IntegerField("per_page")

research_info_get_parser = reqparse.RequestParser()
research_info_get_parser.add_argument('user_id',required=True,type=int,help="User id of requested user",location='args')
research_info_get_parser.add_argument('page',type=int,help="Page index that you want(Starts from 0)",location='args')
research_info_get_parser.add_argument('per_page',type=int,help="Number of items in a page",location='args')
research_info_get_parser.add_argument('auth_token',required=True, type=str,help="Token that sent via email as a URL link",location='headers')

class ResearchInfoPostForm(Form):
    research_title = StringField("research_title",validators=[validators.DataRequired()])
    description = StringField("description")
    year = IntegerField("year",validators=[validators.DataRequired()])

research_info_post_parser = reqparse.RequestParser()
research_info_post_parser.add_argument('research_title',required=True,type=str,help="Title of the research",location='form')
research_info_post_parser.add_argument('description',default='',type=str,help="Description of the work",location='form')
research_info_post_parser.add_argument('year',required=True,type=int,help="Year of the work",location='form')
research_info_post_parser.add_argument('auth_token',required=True, type=str,help="Authentication Token",location='headers')

class ResearchInfoUpdateForm(Form):
    research_id = IntegerField("research_id",validators=[validators.DataRequired()])
    research_title = StringField("research_title")
    description = StringField("description")
    year = IntegerField("year")

research_info_update_parser = reqparse.RequestParser()
research_info_update_parser.add_argument('research_id',required=True,type=int,help='Research ID of the Research',location='form')
research_info_update_parser.add_argument('research_title',default='',type=str,help="Title of the research",location='form')
research_info_update_parser.add_argument('description',default='',type=str,help="Description of the work",location='form')
research_info_update_parser.add_argument('year',default='',type=int,help="Year of the work",location='form')
research_info_update_parser.add_argument('auth_token',required=True, type=str,help="Authentication Token",location='headers')

class ResearchInfoDeleteFrom(Form):
    research_id = IntegerField('research_id',validators=[validators.DataRequired()])

research_info_delete_parser = reqparse.RequestParser()
research_info_delete_parser.add_argument('research_id',required=True,type=int,help='Research ID of the Research',location='form')
research_info_delete_parser.add_argument('auth_token',required=True, type=str,help="Authentication Token",location='headers')

class JobsPostForm(Form):
    name = StringField("name", validators=[validators.DataRequired()])

jobs_post_parser = reqparse.RequestParser()
jobs_post_parser.add_argument('name',required=True, type=str,help="Name of the job",location='form')
jobs_post_parser.add_argument('auth_token',required=True, type=str,help="Authentication Token",location='headers')


class JobsPutForm(Form):
    id = IntegerField("id", validators=[validators.DataRequired()])
    name = StringField("name", default='')

jobs_put_parser = reqparse.RequestParser()
jobs_put_parser.add_argument('id',required=True,type=int,help='Job ID',location='form')
jobs_put_parser.add_argument('name',required=True, type=str,help="Name of the job",location='form')
jobs_put_parser.add_argument('auth_token',required=True, type=str,help="Authentication Token",location='headers')

class JobsDeleteForm(Form):
    id = IntegerField("id", validators=[validators.DataRequired()])

jobs_delete_parser = reqparse.RequestParser()
jobs_delete_parser.add_argument('id',required=True,type=int,help='Job ID',location='form')
jobs_delete_parser.add_argument('auth_token',required=True, type=str,help="Authentication Token",location='headers')

class SkillsPostForm(Form):
    name = StringField("name", validators=[validators.DataRequired()])

skills_post_parser = reqparse.RequestParser()
skills_post_parser.add_argument('name',required=True, type=str,help="Name of the skill",location='form')
skills_post_parser.add_argument('auth_token',required=True, type=str,help="Authentication Token",location='headers')

class SkillsPutForm(Form):
    id = IntegerField("id", validators=[validators.DataRequired()])
    name = StringField("name", default='')

skills_put_parser = reqparse.RequestParser()
skills_put_parser.add_argument('id',required=True,type=int,help='Skill ID',location='form')
skills_put_parser.add_argument('name',required=True, type=str,help="Name of the skill",location='form')
skills_put_parser.add_argument('auth_token',required=True, type=str,help="Authentication Token",location='headers')

class SkillsDeleteForm(Form):
    id = IntegerField("id", validators=[validators.DataRequired()])

skills_delete_parser = reqparse.RequestParser()
skills_delete_parser.add_argument('id',required=True,type=int,help='Skill ID',location='form')
skills_delete_parser.add_argument('auth_token',required=True, type=str,help="Authentication Token",location='headers')

class NotificationGetForm(Form):
    page = IntegerField("page")
    per_page = IntegerField("per_page")

notification_get_parser = reqparse.RequestParser()
notification_get_parser.add_argument('page',type=int,help="Page index that you want(Starts from 0)",location='args')
notification_get_parser.add_argument('per_page',type=int,help="Number of items in a page",location='args')
notification_get_parser.add_argument('auth_token',required=True, type=str,help="Authentication Token",location='headers')

class NotificationPostForm(Form):
    is_email_allowed = IntegerField("is_email_allowed", validators=[validators.optional(), validators.NumberRange(min=0, max=1)])
    is_notification_allowed = IntegerField("is_notification_allowed", validators=[validators.optional(), validators.NumberRange(min=0, max=1)])

notification_post_parser = reqparse.RequestParser()
notification_post_parser.add_argument('is_email_allowed',type=int,help="1 => Allowed 0 => Not Allowed",location='form')
notification_post_parser.add_argument('is_notification_allowed',type=int,help="1 => Allowed 0 => Not Allowed",location='form')
notification_post_parser.add_argument('auth_token',required=True, type=str,help="Authentication Token",location='headers')

class NotificationDeleteForm(Form):
    notification_id = IntegerField('notification_id',validators=[validators.DataRequired()])

notification_delete_parser = reqparse.RequestParser()
notification_delete_parser.add_argument('notification_id',required=True,type=int,help='ID of the Notification that will be deleted',location='form')
notification_delete_parser.add_argument('auth_token',required=True, type=str,help="Authentication Token",location='headers')

front_page_parser = reqparse.RequestParser()
front_page_parser.add_argument('auth_token',required=True, type=str,help="Authentication Token",location='headers')

