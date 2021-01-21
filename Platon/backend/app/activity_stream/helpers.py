from app import app, db
from app.workspace_system.models import *
from app.auth_system.models import *
from app.activity_stream.models import *
from app.follow_system.models import *
from app.auth_system.helpers import profile_photo_link
from sqlalchemy import desc
from flask import make_response



def activity_stream_accept_collaboration_invitation(invitation, invitee_id):
    
    try:
        workspace = Workspace.query.filter(Workspace.id == invitation.workspace_id).first()
        current_user = User.query.filter(User.id ==invitee_id).first()
    except:
        return make_response(jsonify({"error" : "The server is not connected to the database."}), 500)

    # Activity is added into Activity Stream
    activity_stream_entry = ActivityStreamItem(
        activity_context_vocab = "https://www.w3.org/ns/activitystreams",
        activity_summary = "{} {} is now a contributor in {} workspace".format(current_user.name, current_user.surname, workspace.title),
        activity_type = "Join",
        activity_actor_type = "Person",
        activity_actor_id = current_user.id,
        activity_actor_name = (current_user.name + " " + current_user.surname),
        activity_actor_image_url = profile_photo_link(current_user.profile_photo,current_user.id),
        activity_object_type = "Group",
        activity_object_name = workspace.title,
        activity_object_id = workspace.id,
    )
    try:
        db.session.add(activity_stream_entry)
        db.session.commit()
    except:
        return make_response(jsonify({'error': 'Database Connection Error'}), 500)

def activity_stream_follow_activity(logged_in_user, following_user):
    # Add this activity to the Activity Stream
    activity_stream_item = ActivityStreamItem(
        activity_context_vocab = "https://www.w3.org/ns/activitystreams",
        activity_summary = "{} {} started following {} {}".format(logged_in_user.name, logged_in_user.surname, following_user.name, following_user.surname),
        activity_type = "Follow",
        activity_actor_type = "Person",
        activity_actor_id = logged_in_user.id,
        activity_actor_name = (logged_in_user.name + " " + logged_in_user.surname),
        activity_actor_image_url = profile_photo_link(logged_in_user.profile_photo,logged_in_user.id),
        activity_object_type = "Person",
        activity_object_id = following_user.id,
        activity_object_name = following_user.name + " " + following_user.surname,
        activity_object_image_url = profile_photo_link(following_user.profile_photo, following_user.id)
    )

    try:
        db.session.add(activity_stream_item)  # Creating a new database entry.
        db.session.commit()
    except:
        return make_response(jsonify({'error': 'Database Connection Error'}), 500)

def activity_stream_user_comment_activity(user_id, form, comment):
    try:
        current_user = User.query.filter(User.id == user_id).first()
        other_user = User.query.filter(User.id == form.commented_user_id.data).first()
    except:
        return make_response(jsonify({'error': 'DB connection error'}), 500)

    activity_stream_entry = ActivityStreamItem(
        activity_context_vocab = "https://www.w3.org/ns/activitystreams",
        activity_context_ext = 'http://schema.org/Rating',
        activity_summary = "{} {} commented and rated {} {}".format(current_user.name, current_user.surname, other_user.name, other_user.surname),
        activity_type = "Add",
        activity_actor_type = "Person",
        activity_actor_id = current_user.id,
        activity_actor_name = (current_user.name + " " + current_user.surname),
        activity_actor_image_url = profile_photo_link(current_user.profile_photo,current_user.id),
        activity_object_type = "Note",
        activity_object_name = "Comment",
        activity_object_id = comment.id,
        activity_object_content = comment.text,
        activity_object_rating_value = comment.rate,
        activity_target_type = "Person",
        activity_target_id = other_user.id,
        activity_target_name = other_user.name + " " + other_user.surname,
        activity_target_image_url = profile_photo_link(other_user.profile_photo, other_user.id)
    )
    try:
        db.session.add(activity_stream_entry)
        db.session.commit()
    except:
        return make_response(jsonify({'error': 'Database Connection Error'}), 500)


