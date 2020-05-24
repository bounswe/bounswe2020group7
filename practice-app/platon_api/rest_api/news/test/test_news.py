from django.test import TestCase
from platon_api.settings import USER_TABLENAME
from django.db import connection
from django.test import TransactionTestCase, Client
from rest_framework import status
import hashlib


class NewsTestCase(TransactionTestCase):

    """
    Unit tests of the recommended news
    ...
    Attributes
    ----------
    client : Client Object
        a client that will make the requests in the test cases
    valid_token: str
        A token that exists in the test database
    """

    client = Client()
    valid_token = "a8536fdc00ca3edd17f005480c3d30a2acfc4c53528ac8e8e97ae698d25aa982"

    def test_wrong_token(self):
        wrong_token = "1"*64
        resp = NewsTestCase.client.get('api/news/' + wrong_token)
        self.assertEqual(resp.status_code, status.HTTP_404_NOT_FOUND)

    def test_query_inserted_to_database(self):
        with connection.cursor() as cursor:
            sql = "INSERT INTO `" + USER_TABLENAME + "` (`name`, `surname`, `password_hashed`, `e_mail`, `token`, `about_me`, `job_uuid`, `field_of_study`, `forget_password_ans`) VALUES('Ahmet','Dadak','bf12a4a9f83acfa163584eba2882f5905cd2ffb1107e57f889547ba6c7d560d9','ahmet@gmail.com','a8536fdc00ca3edd17f005480c3d30a2acfc4c53528ac8e8e97ae698d25aa982','Junior computer engineering student at Bogazici University. Writes code, scripts, and lyrics. Interested in Web Applications and ML. Interested in sports, literature, design, and music.','0e78a3833926aa1f49dd9f4bb86b7386','Computer Engineering','Ahmet');"
            cursor.execute(sql)
        resp = NewsTestCase.client.get('api/news/' + NewsTestCase.valid_token)
        self.assertEqual(resp.status_code, status.HTTP_200_OK, "User could not be added to database. Check your database name!")