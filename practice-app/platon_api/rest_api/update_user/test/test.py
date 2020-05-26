  
from django.test import TestCase,TransactionTestCase,Client
from django.db import connection,transaction
from platon_api.settings import USER_TABLENAME 
from rest_api.update_user.update_user import updateUser
from rest_api.models import RegisteredUser
import json
import hashlib

# Create your tests here.
class UpdateTest(TransactionTestCase):

    client = Client()

    user_list = [
            ("Umut","Ozdemir",hashlib.sha256("123456".encode("utf-8")).hexdigest(),"umut@gmail.com",hashlib.sha256("umut@gmail.com".encode("utf-8")).hexdigest(),"I am a junior student at Bogazici University and my major degree is in Computer Engineering, also I study a minor degree in Electrics and Electronics Engineering. Finally, I like everything about basketball.","0aa64c1575f51c91930095311b536477","Computer Engineering","Umut"),
            ("Öykü","Yilmaz",hashlib.sha256("1111111".encode("utf-8")).hexdigest(),"oyku@gmail.com",hashlib.sha256("oyku@gmail.com".encode("utf-8")).hexdigest(),"A CmpE student in Bogazici University. Loves to learn new things, swimming, and also doing nothing.","0e78a3833926aa1f49dd9f4bb86b7386","Computer Engineering","Oyku"),
            ("Hasan Ramazan","Yurt",hashlib.sha256("hfaonfsln".encode("utf-8")).hexdigest(),"ramazan@gmail.com",hashlib.sha256("ramazan@gmail.com".encode("utf-8")).hexdigest(),"I am a junior Computer Engineering student at Bogazici University. I can describe myself as creative, sociable and an engineer ready to learn. Also, I am a passionate esports follower.","0e78a3833926aa1f49dd9f4bb86b7386","Computer Engineering","Yurt"),
            ("Ahmet","Dadak",hashlib.sha256("bsfuaıbsbf".encode("utf-8")).hexdigest(),"ahmet@gmail.com",hashlib.sha256("ahmet@gmail.com".encode("utf-8")).hexdigest(),"Junior computer engineering student at Bogazici University. Writes code, scripts, and lyrics. Interested in Web Applications and ML. Interested in sports, literature, design, and music.","0e78a3833926aa1f49dd9f4bb86b7386","Computer Engineering","Ahmet")
    ]
    
    valid_token = "03684b7c7efc7ba7ffe53f32f3c9346d8383fcc13eddd1272705c746ba986f2f"

    def setUp(self):
        with connection.cursor() as cursor:
            # Add some test users
            sql = 'INSERT INTO `'+ USER_TABLENAME +'` (`name`, `surname`, `password_hashed`, `e_mail`, `token`, `about_me`, `job_uuid`, `field_of_study`, `forget_password_ans`) VALUES'
            for test_user in UpdateTest.user_list:
                cursor.execute(sql + "('"+"','".join([str(x) for x in test_user])+"');")

    def test_isUpdated(self):
        token = hashlib.sha256("umut@gmail.com".encode("utf-8")).hexdigest()
        data = {"name":"Meltem", "surname": "Arslan","password1": "123456", "token":token,"field_of_study": "" ,"e_mail":"umut@gmail.com","about_me":"I am a just a huwoman","job_name":"Engineer","forget_password_ans":"Meltem"}
        resp = UpdateTest.client.post('/api/updateUser/',data)

        user = RegisteredUser.objects.get(token = token)
        newInfo = (user.name == "Meltem" and user.surname == "Arslan" and user.field_of_study == "Computer Engineering" and user.about_me == "I am a just a huwoman" and user.forget_password_ans == "Meltem" )
        self.assertEqual(newInfo, True ,"User information could not be changed!")