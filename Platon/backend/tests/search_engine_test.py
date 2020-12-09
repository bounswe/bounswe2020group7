from tests.base_test import BaseTest
from tests.base_test import TestConfig

from app.auth_system.models import User
from app.profile_management.models import Jobs, Skills, UserSkills
from app.auth_system.views import generate_token

from app import db
import json

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
            User("can@deneme.com", False, "cce0c2170d1ae52e099c716165d80119ee36840e3252e57f2b2b4d6bb111d8a5", 3.4,
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