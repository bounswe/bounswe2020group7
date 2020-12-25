from tests.base_test import BaseTest
from tests.base_test import TestConfig

from app.auth_system.models import User
from app.profile_management.models import Jobs, Skills, UserSkills
from app.search_engine.models import SearchHistoryItem
from app.auth_system.views import generate_token
from app.workspace_system.models import Workspace, WorkspaceSkill, Contribution
from app.workspace_system.helpers import WorkspaceState

from app import db
import json
import datetime

class SearchHistoryTests(BaseTest):
    
    def setUp(self):
        # Add artificial users to test login feature
        jobs = [
            Jobs("Academician"),
            Jobs("PhD Student")
        ]

        for job in jobs:
            db.session.add(job)

        db.session.commit()
        # Add artificia users to test login feature
        users = [
            User("umut@deneme.com", True, "b73ec5e4625ffcb6d0d70826f33be7a75d45b37046e26c4b60d9111266d70e32", 3.5,
                 "Umut", "Özdemir", False, None, None, None, 1, "boun"),
            User("can@deneme.com", False, "cce0c2170d1ae52e099c716165d80119ee36840e3252e57f2b2b4d6bb111d8a5", 3.4,
                 "Can", "Deneme", False, None, None, None, 2, "boun")
        ]
        for user in users:
            db.session.add(user)
        db.session.commit()
        
        search_history = [
            SearchHistoryItem(1,"can",0),
            SearchHistoryItem(1,"boun",1),
            SearchHistoryItem(1,"nanonetwork",2),
            SearchHistoryItem(1,"umut",0),
            SearchHistoryItem(1,"umut",0),
            SearchHistoryItem(2,"umut",0),
            SearchHistoryItem(2,"umut",0),
            SearchHistoryItem(2,"computer networks",1)
        ]

        for hist_item in search_history:
            db.session.add(hist_item)
        db.session.commit()
    
    def test_user_search_history(self):
        valid_token = generate_token(1,TestConfig.SESSION_DURATION)
        actual_response = self.client.get("/api/search_engine/search_history",
                                              query_string={"search_type":0},
                                              headers={"auth_token":valid_token})
        expected_response = {"search_history": [
                {
                    "query": "umut",
                    "number_of_use": 2
                },
                {
                    "query": "can",
                    "number_of_use": 1
                }
            ]}
        self.assertEqual(actual_response.status_code,200)
        self.assertEqual(json.loads(actual_response.data),expected_response)
    
    def test_workspace_search(self):
        valid_token = generate_token(1,TestConfig.SESSION_DURATION)
        actual_response = self.client.get("/api/search_engine/search_history",
                                              query_string={"search_type":1},
                                              headers={"auth_token":valid_token})
        expected_response = {"search_history": [
                {
                    "query": "boun",
                    "number_of_use": 1
                }
            ]}
        self.assertEqual(actual_response.status_code,200)
        self.assertEqual(json.loads(actual_response.data),expected_response)

    def test_ue_search_history(self):
        valid_token = generate_token(1,TestConfig.SESSION_DURATION)
        actual_response = self.client.get("/api/search_engine/search_history",
                                              query_string={"search_type":2},
                                              headers={"auth_token":valid_token})
        expected_response = {"search_history": [
                {
                    "query": "nanonetwork",
                    "number_of_use": 1
                }
            ]}
        self.assertEqual(actual_response.status_code,200)
        self.assertEqual(json.loads(actual_response.data),expected_response)
    
    def test_empty_result(self):
        valid_token = generate_token(2,TestConfig.SESSION_DURATION)
        actual_response = self.client.get("/api/search_engine/search_history",
                                              query_string={"search_type":2},
                                              headers={"auth_token":valid_token})
        expected_response = {"search_history": []}
        self.assertEqual(actual_response.status_code,200)
        self.assertEqual(json.loads(actual_response.data),expected_response)
        
