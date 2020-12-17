from wtforms import Form, StringField, IntegerField, BooleanField, FileField, validators
from werkzeug.datastructures import FileStorage
from flask_restplus import reqparse

class FileGetForm(Form):
    workspace_id = IntegerField("workspace_id",validators=[validators.DataRequired()])
    path = StringField("path",validators=[validators.DataRequired()])
    filename = StringField("filename",validators=[validators.DataRequired()])

file_get_parser = reqparse.RequestParser()
file_get_parser.add_argument('workspace_id',required=True,type=int,help="Name of the File",location='args')
file_get_parser.add_argument('path',required=True,type=str,help="Path of the File",location='args')
file_get_parser.add_argument('filename',required=True,type=str,help="Name of the File",location='args')
file_get_parser.add_argument('auth_token', required=False ,type=str,help="Authantication Token(If registered)",location='headers')

class FileInfoForm(Form):
    workspace_id = IntegerField("workspace_id",validators=[validators.DataRequired()])
    path = StringField("path",validators=[validators.DataRequired()])
    filename = StringField("filename",validators=[validators.DataRequired()])

class FileForm(Form):
    new_file = FileField("new_file",validators=[validators.DataRequired()])

file_post_parser = reqparse.RequestParser()
file_post_parser.add_argument('workspace_id',required=True,type=int,help="Name of the File",location='form')
file_post_parser.add_argument('path',required=True,type=str,help="Path of the File",location='form')
file_post_parser.add_argument('filename',required=True,type=str,help="Name of the File",location='form')
file_post_parser.add_argument('new_file',required=True,type=FileStorage,help="Upload File Here",location='files')
file_post_parser.add_argument('auth_token', required=False ,type=str,help="Authantication Token(If registered)",location='headers')
    
file_delete_parser = reqparse.RequestParser()
file_delete_parser.add_argument('workspace_id',required=True,type=int,help="Name of the File",location='form')
file_delete_parser.add_argument('path',required=True,type=str,help="Path of the File",location='form')
file_delete_parser.add_argument('filename',required=True,type=str,help="Name of the File",location='form')
file_delete_parser.add_argument('auth_token', required=False ,type=str,help="Authantication Token(If registered)",location='headers')

class FolderInfo(Form):
    workspace_id = IntegerField("workspace_id",validators=[validators.DataRequired()])
    path = StringField("path",validators=[validators.DataRequired()])
    foldername = StringField("foldername",validators=[validators.DataRequired()])

folder_get_parser = reqparse.RequestParser()
folder_get_parser.add_argument('workspace_id',required=True,type=int,help="Name of the File",location='args')
folder_get_parser.add_argument('path',required=True,type=str,help="Path of the File",location='args')
folder_get_parser.add_argument('filename',required=True,type=str,help="Name of the File",location='args')
folder_get_parser.add_argument('auth_token', required=False ,type=str,help="Authantication Token(If registered)",location='headers')

