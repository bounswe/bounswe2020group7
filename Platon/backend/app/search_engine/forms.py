from wtforms import Form, StringField, IntegerField, BooleanField, validators, DateTimeField
from flask_restplus import reqparse
import datetime

class UserSearchForm(Form):
    search_query = StringField("search_query",validators=[validators.DataRequired()])
    job_filter = IntegerField("job_filter")
    page = IntegerField("page")
    per_page = IntegerField("per_page")

user_search_parser = reqparse.RequestParser()
user_search_parser.add_argument('search_query',required=True,type=str,help="Search Query",location='args')
user_search_parser.add_argument('job_filter',type=int,help="Job Filter(Give as ID)",location='args')
user_search_parser.add_argument('page',type=int,help="Page ID(0-Indexed)",location='args')
user_search_parser.add_argument('per_page',type=int,help="Number of Records in a Page",location='args')
user_search_parser.add_argument('auth_token', type=str,help="Authantication Token(If registered)",location='headers')

class SearchHistoryForm(Form):
    search_type = IntegerField("search_type",validators=[validators.InputRequired()])

search_history_parser = reqparse.RequestParser()
search_history_parser.add_argument('search_type',required=True,type=int,help="Search Type 0 => User, 1 => Workspace, 2 => Upcoming Event",location='args')
search_history_parser.add_argument('auth_token', required=True ,type=str,help="Authantication Token(If registered)",location='headers')

class WorkspaceSearchForm(Form):
    search_query = StringField("search_query",validators=[validators.DataRequired()])
    skill_filter = StringField("skill_filter")
    creator_name = StringField("creator_name")
    creator_surname = StringField("creator_surname")   
    starting_date_start = DateTimeField("starting_date_start", default=None)
    starting_date_end = DateTimeField("starting_date_end", default=None)
    deadline_start = DateTimeField("deadline_start", default=None)
    deadline_end = DateTimeField("deadline_end", default=None)
    sorting_criteria = IntegerField("sorting_criteria",validators=[validators.NumberRange(min=0, max=2),validators.optional()], default=None)
    page = IntegerField("page")
    per_page = IntegerField("per_page")

ws_search_parser = reqparse.RequestParser()
ws_search_parser.add_argument('search_query',required=True,type=str,help="Search Query",location='args')
ws_search_parser.add_argument('skill_filter',type=str,help="Skill Filter",location='args')
ws_search_parser.add_argument('creator_name',type=str,help="Name of the creator",location='args')
ws_search_parser.add_argument('creator_surname',type=str,help="Surname of the creator",location='args')
ws_search_parser.add_argument('starting_date_start',type=datetime.datetime,help="Inclusive.",location='args')
ws_search_parser.add_argument('starting_date_end',type=datetime.datetime,help="Exclusive.",location='args')
ws_search_parser.add_argument('deadline_start',type=datetime.datetime,help="Inclusive.",location='args')
ws_search_parser.add_argument('deadline_end',type=datetime.datetime,help="Exclusive.",location='args')
ws_search_parser.add_argument('sorting_criteria',type=int,help="None => Semantic Rating // 0 => (Ascending) Date Order // 1 => (Ascending) Number of Collaborators Needed Order // 2 => (Ascending) Alphabetical Order)",location='args')
ws_search_parser.add_argument('page',type=int,help="Page ID(0-Indexed)",location='args')
ws_search_parser.add_argument('per_page',type=int,help="Number of Records in a Page",location='args')
ws_search_parser.add_argument('auth_token', type=str,help="Authantication Token(If registered)",location='headers')