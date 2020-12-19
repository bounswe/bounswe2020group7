from flask import make_response,jsonify,request
from flask_restplus import Resource
from functools import wraps

from app.auth_system.models import User
from app.follow_system.models import Follow, FollowRequests
from app.profile_management.models import Jobs
from app.auth_system.helpers import allowed_file

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
                requested_user = User.query.filter((User.id == int(requested_user_id))&(User.is_valid == True)).first()
            except:
                return make_response(jsonify({'error' : 'Database Connection Problem'}),500)
            if requested_user is None:
                return make_response(jsonify({'error': 'Requested user can not be found'}),404)
            if requested_user.is_private:
                try:
                    follow = Follow.query.filter((Follow.follower_id == user_id)&(Follow.following_id == int(requested_user.id))).first()
                except:
                    return make_response(jsonify({'error' : 'Database Connection Problem'}),500)
                if follow is None:
                    response = {
                        'error' : 'The Account that you try to reach is private'
                    }
                    return make_response(jsonify(response),403)
            return func(*args,**kwargs)
        return follow_check
    return follow_required_inner

def follow_required_user(param_loc,requested_user_id_key):
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
                requested_user = User.query.filter((User.id == int(requested_user_id))&(User.is_valid == True)).first()
            except:
                return make_response(jsonify({'error' : 'Database Connection Problem'}),500)
            if requested_user is None:
                return make_response(jsonify({'error': 'Requested user can not be found'}),404)
            if requested_user.is_private:
                try:
                    follow = Follow.query.filter((Follow.follower_id == user_id)&(Follow.following_id == int(requested_user.id))).first()
                except:
                    return make_response(jsonify({'error' : 'Database Connection Problem'}),500)
                if follow is None:
                    try:
                        job = Jobs.query.filter(Jobs.id==requested_user.job_id).first()
                    except:
                        return make_response(jsonify({'error' : 'Database Connection Problem'}),500)

                    try:
                        following_status = -1 # initialized to -1, does not matter.
                        # Checks if the existing user sent the follow request previously.
                        if FollowRequests.query.filter_by(follower_id=user_id, following_id=requested_user_id).first() is not None:
                            following_status = 0 # Represents that the existing user has already sent a follow request to the requested user and it is pending.
                        else:
                            following_status = -1 # Represents that the existing user does not follow the requested user and has not yet sent a request to follow them.
                    except:
                        return make_response(jsonify({'error' : 'Database Connection Problem'}),500)
                    profile_photo = ''
                    if allowed_file(requested_user.profile_photo):
                        profile_photo = "/auth_system/profile_photo?user_id={}".format(requested_user.id)
                    response = {
                        'id': requested_user.id,
                        'name': requested_user.name,
                        'surname': requested_user.surname,
                        'profile_photo': profile_photo,
                        'job':job.name,
                        'following_status': following_status,
                        'institute': requested_user.institution,
                        'is_private': requested_user.is_private,
                        'error' : 'The Account that you try to reach is private'
                    }
                    return make_response(jsonify(response),206)
            return func(*args,**kwargs)
        return follow_check
    return follow_required_inner