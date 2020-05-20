from django.db import connection, transaction
from django.http import HttpResponse, HttpRequest

from django.shortcuts import redirect, render
import re, copy, requests, hashlib


@staticmethod
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
    sql = "SELECT token FROM users WHERE token='{}'".format(token)
    # Create a MySQL cursor
    cursor = connection.cursor()
    # Execute SQL query
    cursor.execute(sql)
    result = cursor.fetchall()
    # Validate the token if there is
    return len(result) != 0

@staticmethod
def get_joke():
    url = 'https://api.jokes.one/jod'
    api_token = "KEY"
    headers = {'content-type': 'application/json',
               'X-JokesOne-Api-Secret': format(api_token)}
    response = requests.get(url, headers=headers)
    return response.json()['contents']['jokes'][0]

def joke_api(response):
    """
        where 'response': HttpResponse, Name parameter given by user

        returns Json object if 'GET' requested else nothing

        This function takes Httpresponse object and if 'POST' request used, it inserts into database
    """
    error = ""
    resp = HttpResponse()  # create response object
    resp.status_code = 200  # set status to 200 as default
    if response.method == "GET":  # if post request sent
        try:
            print(1)
            form = response.GET  # acquire json
            print(2)
            # get fields from json
            token = form.get("token")
            print(token)
            # validity control

            #if verify_token(token=token):
            if True:
                #resp[""] = get_joke()
                print(get_joke())
                resp.status_code = 201  # if successfull, response code 201 CREATED
                error = "SUCCESSFULL"
            else:
                resp.status_code = 503  # if fail, response code 501 INTERNAL SERVER ERROR
                error = "SOME_ERROR_WHILE_INSERTION_TO_DB"

        except:
            error = "VERY_BAD_THING_HAPPENED"
    resp["token"] = token
    resp.write({"response": error})  # return ERROR AS json
    return resp