def activity_stream_accept_collaboration_application(application):
    try:
        workspace = Workspace.query.filter(Workspace.id == application.workspace_id).first()
        current_user = User.query.filter(User.id == application.applicant_id).first()
    except:
        return make_response(jsonify({"error" : "The server is not connected to the database."}), 500)

    activity_stream_entry = ActivityStreamItem(
        activity_context_vocab = "https://www.w3.org/ns/activitystreams",
        activity_summary = "{} {} is now a contributor in {} workspace".format(current_user.name, current_user.surname, workspace.title),
        activity_type = "Join",
        activity_actor_type = "Person",
        activity_actor_id = current_user.id,
        activity_actor_name = (current_user.name + " " + current_user.surname),
        activity_actor_image_url = profile_photo_link(current_user.profile_photo,current_user.id),
        activity_object_type = "Group",
        activity_object_name = workspace.title,
        activity_object_id = workspace.id,
    )
    try:
        db.session.add(activity_stream_entry)
        db.session.commit()
    except:
        return make_response(jsonify({'error': 'Database Connection Error'}), 500)

def activity_stream_quit_workspace(user_id, form):
    try:
        current_user = User.query.filter(User.id == user_id).first()
        workspace = Workspace.query.filter(Workspace.id == form.workspace_id.data).first()
    except:
        return make_response(jsonify({'error': 'DB connection error'}), 500)

    activity_stream_entry = ActivityStreamItem(
        activity_context_vocab = "https://www.w3.org/ns/activitystreams",
        activity_summary = "{} {} left {} workspace".format(current_user.name, current_user.surname, workspace.title),
        activity_type = "Leave",
        activity_actor_type = "Person",
        activity_actor_id = current_user.id,
        activity_actor_name = (current_user.name + " " + current_user.surname),
        activity_actor_image_url = profile_photo_link(current_user.profile_photo,current_user.id),
        activity_object_type = "Group",
        activity_object_name = workspace.title,
        activity_object_id = workspace.id
    )
    try:
        db.session.add(activity_stream_entry)
    except:
        return make_response(jsonify({'error': 'Database Connection Error'}), 500)


def activity_stream_create_workspace(new_workspace, requester_id):
    if not new_workspace.is_private:
        try:
            current_user = User.query.filter(User.id == requester_id).first()
        except:
            return make_response(jsonify({"error" : "The server is not connected to the database."}), 500)

        # Activity is added into Activity Stream
        activity_stream_entry = ActivityStreamItem(
            activity_context_vocab = "https://www.w3.org/ns/activitystreams",
            activity_summary = "{} {} created a workspace named {}".format(current_user.name, current_user.surname, new_workspace.title),
            activity_type = "Create",
            activity_actor_type = "Person",
            activity_actor_id = current_user.id,
            activity_actor_name = (current_user.name + " " + current_user.surname),
            activity_actor_image_url = profile_photo_link(current_user.profile_photo,current_user.id),
            activity_object_type = "Group",
            activity_object_name = new_workspace.title,
            activity_object_id = new_workspace.id,
        )
        try:
            db.session.add(activity_stream_entry)
            db.session.commit()
        except:
            return make_response(jsonify({'error': 'Database Connection Error'}), 500)


def activity_stream_delete_workspace(requested_workspace, requester_id):
    if not requested_workspace.is_private:
        try:
            current_user = User.query.filter(User.id == requester_id).first()
        except:
            return make_response(jsonify({"error" : "The server is not connected to the database."}), 500)

        # Activity is added into Activity Stream
        activity_stream_entry = ActivityStreamItem(
            activity_context_vocab = "https://www.w3.org/ns/activitystreams",
            activity_summary = "{} {} deleted the workspace {}".format(current_user.name, current_user.surname, requested_workspace.title),
            activity_type = "Delete",
            activity_actor_type = "Person",
            activity_actor_id = current_user.id,
            activity_actor_name = (current_user.name + " " + current_user.surname),
            activity_actor_image_url = profile_photo_link(current_user.profile_photo,current_user.id),
            activity_object_type = "Group",
            activity_object_name = requested_workspace.title,
            activity_object_id = requested_workspace.id,
        )
        try:
            db.session.add(activity_stream_entry)
            db.session.commit()
        except:
            return make_response(jsonify({'error': 'Database Connection Error'}), 500)

