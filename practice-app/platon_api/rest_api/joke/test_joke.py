import json
from django.contrib.auth.models import User
from django.urls import reverse
from rest_framework.authtoken.models import Token
from django.test import TestCase,TransactionTestCase,Client
from django.test.client import RequestFactory
from rest_framework.test import APITestCase
from rest_framework import status


class JokeTest(APITestCase):


    def test_valid_token(self):
        c = Client()
        data = { 'token' : ''}
        response = c.get("http://127.0.0.1:8000/api/joke?token=03684b7c7efc7ba7ffe53f32f3c9346d8383fcc13eddd1272705c746ba986f2f")
        self.assertEqual(response.status_code, status.HTTP_200_OK)

    def test_invalid_token(self):
        c = Client()
        data = {'token': 'invalid'}
        response = c.get('http://127.0.0.1:8000/api/joke?token=invalid',follow=True, secure=True)
        self.assertEqual(response.status_code, status.HTTP_401_UNAUTHORIZED)

    def test_without_token(self):
        c = Client()
        data = {}
        response = c.get('http://127.0.0.1:8000/api/joke/')
        self.assertEqual(response.status_code, status.HTTP_401_UNAUTHORIZED)