class UserSearchTests(BaseTest):
    
    def setUp(self):
        # Add artificial users to test login feature
        jobs = [
            Jobs("Academician"),
            Jobs("PhD Student")
        ]

        for job in jobs:
            db.session.add(job)

        db.session.commit()
        # Add artificia users to test login feature
        users = [
            User("umut@deneme.com", True, "b73ec5e4625ffcb6d0d70826f33be7a75d45b37046e26c4b60d9111266d70e32", 3.5,
                 "Umut", "Özdemir", False, None, None, None, 1, "boun"),
            User("can@deneme.com", True, "cce0c2170d1ae52e099c716165d80119ee36840e3252e57f2b2b4d6bb111d8a5", 3.4,
                 "Can", "Deneme", False, None, None, None, 2, "boun")
        ]
        for user in users:
            db.session.add(user)
        db.session.commit()
        
        skills = [
            Skills("Nanonetwork"),
            Skills("C++"),
            Skills("Python")
        ]
        for skill in skills:
            db.session.add(skill)
        db.session.commit()

        user_skills = [
            UserSkills(1,1),
            UserSkills(1,2),
            UserSkills(1,3),
            UserSkills(2,2),
            UserSkills(2,3)
        ]
        for user_skill in user_skills:
            db.session.add(user_skill)
        db.session.commit()
    
    def test_name_search(self):
        actual_response = self.client.get("/api/search_engine/user",query_string={"search_query":"umut"})
        expected_result = {
            "number_of_pages" : 1,
            "result_list" : [
                {  
                    "id": 1,
                    "is_private": 0,
                    "job_id": 1,
                    "name": "Umut",
                    "profile_photo": None,
                    "surname": "Özdemir"
                }
            ]
        }
        self.assertEqual(200,actual_response.status_code)
        self.assertEqual(expected_result,json.loads(actual_response.data))
    
    
    def test_no_result_search(self):
        actual_response = self.client.get("/api/search_engine/user",query_string={"search_query":"halil"})
        expected_result = {
            "number_of_pages" : 1,
            "result_list" : []
        }
        self.assertEqual(200,actual_response.status_code)
        self.assertEqual(expected_result,json.loads(actual_response.data))

    def test_no_result_search_filter(self):
        actual_response = self.client.get("/api/search_engine/user",query_string={"search_query":"umut","job_filter":2})
        expected_result = {
            "number_of_pages" : 1,
            "result_list" : []
        }
        self.assertEqual(200,actual_response.status_code)
        self.assertEqual(expected_result,json.loads(actual_response.data))
    
    def test_skill_search(self):
        actual_response = self.client.get("/api/search_engine/user",query_string={"search_query":"NanoNetwork"})
        expected_result = {
            "number_of_pages" : 1,
            "result_list" : [
                {  
                    "id": 1,
                    "is_private": 0,
                    "job_id": 1,
                    "name": "Umut",
                    "profile_photo": None,
                    "surname": "Özdemir"
                }
            ]
        }
        self.assertEqual(200,actual_response.status_code)
        self.assertEqual(expected_result,json.loads(actual_response.data))

    def test_job_search(self):
        actual_response = self.client.get("/api/search_engine/user",query_string={"search_query":"Academician"})
        expected_result = {
            "number_of_pages" : 1,
            "result_list" : [
                {  
                    "id": 1,
                    "is_private": 0,
                    "job_id": 1,
                    "name": "Umut",
                    "profile_photo": None,
                    "surname": "Özdemir"
                }
            ]
        }
        self.assertEqual(200,actual_response.status_code)
        self.assertEqual(expected_result,json.loads(actual_response.data))
    
    def test_semantic_search(self):
        actual_response = self.client.get("/api/search_engine/user",query_string={"search_query":"SchoolMan"})
        expected_result = {
            "number_of_pages" : 1,
            "result_list" : [
                {  
                    "id": 1,
                    "is_private": 0,
                    "job_id": 1,
                    "name": "Umut",
                    "profile_photo": None,
                    "surname": "Özdemir"
                }
            ]
        }
        self.assertEqual(200,actual_response.status_code)
        self.assertEqual(expected_result,json.loads(actual_response.data))    
    
    def test_institution_search(self):
        actual_response = self.client.get("/api/search_engine/user",query_string={"search_query":"boun"})
        expected_result = {
            "number_of_pages" : 1,
            "result_list" : [
                {  
                    "id": 1,
                    "is_private": 0,
                    "job_id": 1,
                    "name": "Umut",
                    "profile_photo": None,
                    "surname": "Özdemir"
                },
                {
                    'id': 2,
                    'is_private': 0,
                    'job_id': 2,
                    'name': 'Can',
                    'profile_photo': None,
                    'surname': 'Deneme'
                }
            ]
        }
        self.assertEqual(200,actual_response.status_code)
        self.assertEqual(expected_result,json.loads(actual_response.data))

