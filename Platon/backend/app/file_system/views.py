from flask import make_response,jsonify,request
from flask_restplus import Resource,Namespace, fields
from flask import send_from_directory

from app.file_system.forms import FileInfoForm, FileForm, file_post_parser, FileGetForm, file_get_parser,file_delete_parser
from app.file_system.forms import FolderInfoForm, folder_get_parser, FolderPostPutForm, folder_post_put_parser, folder_delete_parser
from app.file_system.helpers import FileSystem
from app.workspace_system.helpers import workspace_exists,active_contribution_required, visibility_required
from app.auth_system.helpers import login_required
from app import api, db
import os
import pathlib
import app

file_system_ns = Namespace("File System",
                                description="File System Endpoints",
                                path = "/file_system")


folder_models = api.model("Folder Model",{
    "files" : fields.List(fields.String),
    "folders" : fields.List(fields.String),
    "cwd" : fields.String
})

@file_system_ns.route("/file")
class FileSystemAPI(Resource):


    @api.doc(responses={
                200: "Valid Data",
                400: "Invalid Input",
                401: "Authantication Problem",
                404: "User if not found",
                403: "Forbidden Path"
    })
    @api.expect(file_get_parser)
    @login_required
    @workspace_exists(param_loc='args',workspace_id_key='workspace_id')
    @visibility_required(param_loc='args',workspace_id_key='workspace_id')
    def get(user_id,self):
        form = FileGetForm(request.args)
        if form.validate():
            ws_path = FileSystem.workspace_base_path(form.workspace_id.data)
            path = ws_path + os.path.sep + form.path.data
            if FileSystem.is_path_forbidden(ws_path,path):
                return make_response(jsonify({"err":"It is forbidden to reach this path"}),403)
            file_path = path + os.path.sep + form.filename.data
            if not FileSystem.is_file_exists(file_path):
                return make_response(jsonify({"err":"There is no such file"}),400) 
            return send_from_directory(path,form.filename.data,as_attachment=True,cache_timeout=10)
        else:
            return make_response(jsonify({"err":"Invalid Input"}),400)

    @api.doc(responses={
                201: "File Successfully Uploaded",
                400: "Invalid Input",
                401: "Authantication Problem",
                404: "User if not found",
                403: "Forbidden Path"
    })
    @api.expect(file_post_parser)
    @login_required
    @workspace_exists(param_loc='form',workspace_id_key='workspace_id')
    @active_contribution_required(param_loc='form',workspace_id_key='workspace_id')
    def post(user_id,self):
        form = FileInfoForm(request.form)
        file_form = FileForm(request.files)
        if file_form.validate() and form.validate():
            ws_path = FileSystem.workspace_base_path(form.workspace_id.data)
            new_file = file_form.new_file.data
            if form.filename.data == '':
                return make_response(jsonify({"err":"Give Appropriate Filename"}),400)
            path = ws_path + os.path.sep + form.path.data
            # Control File Path
            if not FileSystem.is_directory_exists(path):
                return make_response(jsonify({"err":"Give Appropriate Path"}),400)
            if FileSystem.is_path_forbidden(ws_path,path):
                return make_response(jsonify({"err":"It is forbidden to reach this path"}),403)
            file_path = path + os.path.sep + form.filename.data
            # Control File Name is Unique or not
            if FileSystem.is_file_exists(file_path):
                return make_response(jsonify({"err":"Give Appropriate Filename"}),400)
            # Save file to the given path
            new_file.save(file_path)
            return make_response(jsonify({"msg":"Your File is successfully uploaded"}),201)
        else:
            return make_response(jsonify({"err":"Invalid Input"}),400)
    
    @api.doc(responses={
                200: "File Name Successfully Changed",
                400: "Invalid Input",
                401: "Authantication Problem",
                404: "User if not found",
                403: "Forbidden Path"
    })
    @api.expect(file_post_parser)
    @login_required
    @workspace_exists(param_loc='form',workspace_id_key='workspace_id')
    @active_contribution_required(param_loc='form',workspace_id_key='workspace_id')    
    def put(user_id,self):
        form = FileInfoForm(request.form)
        file_form = FileForm(request.files)
        if file_form.validate() and form.validate():
            new_file = file_form.new_file.data
            ws_path = FileSystem.workspace_base_path(form.workspace_id.data)
            # Control given path exists or not
            path = ws_path + os.path.sep + form.path.data
            if not FileSystem.is_directory_exists(path):
                return make_response(jsonify({"err":"Give Appropriate Path"}),400)
            if FileSystem.is_path_forbidden(ws_path,path):
                return make_response(jsonify({"err":"It is forbidden to reach this path"}),403)
            # Control given file exists or not
            file_path = path + os.path.sep + form.filename.data
            if not FileSystem.is_file_exists(file_path):
                return make_response(jsonify({"err":"Give Appropriate Filename"}),400)
            # Update the given file
            os.remove(file_path)
            new_file.save(file_path)
            return make_response(jsonify({"msg":"Your File is successfully changed"}),200)
        else:
            return make_response(jsonify({"err":"Invalid Input"}),400)
    
    @api.doc(responses={
                200: "File Successfully Deleted",
                400: "Invalid Input",
                401: "Authantication Problem",
                404: "User if not found",
                403: "Forbidden Path"
    })
    @api.expect(file_delete_parser)
    @login_required
    @workspace_exists(param_loc='form',workspace_id_key='workspace_id')
    @active_contribution_required(param_loc='form',workspace_id_key='workspace_id')
    def delete(user_id,self):
        form = FileInfoForm(request.form)
        if form.validate():
            ws_path = FileSystem.workspace_base_path(form.workspace_id.data)
            # Control given path exists or not
            path = ws_path + os.path.sep + form.path.data
            if not os.path.exists(path):
                return make_response(jsonify({"err":"Give Appropriate Path"}),400)
            if FileSystem.is_path_forbidden(ws_path,path):
                return make_response(jsonify({"err":"It is forbidden to reach this path"}),403)
            # Control given file exists or not
            file_path = path + os.path.sep + form.filename.data
            if not os.path.isfile(file_path):
                return make_response(jsonify({"err":"Give Appropriate Filename"}),400)
            # Delete file
            os.remove(file_path) 
            return make_response(jsonify({"err":"Your File is successfully deleted"}),200)
        else:
            return make_response(jsonify({"err":"Invalid Input"}),400)

