from flask import make_response,jsonify,request
from flask_restplus import Resource,Namespace, fields
from app.auth_system.helpers import login_required
from app.auth_system.models import User
from app.follow_system.helpers import follow_required
from app.follow_system.models import Follow
from app.profile_management.forms import ResearchInfoGetForm,research_info_get_parser, ResearchInfoPostForm,research_info_post_parser
from app.profile_management.forms import ResearchInfoUpdateForm, research_info_update_parser,ResearchInfoDeleteFrom,research_info_delete_parser
from app.profile_management.forms import notification_get_parser,NotificationDeleteForm,notification_delete_parser
from app.profile_management.forms import front_page_parser
from app.profile_management.models import ResearchInformation,Notification,NotificationRelatedUser
from app.profile_management.helpers import schedule_regularly,ResearchType
from app import api, db

profile_management_ns = Namespace("Profile Management",
                                  description="Profile Management Endpoints",
                                  path = "/profile")

research_info_model = api.model('Research Info', {
    'id': fields.Integer,
    'title': fields.String,
    'description': fields.String,
    'year': fields.Integer
})

research_list_model = api.model('Research List', {
    'research_info': fields.List(
        fields.Nested(research_info_model)
    )
})

related_users_model = api.model('Related Users', {
    'user_id': fields.Integer
})

notification_model = api.model('Notification', {
    'id': fields.Integer,
    'text': fields.String,
    'link': fields.String,
    'timestamp': fields.DateTime,
    'related_users': fields.List(
        fields.String
    )
})

notification_list_model = api.model('Notification List', {
    'notification_list': fields.List(
        fields.Nested(notification_model)
    )
})

@profile_management_ns.route("/research_information")
class ResearchInformationAPI(Resource):
    """
        Research Information Functionality is implemented in this class
    """
    @api.doc(responses={400:'Wrong Input Format',401:'Authantication Problem', 403 : 'Private Account Problem',500:'Database Connection Problem'})
    @api.response(200, 'Valid Response', research_list_model)
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
            
            research_info = ResearchInformation.query.filter((ResearchInformation.id == form.research_id.data) & (ResearchInformation.user_id == user_id))
            if research_info is None:
                return make_response(jsonify({'error':'Please give an appropriate research ID'}),400)
            update_dict = {}
            for key, value in form.data.items():
                if value and key != 'research_id':
                    update_dict[key] = value
            try:
                research_info.update(update_dict)
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
    
    @api.doc(responses={401:'Authantication Problem',500:'Database Connection Problem'})
    @api.response(200,'Valid Response', notification_list_model)
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


@profile_management_ns.route("/front_page")
class FrontPage(Resource):
    '''
    Temporary API for front page for presentation purposes.
    '''

    # GET request
    @api.expect(front_page_parser)
    @api.doc(responses={
                200: "OK",
                500: "The server does not respond."
            })
    def get(self):
        activities = [
                {
                    "message": "Christophe Daussy has created the project 'Frequency metrology and clocks'.",
                    "image": "https://raw.githubusercontent.com/bounswe/bounswe2020group7/master/non_project/christophe_daussy.jpg"
                },
                {
                    "message": "Gwen Stacy has created the project 'Spider Genome Database'.",
                    "image": "https://raw.githubusercontent.com/bounswe/bounswe2020group7/master/non_project/gwen_stacy.jpg"
                },
                {
                    "message": "Christophe Daussy has changed the state of the project 'Frequency metrology and clocks' to 'Search for Collaborators'.",
                    "image": "https://raw.githubusercontent.com/bounswe/bounswe2020group7/master/non_project/christophe_daussy.jpg"
                },
                {
                    "message": "Christophe Daussy has started following Gwen Stacy.",
                    "image": "https://raw.githubusercontent.com/bounswe/bounswe2020group7/master/non_project/christophe_daussy.jpg"
                },
                {
                    "message": "Tony Stark has created the project 'Health effects of cooking in cast iron'.",
                    "image": "https://raw.githubusercontent.com/bounswe/bounswe2020group7/master/non_project/tony_stark.jpg"
                },
                {
                    "message": "Tony Stark has changed the state of the project 'Health effects of cooking in cast iron' to 'Search for Collaborators'.",
                    "image": "https://raw.githubusercontent.com/bounswe/bounswe2020group7/master/non_project/tony_stark.jpg"
                },
                {
                    "message":  "Gwen Stacy has started following Tony Stark.",
                    "image": "https://raw.githubusercontent.com/bounswe/bounswe2020group7/master/non_project/gwen_stacy.jpg"
                },
                {
                    "message": "John Nash has changed the state of the project 'Non-cooperative games' to 'Published'.",
                    "image": "https://raw.githubusercontent.com/bounswe/bounswe2020group7/master/non_project/john_nash.jpg"
                },
                {
                    "message":  "Gwen Stacy has started following John Nash.",
                    "image": "https://raw.githubusercontent.com/bounswe/bounswe2020group7/master/non_project/gwen_stacy.jpg"
                },
                {
                    "message": "John Nash has started following Gwen Stacy.",
                    "image": "https://raw.githubusercontent.com/bounswe/bounswe2020group7/master/non_project/john_nash.jpg"
                }
        ]

        return make_response(jsonify(activities),200)

def register_resources(api):
    schedule_regularly()
    api.add_namespace(profile_management_ns)