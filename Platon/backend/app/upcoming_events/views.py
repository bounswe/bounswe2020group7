from flask import make_response,jsonify,request
from flask_restplus import Resource,Namespace, fields

from app.upcoming_events.forms import UpcomingEventsForm,upcoming_event_parser, personal_calendar_parser
from app.upcoming_events.models import UpcomingEvent
from app.upcoming_events.helpers import schedule_regularly

from app.auth_system.helpers import login_required

from app.workspace_system.models import Contribution, Workspace, Milestone, Issue, IssueAssignee

from app import app,api
import math
from datetime import datetime

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

@upcoming_events_ns.route("/personal_calendar")
class PersonalCalendarAPI(Resource):
    
    # GET request
    @api.expect(personal_calendar_parser)
    @api.doc(responses={
                200: "Calendar has been successfully returned.",
                500: "The server is not connected to the database."
            })
    @login_required
    def get(user_id, self):
        '''
        Returns the the personal calendar (workspaces, issues, milestones with their deadlines) of a user.
        '''

        # Tries to connect to the database.
        # If it fails, an error is raised.
        try:
            # Gets the list of the active contributions of the requester user.
            active_contributions = Contribution.query.filter_by(user_id=user_id, is_active=True).all()
            # Gets the list of the issue assignments of the requester user.
            issue_assignments = IssueAssignee.query.filter_by(assignee_id=user_id).all()
        except:
            return make_response(jsonify({"error" : "The server is not connected to the database."}), 500)
        else:
            # The list that will be returned in the end gets created.
            personal_calendar = []

            # The information of the each workspace the user contributes to gets retrieved from the database.
            # If the workspace has a not-yet-overdue deadline, it gets added to response list.
            # Also, all the milestones (with a deadline) of the contributed workspaces get added.
            for contribution in active_contributions:
                contributed_workspace = Workspace.query.filter_by(id=contribution.workspace_id).first()
                if (contributed_workspace.deadline is not None) and \
                    (contributed_workspace.deadline >= datetime.now()):
                    workspace_deadline_information = {
                                                        "type": 1,
                                                        "title": contributed_workspace.title,
                                                        "deadline": contributed_workspace.deadline,
                                                        "deadline_str": contributed_workspace.deadline.strftime("%Y.%m.%d") if contributed_workspace.deadline is not None else "",
                                                        "workspace_id": contributed_workspace.id,
                                                        "workspace_title": contributed_workspace.title
                                                    }
                    personal_calendar.append(workspace_deadline_information)


                contributed_workspace_milestones = Milestone.query.filter((Milestone.workspace_id == contributed_workspace.id) and \
                                                                            (Milestone.deadline != None) and \
                                                                            (Milestone.deadline >= datetime.now())).all()
                for milestone in contributed_workspace_milestones:
                    if milestone.deadline is not None:
                        milestone_deadline_information = {
                                                            "type": 2,
                                                            "title": milestone.title,
                                                            "deadline": milestone.deadline,
                                                            "deadline_str": milestone.deadline.strftime("%Y.%m.%d %H.%M") if milestone.deadline is not None else "",
                                                            "workspace_id": contributed_workspace.id,
                                                            "workspace_title": contributed_workspace.title
                                                        }
                        personal_calendar.append(milestone_deadline_information)

            # Issues the user has been assigned get added to the response list,
            # if they do not have an overdue deadline.
            for assignment in issue_assignments:
                issue = Issue.query.filter((Issue.id == assignment.issue_id) and \
                                            (Issue.is_open == True) and \
                                            (Issue.deadline != None) and \
                                            (Issue.deadline >= datetime.now())).first()
                if issue.deadline is not None:

                    issue_deadline_information = {
                                                    "type": 3,
                                                    "title": issue.title,
                                                    "deadline": issue.deadline,
                                                    "deadline_str": issue.deadline.strftime("%Y.%m.%d %H.%M") if issue.deadline is not None else "",
                                                    "workspace_id": issue.workspace_id,
                                                    "workspace_title": Workspace.query.filter_by(id=issue.workspace_id).first().title
                                                }
                    personal_calendar.append(issue_deadline_information)

        personal_calendar = sorted(personal_calendar, key=lambda item: item["deadline"])
        return make_response(jsonify(personal_calendar),200) 


def register_resources(api):
    schedule_regularly()
    api.add_namespace(upcoming_events_ns) 

