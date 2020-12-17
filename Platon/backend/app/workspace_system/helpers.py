from flask import make_response,jsonify,request

from app.workspace_system.models import Workspace, Contributions

from functools import wraps

def workspace_exists(param_loc,workspace_id_key):
    """
        param_loc: it only can be "args" or "form" which specifies the location of the following id parameter in a request
        workspace_id_key: key of the parameter that represents the requested wokspace id in the request
    """
    def workspace_exists_inner(func):
        """
            Checks the privacy of the user before giving the user data
        """
        @wraps(func)
        
        def workspace_check(*args,**kwargs):
            if param_loc == 'args':
                try:
                    requested_user_id = request.args.get(workspace_id_key)
                except:
                    return make_response(jsonify({'error':'Wrong input format'}),400) 
            elif param_loc == 'form':
                try:
                    requested_user_id = request.form.get(workspace_id_key)
                except:
                    return make_response(jsonify({'error':'Wrong input format'}),400)
            try:
                workspace = Workspace.query.filter((Workspace.id == int(requested_user_id))).first()
            except:
                return make_response(jsonify({'error' : 'Database Connection Problem'}),500)
            if workspace is None:
                return make_response(jsonify({'error': 'Requested Workspace can not be found'}),404)
            return func(*args,**kwargs)
        return workspace_check
    return workspace_exists_inner