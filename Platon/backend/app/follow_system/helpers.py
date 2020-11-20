from flask import make_response,jsonify,request
from flask_restplus import Resource
from functools import wraps

from app.auth_system.models import User
from app.follow_system.models import Follow

def follow_required(param_loc,requested_user_id_key):
    """
        param_loc: it only can be "args" or "form" which specifies the location of the following id parameter in a request
        requested_user_id_key: key of the parameter that represents the requested user id in the request
    """
    def follow_required_inner(func):
        """
            Checks the privacy of the user before giving the user data
        """
        @wraps(func)
        
        def follow_check(*args,**kwargs):
            if param_loc == 'args':
                try:
                    requested_user_id = request.args.get(requested_user_id_key)
                except:
                    return make_response(jsonify({'error':'Wrong input format'}),400) 
            elif param_loc == 'form':
                try:
                    requested_user_id = request.form.get(requested_user_id_key)
                except:
                    return make_response(jsonify({'error':'Wrong input format'}),400)
            user_id = args[0]
            if int(requested_user_id) == user_id:
                return func(*args,**kwargs)
            try:
                requested_user = User.query.filter((User.id == requested_user_id)&(User.is_valid == True)).first()
            except:
                return make_response(jsonify({'error' : 'Database Connection Problem'}),500)
            if requested_user is None:
                return make_response(jsonify({'error': 'Requested user can not be found'}),404)
            if not requested_user.is_private:
                try:
                    follow = Follow.query.filter((Follow.follower_id == user_id)&(Follow.following_id == requested_user.id)).first()
                except:
                    return make_response(jsonify({'error' : 'Database Connection Problem'}),500)
                if follow is None:
                    return make_response(jsonify({'error' : 'The Account that you try to reach is private'}),403)
            return func(*args,**kwargs)
        return follow_check
    return follow_required_inner