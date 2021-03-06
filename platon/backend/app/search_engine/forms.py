from wtforms import Form, StringField, IntegerField, BooleanField, validators, DateTimeField, ValidationError
from flask_restplus import reqparse
import datetime
import json

class UserSearchForm(Form):
    search_query = StringField("search_query",validators=[validators.DataRequired()])
    job_filter = IntegerField("job_filter")
    sorting_criteria = IntegerField("sorting_criteria",validators=[validators.NumberRange(min=0, max=1),validators.optional()])
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
    creator_name = StringField("creator_name")
    creator_surname = StringField("creator_surname")   
    starting_date_start = DateTimeField("starting_date_start", default=None)
    starting_date_end = DateTimeField("starting_date_end", default=None)
    deadline_start = DateTimeField("deadline_start", default=None)
    deadline_end = DateTimeField("deadline_end", default=None)
    sorting_criteria = IntegerField("sorting_criteria",validators=[validators.NumberRange(min=0, max=5),validators.optional()], default=None)
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
ws_search_parser.add_argument('sorting_criteria',type=int,help="None: Semantic Rating, 0: Ascending Date, 1: Descending Date, 2: Ascending Number of Collaborators Needed, 3: Descending Number of Collaborators Needed, 4: Ascending Alphabetical Order, 5: Descending Alphabetical Order)",location='args')
ws_search_parser.add_argument('page',type=int,help="Page ID(0-Indexed)",location='args')
ws_search_parser.add_argument('per_page',type=int,help="Number of Records in a Page",location='args')
ws_search_parser.add_argument('auth_token', type=str,help="Authantication Token(If registered)",location='headers')

class UpcomingEventsSearchForm(Form):
    search_query = StringField("search_query",validators=[validators.DataRequired()])
    date_filter_start = DateTimeField("date_filter_start", default=None)
    date_filter_end = DateTimeField("date_filter_end", default=None)
    deadline_filter_start = DateTimeField("deadline_filter_start", default=None)
    deadline_filter_end = DateTimeField("deadline_filter_end", default=None)
    sorting_criteria = IntegerField("sorting_criteria",validators=[validators.NumberRange(min=0, max=1),validators.optional()])
    page = IntegerField("page")
    per_page = IntegerField("per_page")

upcoming_events_search_parser = reqparse.RequestParser()
upcoming_events_search_parser.add_argument('search_query',required=True,type=str,help="Search Query",location='args')
upcoming_events_search_parser.add_argument('date_filter_start',type=datetime.datetime,help="Inclusive.",location='args')
upcoming_events_search_parser.add_argument('date_filter_end',type=datetime.datetime,help="Exclusive.",location='args')
upcoming_events_search_parser.add_argument('deadline_filter_start',type=datetime.datetime,help="Inclusive.",location='args')
upcoming_events_search_parser.add_argument('deadline_filter_end',type=datetime.datetime,help="Exclusive.",location='args')
upcoming_events_search_parser.add_argument('sorting_criteria',type=int,help="None => Semantic Rating // 0 => Alphabetical Order(A=>Z) // 1 => Date Order",location='args')
upcoming_events_search_parser.add_argument('page',type=int,help="Page ID(0-Indexed)",location='args')
upcoming_events_search_parser.add_argument('per_page',type=int,help="Number of Records in a Page",location='args')
upcoming_events_search_parser.add_argument('auth_token', type=str,help="Authantication Token(If registered)",location='headers')

def validate_json(form, field):
	try:
		json.loads(field.data)
	except ValueError as e:
		raise ValidationError("This is not a valid JSON string.")

class TagSearchForm(Form):
    search_type = IntegerField("search_type",validators=[validators.NumberRange(min=0, max=1),validators.InputRequired()])
    skills = StringField("skills", validators=[validators.InputRequired(), validate_json])
    page = IntegerField("page")
    per_page = IntegerField("per_page")


tag_search_parser = reqparse.RequestParser()
tag_search_parser.add_argument('search_type',type=int,help="0 => User 1 => Worksapce",location='args')
tag_search_parser.add_argument("skills", required=False, type=str, help="The list of the skills required to be able to join the new workspace", location="args")
tag_search_parser.add_argument('page',type=int,help="Page ID(0-Indexed)",location='args')
tag_search_parser.add_argument('per_page',type=int,help="Number of Records in a Page",location='args')