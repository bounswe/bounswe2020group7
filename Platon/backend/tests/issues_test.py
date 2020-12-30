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
from app.profile_management.models import Jobs
from app.workspace_system.models import Workspace, Contribution, Issue, IssueAssignee, IssueComment
from app.auth_system.views import generate_token
from app import db
import datetime
import json
from app.workspace_system.helpers import WorkspaceState

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

    def test_get_issue_info(self):
        # Can will try to get an issue which does not exist in that workspace.
        # Fail
        valid_token = generate_token(2, datetime.timedelta(minutes=10))

        data = {'issue_id': 1,'workspace_id': 2}
        actual_response = self.client.get('/api/workspaces/issue/info', query_string=data,
                                          headers={'auth_token': valid_token})

        self.assertEqual(actual_response.status_code, 404, 'Incorrect HTTP Response Code')

        # Can(2) will try to get an issue of a public workspace. It should be successful.
        valid_token = generate_token(2, datetime.timedelta(minutes=10))

        data = {'issue_id': 1,'workspace_id': 1}
        actual_response = self.client.get('/api/workspaces/issue/info', query_string=data,
                                          headers={'auth_token': valid_token})

        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')

        # Can(2) will try to get an issue of a private workspace in which
        # he is not anymore active contributor. It should be unsuccessful.
        valid_token = generate_token(2, datetime.timedelta(minutes=10))

        data = {'issue_id': 3,'workspace_id': 3}
        actual_response = self.client.get('/api/workspaces/issue/info', query_string=data,
                                          headers={'auth_token': valid_token})

        self.assertEqual(actual_response.status_code, 401, 'Incorrect HTTP Response Code')

        # Can(2) will try to get an issue with its collaborators.
        # Success
        valid_token = generate_token(2, datetime.timedelta(minutes=10))

        data = {'issue_id': 2,'workspace_id': 2}
        actual_response = self.client.get('/api/workspaces/issue/info', query_string=data,
                                          headers={'auth_token': valid_token})

        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')
        
        expected_list_length = 2
        self.assertEqual(len(actual_response.json.get('contributors')), expected_list_length)

   
    def test_get_issues(self):

        # Can will try to get the issues of a public workspace in which there is no issue.
        valid_token = generate_token(2, datetime.timedelta(minutes=10))

        data = {'workspace_id': 4}
        actual_response = self.client.get('/api/workspaces/issue', query_string=data,
                                          headers={'auth_token': valid_token})

        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')

        # Can(2) will try to get the issues of a public workspace. It should be successful.
        valid_token = generate_token(2, datetime.timedelta(minutes=10))

        data = {'workspace_id': 1}
        actual_response = self.client.get('/api/workspaces/issue', query_string=data,
                                          headers={'auth_token': valid_token})

        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')

        # Umut(1) will try to get the issues of a private workspace. It should be unsuccessful.
        valid_token = generate_token(1, datetime.timedelta(minutes=10))

        data = {'workspace_id': 2}
        actual_response = self.client.get('/api/workspaces/issue', query_string=data,
                                          headers={'auth_token': valid_token})

        self.assertEqual(actual_response.status_code, 401, 'Incorrect HTTP Response Code')

        # Can(2) will try to get the issues of a private workspace in which
        # he is an active contributor. It should be successful.
        valid_token = generate_token(2, datetime.timedelta(minutes=10))

        data = {'workspace_id': 2}
        actual_response = self.client.get('/api/workspaces/issue', query_string=data,
                                          headers={'auth_token': valid_token})

        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')

        # Can(2) will try to get the issues of a private workspace in which
        # he is not anymore active contributor. It should be unsuccessful.

        valid_token = generate_token(2, datetime.timedelta(minutes=10))

        data = {'workspace_id': 3}
        actual_response = self.client.get('/api/workspaces/issue', query_string=data,
                                          headers={'auth_token': valid_token})

        self.assertEqual(actual_response.status_code, 401, 'Incorrect HTTP Response Code')

    def test_post_issues(self):

        # Can(2) will try to post an issue into an workspace in which he is a contributor. Success.
        valid_token = generate_token(2, datetime.timedelta(minutes=10))
        data = {'workspace_id': 2, 'title': "vlog idea", 'description': "why not vlogging the diffs?", 'deadline': datetime.datetime(2020, 12, 30)}
        actual_response = self.client.post('/api/workspaces/issue', data=data,
                                          headers={'auth_token': valid_token})

        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')

        # Can(2) will try to post an issue into an workspace in which he is a contributor without a deadline. Success.
        valid_token = generate_token(2, datetime.timedelta(minutes=10))
        data = {'workspace_id': 2, 'title': "vlog idea vol 2", 'description': "why not vlogging the diffs?"}
        actual_response = self.client.post('/api/workspaces/issue', data=data,
                                          headers={'auth_token': valid_token})

        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')


        # Can(2) will try to post an issue into an workspace in which he is not a contributor. Fail.
        valid_token = generate_token(2, datetime.timedelta(minutes=10))
        data = {'workspace_id': 1, 'title': "vlog idea", 'description': "why not vlogging the diffs?", 'deadline': datetime.datetime(2020, 12, 30)}
        actual_response = self.client.post('/api/workspaces/issue', data=data,
                                          headers={'auth_token': valid_token})

        # 404 is used, because active_contribution_required decorator returns 404.
        self.assertEqual(actual_response.status_code, 404, 'Incorrect HTTP Response Code')

        # Check if issue_id is returned.
        # Can(2) will try to post an issue into an workspace in which he is a contributor. Success.
        valid_token = generate_token(2, datetime.timedelta(minutes=10))
        data = {'workspace_id': 2, 'title': "issue id test", 'description': "burhan's request", 'deadline': datetime.datetime(2020, 12, 30)}
        actual_response = self.client.post('/api/workspaces/issue', data=data,
                                          headers={'auth_token': valid_token})

        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')
        self.assertEqual(actual_response.json.get("issue_id"), 6, "Incorrect issue_id")

    def test_put_issues(self):

        # Can(2) will try to update an issue at an workspace in which he is a contributor. Success.
        valid_token = generate_token(2, datetime.timedelta(minutes=10))
        data = {'workspace_id': 2, 'issue_id': 2, 'description': "hi alperen", 'deadline': datetime.datetime(2020, 12, 30), 'is_open': False}
        actual_response = self.client.put('/api/workspaces/issue', data=data,
                                          headers={'auth_token': valid_token})

        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')

        # Can(2) will try to update an issue at an workspace in which he is not a contributor. Fail.
        valid_token = generate_token(2, datetime.timedelta(minutes=10))
        data = {'workspace_id': 1, 'issue_id': 2, 'title': "alperen related issue 2", 'description': "hi alperen", 'deadline': datetime.datetime(2020, 12, 30), 'is_open': True}
        actual_response = self.client.put('/api/workspaces/issue', data=data,
                                          headers={'auth_token': valid_token})
        
        # 404 is used, because active_contribution_required decorator returns 404.
        self.assertEqual(actual_response.status_code, 404, 'Incorrect HTTP Response Code')

    def test_delete_issues(self):

        # Can(2) will try to delete an issue at an workspace in which he is a contributor. Success.
        valid_token = generate_token(2, datetime.timedelta(minutes=10))
        data = {'workspace_id': 2, 'issue_id': 2}
        actual_response = self.client.delete('/api/workspaces/issue', data=data,
                                          headers={'auth_token': valid_token})

        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')

        # Can(2) will try to delete an issue at an workspace in which he is not a contributor. Fail.
        valid_token = generate_token(2, datetime.timedelta(minutes=10))
        data = {'workspace_id': 1, 'issue_id': 1}
        actual_response = self.client.delete('/api/workspaces/issue', data=data,
                                          headers={'auth_token': valid_token})

        # 404 is used, because active_contribution_required decorator returns 404.
        self.assertEqual(actual_response.status_code, 404, 'Incorrect HTTP Response Code')

    def test_get_issue_assignees(self):

        # Hilal(4) will try to get the issue assignees of a public workspace in which there is no issue assignee.
        # Success
        valid_token = generate_token(4, datetime.timedelta(minutes=10))

        data = {'workspace_id': 3, 'issue_id': 3}
        actual_response = self.client.get('/api/workspaces/issue/assignee', query_string=data,
                                          headers={'auth_token': valid_token})

        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')

        # Can(2) will try to get the issue assignees at a public workspace. Success.
        valid_token = generate_token(2, datetime.timedelta(minutes=10))

        data = {'workspace_id': 1, 'issue_id': 1}
        actual_response = self.client.get('/api/workspaces/issue/assignee', query_string=data,
                                          headers={'auth_token': valid_token})

        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')

        # Can(2) will try to get the issue assignees at a private workspace. Fail.
        valid_token = generate_token(2, datetime.timedelta(minutes=10))

        data = {'workspace_id': 3, 'issue_id': 3}
        actual_response = self.client.get('/api/workspaces/issue/assignee', query_string=data,
                                          headers={'auth_token': valid_token})

        self.assertEqual(actual_response.status_code, 401, 'Incorrect HTTP Response Code')

        # Can(2) will try to get the issue assignees at a private workspace 
        # in which he is a contributor. Success.
        valid_token = generate_token(2, datetime.timedelta(minutes=10))

        data = {'workspace_id': 2, 'issue_id':2}
        actual_response = self.client.get('/api/workspaces/issue/assignee', query_string=data,
                                          headers={'auth_token': valid_token})

        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')

    def test_post_issue_assignees(self):

        # Hilal(4) will try to post an issue assignee at an workspace in which she is a contributor. 
        # Assignee(Umut(1)) is also the contributor of the workspace.
        # Success.
        valid_token = generate_token(4, datetime.timedelta(minutes=10))
        data = {'workspace_id': 3, 'issue_id': 3, 'assignee_id': 1}
        actual_response = self.client.post('/api/workspaces/issue/assignee', data=data,
                                          headers={'auth_token': valid_token})

        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')

        # Can(2) will try to post an issue assignee at an workspace in which he is a contributor. 
        # Assignee is not a contributor of the workspace.
        # Fail.
        valid_token = generate_token(2, datetime.timedelta(minutes=10))
        data = {'workspace_id': 2, 'issue_id': 2, 'assignee_id': 4}
        actual_response = self.client.post('/api/workspaces/issue/assignee', data=data,
                                          headers={'auth_token': valid_token})

        self.assertEqual(actual_response.status_code, 404, 'Incorrect HTTP Response Code')

        # Can(2) will try to post issue assignee at an workspace in which he is not a contributor. Fail.
        valid_token = generate_token(2, datetime.timedelta(minutes=10))
        data = {'workspace_id': 1, 'issue_id': 1, 'assignee_id': 4}
        actual_response = self.client.post('/api/workspaces/issue/assignee', data=data,
                                          headers={'auth_token': valid_token})

        self.assertEqual(actual_response.status_code, 404, 'Incorrect HTTP Response Code')

        # Can(2) will try to post an issue assignee at an workspace in which he is a contributor.
        # However, that person already exists in the issue assignees.
        # Fail.
        valid_token = generate_token(2, datetime.timedelta(minutes=10))
        data = {'workspace_id': 2, 'issue_id': 2, 'assignee_id': 3}
        actual_response = self.client.post('/api/workspaces/issue/assignee', data=data,
                                          headers={'auth_token': valid_token})

        self.assertEqual(actual_response.status_code, 403, 'Incorrect HTTP Response Code')

    def test_delete_issue_assignees(self):

        # Can(2) will try to remove issue assignee at an workspace in which he is a contributor. Success.
        valid_token = generate_token(2, datetime.timedelta(minutes=10))
        data = {'workspace_id': 2, 'issue_id': 2, 'assignee_id': 3}
        actual_response = self.client.delete('/api/workspaces/issue/assignee', data=data,
                                          headers={'auth_token': valid_token})
        
        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')

        # Can(2) will try to remove issue assignee at an workspace in which he is not a contributor. Fail.
        valid_token = generate_token(2, datetime.timedelta(minutes=10))
        data = {'workspace_id': 1, 'issue_id': 1, 'assignee_id': 1}
        actual_response = self.client.delete('/api/workspaces/issue/assignee', data=data,
                                          headers={'auth_token': valid_token})
        
        self.assertEqual(actual_response.status_code, 404, 'Incorrect HTTP Response Code')

    def test_get_issue_comments(self):

        # Can(2) will try to get the issue comments at a public workspace which has no issue comments. 
        # It will return partial list since it is empty. Success.
        valid_token = generate_token(2, datetime.timedelta(minutes=10))

        data = {'workspace_id': 1, 'issue_id': 1}
        actual_response = self.client.get('/api/workspaces/issue/comment', query_string=data,
                                          headers={'auth_token': valid_token})

        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')

        # Can(2) will try to get the issue comments at a private workspace. Fail.
        valid_token = generate_token(2, datetime.timedelta(minutes=10))

        data = {'workspace_id': 3, 'issue_id': 3}
        actual_response = self.client.get('/api/workspaces/issue/comment', query_string=data,
                                          headers={'auth_token': valid_token})

        self.assertEqual(actual_response.status_code, 401, 'Incorrect HTTP Response Code')

        # Can(2) will try to get the issue comments at a private workspace 
        # in which he is a contributor. Success.
        valid_token = generate_token(2, datetime.timedelta(minutes=10))

        data = {'workspace_id': 2, 'issue_id': 2}
        actual_response = self.client.get('/api/workspaces/issue/comment', query_string=data,
                                          headers={'auth_token': valid_token})

        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')

    def test_post_issue_comments(self):

        # Can(2) will try to post issue comment at an workspace in which he is a contributor. Success.
        valid_token = generate_token(2, datetime.timedelta(minutes=10))
        data = {'workspace_id': 2, 'issue_id': 2, 'comment': "GREAAAT"}
        actual_response = self.client.post('/api/workspaces/issue/comment', data=data,
                                          headers={'auth_token': valid_token})

        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')

        # Can(2) will try to post issue comment at an workspace in which he is not a contributor. Fail.
        valid_token = generate_token(2, datetime.timedelta(minutes=10))
        data = {'workspace_id': 1, 'issue_id': 1, 'comment': "BADDDD"}
        actual_response = self.client.post('/api/workspaces/issue/comment', data=data,
                                          headers={'auth_token': valid_token})

        self.assertEqual(actual_response.status_code, 404, 'Incorrect HTTP Response Code')

    def test_delete_issue_comments(self):

        # Can(2) will try to remove issue comment at an workspace in which he is a contributor. 
        # Also it is his comment.
        # Success.
        valid_token = generate_token(2, datetime.timedelta(minutes=10))
        data = {'workspace_id': 2, 'issue_id': 2, 'comment_id': 1}
        actual_response = self.client.delete('/api/workspaces/issue/comment', data=data,
                                          headers={'auth_token': valid_token})

        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')

        # Can(2) will try to remove issue comment at an workspace in which he is a contributor. 
        # However it is not his comment.
        # Fail.
        valid_token = generate_token(2, datetime.timedelta(minutes=10))
        data = {'workspace_id': 2, 'issue_id': 2, 'comment_id': 2}
        actual_response = self.client.delete('/api/workspaces/issue/comment', data=data,
                                          headers={'auth_token': valid_token})

        self.assertEqual(actual_response.status_code, 401, 'Incorrect HTTP Response Code')

        # Can(2) will try to remove issue comment at an workspace in which he is not a contributor. Fail.
        valid_token = generate_token(2, datetime.timedelta(minutes=10))
        data = {'workspace_id': 3, 'issue_id': 3, 'comment_id': 3}
        actual_response = self.client.delete('/api/workspaces/issue/comment', data=data,
                                          headers={'auth_token': valid_token})

        self.assertEqual(actual_response.status_code, 404, 'Incorrect HTTP Response Code')

    def tearDown(self):
        super().tearDown()