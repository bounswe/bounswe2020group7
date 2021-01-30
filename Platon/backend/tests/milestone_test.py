## ASSUMPTION!!!!!!!!
        # workspace state is 1, for Search for Collaborators State
        # workspace state is 2, for Ongoing State
        # workspace state is 3, for Published State



from tests.base_test import BaseTest
from tests.base_test import TestConfig
from app.auth_system.models import User
from app.profile_management.models import Jobs
from app.workspace_system.models import Workspace, Contribution, Issue, IssueAssignee, IssueComment, Milestone
from app.auth_system.views import generate_token
from app import db
import datetime
import json
from app.workspace_system.helpers import WorkspaceState

class MilestoneTest(BaseTest):
    """
        Unit Tests of the Milestones
    """

    def setUp(self):

        super().setUp()

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

        milestones = [
            Milestone(1, 1, "coronovirus milestone", "eat more sarimsak daily", datetime.datetime(2020, 12, 31, 23, 59, 59)),
            Milestone(2, 2, "Can's milestone", "denemeeee", datetime.datetime(2020, 12, 31)),
            Milestone(4, 3, "Hilal's milestone", "really", datetime.datetime(2020, 12, 31, 23, 59, 59))
        ]

        for milestone in milestones:
            db.session.add(milestone)
        db.session.commit()

        
    def test_get_milestones(self):

        # Can will try to get the milestones of a public workspace in which there is no milestone.
        valid_token = generate_token(2, datetime.timedelta(minutes=10))

        data = {'workspace_id': 4}
        actual_response = self.client.get('/api/workspaces/milestone', query_string=data,
                                          headers={'auth_token': valid_token})

        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')

        # Can(2) will try to get the milestones of a public workspace. It should be successful.
        valid_token = generate_token(2, datetime.timedelta(minutes=10))

        data = {'workspace_id': 1}
        actual_response = self.client.get('/api/workspaces/milestone', query_string=data,
                                          headers={'auth_token': valid_token})

        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')

        # Umut(1) will try to get the milestones of a private workspace. It should be unsuccessful.
        valid_token = generate_token(1, datetime.timedelta(minutes=10))

        data = {'workspace_id': 2}
        actual_response = self.client.get('/api/workspaces/milestone', query_string=data,
                                          headers={'auth_token': valid_token})

        self.assertEqual(actual_response.status_code, 401, 'Incorrect HTTP Response Code')

        # Can(2) will try to get the milestones of a private workspace in which
        # he is an active contributor. It should be successful.
        valid_token = generate_token(2, datetime.timedelta(minutes=10))

        data = {'workspace_id': 2}
        actual_response = self.client.get('/api/workspaces/milestone', query_string=data,
                                          headers={'auth_token': valid_token})

        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')

        # Can(2) will try to get the milestones of a private workspace in which
        # he is not anymore active contributor. It should be unsuccessful.

        valid_token = generate_token(2, datetime.timedelta(minutes=10))

        data = {'workspace_id': 3}
        actual_response = self.client.get('/api/workspaces/milestone', query_string=data,
                                          headers={'auth_token': valid_token})

        self.assertEqual(actual_response.status_code, 401, 'Incorrect HTTP Response Code')

    def test_post_milestones(self):

        # Can(2) will try to post an milestone into an workspace in which he is a contributor. Success.
        valid_token = generate_token(2, datetime.timedelta(minutes=10))
        data = {'workspace_id': 2, 'title': "cust milestone", 'description': "where are the deliverables?", 'deadline': datetime.datetime(2020, 12, 30)}
        actual_response = self.client.post('/api/workspaces/milestone', data=data,
                                          headers={'auth_token': valid_token})

        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')


        # Can(2) will try to post an milestone into an workspace in which he is not a contributor. Fail.
        valid_token = generate_token(2, datetime.timedelta(minutes=10))
        data = {'workspace_id': 1, 'title': "customer milestone", 'description': "where are your deliverables?", 'deadline': datetime.datetime(2020, 12, 30)}
        actual_response = self.client.post('/api/workspaces/milestone', data=data,
                                          headers={'auth_token': valid_token})

        # 404 is used, because active_contribution_required decorator returns 404.
        self.assertEqual(actual_response.status_code, 404, 'Incorrect HTTP Response Code')

    def test_put_milestones(self):

        # Can(2) will try to update an milestone at an workspace in which he is a contributor. Success.
        valid_token = generate_token(2, datetime.timedelta(minutes=10))
        data = {'workspace_id': 2, 'milestone_id': 2, 'description': "MILESTONEEEEEEE", 'deadline': datetime.datetime(2020, 12, 30)}
        actual_response = self.client.put('/api/workspaces/milestone', data=data,
                                          headers={'auth_token': valid_token})

        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')

        # Can(2) will try to update an milestone at an workspace in which he is not a contributor. Fail.
        valid_token = generate_token(2, datetime.timedelta(minutes=10))
        data = {'workspace_id': 1, 'milestone_id': 2, 'title': "milestoneeeee", 'description': "goodbye", 'deadline': datetime.datetime(2020, 12, 30)}
        actual_response = self.client.put('/api/workspaces/milestone', data=data,
                                          headers={'auth_token': valid_token})
        
        # 404 is used, because active_contribution_required decorator returns 404.
        self.assertEqual(actual_response.status_code, 404, 'Incorrect HTTP Response Code')

    def test_delete_milestones(self):

        # Can(2) will try to delete an milestone at an workspace in which he is a contributor. Success.
        valid_token = generate_token(2, datetime.timedelta(minutes=10))
        data = {'workspace_id': 2, 'milestone_id': 2}
        actual_response = self.client.delete('/api/workspaces/milestone', data=data,
                                          headers={'auth_token': valid_token})

        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')

        # Can(2) will try to delete an milestone at an workspace in which he is not a contributor. Fail.
        valid_token = generate_token(2, datetime.timedelta(minutes=10))
        data = {'workspace_id': 1, 'milestone_id': 1}
        actual_response = self.client.delete('/api/workspaces/milestone', data=data,
                                          headers={'auth_token': valid_token})

        # 404 is used, because active_contribution_required decorator returns 404.
        self.assertEqual(actual_response.status_code, 404, 'Incorrect HTTP Response Code')

    def tearDown(self):
        super().tearDown()