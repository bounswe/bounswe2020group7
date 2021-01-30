from wtforms import Form, IntegerField, StringField, validators
from flask_restplus import reqparse

class UserForm(Form):
    number_of_recommendations = IntegerField("number_of_recommendations",validators=[validators.DataRequired()])

recommendation_parser = reqparse.RequestParser()
recommendation_parser.add_argument('number_of_recommendations',required=True,type=int,help="Page index that you want(Starts from 0)",location='args')
recommendation_parser.add_argument('auth_token',required=True, type=str,help="Token",location='headers')

class WorkspaceForm(Form):
    workspace_id = IntegerField("workspace_id",validators=[validators.DataRequired()])
    number_of_recommendations = IntegerField("number_of_recommendations",validators=[validators.DataRequired()])

recommendation_ws_parser = reqparse.RequestParser()
recommendation_ws_parser.add_argument('workspace_id',required=True,type=int,help="Worksapce ID",location='args')
recommendation_ws_parser.add_argument('number_of_recommendations',required=True,type=int,help="Page index that you want(Starts from 0)",location='args')
recommendation_ws_parser.add_argument('auth_token',required=True, type=str,help="Token",location='headers')