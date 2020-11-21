from tests.base_test import BaseTest
from tests.base_test import TestConfig
from app.auth_system.models import User
from app.profile_management.models import ResearchInformation
from app.auth_system.views import generate_token
from app.profile_management.views import ResearchType
from app.profile_management.helpers import ResearchInfoFetch
from app.follow_system.models import Follow
from app import db
import jwt
import json


class ResearchInfoTests(BaseTest):
    
    def setUp(self):
        super().setUp()
        # Add artificia users to test login feature
        users = [
            User("umut@deneme.com",True,"b73ec5e4625ffcb6d0d70826f33be7a75d45b37046e26c4b60d9111266d70e32",3.5,"Umut","Özdemir",False,None,None,None),
            User("can@deneme.com",False,"cce0c2170d1ae52e099c716165d80119ee36840e3252e57f2b2b4d6bb111d8a5",4.6,"Can","Deneme",True,None,None,None)
        ]
        for user in users:
            db.session.add(user)
        db.session.commit()
        research_infos = [
            ResearchInformation(1,"Multi Model Sensor Fusion","A Multi Model Sensor Fusion using Extended Kalman Filters",2020,ResearchType.FETCHED),
            ResearchInformation(1,"Estimating Channel Coefficients using ANN","Using a CNN Model Estimate Channel Coefficients of a 2D Diffusion based Channel",2020,ResearchType.HAND_WRITTEN),
            ResearchInformation(2,"An Academic Collaboration Platform","An Academic Collaboration Platform named Platon",2020,ResearchType.FETCHED)

        ]
        for research_info in research_infos:
            db.session.add(research_info)
        db.session.commit()
        follows = [
            Follow(1,2),
            Follow(2,1)
        ]
        for follow in follows:
            db.session.add(follow)
        db.session.commit()
        
    def test_add_research_info_valid(self):
        valid_token = generate_token(1,TestConfig.SESSION_DURATION)
        data = {
            'research_title' : 'Radar Preprocessing using DBCAN Clustring',
            'description' : 'Preprocessing the radar hits using DBCAN Algorithm',
            'year' : 2020
        }
        expected_response = {
            'msg' : 'Successfully added'
        }
        actual_response = self.client.post("/api/profile/research_information",data=data,headers={'auth_token' : valid_token})
        self.assertEqual(expected_response,json.loads(actual_response.data))
        self.assertEqual(201,actual_response.status_code)
        self.assertIsNotNone(ResearchInformation.query.filter(ResearchInformation.research_title == 'Radar Preprocessing using DBCAN Clustring').first())
    

    def test_add_research_info_invalid(self):
        valid_token = generate_token(1,TestConfig.SESSION_DURATION)
        data = {
            'title' : 'Radar Preprocessing using DBCAN Clustring',
            'description' : 'Preprocessing the radar hits using DBCAN Algorithm'
        }
        expected_response = {
            'error' : 'Wrong input format'
        }
        actual_response = self.client.post("/api/profile/research_information",data=data,headers={'auth_token' : valid_token})
        self.assertEqual(expected_response,json.loads(actual_response.data))
        self.assertEqual(400,actual_response.status_code)
        self.assertIsNone(ResearchInformation.query.filter(ResearchInformation.research_title == 'Radar Preprocessing using DBCAN Clustring').first())
       
    def test_remove_research_info_valid(self):
        valid_token = generate_token(1,TestConfig.SESSION_DURATION)
        data = {
            'research_id':1
        }
        expected_result = {
            'msg':'Successfully Deleted'
        }
        actual_response = self.client.delete("/api/profile/research_information",data=data,headers={'auth_token' : valid_token})
        self.assertEqual(expected_result,json.loads(actual_response.data))
        self.assertEqual(200,actual_response.status_code)
        self.assertIsNone(ResearchInformation.query.filter(ResearchInformation.id == 1).first())
        
    def test_remove_research_info_invalid(self):
        valid_token = generate_token(2,TestConfig.SESSION_DURATION)
        data = {
            'research_id':1
        }
        expected_result = {
            'error':'You can not delete other user\'s information'
        }
        actual_response = self.client.delete("/api/profile/research_information",data=data,headers={'auth_token' : valid_token})
        self.assertEqual(expected_result,json.loads(actual_response.data))
        self.assertEqual(400,actual_response.status_code)
        self.assertIsNotNone(ResearchInformation.query.filter(ResearchInformation.id == 1).first())

    def test_get_research_info_invalid(self):
        invalid_token = generate_token(1,TestConfig.SESSION_DURATION) + "c"
        expected_response = {'error' : 'Wrong Token Format'}
        actual_response = self.client.get("/api/profile/research_information",data={'user_id':2},headers={'auth_token' : invalid_token})
        self.assertEqual(expected_response,json.loads(actual_response.data))
        self.assertEqual(401,actual_response.status_code)
    
    def test_fetch_RG_info(self):
        RG_name = "Meriç_Turan"
        expected_response = [
            { 
                "title": "Intelligent network data analytics function in 5G cellular networks using machine learning.",
                "description": "",
                'year': 2020
            },
            { 
                "title": "Transmitter Localization in Vessel-Like Diffusive Channels Using Ring-Shaped Molecular Receivers.",
                "description": "",
                'year': 2018
            },
            { 
                "title": "Channel Model of Molecular Communication via Diffusion in a Vessel-Like Environment Considering a Partially Covering Receiver.",
                "description": "",
                'year': 2018
            },
            { 
                "title": "Performance analysis of power adjustment methods in molecular communication via diffusion.",
                "description": "",
                'year': 2018
            },
            { 
                "title": "MOL-eye - A new metric for the performance evaluation of a molecular signal.",
                "description": "",
                'year': 2018
            },            
            { 
                "title": "Channel Model of Molecular Communication via Diffusion in a Vessel-like Environment Considering a Partially Covering Receiver.",
                "description": "",
                'year': 2018
            },            
            { 
                "title": "Note recognition-based mobile application for guitar training.",
                "description": "",
                'year': 2017
            },            
            { 
                "title": "MOL-Eye - A New Metric for the Performance Evaluation of a Molecular Signal.",
                "description": "",
                'year': 2017
            }
        ]
        actual_result = ResearchInfoFetch.fetch_research_gate_info(RG_name)
        for i in expected_response:
            self.assertIn(i,actual_result)

    def test_fetch_GS_info(self):
        GS_info = "QAzjUf8AAAAJ"
        expected_response = [
            {
                'title': 'Channel Model of Molecular Communication via Diffusion in a Vessel-like Environment Considering a Partially Covering Receiver',
                'description': '',
                'year': 2018
            },
            {
                'title': 'Transmitter localization in vessel-like diffusive channels using ring-shaped molecular receivers',
                'description': '',
                'year': 2018
            },
            {
                'title': 'Mol-eye: A new metric for the performance evaluation of a molecular signal',
                'description': '',
                'year': 2018
            },
            {
                'title': 'Performance analysis of power adjustment methods in molecular communication via diffusion',
                'description': '',
                'year': 2018
            },
            {
                'title': 'Intelligent network data analytics function in 5G cellular networks using machine learning',
                'description': '',
                'year': 2020
            },
            {
                'title': 'Note recognition-based mobile application for guitar training',
                'description': '',
                'year': 2017
            }
                ]
        actual_result = ResearchInfoFetch.fetch_google_scholar_info(GS_info)
        for i in expected_response:
            self.assertIn(i,actual_result)
    