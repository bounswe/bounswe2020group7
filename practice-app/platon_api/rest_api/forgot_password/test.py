from rest_framework.test import APIClient, APITestCase
from django.urls import reverse
from rest_framework import status
from rest_framework.authtoken.models import Token
from django.db import connection
from rest_api.models import RegisteredUser
import json, hashlib
from platon_api.settings import USER_TABLENAME 
from django.test import TestCase,TransactionTestCase



class TestForForgotPassword(TransactionTestCase):
    # Testing for creation of user with valid input

    user_list = [
            ("Umut","Ozdemir",hashlib.sha256("123456".encode("utf-8")).hexdigest(),"umut@gmail.com",hashlib.sha256("umut@gmail.com".encode("utf-8")).hexdigest(),"I am a junior student at Bogazici University and my major degree is in Computer Engineering, also I study a minor degree in Electrics and Electronics Engineering. Finally, I like everything about basketball.","0aa64c1575f51c91930095311b536477","Computer Engineering","Umut")
                ]

    def setUp(self):
        with connection.cursor() as cursor:
            # Add some test users
            sql = 'INSERT INTO `'+ USER_TABLENAME +'` (`name`, `surname`, `password_hashed`, `e_mail`, `token`, `about_me`, `job_uuid`, `field_of_study`, `forget_password_ans`) VALUES'
            for test_user in TestForForgotPassword.user_list:
                cursor.execute(sql + "('"+"','".join([str(x) for x in test_user])+"');")
               
    def test_validToken(self):
        data = {"e_mail":"umut@gmail.com", "password":"123456", "password_again":"123456", "forget_password_ans":"Umut"}
        response = self.client.post("/api/forgotpassword/", data)
        self.assertEqual(response.status_code, status.HTTP_201_CREATED)
    
    # Testing for creation of user with valid input, and one more item that is irrelevant
    def test_answer_secret_question(self):
        data = {"e_mail":"umut@gmail.com", "password":"123456", "password_again":"123456", "forget_password_ans":"teacher"}
        response = self.client.post("/api/forgotpassword/", data)
        self.assertEqual(response.status_code, 400)
    
    # Testing for creation of user with valid input, except that passwords doesnot match  
    def test_unmatchingPassword(self):
        data = {"e_mail":"umut@gmail.com","password":"123456", "password_again":"1234567","forget_password_ans":"Umut"}
        response = self.client.post("/api/forgotpassword/", data)
        self.assertEqual(response.status_code,400)
    
    # Testing for creation of user with valid input, except that email is wrong in terms of regex   
    def test_wrongMail(self):
        data = {"e_mail":"umutgmail.com",
                "password":"123456", "password_again":"123456", "forget_password_ans":"Umut"}
        response = self.client.post("/api/forgotpassword/", data)
        self.assertEqual(response.status_code, 404)
    
    # Testing for creation of user with empty input  
    def test_empty(self):
        data = {"e_mail":"","password":"", "password_again":"","forget_password_ans":""}
        response = self.client.post("/api/forgotpassword/", data)
        self.assertEqual(response.status_code, 400)
        
    # Testing for creation of user with valid input except that one field is missing   
    def test_deficientPost(self):
        data = {"e_mail":"umut@gmail.com", "password":"123456", "forget_password_ans":"Umut"}
        response = self.client.post("/api/forgotpassword/", data)
        self.assertEqual(response.status_code, 400)
        
     # Testing for creation of user with valid input except that one field is wrongly named 
