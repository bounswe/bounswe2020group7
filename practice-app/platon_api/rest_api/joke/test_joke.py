from rest_framework.test import APITestCase
from rest_framework import status

class JokeTest(APITestCase):


    def test_valid_token(self):
        data = { "token" : "03684b7c7efc7ba7ffe53f32f3c9346d8383fcc13eddd1272705c746ba986f2f"}
        response = self.client.request("/api/joke" ,data)
        self.assertEqual(response.status_code, status.HTTP_200_OK)

    def test_invalid_token(self):
        data = {"token": "invalid"}
        response = self.client.request("/api/joke", data)
        self.assertEqual(response.status_code, status.HTTP_401_UNAUTHORIZED)

    def test_without_token(self):
        data = {}
        response = self.client.request("/api/joke", data)
        self.assertEqual(response.status_code, status.HTTP_401_UNAUTHORIZED)