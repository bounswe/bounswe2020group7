from wtforms import Form, IntegerField, StringField, validators
from app import api
from flask_restplus import reqparse

class ResearchInfoGetForm(Form):
    user_id = IntegerField("user_id",validators=[validators.DataRequired()])

research_info_get_parser = reqparse.RequestParser()
research_info_get_parser.add_argument('user_id',required=True,type=int,help="User id of requested user",location='args')
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
    research_title = StringField("research_title",default='')
    description = StringField("description",default='')
    year = IntegerField("year",default=-1)

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