class WorkspaceSearchTests(BaseTest):
    '''
        Tests for workspace search.
    '''
    
    def setUp(self):

        super().setUp()

        jobs = [
            Jobs("academician"),
            Jobs("PhD student")
        ]

        for job in jobs:
            db.session.add(job)

        db.session.commit()

        # Umut and Can are public users. Alperen and Hilal are private users.
        users = [
            User("umut@deneme.com", True, "b73ec5e4625ffcb6d0d70826f33be7a75d45b37046e26c4b60d9111266d70e32", 3.5,
                 "Umut", "Özdemir", False, None, None, None, 1, "boun"),
            User("can@deneme.com", True, "cce0c2170d1ae52e099c716165d80119ee36840e3252e57f2b2b4d6bb111d8a5", 3.4,
                 "Can", "Bolukbas", False, None, None, None, 2, "boun"),
            User("alperen@deneme.com", True, "hashedpassword", 4.6, "Alperen", "Ozprivate", True, None, None, None, 1, "boun"),
            User("hilal@deneme.com", True, "hasheddpassword", 4.5, "Hilal", "Private", True, None, None, None, 1, "boun")
        ]
        for user in users:
            db.session.add(user)

        db.session.commit()

        workspaces = [
            Workspace(creator_id = 1, is_private = 0, title = "coronavirus study", description = "Swiss scientists wondering if Sarimsak will cure COVID.", deadline = datetime.datetime(2021, 3, 3, 1, 1, 45), max_collaborators = 5, state = WorkspaceState.search_for_collaborator.value), # public workspace by Umut on state 1
            Workspace(creator_id = 2, is_private = 0, title = "SWE difficulties", description = "Investigating how to make workspaces great again.", deadline = datetime.datetime(2021, 4, 3, 0, 1, 45), max_collaborators = 5, state = WorkspaceState.search_for_collaborator.value), # public workspace by Can on state 1
            Workspace(creator_id = 4, is_private = 0, title = "honeybadger", description = "searching for bravest animal in the universe.", deadline = datetime.datetime(2021, 5, 3, 0, 1, 45), max_collaborators = 5, state = WorkspaceState.search_for_collaborator.value), #public workspace by Hilal on state 1
            Workspace(creator_id = 3, is_private = 1, title = "life", description = "is hard. be bravest.", state = WorkspaceState.search_for_collaborator.value), # private workspace by Alperen on state 1
        ]

        for workspace in workspaces:
            db.session.add(workspace)
        
        db.session.commit()

        # since we want different timestamp values of workspaces to check sorting functionality. 
        extra_workspace = Workspace(creator_id = 3, is_private = 0, title = "hello darkness", description = "my old friend. be bravest.", deadline =datetime.datetime(2021, 12, 4, 0, 1), max_collaborators = 2, state = WorkspaceState.search_for_collaborator.value) # public workspace by Alperen on state 1
        db.session.add(extra_workspace)
        db.session.commit()

        skills = [
            Skills("bioinformatics"),
            Skills("C++"),
            Skills("Python"),
            Skills("bash"),
            Skills("skill")
        ]

        for skill in skills:
            db.session.add(skill)
        db.session.commit()

        workspace_skills = [
            WorkspaceSkill(workspace_id = 1, skill_id = 1),
            WorkspaceSkill(workspace_id = 1, skill_id = 2),
            WorkspaceSkill(workspace_id = 1, skill_id = 3),
            WorkspaceSkill(workspace_id = 2, skill_id = 2),
            WorkspaceSkill(workspace_id = 2, skill_id = 3),
            WorkspaceSkill(workspace_id = 2, skill_id = 4),
            WorkspaceSkill(workspace_id = 5, skill_id = 5)
        ]

        for workspace_skill in workspace_skills:
            db.session.add(workspace_skill)

        db.session.commit()

        contributions = [
            Contribution(workspace_id = 1, user_id = 1, is_active = 1), # Umut will be active in coronovirus study
            Contribution(workspace_id = 2, user_id = 2, is_active = 1), # can will be active in SWE difficulties
            Contribution(workspace_id = 2, user_id = 3, is_active = 1), # alperen will be active in SWE difficulties
            Contribution(workspace_id = 3, user_id = 2, is_active = 0), # can will be inactive in honey badger study
            Contribution(workspace_id = 3, user_id = 4, is_active = 1), # hilal will be active in honey badger study
            Contribution(workspace_id = 3, user_id = 1, is_active = 1), # umut will be active in honey badger study
            Contribution(workspace_id = 4, user_id = 3, is_active = 1), # alperen will be active in life.
            Contribution(workspace_id = 5, user_id = 3, is_active = 1), # alperen will be active in hello darkness.
        ]

        for contribution in contributions:
            db.session.add(contribution)

        db.session.commit()


    # Check if searched element appears in the workspace title.
    # Upper case is ignored.
    def test_searched_element_in_title(self):

        # Searched element is one word query.
        # Can will search "coronavirus". 
        # Success
        valid_token = generate_token(2, datetime.timedelta(minutes=10))
        data = {'search_query': "coronavirus"}
        expected_status_code = 200
        actual_response = self.client.get('/api/search_engine/workspace', query_string=data,
                                          headers={'auth_token': valid_token})
        self.assertEqual(actual_response.status_code, expected_status_code, 'Incorrect HTTP Response Code')

        expected_workspace_id = 1
        result_list = actual_response.json.get('result_list')
        found = False
        for result in result_list:
            if result.get("id")==expected_workspace_id:
                found = True
                break
        self.assertTrue(found, "Workspace is not found in the searched list")

        # searched element is multiple words.
        # Can will search "coronavirus study". 
        # Success
        valid_token = generate_token(2, datetime.timedelta(minutes=10))
        data = {'search_query': "coronavirus study"}
        expected_status_code = 200
        actual_response = self.client.get('/api/search_engine/workspace', query_string=data,
                                          headers={'auth_token': valid_token})
        self.assertEqual(actual_response.status_code, expected_status_code, 'Incorrect HTTP Response Code')
        
        expected_workspace_id = 1
        result_list = actual_response.json.get('result_list')
        found = False
        for result in result_list:
            if result.get("id")==expected_workspace_id:
                found = True
                break
        self.assertTrue(found, "Workspace is not found in the searched list")

        # searched element is multiple words and contains white spaces.
        # Can will search "cORonaviRus     sTudy  ". 
        # Success
        valid_token = generate_token(2, datetime.timedelta(minutes=10))
        data = {'search_query': "coronavirus study"}
        expected_status_code = 200
        actual_response = self.client.get('/api/search_engine/workspace', query_string=data,
                                          headers={'auth_token': valid_token})
        self.assertEqual(actual_response.status_code, expected_status_code, 'Incorrect HTTP Response Code')

        expected_workspace_id = 1
        result_list = actual_response.json.get('result_list')
        found = False
        for result in result_list:
            if result.get("id")==expected_workspace_id:
                found = True
                break
        self.assertTrue(found, "Workspace is not found in the searched list")


    # Check if searched element appears in the workspace description
    def test_searched_element_in_description(self):

        # Searched element is one word query.
        # Can will search "Swiss".
        valid_token = generate_token(2, datetime.timedelta(minutes=10))
        data = {'search_query': "Swiss"} 
        # Success
        expected_status_code = 200
        actual_response = self.client.get('/api/search_engine/workspace', query_string=data,
                                          headers={'auth_token': valid_token})
        self.assertEqual(actual_response.status_code, expected_status_code, 'Incorrect HTTP Response Code')
        expected_workspace_id = 1
        result_list = actual_response.json.get('result_list')
        found = False
        for result in result_list:
            if result.get("id")==expected_workspace_id:
                found = True
                break
        self.assertTrue(found, "Workspace is not found in the searched list")

        # Check if upper case letters are ignored.
        # Searched element is one word query.
        # Can will search "swiss".
        valid_token = generate_token(2, datetime.timedelta(minutes=10))
        data = {'search_query': "swiss"} 
        # Success
        expected_status_code = 200
        actual_response = self.client.get('/api/search_engine/workspace', query_string=data,
                                          headers={'auth_token': valid_token})
        self.assertEqual(actual_response.status_code, expected_status_code, 'Incorrect HTTP Response Code')

        expected_workspace_id = 1
        result_list = actual_response.json.get('result_list')
        found = False
        for result in result_list:
            if result.get("id")==expected_workspace_id:
                found = True
                break
        self.assertTrue(found, "Workspace is not found in the searched list")

        # searched element is multiple words.
        # Can will be the user.
        valid_token = generate_token(2, datetime.timedelta(minutes=10))
        # User is searching "bravest animal".
        data = {'search_query': "bravest animal"} 
        # Should be success.
        expected_status_code = 200
        actual_response = self.client.get('/api/search_engine/workspace', query_string=data,
                                          headers={'auth_token': valid_token})
        self.assertEqual(actual_response.status_code, expected_status_code, 'Incorrect HTTP Response Code')

        expected_workspace_id = 3
        result_list = actual_response.json.get('result_list')
        found = False
        for result in result_list:
            if result.get("id")==expected_workspace_id:
                found = True
                break
        self.assertTrue(found, "Workspace is not found in the searched list")


    # Check if private workspaces does not appear in the search.
    def test_dont_show_private_workspaces(self):

        # Can will search "life".
        valid_token = generate_token(2, datetime.timedelta(minutes=10))
        data = {'search_query': "life"} 
        # Success
        expected_status_code = 200
        actual_response = self.client.get('/api/search_engine/workspace', query_string=data,
                                          headers={'auth_token': valid_token})
        self.assertEqual(actual_response.status_code, expected_status_code, 'Incorrect HTTP Response Code')
        
        private_workspace_id = 4
        result_list = actual_response.json.get('result_list')
        found = False
        for result in result_list:
            if result.get("id")==private_workspace_id:
                found = True
                break
        self.assertFalse(found, "Result format is not the expected one.")

    # Check if searched element does not appear in title but is similar to a word in title semantically.
    def test_similar_element_in_title(self):
        valid_token = generate_token(2, datetime.timedelta(minutes=10))
        data = {'search_query': 'difficult'} 
        # Success
        expected_status_code = 200
        actual_response = self.client.get('/api/search_engine/workspace', query_string=data,
                                          headers={'auth_token': valid_token})
        self.assertEqual(actual_response.status_code, expected_status_code, 'Incorrect HTTP Response Code')
        
        expected_workspace_id = 2
        result_list = actual_response.json.get('result_list')
        found = False
        for result in result_list:
            if result.get("id")==expected_workspace_id:
                found = True
                break
        self.assertTrue(found, "Workspace is not found in the searched list")

    # Check if searched element does not appear in description but is similar to a word in description semanticallly.
    def test_similar_element_in_description(self):
        valid_token = generate_token(2, datetime.timedelta(minutes=10))
        data = {'search_query': 'brave'} 
        # Success
        expected_status_code = 200
        actual_response = self.client.get('/api/search_engine/workspace', query_string=data,
                                          headers={'auth_token': valid_token})
        self.assertEqual(actual_response.status_code, expected_status_code, 'Incorrect HTTP Response Code')
        
        expected_workspace_id = 3
        result_list = actual_response.json.get('result_list')
        found = False
        for result in result_list:
            if result.get("id")==expected_workspace_id:
                found = True
                break
        self.assertTrue(found, "Workspace is not found in the searched list")

    # Check if searched elemet does not appear in title or description. It should return no values.
    def test_no_element_found(self):
        valid_token = generate_token(2, datetime.timedelta(minutes=10))
        data = {'search_query': 'sattas los mancos'} 
        # Success
        expected_status_code = 200
        actual_response = self.client.get('/api/search_engine/workspace', query_string=data,
                                          headers={'auth_token': valid_token})
        self.assertEqual(actual_response.status_code, expected_status_code, 'Incorrect HTTP Response Code')

        self.assertEqual(len(actual_response.json.get('result_list')), 0, "Workspace is not found in the searched list")

    # Check if skill filter works.
    def test_skill_filter(self):
    
        # Check that without a skill, workspace_ids 3 and 5 is found with "bravest" query.
        valid_token = generate_token(2, datetime.timedelta(minutes=10))
        data = {'search_query': 'bravest'} 
        # Success
        expected_status_code = 200
        actual_response = self.client.get('/api/search_engine/workspace', query_string=data,
                                          headers={'auth_token': valid_token})
        self.assertEqual(actual_response.status_code, expected_status_code, 'Incorrect HTTP Response Code')

        expected_workspace_ids = [3,5]
        result_list = actual_response.json.get('result_list')
        actual_result = []
        for result in result_list:
            actual_result.append(result.get("id"))

        self.assertEqual(actual_result, expected_workspace_ids, "Expected workspace ids and actual lists are not equal!")

        # Check that with a skill, only workspace_id=5 is found with "bravest" query.
        valid_token = generate_token(2, datetime.timedelta(minutes=10))
        data = {'search_query': 'bravest', 'skill_filter': 'skill'} 
        # Success
        expected_status_code = 200
        actual_response = self.client.get('/api/search_engine/workspace', query_string=data,
                                          headers={'auth_token': valid_token})
        self.assertEqual(actual_response.status_code, expected_status_code, 'Incorrect HTTP Response Code')

        expected_workspace_ids = [5]
        result_list = actual_response.json.get('result_list')
        actual_result = []
        for result in result_list:
            actual_result.append(result.get("id"))

        self.assertEqual(actual_result, expected_workspace_ids, "Expected workspace ids and actual lists are not equal!")
      
    # Check if starting date filter works.
    def test_starting_date_filter(self):

        # Check that without a date filter, workspace_ids 3 and 5 is found with "bravest" query.
        valid_token = generate_token(2, datetime.timedelta(minutes=10))
        data = {'search_query': 'bravest'} 
        # Success
        expected_status_code = 200
        actual_response = self.client.get('/api/search_engine/workspace', query_string=data,
                                          headers={'auth_token': valid_token})
        self.assertEqual(actual_response.status_code, expected_status_code, 'Incorrect HTTP Response Code')

        expected_workspace_ids = [3,5]
        result_list = actual_response.json.get('result_list')
        actual_result = []
        for result in result_list:
            actual_result.append(result.get("id"))

        self.assertEqual(actual_result, expected_workspace_ids, "Expected workspace ids and actual lists are not equal!")

        # Check that with a date filter, nothing is found.
        valid_token = generate_token(2, datetime.timedelta(minutes=10))
        data = {'search_query': 'bravest', 
                'starting_date_start': datetime.datetime(2020,12,1,23,59,59), 
                'starting_date_end': datetime.datetime(2020,12,2,23,59,59), 
                }
        # Success
        expected_status_code = 200
        actual_response = self.client.get('/api/search_engine/workspace', query_string=data,
                                          headers={'auth_token': valid_token})
        self.assertEqual(actual_response.status_code, expected_status_code, 'Incorrect HTTP Response Code')

        self.assertEqual(len(actual_response.json.get('result_list')), 0, "No workspace should have been found!!!")

    # Check if deadline filter works.
    def test_deadline_filter(self):
        # Check that without a deadline filter, workspace_ids 3 and 5 is found with "bravest" query.
        valid_token = generate_token(2, datetime.timedelta(minutes=10))
        data = {'search_query': 'bravest'} 
        # Success
        expected_status_code = 200
        actual_response = self.client.get('/api/search_engine/workspace', query_string=data,
                                          headers={'auth_token': valid_token})
        self.assertEqual(actual_response.status_code, expected_status_code, 'Incorrect HTTP Response Code')

        expected_workspace_ids = [3,5]
        result_list = actual_response.json.get('result_list')
        actual_result = []
        for result in result_list:
            actual_result.append(result.get("id"))

        self.assertEqual(actual_result, expected_workspace_ids, "Expected workspace ids and actual lists are not equal!")

        # Check that with a deadline filter, only workspace_id=5 is found with "bravest" query.
        valid_token = generate_token(2, datetime.timedelta(minutes=10))
        data = {'search_query': 'bravest', 
                'deadline_start': datetime.datetime(2021,7,1,23,59,59), 
                'deadline_end': datetime.datetime(2021,12,30,23,59,59)
                }
        # Success
        expected_status_code = 200
        actual_response = self.client.get('/api/search_engine/workspace', query_string=data,
                                          headers={'auth_token': valid_token})
        self.assertEqual(actual_response.status_code, expected_status_code, 'Incorrect HTTP Response Code')

        expected_workspace_ids = [5]
        result_list = actual_response.json.get('result_list')
        actual_result = []
        for result in result_list:
            actual_result.append(result.get("id"))

        self.assertEqual(actual_result, expected_workspace_ids, "Expected workspace ids and actual lists are not equal!")

    # Check if sorting criterias work.
    def test_sorting_with_date(self):
        
        valid_token = generate_token(2, datetime.timedelta(minutes=10))
        data = {'search_query': 'bravest', 'sorting_criteria': 1}
        # Success
        expected_status_code = 200
        actual_response = self.client.get('/api/search_engine/workspace', query_string=data,
                                          headers={'auth_token': valid_token})
        self.assertEqual(actual_response.status_code, expected_status_code, 'Incorrect HTTP Response Code')

        expected_workspace_ids = [5,3]
        result_list = actual_response.json.get('result_list')
        actual_result = []
        for result in result_list:
            actual_result.append(result.get("id"))

        self.assertEqual(actual_result, expected_workspace_ids, "Expected workspace ids and actual lists are not equal!")

    # Check if sorting criterias work.
    def test_sorting_with_number_of_collaborators_needed(self):
        
        valid_token = generate_token(2, datetime.timedelta(minutes=10))
        data = {'search_query': 'bravest', 'sorting_criteria': 3} # number of collaborators needed.
        # Success
        expected_status_code = 200
        actual_response = self.client.get('/api/search_engine/workspace', query_string=data,
                                          headers={'auth_token': valid_token})
        self.assertEqual(actual_response.status_code, expected_status_code, 'Incorrect HTTP Response Code')

        expected_workspace_ids = [3, 5]
        result_list = actual_response.json.get('result_list')
        actual_result = []
        for result in result_list:
            actual_result.append(result.get("id"))

        self.assertEqual(actual_result, expected_workspace_ids, "Expected workspace ids and actual lists are not equal!")

    # Check if sorting criterias work.
    def test_sorting_with_alphabetical_order(self):
        
        valid_token = generate_token(2, datetime.timedelta(minutes=10))
        data = {'search_query': 'bravest', 'sorting_criteria': 5} # alphabetical order.
        # Success
        expected_status_code = 200
        actual_response = self.client.get('/api/search_engine/workspace', query_string=data,
                                          headers={'auth_token': valid_token})
        self.assertEqual(actual_response.status_code, expected_status_code, 'Incorrect HTTP Response Code')

        # It should be in ascending order
        expected_workspace_ids = [3, 5]
        result_list = actual_response.json.get('result_list')
        actual_result = []
        for result in result_list:
            actual_result.append(result.get("id"))

        self.assertEqual(actual_result, expected_workspace_ids, "Expected workspace ids and actual lists are not equal!")

    # Check if founder name and surname filter works.
    def test_filter_founder_name_surname(self):
        
        valid_token = generate_token(2, datetime.timedelta(minutes=10))
        data = {'search_query': 'bravest', 'creator_name': "alperen", 'creator_surname': "ozprivate"}
        # Success
        expected_status_code = 200
        actual_response = self.client.get('/api/search_engine/workspace', query_string=data,
                                          headers={'auth_token': valid_token})
        self.assertEqual(actual_response.status_code, expected_status_code, 'Incorrect HTTP Response Code')

        expected_workspace_id = [5]
        result_list = actual_response.json.get('result_list')
        actual_result = []
        for result in result_list:
            actual_result.append(result.get("id"))
        self.assertEqual(actual_result, expected_workspace_id, "Expected workspace id and actual result are not equal!")    

    def tearDown(self):
        super().tearDown()