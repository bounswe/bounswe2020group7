from tests.base_test import BaseTest
from tests.base_test.TestConfig import JWT_SESSION_KEY,JWT_ALGORITHM,SESSION_DURATION
from app.auth_system.models import User
from app import db
import jwt
import datetime

class LoginTest(BaseTest):
    """
        Unit Tests of the login module
    """
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

    def test_valid_login(self):
        expected_response = jwt.encode({'id':1,'expire_time':datetime.datetime.now()+SESSION_DURATION},JWT_SESSION_KEY,JWT_ALGORITHM)
        actual_response = ""
    
        actual_info = jwt.decode(actual_response['token'], JWT_SESSION_KEY, algorithms=[JWT_ALGORITHM])
        expected_info = jwt.decode(expected_response, JWT_SESSION_KEY, algorithms=[JWT_ALGORITHM])
    
        self.assertEqual(actual_response.code,200,'Incorrect HTTP Response Code')
        self.assertEqual(actual_info['id'],expected_info['id'],'Incorrect ID')

    def test_invalid_login(self):
        expected_response = {'error' : 'Wrong e-mail or password'}
        actual_response = ""
        
        self.assertEqual(actual_response.code,401,'Incorrect HTTP Response Code')
        self.assertEqual(actual_response['error'],expected_response['error'],'Incorrect Error Message')
    
    def test_not_activated_user(self):
        expected_response = {'error' : 'Please activate your account'}
        actual_response = ""
        
        self.assertEqual(actual_response.code,401,'Incorrect HTTP Response Code')
        self.assertEqual(actual_response['error'],expected_response['error'],'Incorrect Error Message')
        