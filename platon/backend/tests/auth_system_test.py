from tests.base_test import BaseTest
from tests.base_test import TestConfig
from app.auth_system.models import User
from app.profile_management.models import Jobs
from app.auth_system.views import generate_token
from app import db
import jwt
import datetime
import json

class LoginTest(BaseTest):
    """
        Unit Tests of the login module
    """
    def setUp(self):
        
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

    def test_valid_login(self):
        data = {'e_mail' : 'umut@deneme.com', 'password' : 'umut1234'}
        actual_response = self.client.post('/api/auth_system/login',data = data)

        actual_info = jwt.decode(json.loads(actual_response.data)['token'], TestConfig.JWT_SESSION_KEY, algorithms=[TestConfig.JWT_ALGORITHM])
        expected_info = {'id':1,'expire_time':(datetime.datetime.now()+TestConfig.SESSION_DURATION).isoformat()}

        self.assertEqual(actual_response.status_code,200,'Incorrect HTTP Response Code')
        self.assertEqual(actual_info['id'],expected_info['id'],'Incorrect ID')

    def test_invalid_login(self):
        expected_response = {'error' : 'Wrong e-mail or password'}
        data = {'e_mail' : 'umut@deneme.com', 'password' : 'umut14'}
        actual_response = self.client.post('/api/auth_system/login',data = data)

        self.assertEqual(actual_response.status_code,401,'Incorrect HTTP Response Code')
        self.assertEqual(json.loads(actual_response.data)['error'],expected_response['error'],'Incorrect Error Message')
    
    def test_not_activated_user(self):
        expected_response = {'error' : 'Please activate your account'}
        data = {'e_mail' : 'can@deneme.com', 'password' : 'can1234'}
        actual_response = self.client.post('/api/auth_system/login',data = data)

        self.assertEqual(actual_response.status_code,401,'Incorrect HTTP Response Code')
        self.assertEqual(json.loads(actual_response.data)['error'],expected_response['error'],'Incorrect Error Message')


class ResetPasswordTest(BaseTest):
    """
        Unit Tests of the reset password module
    """
    def setUp(self):
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

    def test_reset_password_valid(self):
        valid_token = generate_token(1,datetime.timedelta(minutes=10))
        expected_response = {'mgs' : 'Password successfully changed'}
        actual_response = self.client.post("/api/auth_system/reset_password",data={'new_password' : '123456','new_password_repeat' : '123456'},headers={'auth_token' : valid_token})
        
        self.assertEqual(actual_response.status_code,200)
        self.assertEqual(json.loads(actual_response.data),expected_response)

    def test_reset_password_invalid_input(self):
        valid_token = generate_token(1,datetime.timedelta(minutes=10))
        expected_response = {'error' : 'Passwords are not matched' }
        actual_response = self.client.post("/api/auth_system/reset_password",data={'new_password' : '123','new_password_repeat' : '123456'},headers={'auth_token' : valid_token})
        
        self.assertEqual(actual_response.status_code,400)
        self.assertEqual(json.loads(actual_response.data),expected_response)

    def test_reset_password_invalid_input2(self):
        valid_token = generate_token(1,datetime.timedelta(minutes=10))
        expected_response = {'error' : 'Write new password twice'}
        actual_response = self.client.post("/api/auth_system/reset_password",data={'new_password' : '123456','new_password_re' : '123456'},headers={'auth_token' : valid_token})
        
        self.assertEqual(actual_response.status_code,400)
        self.assertEqual(json.loads(actual_response.data),expected_response)
    
    def test_reset_password_invalid_token(self):
        valid_token = "abkfblkjdgfbajksfbdkajsfdbkjasfdlbjk"
        expected_response = {'error' : 'Wrong Token Format'}
        actual_response = self.client.post("/api/auth_system/reset_password",data={'new_password' : '123456','new_password_repeat' : '123456'},headers={'auth_token' : valid_token})
        
        self.assertEqual(actual_response.status_code,401)
        self.assertEqual(json.loads(actual_response.data),expected_response)

class PostUserSkillTest(BaseTest):
    """
        Unit Tests of the post user skill module
    """
    def setUp(self):
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

    def test_post_userskill(self):
        valid_token = generate_token(1,datetime.timedelta(minutes=10))
        expected_response = {'msg': 'Skill is successfully added'}
        actual_response = self.client.post("/api/auth_system/skills", data={'skill' : 'javacript'},headers={'auth_token' : valid_token})
        self.assertEqual(actual_response.status_code,200)
        self.assertEqual(json.loads(actual_response.data),expected_response)