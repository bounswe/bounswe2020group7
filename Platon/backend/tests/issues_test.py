'''
    For states, use enum!!!!!!!
'''

## ASSUMPTION!!!!!!!!
        # workspace state is 1, for Search for Collaborators State
        # workspace state is 2, for Ongoing State
        # workspace state is 3, for Published State



from tests.base_test import BaseTest
from tests.base_test import TestConfig
from app.auth_system.models import User
from app.workspace_system.models import Workspace, Contribution, Issue, IssueAssignee, IssueComment
from app.auth_system.views import generate_token
from app import db
import datetime
import json

class IssuesTest(BaseTest):
    """
        Unit Tests of the Issues
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

        workspaces = [
            Workspace(1, 0, "coronovirus study", 1), # public workspace by Umut on state 1
            Workspace(2, 1, "SWE difficulties", 1) # private workspace by Can on state 1
            Workspace(4, 1, "honeybadger", 1) # private workspace by Hilal on state 1
        ]

        for workspace in workspaces:
            db.session.add(workspace)
        
        db.session.commit()

        contributions = [
            Contribution(1, 1, 1), # Umut will be active in coronovirus study
            Contribution(2, 2, 1), # can will be active in SWE difficulties
            Contribution(2, 3, 1),  # alperen will be active in SWE difficulties
            Contribution(3, 2, 0), # can will be inactive in honey badger study
            Contribution(3, 4, 1) # hilal will be active in honey badger study
        ]

        for contribution in contributions:
            db.session.add(contribution)
        db.session.commit()

        issues = [
            Issue(1, 1, "cure of coronovirus", "eat more sarimsak daily"),
            Issue(2, 2, "alperen's assigned issue", "denemeeee")
        ]

        for issue in issues:
            db.session.add(issue)
        db.session.commit()

        issue_assignees = [
            IssueAssignee(2, 3) # alperen is assigned on issue which has issue id 2.
        ]

        for issue_assignee in issue_assignees:
            db.session.add(issue_assignee)
        db.session.commit()

        issue_comments = [
            IssueComment(2, 2, "makes sense") # can make comment on alperen's assigned issue
        ]

        for issue_comment in issue_comments:
            db.session.add(issue_comment)
        db.session.commit()
        


    def test_get_issues(self):
        ''' 
            - Input:
                - token: STRING - Header
                - workspace_id: INT 
                - page: INT 
                - per_page: INT
        '''
        # Can(2) will try to get the issues of a public workspace. It should be successful.
        valid_token = generate_token(2, datetime.timedelta(minutes=10))

        data = {'workspace_id': 1}
        actual_response = self.client.get('/api/workspace/issue', query_string=data,
                                          headers={'auth_token': valid_token})

        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')

        # Umut(1) will try to get the issues of a private workspace. It should be unsuccessful.
        valid_token = generate_token(1, datetime.timedelta(minutes=10))

        data = {'workspace_id': 2}
        actual_response = self.client.get('/api/workspace/issue', query_string=data,
                                          headers={'auth_token': valid_token})

        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')

        # Can(2) will try to get the issues of a private workspace in which
        # he is an active contributor. It should be successful.
        valid_token = generate_token(1, datetime.timedelta(minutes=10))

        data = {'workspace_id': 2}
        actual_response = self.client.get('/api/workspace/issue', query_string=data,
                                          headers={'auth_token': valid_token})

        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')

        # Can(2) will try to get the issues of a private workspace in which
        # he is not anymore active contributor. It should be unsuccessful.

        valid_token = generate_token(2, datetime.timedelta(minutes=10))

        data = {'workspace_id': 3}
        actual_response = self.client.get('/api/workspace/issue', query_string=data,
                                          headers={'auth_token': valid_token})

        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')

    def test_post_issues(self):
        # test for the POST method

        # Can(2) will try to post an issue into an workspace in which he is a contributor. Success.

        # Can(2) will try to post an issue into an workspace in which he is not a contributor. Fail.

        pass

    def test_put_issues(self):
        # test for the PUT method

        # Can(2) will try to update an issue at an workspace in which he is a contributor. Success.

        # Can(2) will try to update an issue at an workspace in which he is not a contributor. Fail.

        pass

    def test_delete_issues(self):
        # test for the DELETE method

        # Can(2) will try to delete an issue at an workspace in which he is a contributor. Success.

        # Can(2) will try to delete an issue at an workspace in which he is not a contributor. Fail.
        pass

    def test_get_issue_assignees(self):
        # test for the GET method of IssueAssigneeAPI

        # Can(2) will try to get the issue assignees at a public workspace. Success.

        # Can(2) will try to get the issue assignees at a private workspace. Fail.

        # Can(2) will try to get the issue assignees at a private workspace 
        # in which he is a contributor. Success.

        pass

    def test_post_issue_assignees(self):
        # test for the POST method of IssueAssigneeAPI

        # Can(2) will try to post issue assignee at an workspace in which he is a contributor. Success.

        # Can(2) will try to post issue assignee at an workspace in which he is not a contributor. Fail.
        pass

    def test_delete_issue_assignees(self):
        # test for the DELETE method of IssueAssigneeAPI

        # Can(2) will try to remove issue assignee at an workspace in which he is a contributor. Success.

        # Can(2) will try to remove issue assignee at an workspace in which he is not a contributor. Fail.
        pass

    def test_get_issue_comments(self):
        # test for the GET method of IssueCommentAPI

        # Can(2) will try to get the issue comments at a public workspace. Success.

        # Can(2) will try to get the issue comments at a private workspace. Fail.

        # Can(2) will try to get the issue comments at a private workspace 
        # in which he is a contributor. Success.

        
        pass

    def test_post_issue_comments(self):
        # test for the POST method of IssueCommentAPI

        # Can(2) will try to post issue comment at an workspace in which he is a contributor. Success.

        # Can(2) will try to post issue comment at an workspace in which he is not a contributor. Fail.

        pass

    def test_delete_issue_comments(self):
        # test for the DELETE method of IssueCommentAPI

        # Can(2) will try to remove issue comment at an workspace in which he is a contributor. Success.

        # Can(2) will try to remove issue comment at an workspace in which he is not a contributor. Fail.
        pass

    def tearDown(self):
        super().tearDown()