@file_system_ns.route("/folder")
class FolderSystemAPI(Resource):

    @api.doc(responses={
                400: "Invalid Input",
                401: "Authantication Problem",
                404: "User if not found",
                403: "Forbidden Path"
    })
    @api.response(200, 'Valid Folder', folder_models)
    @api.expect(folder_get_parser)
    @login_required
    @workspace_exists(param_loc='args',workspace_id_key='workspace_id')
    @visibility_required(param_loc='args',workspace_id_key='workspace_id')
    def get(user_id,self):
        form = FolderInfoForm(request.args)
        if form.validate():
            ws_path = FileSystem.workspace_base_path(form.workspace_id.data)
            folder_path = ws_path + os.path.sep + form.path.data
            # Control given path exits or not
            if not FileSystem.is_directory_exists(folder_path):
                return make_response(jsonify({"err":"Give Appropriate Path"}),400)
            if FileSystem.is_path_forbidden(ws_path,folder_path):
                return make_response(jsonify({"err":"It is forbidden to reach this path"}),403)
            file_list = []
            folder_list = []
            # Find list of files and foldes in the given path
            for element in os.listdir(folder_path):
                if FileSystem.is_file_exists(folder_path + os.path.sep + element):
                    file_list.append(element)
                elif FileSystem.is_directory_exists(folder_path + os.path.sep + element):
                    folder_list.append(element)
            return make_response(jsonify({"files":file_list,"folders":folder_list,"cwd":form.path.data}),200)            
        else:
            return make_response(jsonify({"err":"Invalid Input"}),400)

    @api.doc(responses={
                201: "Folder Successfully Created",
                400: "Invalid Input",
                401: "Authantication Problem",
                404: "User if not found",
                403: "Forbidden Path"
    })
    @api.expect(folder_post_put_parser)
    @login_required
    @workspace_exists(param_loc='form',workspace_id_key='workspace_id')
    @active_contribution_required(param_loc='form',workspace_id_key='workspace_id')
    def post(user_id,self):
        form = FolderPostPutForm(request.form)
        if form.validate():
            ws_path = FileSystem.workspace_base_path(form.workspace_id.data)
            # Control given path exists or not
            path = ws_path + os.path.sep + form.path.data
            if not FileSystem.is_directory_exists(path):
                return make_response(jsonify({"err":"Give Appropriate Path"}),400)
            if FileSystem.is_path_forbidden(ws_path,path):
                return make_response(jsonify({"err":"It is forbidden to reach this path"}),403)
            # Control new folder name is valid or not
            new_folder_path = path + os.path.sep + form.new_folder_name.data
            if FileSystem.is_directory_exists(new_folder_path):
                return make_response(jsonify({"err":"Given Folder Exists"}),400)
            # Create folder
            try:
                os.mkdir(new_folder_path)
            except:
                return make_response(jsonify({"err":"Recursive directories can not be created"}),400)

            return make_response(jsonify({"msg":"Folder is successfully created"}),201)
        else:
            return make_response(jsonify({"err":"Invalid Input"}),400)

    @api.doc(responses={
                200: "Folder Name Successfully Changed",
                400: "Invalid Input",
                401: "Authantication Problem",
                404: "User if not found",
                403: "Forbidden Path"
    })
    @api.expect(folder_post_put_parser)
    @login_required
    @workspace_exists(param_loc='form',workspace_id_key='workspace_id')
    @active_contribution_required(param_loc='form',workspace_id_key='workspace_id')
    def put(user_id,self):
        form = FolderPostPutForm(request.form)
        if form.validate():
            ws_path = FileSystem.workspace_base_path(form.workspace_id.data)
            # Control given path exists or not
            path = ws_path + os.path.sep + form.path.data
            if os.path.abspath(ws_path) == os.path.abspath(path):
                return make_response(jsonify({"err":"It is forbidden to change the name of main folder"}),403)
            if not FileSystem.is_directory_exists(path):
                return make_response(jsonify({"err":"Give Appropriate Path"}),400)
            if FileSystem.is_path_forbidden(ws_path,path):
                return make_response(jsonify({"err":"It is forbidden to reach this path"}),403)
            # Calculate new path of the folder
            new_folder_path = path.split(os.path.sep)
            new_folder_path[-1] = form.new_folder_name.data
            new_folder_path = os.path.sep.join(new_folder_path)
            if FileSystem.is_directory_exists(new_folder_path):
                return make_response(jsonify({"err":"Give Appropriate New File Name"}),400)
            # Rename folder
            os.rename(path,new_folder_path)
            return make_response(jsonify({"msg":"Folder Name is successfully changed"}),200)

        else:
            return make_response(jsonify({"err":"Invalid Input"}),400)

    @api.doc(responses={
                200: "Folder Successfully Deleted",
                400: "Invalid Input",
                401: "Authantication Problem",
                404: "User if not found",
                403: "Forbidden Path"
    })
    @api.expect(folder_delete_parser)
    @login_required
    @workspace_exists(param_loc='form',workspace_id_key='workspace_id')
    @active_contribution_required(param_loc='form',workspace_id_key='workspace_id')
    def delete(user_id,self):
        form = FolderInfoForm(request.form)
        if form.validate():
            ws_path = FileSystem.workspace_base_path(form.workspace_id.data)
            if form.path.data == ".":
                return make_response(jsonify({"err":"Main Folder can not be deleted"}),400)
            # Control given path exists or not
            folder_path = ws_path + os.path.sep + form.path.data
            if FileSystem.is_path_forbidden(ws_path,folder_path):
                return make_response(jsonify({"err":"It is forbidden to reach this path"}),403)
            if not FileSystem.is_directory_exists(folder_path):
                return make_response(jsonify({"err":"Folder does not exist"}),400)
            # Delete all contents of the folder
            FileSystem.delete_all_content(folder_path)
            return make_response(jsonify({"msg":"Folder is successfully deleted"}),200)
        else:
            return make_response(jsonify({"err":"Invalid Input"}),400)

def register_resources(api):
    api.add_namespace(file_system_ns) 