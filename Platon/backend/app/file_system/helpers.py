from flask import current_app as app
import os
import shutil

class FileSystem():

    @staticmethod
    def is_path_forbidden(workspace_path,requested_path):
        return os.path.abspath(workspace_path) not in os.path.abspath(requested_path)

    @staticmethod
    def initialize_ws(workspace_id):
        """
            !!!! CALL THIS FUNCTION IN CREATE WORKSPACE !!!!
            Creates the main folder of the workspace
            workspace_id: workspace_id
        """
        path = FileSystem.workspace_base_path(workspace_id)
        # Create new Directory if It does not exists
        if not FileSystem.is_directory_exists(path):
            os.makedirs(path)
        else:
            FileSystem.delete_all_content(path)
            os.makedirs(path)

    @staticmethod
    def delete_all_content(path):
        """
            Deletes all flders and files under the given directory including given one
        """
        shutil.rmtree(path)
    
    @staticmethod
    def delete_ws_files(workspace_id):
        """
            Deletes all contents of a workspace
        """
        path = FileSystem.workspace_base_path(workspace_id)
        FileSystem.delete_all_content(path)

    @staticmethod
    def is_directory_exists(path):
        """
            Returns a directory exists or not
        """
        return os.path.isdir(path)
    
    @staticmethod
    def is_file_exists(file_path):
        """
            Returns a file exists or not
        """
        return os.path.isfile(file_path)
    
    @staticmethod
    def workspace_base_path(workspace_id):
        """
            Returns the base directory of a workspace
        """
        return app.config["WORKSPACE_FILE_PATH"] + os.path.sep + str(workspace_id)