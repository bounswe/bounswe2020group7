from wtforms import Form, StringField, IntegerField, BooleanField, validators
from flask_restplus import reqparse

class UserSearchForm(Form):
    search_query = StringField("search_query",validators=[validators.DataRequired()])
    job_filter = IntegerField("job_filter")
    sorting_criteria = IntegerField("sorting_criteria",validators=[validators.NumberRange(min=0, max=1)])
    page = IntegerField("page")
    per_page = IntegerField("per_page")

user_search_parser = reqparse.RequestParser()
user_search_parser.add_argument('search_query',required=True,type=str,help="Search Query",location='args')
user_search_parser.add_argument('job_filter',type=int,help="Job Filter(Give as ID)",location='args')
user_search_parser.add_argument('sorting_criteria',type=int,help="None => Semantic Rating // 0 => Alphabetical Order(A=>Z) // 1 => Alphabetical Order (Z=>A)",location='args')
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
    event_filter = StringField("event_filter")
    page = IntegerField("page")
    per_page = IntegerField("per_page")

ws_search_parser = reqparse.RequestParser()
ws_search_parser.add_argument('search_query',required=True,type=str,help="Search Query",location='args')
ws_search_parser.add_argument('skill_filter',type=str,help="Skill Filter",location='args')
ws_search_parser.add_argument('event_filter',type=str,help="Give Acronym",location='args')
ws_search_parser.add_argument('page',type=int,help="Page ID(0-Indexed)",location='args')
ws_search_parser.add_argument('per_page',type=int,help="Number of Records in a Page",location='args')
ws_search_parser.add_argument('auth_token', type=str,help="Authantication Token(If registered)",location='headers')