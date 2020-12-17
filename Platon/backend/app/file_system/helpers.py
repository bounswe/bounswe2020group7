import app

class FileSystem():

    @staticmethod
    def initialize_ws(workspace_id):
        """
            !!!! CALL THIS FUNCTION IN CREATE WORKSPACE !!!!
            Creates the main folder of the workspace
            workspace_id: workspace_id
        """
        path = app.config["WORKSPACE_FILE_PATH"]