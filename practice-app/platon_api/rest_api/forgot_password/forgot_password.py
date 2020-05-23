from django.db import connection
from platon_api.settings import DATABASES, USER_TABLENAME, WEBSITE_URL, JOB_LIST_API_URL
from django.http import JsonResponse
from itsdangerous import TimedJSONWebSignatureSerializer as Serializer

import re,copy, hashlib, random, json, string, requests

from rest_framework.response import Response

if request.GET.get("token"):
    token = request.GET["token"]


class Forgot_Password:

    @staticmethod
    def test_validate_token(token=None):
        """
            where 'token': string, 64 characther string that can be token

            returns True if token exists in the DB

            This function verifies the token if there exits.

        """
        # If there is no token we cannot validate
        if token is None:
            return False
        # SQL Query to verify token from database
        sql = "SELECT token FROM "+ USER_TABLENAME +" WHERE token='{}'".format(token)
        sql_update = "SELECT token FROM "+ USER_TABLENAME +" WHERE token='{}'".format(token)
        # Create a MySQL cursor
        cursor = connection.cursor()
        # Execute SQL query
        cursor.execute(sql)
        result = cursor.fetchall()
        # Validate the token if there is
        return len(result)!=0


    def get_reset_token(self,expires_sec=1800):
        s = serializer(app.config['SECRET_KEY'], expires_sec)
        return s.dumps({'user_id': self.id}).decode('utf-8')

    @staticmethod
    def verify_reset_token(token):
        s = Serializer(appiconfig['SECRET_KEY'])
        try: 
            user_is = s.loads(token)['user_is'] 
        except:
            return None
        return User.query.get(user_id)



    def forgot_password:


        