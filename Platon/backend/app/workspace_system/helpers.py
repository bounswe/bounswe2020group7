from flask import make_response,jsonify,request

from app.workspace_system.models import Workspace, Contribution

import atexit
from apscheduler.schedulers.background import BackgroundScheduler
from functools import wraps

from app import app,db

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

class TredingProjectManager():
    """
        It is used to calculate nex tranding score
        trending_score[t+1] = trnding_score[t]*aging_factor + view_count[t]    
        It must be between 0 and 1
    """
    aging_factor = 0.8

    @staticmethod
    def update_trending_point(workspace):
        """
            Updates the trending score according to the aging factor
            `workspace`: Workspace instance
        """
        prev_score = workspace.trending_score
        prev_count = workspace.view_count
        workspace.trending_score = prev_score * TredingProjectManager.aging_factor + prev_count
        workspace.view_count = 0

    @staticmethod
    def update_all_trending_points():
        with app.app_context():
            try:
               all_workspaces = Workspace.query.all()
            except:
                return
            for workspace in all_workspaces:
                TredingProjectManager.update_trending_point(workspace)
            db.session.commit()

def schedule_regularly():
    scheduler = BackgroundScheduler()
    scheduler.add_job(func=TredingProjectManager.update_all_trending_points, trigger="interval",seconds=60*60)
    scheduler.start()
    # Shut down the scheduler when exiting the app
    atexit.register(lambda: scheduler.shutdown())    