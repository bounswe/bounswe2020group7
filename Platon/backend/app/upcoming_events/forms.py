from wtforms import Form, StringField, IntegerField, BooleanField, validators
from flask_restplus import reqparse

class UpcomingEventsForm(Form):
    page = IntegerField("page", validators=[validators.InputRequired()])
    per_page = IntegerField("per_page", validators=[validators.DataRequired()])

upcoming_event_parser = reqparse.RequestParser()
upcoming_event_parser.add_argument('page',type=int,help="Page ID(0-Indexed)",location='args')
upcoming_event_parser.add_argument('per_page',type=int,help="Number of Records in a Page",location='args')