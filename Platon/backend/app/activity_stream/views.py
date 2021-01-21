from flask import make_response, jsonify, request
from flask_restplus import Resource, Namespace, fields
from flask import current_app as app
import math
import requests

from app import api, db
from app.activity_stream.models import *
from app.follow_system.models import *
from app.workspace_system.models import *
from app.activity_stream.forms import *
from app.auth_system.helpers import *
from app.activity_stream.helpers import *

from sqlalchemy import desc

activity_stream_ns = Namespace("Activity Stream",
                            description="Activity Stream Endpoints",
                            path = "/activity_stream")

activity_stream_item_context_model = api.model('Activity Stream Item Context', {
	"@vocab": fields.String,
    "@language": fields.String
})

activity_stream_item_image_model = api.model('Activity Stream Item Image', {
	"type": fields.String,
    "url": fields.String
})

activity_stream_item_actor_model = api.model('Activity Stream Item Actor', {
    "type": fields.String,
    "id": fields.Integer,
    "name": fields.String,
    "image": fields.Nested(activity_stream_item_image_model)
})

activity_stream_item_object_model = api.model('Activity Stream Item Object', {
    "type": fields.String,
    "id": fields.Integer,
    "name": fields.String,
    "image": fields.Nested(activity_stream_item_image_model),
    "content": fields.String,
    "ext:ratingValue": fields.Integer
})

activity_stream_item_target_model = api.model('Activity Stream Item Target', {
    "type": fields.String,
    "id": fields.Integer,
    "name": fields.String,
    "image": fields.Nested(activity_stream_item_image_model)
})

activity_stream_item_model = api.model('Activity Stream Item', {
    "@context": fields.Nested(activity_stream_item_context_model),
    "summary": fields.String,
    "type": fields.String,
    "actor": fields.Nested(activity_stream_item_actor_model),
    "object": fields.Nested(activity_stream_item_object_model),
    "target": fields.Nested(activity_stream_item_target_model)
})

activity_stream_model = api.model('Activity Stream', {
    "@context": fields.String,
    "summary": fields.String,
    "type": fields.String,
    "id": fields.Integer,
    "orderedItems": fields.List(
        fields.Nested(activity_stream_item_model)
    )
})

@activity_stream_ns.route("")
class ActivityStreamAPI(Resource):
    
    @api.doc(responses={401 : 'Account Problems', 400 : 'Input Format Error' ,500 : ' Database Connection Error', 404: 'Not found'})
    @api.response(200, 'Success', activity_stream_model)
    @api.expect(get_activity_stream_parser)
    @login_required
    def get(user_id, self):
        '''
            Returns activity stream of an user.
        '''
        '''
            # Pagination functionality
            number_of_pages = 1
            if form.page.data is not None and form.per_page.data is not None:
                per_page = form.per_page.data
                number_of_pages = math.ceil(len(return_list) / per_page)
                # Assign the page index to the maximum if it exceeds the max index
                page = form.page.data if form.page.data < number_of_pages else number_of_pages-1
                return_list = return_list[page*per_page:(page+1)*per_page]
            return make_response(jsonify({'number_of_pages': number_of_pages ,'result': return_list}),200)
        '''

        # Parses the form data.
        form = GetActivityStreamForm(request.args)
        
        if form.validate():

            # Get current user's followings list.
            try:
                followings_list = Follow.query.filter(Follow.follower_id==user_id).all()
            except:
                return make_response(jsonify({"error" : "The server is not connected to the database."}), 500)

            followings_ids_list = []
            for followings_item in followings_list:
                followings_ids_list.append(followings_item.following_id)

            # Get current user's workspaces list.
            try:
                contributions = Contribution.query.filter(Contribution.user_id==user_id).all()
            except:
                return make_response(jsonify({"error": "Database Connection Error"}),500)
            
            workspace_ids_list = []
            for contribution in contributions:
                if contribution.is_active == 0:
                    continue
                try:
                    ws = Workspace.query.get(contribution.workspace_id)
                except:
                    return make_response(jsonify({"error": "Database Connection Error"}),500)

                workspace_ids_list.append(ws.id)

            activity_stream_list = []
            for following_id in followings_ids_list:
                try:
                    # Returns activity stream items with descending timestamp value.
                    activity_stream_items_list = ActivityStreamItem.query.filter(ActivityStreamItem.activity_actor_id == following_id).order_by(desc(ActivityStreamItem.timestamp)).all()
                except:
                    # not sure to return error.
                    return make_response(jsonify({"error" : "The server is not connected to the database."}), 500)

                activity_stream_list += activity_stream_items_list
            
            # Activities of the workspace that the user is active should be added. 
            activity_stream_list_of_workspaces = get_activities_in_workspaces(user_id, workspace_ids_list)

            # these two activity streams list should be combined and sorted again.
            activity_stream_list = sort_activities(activity_stream_list_of_workspaces, activity_stream_list)

            # Pagination applied
            per_page = form.per_page.data
            number_of_pages = math.ceil(len(activity_stream_list) / per_page)
            # Assign the page index to the maximum if it exceeds the max index
            page = form.page.data if form.page.data < number_of_pages else number_of_pages-1
            activity_stream_list_paginated = activity_stream_list[page*per_page:(page+1)*per_page]

            # Formatting for W3C Activity Stream format.
            activity_stream_list_paginated_formatted = []
            ordered_items = []
            for activity_stream_item in activity_stream_list_paginated:

                activity_stream_item_context = {
                    '@vocab': activity_stream_item.activity_context_vocab,
                    'ext': activity_stream_item.activity_context_ext,
                    '@language': "en"
                }

                activity_stream_item_actor = {
                    "type": activity_stream_item.activity_actor_type,
                    "id": activity_stream_item.activity_actor_id,
                    "name": activity_stream_item.activity_actor_name,
                    "image": {
                        'type': "Image",
                        'url': activity_stream_item.activity_actor_image_url
                    }
                }

                activity_stream_item_object = {
                    "type": activity_stream_item.activity_object_type,
                    "id": activity_stream_item.activity_object_id,
                    "name": activity_stream_item.activity_object_name,
                    "image": {
                        'type': "Image",
                        'url': activity_stream_item.activity_object_image_url
                    },
                    "content": activity_stream_item.activity_object_content,
                    "ext:ratingValue": activity_stream_item.activity_object_rating_value
                }

                activity_stream_item_target = {
                    "type": activity_stream_item.activity_target_type,
                    "id": activity_stream_item.activity_target_id,
                    "name": activity_stream_item.activity_target_name,
                    "image": {
                        'type': "Image",
                        'url': activity_stream_item.activity_target_image_url
                    }
                }

                ordered_items.append({
                    "@context": activity_stream_item_context,
                    "summary": activity_stream_item.activity_summary,
                    "type": activity_stream_item.activity_type,
                    "actor": activity_stream_item_actor,
                    "object": activity_stream_item_object,
                    "target": activity_stream_item_target 
                })

            
            return_list = {
                "@context": "https://www.w3.org/ns/activitystreams",
                "summary": "Page {} of Activity Stream".format(page),
                "type": "OrderedCollectionPage",
                "id": page,
                "orderedItems": ordered_items
            }

            return make_response(return_list, 200)
        
        else:
            return make_response(jsonify({'error': 'Input Format Error'}), 400)

def register_resources(api):
    api.add_namespace(activity_stream_ns)