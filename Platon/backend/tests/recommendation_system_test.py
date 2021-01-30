from tests.base_test import BaseTest
from app.auth_system.models import User
from app.profile_management.models import Jobs,Skills,UserSkills
from app.auth_system.views import generate_token
from app.follow_system.models import Follow, FollowRequests
from app.workspace_system.models import Workspace, Contribution,WorkspaceSkill
from app.workspace_system.helpers import WorkspaceState
from app.recommendation_system.helpers import RecommendationSystem


from app import db
import datetime
import json

class RecommendationSystemTest(BaseTest):

    def setUp(self):

        jobs = [
            Jobs("academician"),
            Jobs("PhD student")
        ]

        for job in jobs:
            db.session.add(job)

        db.session.commit()

        skills = [
            Skills("Java"),
            Skills("C"),
            Skills("Python"),
            Skills("Design Patterns"),
            Skills("Deep Learning"),
            Skills("Medicine")
        ]
        
        for skill in skills:
            db.session.add(skill)
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

        user_skills = [
            UserSkills(1,2),
            UserSkills(1,3),
            UserSkills(1,5),
            UserSkills(2,6),
            UserSkills(2,3),
            UserSkills(2,1),
            UserSkills(3,4),
            UserSkills(3,1),
            UserSkills(3,5),
            UserSkills(4,6),
            UserSkills(4,3)
        ]
        
        for user_skill in user_skills:
            db.session.add(user_skill)

        db.session.commit()

        # Add artificial users to test follow feature.
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
            Workspace(creator_id = 2, is_private = 0, title = "SWE difficulties", description = "deneme", state = WorkspaceState.search_for_collaborator.value), # private workspace by Can on state 1
            Workspace(creator_id = 4, is_private = 1, title = "honeybadger", description = "deneme", state = WorkspaceState.search_for_collaborator.value), # private workspace by Hilal on state 1
            Workspace(creator_id = 3, is_private = 0, title = "bos", description = "deneme", state = WorkspaceState.search_for_collaborator.value) # public workspace by Alperen on state 1
        ]

        for workspace in workspaces:
            db.session.add(workspace)
        
        db.session.commit()

        workspace_skills = [
            WorkspaceSkill(workspace_id = 1, skill_id = 6),
            WorkspaceSkill(workspace_id = 1, skill_id = 5),
            WorkspaceSkill(workspace_id = 1, skill_id = 3),
            WorkspaceSkill(workspace_id = 2, skill_id = 1),
            WorkspaceSkill(workspace_id = 2, skill_id = 2),
            WorkspaceSkill(workspace_id = 2, skill_id = 3),
            WorkspaceSkill(workspace_id = 2, skill_id = 4),
            WorkspaceSkill(workspace_id = 3, skill_id = 5),
            WorkspaceSkill(workspace_id = 3, skill_id = 2),
            WorkspaceSkill(workspace_id = 4, skill_id = 1),
            WorkspaceSkill(workspace_id = 4, skill_id = 3)
        ]

        for workspace_skill in workspace_skills:
            db.session.add(workspace_skill)
        
        db.session.commit()
        
        contributions = [
            Contribution(workspace_id = 1, user_id = 1, is_active = 1), # Umut will be active in coronovirus study
            Contribution(workspace_id = 2, user_id = 2, is_active = 1), # can will be active in SWE difficulties
            Contribution(workspace_id = 2, user_id = 3, is_active = 1),  # alperen will be active in SWE difficulties
            Contribution(workspace_id = 3, user_id = 2, is_active = 0), # can will be inactive in honey badger study
            Contribution(workspace_id = 3, user_id = 4, is_active = 1), # hilal will be active in honey badger study
            Contribution(workspace_id = 4, user_id = 3, is_active = 1) # alperen will be active in bos
        ]

        for contribution in contributions:
            db.session.add(contribution)
        db.session.commit()

        # Initialize Recommendation System
        RecommendationSystem.update_all_follow_recommendations()
        RecommendationSystem.update_all_workspace_recommendations()
        RecommendationSystem.update_all_collaboration_recommendations()
    
    def test_workspace_recommendations(self):
        valid_token = generate_token(1, datetime.timedelta(minutes=10))
        actual_response = self.client.get('/api/recommendation_system/workspace', query_string={"number_of_recommendations":4},
                                          headers={'auth_token': valid_token})
        expected_response = {'recommendation_list': [{  'contributor_list': 
                                                                        [{'id': 3,
                                                                            'name': 'Alperen',
                                                                            'surname': 'Ozprivate'}],
                                                        'creator_id': 3,
                                                        'description': 'deneme',
                                                        'id': 4,
                                                        'state': 0,
                                                        'title': 'bos'},
                                                    {   'contributor_list': [{'id': 2, 'name': 'Can', 'surname': 'Deneme'},
                                                                            {'id': 3, 'name': 'Alperen', 'surname': 'Ozprivate'}],
                                                        'creator_id': 2,
                                                        'description': 'deneme',
                                                        'id': 2,
                                                        'state': 0,
                                                        'title': 'SWE difficulties'}]}

        
        self.assertEqual(expected_response, json.loads(actual_response.data))
        self.assertEqual(200, actual_response.status_code)
    
    def test_follow_recommendations(self):
        valid_token = generate_token(3, datetime.timedelta(minutes=10))
        actual_response = self.client.get('/api/recommendation_system/follow', query_string={"number_of_recommendations":4},
                                          headers={'auth_token': valid_token})
        expected_response = {'recommendation_list': 
                                [
                                    {'id': 4, 
                                    'institution': 'boun', 
                                    'is_private': 1, 
                                    'job': 'academician', 
                                    'name': 'Hilal', 
                                    'profile_photo': '/auth_system/logo', 
                                    'surname': 'Private'}
                                    ]}

        self.assertEqual(expected_response, json.loads(actual_response.data))
        self.assertEqual(200, actual_response.status_code)
    
    def test_collaboration_recommendations(self):
        valid_token = generate_token(4, datetime.timedelta(minutes=10))
        actual_response = self.client.get('/api/recommendation_system/collaboration', query_string={"number_of_recommendations":4,"workspace_id":3},
                                          headers={'auth_token': valid_token})
        expected_response = {'recommendation_list': [
                                                        {
                                                            'id': 1, 
                                                            'institution': 'boun', 
                                                            'is_private': 0, 
                                                            'job': 'academician', 
                                                            'name': 'Umut', 
                                                            'profile_photo': '/auth_system/logo', 
                                                            'surname': 'Özdemir'
                                                        }, 
                                                        {
                                                            'id': 2, 
                                                            'institution': 'boun', 
                                                            'is_private': 0, 
                                                            'job': 'PhD student', 
                                                            'name': 'Can', 
                                                            'profile_photo': '/auth_system/logo', 
                                                            'surname': 'Deneme'
                                                        }, 
                                                        {
                                                            'id': 3, 
                                                            'institution': 'boun', 
                                                            'is_private': 1, 
                                                            'job': 'academician', 
                                                            'name': 'Alperen', 
                                                            'profile_photo': '/auth_system/logo', 
                                                            'surname': 'Ozprivate'
                                                        }
                                                            ]}

        self.assertEqual(expected_response, json.loads(actual_response.data))
        self.assertEqual(200, actual_response.status_code)
    