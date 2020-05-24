# class UnitTestDelete():

from django.test import TestCase,TransactionTestCase,Client
from django.db import connection,transaction
from platon_api.settings import USER_TABLENAME 
from rest_api.delete_user_t.delete_user_f import DeleteUser
import json
import hashlib

# Create your tests here.
class DeleteTest(TransactionTestCase):

    
    """
    In this class, the unit tests of the search engine is implemented.
    ...
    Attributes
    ----------
    client : Client Object
        a client that will make the requests in the test cases
    user_list: list
        This is a list of tuples. The tuples in the list includes the users that I added to the test database for test cases
    
    valid_token: str
        A token that exists in the test database
    """
   
   
    client = Client()
   
    user_list = [
            ("Umut","Ozdemir",hashlib.sha256("123456".encode("utf-8")).hexdigest(),"umut@gmail.com",hashlib.sha256("umut@gmail.com".encode("utf-8")).hexdigest(),"I am a junior student at Bogazici University and my major degree is in Computer Engineering, also I study a minor degree in Electrics and Electronics Engineering. Finally, I like everything about basketball.","0aa64c1575f51c91930095311b536477","Computer Engineering","Umut"),
            ("Öykü","Yilmaz",hashlib.sha256("1111111".encode("utf-8")).hexdigest(),"oyku@gmail.com",hashlib.sha256("oyku@gmail.com".encode("utf-8")).hexdigest(),"A CmpE student in Bogazici University. Loves to learn new things, swimming, and also doing nothing.","0e78a3833926aa1f49dd9f4bb86b7386","Computer Engineering","Oyku"),
            ("Hasan Ramazan","Yurt",hashlib.sha256("hfaonfsln".encode("utf-8")).hexdigest(),"ramazan@gmail.com",hashlib.sha256("ramazan@gmail.com".encode("utf-8")).hexdigest(),"I am a junior Computer Engineering student at Bogazici University. I can describe myself as creative, sociable and an engineer ready to learn. Also, I am a passionate esports follower.","0e78a3833926aa1f49dd9f4bb86b7386","Computer Engineering","Yurt"),
            ("Ahmet","Dadak",hashlib.sha256("bsfuaıbsbf".encode("utf-8")).hexdigest(),"ahmet@gmail.com",hashlib.sha256("ahmet@gmail.com".encode("utf-8")).hexdigest(),"Junior computer engineering student at Bogazici University. Writes code, scripts, and lyrics. Interested in Web Applications and ML. Interested in sports, literature, design, and music.","0e78a3833926aa1f49dd9f4bb86b7386","Computer Engineering","Ahmet")
    ]
    
    valid_token = hashlib.sha256("umut@gmail.com".encode("utf-8")).hexdigest()

    def setUp(self):
        """
            This function creates the initial test database with users in the user_list above
        """
        with connection.cursor() as cursor:
            # Add some test users
            sql = 'INSERT INTO `'+ USER_TABLENAME +'` (`name`, `surname`, `password_hashed`, `e_mail`, `token`, `about_me`, `job_uuid`, `field_of_study`, `forget_password_ans`) VALUES'
            for test_user in DeleteTest.user_list:
                cursor.execute(sql + "('"+"','".join([str(x) for x in test_user])+"');")

    # Test for if there is no input 
    def test_without_input(self):
        """
            This function checks if the response is "No token found." when the input is empty
        """
        resp = DeleteTest.client.post('/api/deleteuser/')
        self.assertEqual(json.loads(resp.content),"No token found.","No Input Found")

    # Test for if there is no token 
    def test_without_token(self):
        """
            This function checks if the response is "No token found." when the token is not given
        """
        resp = DeleteTest.client.post('/api/deleteuser/',{"email":"umut@gmail.com"})
        self.assertEqual(json.loads(resp.content),"No token found.","No Token Found")    

    # Test for if there is wrong token given to the request
    def test_wrong_token(self):
        """
            This function checks if the response is "No user found." when the token is not the right one.
        """
        wrong_token = "1"*64
        resp = DeleteTest.client.post('/api/deleteuser/',{"token":wrong_token,"email":"umut@gmail.com"})
        self.assertEqual(json.loads(resp.content),"No user found.","Token Verification is not True")
    
    # Test for if there is no mail address 
    def test_without_mail(self):
        """
            This function checks if the response is "No e-mail address found." when the mail is not given
        """
        resp = DeleteTest.client.post('/api/deleteuser/',{'token':DeleteTest.valid_token})
        self.assertEqual(json.loads(resp.content),"No e-mail address found.","No Mail Found")    

    # Test for if there is wrong token given to the request
    def test_wrong_mail(self):
        """
            This function checks if the response is "No token found." when the token is not given
        """
        resp = DeleteTest.client.post('/api/deleteuser/',{"token":DeleteTest.valid_token,"email":"oyku@gmail.com"})
        self.assertEqual(json.loads(resp.content),"No user found.","Mail Verification is not True")
    
    def test_delete_user(self):
        """
            This function checks if therer exists 3 users in the database after deletion.
        """
        cursor = connection.cursor()
        resp = DeleteTest.client.post('/api/deleteuser/',{"token":DeleteTest.valid_token,"email":"umut@gmail.com"})
        stmt = 'SELECT * FROM `'+ USER_TABLENAME + "`"
        cursor.execute(stmt)
        records = cursor.fetchall()
        self.assertEqual(len(records), 3 ,"Not deleted")
        