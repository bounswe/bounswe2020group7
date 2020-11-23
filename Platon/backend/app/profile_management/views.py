from flask import make_response,jsonify,request
from flask_restplus import Resource,Namespace

from app.auth_system.helpers import login_required
from app.auth_system.models import User
from app.follow_system.helpers import follow_required
from app.follow_system.models import Follow
from app.profile_management.forms import ResearchInfoGetForm,research_info_get_parser, ResearchInfoPostForm,research_info_post_parser
from app.profile_management.forms import ResearchInfoUpdateForm, research_info_update_parser,ResearchInfoDeleteFrom,research_info_delete_parser
from app.profile_management.forms import notification_get_parser,NotificationDeleteForm,notification_delete_parser
from app.profile_management.models import ResearchInformation,Notification,NotificationRelatedUser
from app.profile_management.helpers import schedule_regularly
from app import api, db

profile_management_ns = Namespace("Profile Management",
                                  description="Profile Management Endpoints",
                                  path = "/profile")

@profile_management_ns.route("/research_information")
class ResearchInformationAPI(Resource):
    """
        Research Information Functionality is implemented in this class
    """
    @api.doc(responses={200:'Valid Response',400:'Wrong Input Format',401:'Authantication Problem', 403 : 'Private Account Problem',500:'Database Connection Problem'})
    @api.expect(research_info_get_parser)
    @login_required
    @follow_required(param_loc = 'args', requested_user_id_key='user_id')
    def get(user_id,self):
        """
            Returns all research information of a user
        """
        form = ResearchInfoGetForm(request.args)
        if form.validate():
            try:
                all_research_info = ResearchInformation.query.filter(ResearchInformation.user_id == form.user_id.data).all()
            except:
                return make_response(jsonify({'error' : 'Database Connection Problem'}),500)
            return make_response(jsonify({'research_info' :[{'id': info.id,'title': info.research_title, 'description': info.description, 'year': info.year}for info in all_research_info]}),200)
        else:
            return make_response(jsonify({'error':'Wrong input format'}),400)
    
    @api.doc(responses={201:'Successfully Created',400:'Wrong Input Format',401:'Authantication Problem',500:'Database Connection Problem'})
    @api.expect(research_info_post_parser)
    @login_required
    def post(user_id,self):
        """
            Adds new Research Information
        """
        form = ResearchInfoPostForm(request.form)
        if form.validate():
            new_research_info = ResearchInformation(user_id,form.research_title.data,form.description.data,form.year.data,int(ResearchType.HAND_WRITTEN))
            try:
                db.session.add(new_research_info)
                db.session.commit()
            except:
                return make_response(jsonify({'error' : 'Database Connection Problem'}),500)
            return make_response(jsonify({'msg' : 'Successfully added'}),201)
        else:
            return make_response(jsonify({'error':'Wrong input format'}),400)

    @api.doc(responses={201:'Successfully Updated',400:'Wrong Input Format',401:'Authantication Problem',500:'Database Connection Problem'})
    @api.expect(research_info_update_parser)
    @login_required
    def put(user_id,self):
        """
            Updates a Research Information
        """
        form = ResearchInfoUpdateForm(request.form)
        if form.validate():
            try:
                research_info = ResearchInformation.query.filter((ResearchInformation.id == form.research_id.data) & (ResearchInformation.user_id == user_id))
                if research_info is None:
                    return make_response(jsonify({'error':'Please give an appropriate research ID'}),400)
                research_info.research_title = form.research_title.data if form.research_title.data != '' else research_info.research_title
                research_info.description = form.description.data if form.description.data != '' else research_info.description
                research_info.year = form.year.data if form.year.data !=  -1 else research_info.year
                db.session.commit()
            except:
                return make_response(jsonify({'error' : 'Database Connection Problem'}),500)
            return make_response(jsonify({'msg' : ' Successfully changed'}),201)

        else:
            return make_response(jsonify({'error':'Wrong input format'}),400)

    @api.doc(responses={200:'Successfully Deleted',400:'Wrong Input Format',401:'Authantication Problem',500:'Database Connection Problem'})
    @api.expect(research_info_delete_parser)
    @login_required
    def delete(user_id,self):
        """
            Deletes Research Information
        """
        form = ResearchInfoDeleteFrom(request.form)
        if form.validate():
            try:
                research_info = ResearchInformation.query.filter((ResearchInformation.id == form.research_id.data) & (ResearchInformation.user_id == user_id)).first()
                if research_info is None:
                    return make_response(jsonify({'error':'You can not delete other user\'s information'}),400)
                db.session.delete(research_info)
                db.session.commit()
                return make_response(jsonify({'msg' : 'Successfully Deleted'}),200)
            except:
                return make_response(jsonify({'error' : 'Database Connection Problem'}),500)
        else:
            return make_response(jsonify({'error':'Wrong input format'}),400)

@profile_management_ns.route("/notifications")
class NotificationAPI(Resource):
    
    @api.doc(responses={200:'Valid Response',401:'Authantication Problem',500:'Database Connection Problem'})
    @api.expect(notification_get_parser)
    @login_required
    def get(user_id,self):
        """
            Returns all notifications of the logged in user with related user lists
        """
        try:
            all_notifications = Notification.query.filter(Notification.owner_id == user_id).all()
        except:
            return make_response(jsonify({'error' : 'Database Connection Problem'}),500)

        try:
            related_users = {}
            for notification in all_notifications:
                related_users[notification.id] =  NotificationRelatedUser.query.filter(NotificationRelatedUser.notification_id == notification.id).all()
        except:
            return make_response(jsonify({'error' : 'Database Connection Problem'}),500)

        response = [{'id': notification.id, 'text': notification.text, 'link': notification.link,'timestamp' : notification.timestamp ,'related_users': [ user.related_user_id for user in related_users[notification.id]]} for notification in all_notifications]
        return make_response(jsonify(response),200)

    @api.doc(responses={200:'Successfully Deleted',400:'Wrong Input Format',401:'Authantication Problem',404:'Notification Not Found',500:'Database Connection Problem'})
    @api.expect(notification_delete_parser)
    @login_required
    def delete(user_id,self):
        """
            Deletes the given notification
        """
        form = NotificationDeleteForm(request.form)
        if form.validate():
            try:
                notification = Notification.query.filter((Notification.owner_id == user_id)&(Notification.id == form.notification_id.data)).first()
            except:
                return make_response(jsonify({'error' : 'Database Connection Problem'}),500)
            if notification is None:
                return make_response(jsonify({'error':'Notification Not Found'}),404)
            # Because of te Foreign Key Other Related records also deleted from the DB.
            try:
                db.session.delete(notification)
                db.session.commit()
            except:
                return make_response(jsonify({'error' : 'Database Connection Problem'}),500)
            return make_response(jsonify({'msg' : 'Successfully Deleted'}),200)
        else:
            return make_response(jsonify({'error':'Wrong input format'}),400)


def register_resources(api):
    schedule_regularly()
    api.add_namespace(profile_management_ns)