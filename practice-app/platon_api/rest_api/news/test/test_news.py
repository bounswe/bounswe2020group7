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
    """

    client = Client(enforce_csrf_checks=True)

    def test_wrong_token(self):
        wrong_token = "1"*64
        resp = NewsTestCase.client.get('api/news/' + wrong_token)
        self.assertEqual(resp.status_code, status.HTTP_404_NOT_FOUND)
