from wtforms import Form, StringField, IntegerField, BooleanField, validators
from app import api
from flask_restplus import reqparse

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
user_search_parser.add_argument('auth_token',required=True, type=str,help="Authantication Token",location='headers')
