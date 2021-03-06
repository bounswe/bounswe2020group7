"""
Created on MAY 22, 2020
This script controls the joke api of PLATON_API, using django&mysql backend.
Endpoint description:
    http://localhost:8000/api/joke/

    'POST':
        Produces error

    'GET':
        Returns a joke from https://api.jokes.one/jod api.
        Get token from request header.
        JSON Format : { 'token': "" }   #token of the user


@author: Ertugrul Bulbul, ertbulbul
@company: Group7
"""

import requests
from django.db import connection
from rest_framework.response import Response
from platon_api.settings import USER_TABLENAME



def verify_token(token=None):
    """
        where 'token': string, 64 characther string that can be token
        returns True if token exists in the DB
        This function verifies the token if there exits.
    """
    # If there is no token we cannot validate
    if token is None:
        return False
    # SQL Query to verify token from database
    sql = "SELECT token FROM "+USER_TABLENAME+" WHERE token='{}'".format(token)
    # Create a MySQL cursor
    cursor = connection.cursor()
    # Execute SQL query
    cursor.execute(sql)
    result = cursor.fetchall()
    # Validate the token if there is
    return len(result) != 0


def get_joke():
    """

        return a python dictionary, json that includes joke of the day

        This function returns a joke using an external api.

    """

    url = 'https://api.jokes.one/jod'
    api_token = "zaCELgL.0imfnc8mVLWwsAawjYr4Rx-Af50DDqtlx"
    headers = {'content-type': 'application/json',
               'X-JokesOne-Api-Secret': format(api_token)}

    try:
        joke = requests.get(url, headers=headers).json()['contents']['jokes'][0]['joke']
    except:
        joke = False

    return joke

def joke_api(request):
    """
           where 'response': rest_framework response
           returns response from rest_framework

           This function only accepts GET requests, and valid token is neccessary to return joke
    """

    if request.GET.get("token"):
        token = request.GET["token"]
    else:
        token = None

    response = Response()
    response["Content-type"] = "application/json"
    json = {}
    if request.method == "GET":  # if get request sent
        try:
            joke = get_joke()
            # validity control
            if verify_token(token=token):
                # Successful request
                if joke != False:
                    json = {'status' : 'success','token': token,'title' : joke['title'],'joke' : joke['text']}
                else:
                    # Since the API that I used only accepts 10 requests in an hour, it returns this data if it exceeds 10 requests
                    json = {'status' : 'success','token': token,'title' : 'No Joke','joke' : 'The joke will be ready in just an hour'}

                response.status_code = 200
            else:
                # Unauthorized client
                json = {'status' : 'invalid_token'}
                response.status_code = 401
        except:
            # Internal error
            response.status_code = 500

    response.data = json
    return response