def get_workspace_title_of_issue(issue):
    try:
        workpace = Workspace.query.filter(Workspace.id == issue.workspace_id).first()
    except:
        return make_response(jsonify({"error" : "The server is not connected to the database."}), 500)

    return workpace.title

def activity_stream_create_issue(user_id, issue):
    try:
        current_user = User.query.filter(User.id == user_id).first()
    except:
        return make_response(jsonify({"error" : "The server is not connected to the database."}), 500)

    try:
        # Activity is added into Activity Stream
        activity_stream_entry = ActivityStreamItem(
            activity_context_vocab = "https://www.w3.org/ns/activitystreams",
            activity_summary = "{} {} created an issue titled '{}' in the workspace '{}'".format(current_user.name, current_user.surname, issue.title, get_workspace_title_of_issue(issue)),
            activity_type = "Create",
            activity_actor_type = "Person",
            activity_actor_id = current_user.id,
            activity_actor_name = (current_user.name + " " + current_user.surname),
            activity_actor_image_url = profile_photo_link(current_user.profile_photo,current_user.id),
            activity_object_type = "Note",
            activity_object_name = issue.title,
            activity_object_id = issue.id,
            activity_object_content = issue.description,
            activity_target_type = "Group",
            activity_target_id = issue.workspace_id,
            activity_target_name = get_workspace_title_of_issue(issue)
        )
    except:
        make_response(jsonify({"error" : "error happened while creating activity stream entry"}), 500)
    
    try:
        db.session.add(activity_stream_entry)
        db.session.commit()
    except:
        return make_response(jsonify({'error': 'Database Connection Error'}), 500)

def sort_activities(activity_list_1, activity_list_2):
    # since they are both in sorted order, merge operation can be performed which is O(N)
    # however, right now I'll just use built-in sort which is probably O(N*logN)
    return sorted(activity_list_1+activity_list_2, key=lambda x: x.timestamp, reverse=True)

def get_activities_in_workspaces(user_id, workspace_ids_list):
    activity_stream_list = []
    for workspace_id in workspace_ids_list:
        try:
            # Returns activity stream items with descending timestamp value.
            # Design Choice: If activity actor is the current user, don't show this activity in activity stream.
            activity_stream_items_list = ActivityStreamItem.query.filter((ActivityStreamItem.activity_target_id == workspace_id)&(ActivityStreamItem.activity_target_type == "Group")&(ActivityStreamItem.activity_actor_id != user_id)).order_by(desc(ActivityStreamItem.timestamp)).all()
        except:
            # not sure to return error.
            return make_response(jsonify({"error" : "The server is not connected to the database."}), 500)

        activity_stream_list += activity_stream_items_list
    return activity_stream_list

def activity_stream_post_file(user_id, workspace_id, filename):
    try:
        current_user = User.query.filter(User.id == user_id).first()
        workspace = Workspace.query.filter(Workspace.id == workspace_id).first()
    except:
        return make_response(jsonify({"error" : "The server is not connected to the database."}), 500)
    
    try:
        # Activity is added into Activity Stream
        activity_stream_entry = ActivityStreamItem(
            activity_context_vocab = "https://www.w3.org/ns/activitystreams",
            activity_summary = "{} {} uploaded a new file named '{}' to the workspace '{}'".format(current_user.name, current_user.surname, filename, workspace.title),
            activity_type = "Create",
            activity_actor_type = "Person",
            activity_actor_id = current_user.id,
            activity_actor_name = (current_user.name + " " + current_user.surname),
            activity_actor_image_url = profile_photo_link(current_user.profile_photo,current_user.id),
            activity_object_type = "Document",
            activity_object_name = filename,
            activity_target_type = "Group",
            activity_target_id = workspace.id,
            activity_target_name = workspace.title
        )
    except:
        make_response(jsonify({"error" : "error happened while creating activity stream entry"}), 500)
    
    try:
        db.session.add(activity_stream_entry)
        db.session.commit()
    except:
        return make_response(jsonify({'error': 'Database Connection Error'}), 500)

