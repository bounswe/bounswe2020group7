from wtforms import Form, StringField, IntegerField, FieldList, validators, DateTimeField, BooleanField, ValidationError
import datetime, json
from flask_restplus import reqparse

class GetActivityStreamForm(Form):
    page = IntegerField("page", validators=[validators.InputRequired()])
    per_page = IntegerField("per_page", validators=[validators.DataRequired()])

get_activity_stream_parser = reqparse.RequestParser()
get_activity_stream_parser.add_argument('page', required=True, type=int, help="Page index that you want(Starts from 0)", location='args')
get_activity_stream_parser.add_argument('per_page', required=True, type=int, help="Number of items in a page", location='args')
get_activity_stream_parser.add_argument('auth_token',required=True, type=str, help="Authentication Token", location='headers')