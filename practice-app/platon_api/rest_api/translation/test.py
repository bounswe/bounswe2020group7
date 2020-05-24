from rest_framework import status
from rest_api.models import RegisteredUser
from django.test import TransactionTestCase,Client

import json
import hashlib


class TestTranslation(TransactionTestCase):
    #test for translation

    client = Client()

    validToken = hashlib.sha256("oyku@gmail.com".encode("utf-8")).hexdigest()
    unvalidToken = "asfdasgsgfsdfsfqdf"

    #creates and saves registered users for test
    def setUp(self):
        ru = RegisteredUser(name='Umut',surname='Ozdemir',password_hashed=hashlib.sha256("123456".encode("utf-8")).hexdigest(),e_mail='umut@gmail.com',token=hashlib.sha256("umut@gmail.com".encode("utf-8")).hexdigest(),about_me="I am a junior student at Bogazici University and my major degree is in Computer Engineering, also I study a minor degree in Electrics and Electronics Engineering. Finally, I like everything about basketball.",job_uuid='0aa64c1575f51c91930095311b536477',field_of_study="Computer Engineering",forget_password_ans='Umut')
        ru1 = RegisteredUser(name='Öykü',surname='Yilmaz',password_hashed=hashlib.sha256("1111111".encode("utf-8")).hexdigest(),e_mail='oyku@gmail.com',token=hashlib.sha256("oyku@gmail.com".encode("utf-8")).hexdigest(),about_me="A CmpE student in Bogazici University. Loves to learn new things, swimming, and also doing nothing.",job_uuid='0e78a3833926aa1f49dd9f4bb86b7386',field_of_study="Computer Engineering",forget_password_ans='Oyku')
        ru2 = RegisteredUser(name='Hasan Ramazan',surname='Yurt',password_hashed=hashlib.sha256("hfaonfsln".encode("utf-8")).hexdigest(),e_mail='ramazan@gmail.com',token=hashlib.sha256("ramazan@gmail.com".encode("utf-8")).hexdigest(),about_me="I am a junior Computer Engineering student at Bogazici University. I can describe myself as creative, sociable and an engineer ready to learn. Also, I am a passionate esports follower.",job_uuid='0e78a3833926aa1f49dd9f4bb86b7386',field_of_study="Computer Engineering",forget_password_ans='Yurt')
        ru3 = RegisteredUser(name='Ahmet',surname='Dadak',password_hashed=hashlib.sha256("bsfuaıbsbf".encode("utf-8")).hexdigest(),e_mail='ahmet@gmail.com',token=hashlib.sha256("ahmet@gmail.com".encode("utf-8")).hexdigest(),about_me="Junior computer engineering student at Bogazici University. Writes code, scripts, and lyrics. Interested in Web Applications and ML. Interested in sports, literature, design, and music.",job_uuid='0e78a3833926aa1f49dd9f4bb86b7386',field_of_study="Computer Engineering",forget_password_ans='Ahmet')
        ru.save()
        ru1.save()
        ru2.save()
        ru3.save()

    # Testing for translation of about me with valid input
    def test_validTranslation(self):
        response = self.client.get("/api/translation/"+self.validToken)
        self.assertEqual(response.status_code, 200)

    #Testing for translation of about me with valid input with more than five
    def test_keyErrorTranslation(self):
        response = self.client.get("/api/translation/"+self.validToken)
        self.assertEqual(response.status_code, 200)

    # Testing for translation of about me with unvalid input
    def test_noTokenInDb(self):
        response = self.client.get("/api/translation/" + self.unvalidToken)
        self.assertEqual(response.status_code, 404)