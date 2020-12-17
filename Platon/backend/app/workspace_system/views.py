from flask import make_response,jsonify,request
from flask_restplus import Resource,Namespace, fields
from flask import send_from_directory

from app.workspace_system.forms import FileInfoForm, FileForm, file_post_parser, FileGetForm, file_get_parser,file_delete_parser
from app.workspace_system.models import Workspace 
from app import api, db
import os
import pathlib
import app

workspace_system_ns = Namespace("Workspace System",
                                description="Workspace System Endpoints",
                                path = "/workspace_system")

@workspace_system_ns.route("/file")
class FileSystem(Resource):

    @api.expect(file_get_parser)
    def get(self):
        form = FileGetForm(request.args)
        if form.validate():
            ws_path = app.config["WORKSPACE_FILE_PATH"] + os.path.sep + str(form.workspace_id.data)
            path = ws_path + os.path.sep + form.path.data
            file_path = path + os.path.sep + form.filename.data
            if not os.path.isfile(file_path):
                return make_response(jsonify({"err":"There is no such file"}),400) 
            return send_from_directory(path,form.filename.data)
        else:
            return make_response(jsonify({"err":"Invalid Input"}),400)

    @api.expect(file_post_parser)
    def post(self):
        form = FileInfoForm(request.form)
        file_form = FileForm(request.files)
        if file_form.validate() and form.validate():
            # Control if this is the First File of the Workspace
            try:
                workspace = Workspace.query.filter(Workspace.id == form.workspace_id.data).first()
            except:
                return make_response(jsonify({"err":"Database Connection Error"}),500)
            if workspace is None:
                return make_response(jsonify({"err":"Give existing Workspace ID"}),500)
            ws_path = app.config["WORKSPACE_FILE_PATH"] + os.path.sep + str(form.workspace_id.data)
            try:
                workspace.has_any_file = True
                db.session.update(workspace)
                db.session.commit()
                # Create the Folder of the Workspace
                if int(workspace.has_any_file) == 0:
                    os.makedirs(ws_path)
            except:
                return make_response(jsonify({"err":"Database Connection Error"}),500)

            new_file = file_form.new_file.data
            if form.filename.data == '':
                return make_response(jsonify({"err":"Give Appropriate Filename"}),400)
            path = ws_path + os.path.sep + form.path.data
            if not os.path.exists(path):
                return make_response(jsonify({"err":"Give Appropriate Path"}),400)
            
            file_path = path + os.path.sep + form.filename.data
            if os.path.isfile(file_path):
                return make_response(jsonify({"err":"Give Appropriate Filename"}),400)

            new_file.save(file_path)
            return make_response(jsonify({"err":"Your File is successfully uploaded"}),200)
        else:
            return make_response(jsonify({"err":"Invalid Input"}),400)
    
    @api.expect(file_post_parser)
    def put(self):
        form = FileInfoForm(request.form)
        file_form = FileForm(request.files)
        if file_form.validate() and form.validate():
            # Control if this is the First File of the Workspace
            try:
                workspace = Workspace.query.filter(Workspace.id == form.workspace_id.data).first()
            except:
                return make_response(jsonify({"err":"Database Connection Error"}),500)
            if workspace is None:
                return make_response(jsonify({"err":"Give existing Workspace ID"}),500)
            ws_path = app.config["WORKSPACE_FILE_PATH"] + os.path.sep + str(form.workspace_id.data)
            try:
                workspace.has_any_file = True
                db.session.update(workspace)
                db.session.commit()
                # Create the Folder of the Workspace
                if int(workspace.has_any_file) == 0:
                    return make_response(jsonify({"err":"Workspace does not contain any File"}),500)
            except:
                return make_response(jsonify({"err":"Database Connection Error"}),500)

            new_file = file_form.new_file.data

            path = ws_path + os.path.sep + form.path.data
            if not os.path.exists(path):
                return make_response(jsonify({"err":"Give Appropriate Path"}),400)
            
            file_path = path + os.path.sep + form.filename.data
            if not os.path.isfile(file_path):
                return make_response(jsonify({"err":"Give Appropriate Filename"}),400)
            
            os.remove(file_path)
            new_file.save(file_path)
            return make_response(jsonify({"err":"Your File is successfully changed"}),200)
        else:
            return make_response(jsonify({"err":"Invalid Input"}),400)
    
    @api.expect(file_delete_parser)
    def delete(self):
        form = FileInfoForm(request.form)
        if form.validate():
            # Control if this is the First File of the Workspace
            try:
                workspace = Workspace.query.filter(Workspace.id == form.workspace_id.data).first()
            except:
                return make_response(jsonify({"err":"Database Connection Error"}),500)
            if workspace is None:
                return make_response(jsonify({"err":"Give existing Workspace ID"}),500)
            ws_path = app.config["WORKSPACE_FILE_PATH"] + os.path.sep + str(form.workspace_id.data)
            try:
                workspace.has_any_file = True
                db.session.update(workspace)
                db.session.commit()
                # Create the Folder of the Workspace
                if int(workspace.has_any_file) == 0:
                    return make_response(jsonify({"err":"Workspace does not contain any File"}),500)
            except:
                return make_response(jsonify({"err":"Database Connection Error"}),500)

            path = ws_path + os.path.sep + form.path.data
            if not os.path.exists(path):
                return make_response(jsonify({"err":"Give Appropriate Path"}),400)
            
            file_path = path + os.path.sep + form.filename.data
            if not os.path.isfile(file_path):
                return make_response(jsonify({"err":"Give Appropriate Filename"}),400)
            
            os.remove(file_path) 
            return make_response(jsonify({"err":"Your File is successfully deleted"}),200)
 
        else:
            return make_response(jsonify({"err":"Invalid Input"}),400)

@workspace_system_ns.route("/folder")
class FolderSystem(Resource):

    def get(self):
        pass

            
def register_resources(api):
    api.add_namespace(workspace_system_ns) 