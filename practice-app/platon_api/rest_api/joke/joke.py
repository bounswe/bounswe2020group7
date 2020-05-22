import requests
from django.db import connection
from django.http import JsonResponse
from django.db import connection


from rest_framework.response import Response





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


def get_joke():

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

    response = Response()
    response["Content-type"] = "application/json"
    json = {}
    if request.method == "GET":  # if get request sent
        try:
            form = request.GET  # acquire json
            token = form.get("token")
            joke = get_joke()

            # validity control
            if verify_token(token=token):
                # Successful request
                if joke != False:
                    json = {'status' : 'success','token': token,'title' : joke['title'],'joke' : joke['text']}
                else:
                    json = {'status' : 'success','token': token,'title' : 'No Joke','joke' : 'The joke will be ready in just an hour'}

                response.status_code = 200
            else:
                # Unauthorized client
                json = {'status' : 'invalid_token'}
                response.status_code = 401

        except:
            # Internal error
            json = {'status': 'bad_request'}
            response.status_code = 500

    response.data = json
    return response