def activity_stream_post_folder(user_id, workspace_id, new_folder_name):
    try:
        current_user = User.query.filter(User.id == user_id).first()
        workspace = Workspace.query.filter(Workspace.id == workspace_id).first()
    except:
        return make_response(jsonify({"error" : "The server is not connected to the database."}), 500)
    
    try:
        # Activity is added into Activity Stream
        activity_stream_entry = ActivityStreamItem(
            activity_context_vocab = "https://www.w3.org/ns/activitystreams",
            activity_summary = "{} {} created a new folder named '{}' in the workspace '{}'".format(current_user.name, current_user.surname, new_folder_name, workspace.title),
            activity_type = "Create",
            activity_actor_type = "Person",
            activity_actor_id = current_user.id,
            activity_actor_name = (current_user.name + " " + current_user.surname),
            activity_actor_image_url = profile_photo_link(current_user.profile_photo,current_user.id),
            activity_object_type = "Document",
            activity_object_name = new_folder_name,
            activity_target_type = "Group",
            activity_target_id = workspace.id,
            activity_target_name = workspace.title
        )
    except:
        make_response(jsonify({"error" : "error happened while creating activity stream entry"}), 500)
    
    try:
        db.session.add(activity_stream_entry)
        db.session.commit()
    except:
        return make_response(jsonify({'error': 'Database Connection Error'}), 500)

def activity_stream_post_milestone(user_id, milestone):
    try:
        current_user = User.query.filter(User.id == user_id).first()
        workspace = Workspace.query.filter(Workspace.id == milestone.workspace_id).first()
    except:
        return make_response(jsonify({"error" : "The server is not connected to the database."}), 500)
    
    try:
        # Activity is added into Activity Stream
        activity_stream_entry = ActivityStreamItem(
            activity_context_vocab = "https://www.w3.org/ns/activitystreams",
            activity_summary = "{} {} created a milestone titled '{}' in the workspace '{}'".format(current_user.name, current_user.surname, milestone.title, workspace.title),
            activity_type = "Create",
            activity_actor_type = "Person",
            activity_actor_id = current_user.id,
            activity_actor_name = (current_user.name + " " + current_user.surname),
            activity_actor_image_url = profile_photo_link(current_user.profile_photo,current_user.id),
            activity_object_type = "Event",
            activity_object_name = milestone.title,
            activity_object_id = milestone.id,
            activity_target_type = "Group",
            activity_target_id = workspace.id,
            activity_target_name = workspace.title
        )
    except:
        make_response(jsonify({"error" : "error happened while creating activity stream entry"}), 500)
    
    try:
        db.session.add(activity_stream_entry)
        db.session.commit()
    except:
        return make_response(jsonify({'error': 'Database Connection Error'}), 500)

def activity_stream_delete_folder(user_id, workspace_id, path):
    try:
        current_user = User.query.filter(User.id == user_id).first()
        workspace = Workspace.query.filter(Workspace.id == workspace_id).first()
    except:
        return make_response(jsonify({"error" : "The server is not connected to the database."}), 500)
    print(path)
    folder_name = path.split("/")[-1]
    try:
        # Activity is added into Activity Stream
        activity_stream_entry = ActivityStreamItem(
            activity_context_vocab = "https://www.w3.org/ns/activitystreams",
            activity_summary = "{} {} deleted the folder named '{}' in the workspace '{}'".format(current_user.name, current_user.surname, folder_name, workspace.title),
            activity_type = "Create",
            activity_actor_type = "Person",
            activity_actor_id = current_user.id,
            activity_actor_name = (current_user.name + " " + current_user.surname),
            activity_actor_image_url = profile_photo_link(current_user.profile_photo,current_user.id),
            activity_object_type = "Document",
            activity_object_name = folder_name,
            activity_target_type = "Group",
            activity_target_id = workspace.id,
            activity_target_name = workspace.title
        )
    except:
        make_response(jsonify({"error" : "error happened while creating activity stream entry"}), 500)
    
    try:
        db.session.add(activity_stream_entry)
        db.session.commit()
    except:
        return make_response(jsonify({'error': 'Database Connection Error'}), 500)