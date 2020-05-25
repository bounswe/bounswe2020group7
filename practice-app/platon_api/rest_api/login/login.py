"""
Created on MAY 23, 2020
This script controls the login functionality api of PLATON_API, using django&mysql backend.
Endpoint description:
    http://localhost:8000/api/login/
   
    'POST':
        Produces error
    'GET':
        Gets dictionary from body.
        JSON Format : {  
                        e_mail = "Your email will be here.",
                        password = "Your password will be here, hashed for protection."
                        }
@author: Kerem Uslular
@company: Group7
"""
from django.shortcuts import render, redirect
from django.db import connection
from platon_api.settings import DATABASES, USER_TABLENAME, WEBSITE_URL
import re, hashlib, random
import django.http.request
from rest_api.models import RegisteredUser
from django.http import HttpResponse

class login():
    """
    In this class the Login System endpoint is implemented.
     
    """

    @staticmethod
    def login(request):

        """
            where 'request' : HTTP Request that comes from the view class
            returns responses
            This is the function that verifies the user and gives the user a token.
        """

        if request.method == "GET":

            e_mail = request.GET.get("e_mail")
            password = request.GET.get("password")
            password = hashlib.sha256(password.encode("utf-8")).hexdigest()

            if (e_mail == None) or (password == None):
                return HttpResponse("Please fill all the required fields.", status=400)

             # regular expression for texts
            regex = r"[A-Za-zöçşüığİÖÇĞÜŞ]{0,50}( [A-Za-zöçşüığİÖÇĞÜŞ]{0,50})?"

             # Mail type validity check with regex
            if len(e_mail) != 0  and re.match(r"(^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$)", e_mail) == None:
                return HttpResponse("Please use a valid e-mail address.", status=400)


            val_sql = "SELECT id FROM "+ USER_TABLENAME +" WHERE e_mail = '{0}' AND password_hashed = '{1}'".format(e_mail, password)
            cursor = connection.cursor()
            # Execute SQL query
            cursor.execute(val_sql)
            tup = cursor.fetchone()

            if tup == None:
                return HttpResponse("Invalid e-mail or password!", status=400)

            user_id = int(tup[0])

            try:

                con_token = e_mail + str(random.randint(10000, 99999))
                hash_token = hashlib.sha256(con_token.encode()).hexdigest()

                tok_sql = "UPDATE "+ USER_TABLENAME +" SET token = '{0}' WHERE id = '{1}'".format(hash_token,user_id)

                # Execute SQL query
                cursor.execute(tok_sql)
                connection.commit()

                return HttpResponse(hash_token, status=200)
            except:
                return HttpResponse("Unexpected error occured.", status=500)
