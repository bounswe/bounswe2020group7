from tests.base_test import BaseTest
from tests.base_test import TestConfig
from app.auth_system.models import User
from app.profile_management.models import Jobs
from app.workspace_system.models import Workspace, Contribution, Issue, IssueAssignee, IssueComment
from app.auth_system.views import generate_token
from app import db
import datetime
import json
from app.workspace_system.helpers import WorkspaceState

class ActivityStreamTest(BaseTest):
    """
        Unit Tests of the Activity Stream
    """

    def setUp(self):

        jobs = [
            Jobs("academician"),
            Jobs("PhD student")
        ]

        for job in jobs:
            db.session.add(job)

        db.session.commit()

        # Umut and Can are public users. Alperen is private user.
        users = [
            User("umut@deneme.com", True, "b73ec5e4625ffcb6d0d70826f33be7a75d45b37046e26c4b60d9111266d70e32", 3.5,
                 "Umut", "Özdemir", False, None, None, None, 1, "boun"),
            User("can@deneme.com", True, "cce0c2170d1ae52e099c716165d80119ee36840e3252e57f2b2b4d6bb111d8a5", 3.4,
                 "Can", "Deneme", False, None, None, None, 2, "boun"),
            User("alperen@deneme.com", True, "hashedpassword", 4.6, "Alperen", "Ozprivate", True, None, None, None, 1, "boun"),
            User("hilal@deneme.com", True, "hasheddpassword", 4.5, "Hilal", "Private", True, None, None, None, 1, "boun")
        ]
        for user in users:
            db.session.add(user)

        db.session.commit()

        follows = [
            Follow(1, 2),  # Umut follows Can
            Follow(3, 2),  # Alperen follows Can
            Follow(3, 1)  # Alperen follows Umut
        ]
        for follow in follows:
            db.session.add(follow)

        db.session.commit()

        follow_requests = [
            FollowRequests(2, 3)  # Can sent follow request to Alperen.
        ]

        for follow_request in follow_requests:
            db.session.add(follow_request)

        db.session.commit()

        workspaces = [
            Workspace(creator_id = 1, is_private = 0, title = "coronovirus study", description = "deneme", state = WorkspaceState.search_for_collaborator.value), # public workspace by Umut on state 1
            Workspace(creator_id = 2, is_private = 1, title = "SWE difficulties", description = "deneme", state = WorkspaceState.search_for_collaborator.value), # private workspace by Can on state 1
            Workspace(creator_id = 4, is_private = 1, title = "honeybadger", description = "deneme", state = WorkspaceState.search_for_collaborator.value), # private workspace by Hilal on state 1
            Workspace(creator_id = 3, is_private = 0, title = "bos", description = "deneme", state = WorkspaceState.search_for_collaborator.value) # public workspace by Alperen on state 1
        ]

        for workspace in workspaces:
            db.session.add(workspace)
        
        db.session.commit()

        contributions = [
            Contribution(workspace_id = 1, user_id = 1, is_active = 1), # Umut will be active in coronovirus study
            Contribution(workspace_id = 2, user_id = 2, is_active = 1), # can will be active in SWE difficulties
            Contribution(workspace_id = 2, user_id = 3, is_active = 1),  # alperen will be active in SWE difficulties
            Contribution(workspace_id = 3, user_id = 2, is_active = 0), # can will be inactive in honey badger study
            Contribution(workspace_id = 3, user_id = 4, is_active = 1), # hilal will be active in honey badger study
            Contribution(workspace_id = 3, user_id = 1, is_active = 1), # umut will be active in honey badger study
            Contribution(workspace_id = 4, user_id = 3, is_active = 1) # alperen will be active in bos
        ]

        for contribution in contributions:
            db.session.add(contribution)
        db.session.commit()

        issues = [
            Issue(1, 1, "cure of coronovirus", "eat more sarimsak daily"),
            Issue(2, 2, "alperen's assigned issue", "denemeeee"),
            Issue(4, 3, "bravest animal is honeybadger", "really")
        ]

        for issue in issues:
            db.session.add(issue)
        db.session.commit()

        issue_assignees = [
            IssueAssignee(2, 3), # alperen is assigned on issue which has issue id 2.
            IssueAssignee(1, 1) # umut is assigned on issue wihch has issue id 1.
        ]

        for issue_assignee in issue_assignees:
            db.session.add(issue_assignee)
        db.session.commit()

        issue_comments = [
            IssueComment(2, 2, "makes sense"), # Can makes comment on an issue
            IssueComment(2, 3, "what is this?"), # Alperen makes comment on an issue
            IssueComment(3, 4, "hello world") # Hilal makes comment on an issue
        ]

        for issue_comment in issue_comments:
            db.session.add(issue_comment)
        db.session.commit()


    # Check if the related user started following someone.
    def test_follow_activity(self):
        pass
    # Check if related user commented on someone.
    def test_user_comment_activity(self):
        pass
    # Check if related user is now contributing to a workspace. 
    def test_new_contribution(self):
        pass
    # Check if related user stopped contributing to a workspace. 
    def test_stop_contribution(self):
        pass
    # Check if related user created a workspace
    def test_create_workspace(self):
        pass
    # Check if related user deleted a workspace
    def test_delete_workspace(self):
        pass
    # Check if the workspace of the related user is updated their state.
    def test_update_state(self):
        pass

    def tearDown(self):
        super().tearDown()