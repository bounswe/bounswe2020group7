from flask import make_response,jsonify,request
from flask_restplus import Resource,Namespace, fields

from app.upcoming_events.forms import UpcomingEventsForm,upcoming_event_parser
from app.upcoming_events.models import UpcomingEvent
from app.upcoming_events.helpers import schedule_regularly

from app import app,api
import math

upcoming_events_ns = Namespace("Upcoming Events",
                                description="Upcoming Events Endpoints",
                                path = "/upcoming_events")

upcoming_event_model = api.model('Upcoming Event', {
    "id": fields.Integer,
    "acronym": fields.String,
    "title": fields.String,
    "location": fields.String,
    "link": fields.String,
    "date": fields.String,
    "deadline": fields.String
})

upcoming_events_model = api.model('Upcoming Event List',{
    "number_of_pages": fields.Integer,
    'upcoming_events': fields.List(
        fields.Nested(upcoming_event_model)
    )
})

@upcoming_events_ns.route("")
class UpcomingEventsAPI(Resource):

    @api.doc(responses={500:'Database Connection Problem'})
    @api.response(200, 'Valid Response', upcoming_events_model)
    @api.expect(upcoming_event_parser)
    def get(self):
        try:
            all_events = UpcomingEvent.query.all()
        except:
            return make_response(jsonify({"error": "Database Connection Problem."}), 500)
        all_upcoming_events = [
                {
                    "id": event.id,
                    "acronym": event.acronym,
                    "title": event.title,
                    "location": event.location,
                    "link": event.link,
                    "date": event.date,
                    "deadline": event.deadline
                }
                for event in all_events]
        form = UpcomingEventsForm(request.args)
        number_of_pages = 1
        if form.validate():
            per_page = form.per_page.data
            number_of_pages = math.ceil(len(all_upcoming_events)/per_page)
            page = form.page.data if form.page.data < number_of_pages else number_of_pages-1
            all_upcoming_events = all_upcoming_events[page*per_page:(page+1)*per_page]
        return make_response(jsonify({'number_of_pages':number_of_pages, 'upcoming_events': all_upcoming_events}),200) 

def register_resources(api):
    schedule_regularly()
    api.add_namespace(upcoming_events_ns) 

