from rest_framework.test import APITestCase
from rest_framework import status


class JokeTest(APITestCase):


    # this test is for valid token
    def test_valid_token(self):
        #before testing please enter a valid token
        validToken = '03684b7c7efc7ba7ffe53f32f3c9346d8383fcc13eddd1272705c746ba986f2f'
        data = {'token': validToken}
        response = self.client.get('/api/joke/token=03684b7c7efc7ba7ffe53f32f3c9346d8383fcc13eddd1272705c746ba986f2f')
        self.assertEqual(response.status_code, status.HTTP_200_OK)

    # this test is for invalid token
    def test_invalid_token(self):
        data = {'token': 'invalid'}
        response = self.client.get('/api/joke/', data)
        self.assertEqual(response.status_code, status.HTTP_401_UNAUTHORIZED)

    #this test is for without token scenario
    def test_without_token(self):
        response = self.client.get('/api/joke/')
        self.assertEqual(response.status_code, status.HTTP_401_UNAUTHORIZED)