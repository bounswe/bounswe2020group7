from rest_framework.test import APITestCase
from rest_framework import status
import hashlib
from django.db import connection
from platon_api.settings import USER_TABLENAME
from django.test import TransactionTestCase,Client


class JokeTest(TransactionTestCase):

    user_list = [
        ("Umut", "Ozdemir", hashlib.sha256("123456".encode("utf-8")).hexdigest(), "umut@gmail.com",
         hashlib.sha256("umut@gmail.com".encode("utf-8")).hexdigest(),
         "I am a junior student at Bogazici University and my major degree is in Computer Engineering, also I study a minor degree in Electrics and Electronics Engineering. Finally, I like everything about basketball.",
         "0aa64c1575f51c91930095311b536477", "Computer Engineering", "Umut"),
        ]

    validToken = hashlib.sha256("umut@gmail.com".encode("utf-8")).hexdigest()

    def setUp(self):
        with connection.cursor() as cursor:
            # Add some test users
            sql = 'INSERT INTO `' + USER_TABLENAME + '` (`name`, `surname`, `password_hashed`, `e_mail`, `token`, `about_me`, `job_uuid`, `field_of_study`, `forget_password_ans`) VALUES'
            for test_user in JokeTest.user_list:
                cursor.execute(sql + "('" + "','".join([str(x) for x in test_user]) + "');")

    # this test is for valid token
    def test_valid_token(self):
        #before testing please enter a valid token

        data = {'token': JokeTest.validToken}
        response = self.client.get('/api/joke/', data)
        print("jokeeeeeeeeee")

        self.assertEqual(response.status_code, status.HTTP_200_OK)

    # this test is for invalid token
    def test_invalid_token(self):
        data = {'token': 'invalid'}
        response = self.client.get('/api/joke/', data)
        self.assertEqual(response.status_code, status.HTTP_401_UNAUTHORIZED)

    #this test is for without token scenario
    def test_without_token(self):
        data = {}
        response = self.client.get('/api/joke/', data)
        self.assertEqual(response.status_code, status.HTTP_401_UNAUTHORIZED)