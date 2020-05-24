# class UnitTestDelete():

from django.test import TestCase,TransactionTestCase,Client
from django.db import connection,transaction
from platon_api.settings import USER_TABLENAME 
from rest_api.delete_user_t.delete_user_f import DeleteUser
import json
import hashlib

# Create your tests here.
class SearchTest(TransactionTestCase):

    client = Client()

    user_list = [
            ("Umut","Ozdemir",hashlib.sha256("123456".encode("utf-8")).hexdigest(),"umut@gmail.com",hashlib.sha256("umut@gmail.com".encode("utf-8")).hexdigest(),"I am a junior student at Bogazici University and my major degree is in Computer Engineering, also I study a minor degree in Electrics and Electronics Engineering. Finally, I like everything about basketball.","0aa64c1575f51c91930095311b536477","Computer Engineering","Umut"),
            ("Öykü","Yilmaz",hashlib.sha256("1111111".encode("utf-8")).hexdigest(),"oyku@gmail.com",hashlib.sha256("oyku@gmail.com".encode("utf-8")).hexdigest(),"A CmpE student in Bogazici University. Loves to learn new things, swimming, and also doing nothing.","0e78a3833926aa1f49dd9f4bb86b7386","Computer Engineering","Oyku"),
            ("Hasan Ramazan","Yurt",hashlib.sha256("hfaonfsln".encode("utf-8")).hexdigest(),"ramazan@gmail.com",hashlib.sha256("ramazan@gmail.com".encode("utf-8")).hexdigest(),"I am a junior Computer Engineering student at Bogazici University. I can describe myself as creative, sociable and an engineer ready to learn. Also, I am a passionate esports follower.","0e78a3833926aa1f49dd9f4bb86b7386","Computer Engineering","Yurt"),
            ("Ahmet","Dadak",hashlib.sha256("bsfuaıbsbf".encode("utf-8")).hexdigest(),"ahmet@gmail.com",hashlib.sha256("ahmet@gmail.com".encode("utf-8")).hexdigest(),"Junior computer engineering student at Bogazici University. Writes code, scripts, and lyrics. Interested in Web Applications and ML. Interested in sports, literature, design, and music.","0e78a3833926aa1f49dd9f4bb86b7386","Computer Engineering","Ahmet")
    ]
    
    valid_token = "03684b7c7efc7ba7ffe53f32f3c9346d8383fcc13eddd1272705c746ba986f2f"

    def setUp(self):
        cursor = connection.cursor()
        stmt = 'INSERT INTO `'+ USER_TABLENAME +'` (`name`, `surname`, `password_hashed`, `e_mail`, `token`, `about_me`, `job_uuid`, `field_of_study`, `forget_password_ans`) VALUES'
        
        for test_user in SearchTest.user_list:
            cursor.execute(stmt + "('"+"','".join([str(x) for x in test_user])+"');")

    # Test for if there is no input 
    def test_without_input(self):

        resp = SearchTest.client.get('/api/deleteuser/')
        self.assertEqual(json.loads(resp.content),"No token found.","No Input Found")

    # Test for if there is no token 
    def test_without_token(self):
        resp = SearchTest.client.get('/api/deleteuser/',{"email":"oyku@gmail.com"})
        self.assertEqual(json.loads(resp.content),"No token found.","No Token Found")    

    # Test for if there is wrong token given to the request
    def test_wrong_token(self):

        wrong_token = "1"*64
        resp = SearchTest.client.get('/api/deleteuser/',{"token":wrong_token,"email":"oyku@gmail.com"})
        self.assertEqual(json.loads(resp.content),"No user found.","Token Verification is not True")
    
    # Test for if there is no mail address 
    def test_without_mail(self):
        resp = SearchTest.client.get('/api/deleteuser/',{'token':SearchTest.valid_token})
        self.assertEqual(json.loads(resp.content),"No e-mail address found.","No Mail Found")    

    # Test for if there is wrong token given to the request
    def test_wrong_mail(self):

        resp = SearchTest.client.get('/api/deleteuser/',{"token":SearchTest.valid_token,"email":"sevval@gmail.com"})
        self.assertEqual(json.loads(resp.content),"No user found.","Mail Verification is not True")
 