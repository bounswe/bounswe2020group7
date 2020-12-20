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
            Workspace(1, 0, "coronovirus study", WorkspaceState.search_for_collaborator.value), # public workspace by Umut on state 1
            Workspace(2, 1, "SWE difficulties", WorkspaceState.search_for_collaborator.value), # private workspace by Can on state 1
            Workspace(4, 1, "honeybadger", WorkspaceState.search_for_collaborator.value), # private workspace by Hilal on state 1
            Workspace(3, 0, "bos", WorkspaceState.search_for_collaborator.value) # public workspace by Alperen on state 1
        ]

        for workspace in workspaces:
            db.session.add(workspace)
        
        db.session.commit()

        contributions = [
            Contribution(1, 1, 1), # Umut will be active in coronovirus study
            Contribution(2, 2, 1), # can will be active in SWE difficulties
            Contribution(2, 3, 1),  # alperen will be active in SWE difficulties
            Contribution(3, 2, 0), # can will be inactive in honey badger study
            Contribution(3, 4, 1), # hilal will be active in honey badger study
            Contribution(3, 1, 1), # umut will be active in honey badger study
            Contribution(4, 3, 1) # alperen will be active in bos
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



        '''
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
        '''
        
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