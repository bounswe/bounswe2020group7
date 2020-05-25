from rest_framework import status
import hashlib
from django.db import connection
from platon_api.settings import USER_TABLENAME
from django.test import TransactionTestCase,Client


class JokeTest(TransactionTestCase):
    """
       In this class, the unit tests of the search engine is implemented.
       ...
       Attributes
       ----------
       client : Client Object
           a client that will make the requests in the test cases
       user_list: list
           This is a list of tuples. The tuples in the list includes the users that I added to the test database for test cases

       validToken: str
           A token that exists in the test database
    """

    user_list = [
        ("Umut", "Ozdemir", hashlib.sha256("123456".encode("utf-8")).hexdigest(), "umut@gmail.com",
         hashlib.sha256("umut@gmail.com".encode("utf-8")).hexdigest(),
         "I am a junior student at Bogazici University and my major degree is in Computer Engineering, also I study a minor degree in Electrics and Electronics Engineering. Finally, I like everything about basketball.",
         "0aa64c1575f51c91930095311b536477", "Computer Engineering", "Umut"),
        ]
    client = Client()
    validToken = hashlib.sha256("umut@gmail.com".encode("utf-8")).hexdigest()

    def setUp(self):
        """
            This function creates the initial test database with users in the user_list above
         """
        with connection.cursor() as cursor:
            # Add some test users
            sql = 'INSERT INTO `' + USER_TABLENAME + '` (`name`, `surname`, `password_hashed`, `e_mail`, `token`, `about_me`, `job_uuid`, `field_of_study`, `forget_password_ans`) VALUES'
            for test_user in JokeTest.user_list:
                cursor.execute(sql + "('" + "','".join([str(x) for x in test_user]) + "');")


    def test_valid_token(self):
        """
            Test if api functionality works correctly or not in valid token case
        """
        data = {'token': JokeTest.validToken}
        response = JokeTest.client.get('/api/joke', data,follow=True)
        self.assertEqual(response.status_code, status.HTTP_200_OK)

    def test_invalid_token(self):
        """
            Test if api functionality works correctly or not in invalid token case
        """
        data = {'token': 'invalid'}
        response = JokeTest.client.get('/api/joke', data,follow=True)
        self.assertEqual(response.status_code, status.HTTP_401_UNAUTHORIZED)

    #this test is for without token scenario
    def test_without_token(self):
        """
            Test if api functionality works correctly or not in without token case
        """
        data = {}
        response = JokeTest.client.get('/api/joke/', data,follow=True)
        self.assertEqual(response.status_code, status.HTTP_401_UNAUTHORIZED)