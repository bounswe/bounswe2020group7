from flask import make_response,jsonify,request

from app.workspace_system.models import Workspace, Contribution
from functools import wraps

def workspace_exists(param_loc,workspace_id_key):
    """
        param_loc: it only can be "args" or "form" which specifies the location of the requested wokspace id in the request
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
                    workspace_id = int(request.args.get(workspace_id_key))
                except:
                    return make_response(jsonify({'error':'Wrong input format'}),400) 
            elif param_loc == 'form':
                try:
                    workspace_id = int(request.form.get(workspace_id_key))
                except:
                    return make_response(jsonify({'error':'Wrong input format'}),400)
            try:
                workspace = Workspace.query.filter((Workspace.id == workspace_id)).first()
            except:
                return make_response(jsonify({'error' : 'Database Connection Problem'}),500)
            if workspace is None:
                return make_response(jsonify({'error': 'Requested Workspace can not be found'}),404)
            return func(*args,**kwargs)
        return workspace_check
    return workspace_exists_inner

def active_contribution_required(param_loc,workspace_id_key):
    """
        param_loc: it only can be "args" or "form" which specifies the location of the requested wokspace id in the request
        workspace_id_key: key of the parameter that represents the requested wokspace id in the request
    """
    def active_contribution_required_inner(func):
        """
            Checks the privacy of the user before giving the user data
        """
        @wraps(func)
        def contribution_check(*args,**kwargs):
            if param_loc == 'args':
                try:
                    workspace_id = int(request.args.get(workspace_id_key))
                except:
                    return make_response(jsonify({'error':'Wrong input format'}),400) 
            elif param_loc == 'form':
                try:
                    workspace_id = int(request.form.get(workspace_id_key))
                except:
                    return make_response(jsonify({'error':'Wrong input format'}),400)
            user_id = args[0]
            try:
                contribution = Contribution.query.filter((Contribution.workspace_id == workspace_id) & (Contribution.user_id == user_id)).first()
            except:
                return make_response(jsonify({'error' : 'Database Connection Problem'}),500)
            if contribution is None:
                return make_response(jsonify({'error': 'You have to contribute the Workspace'}),404)
            if int(contribution.is_active) != 1:
                return make_response(jsonify({'error': 'You have to contribute the Workspace actively'}),404)
            return func(*args,**kwargs)
        return contribution_check
    return active_contribution_required_inner