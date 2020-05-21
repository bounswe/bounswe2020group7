import requests
from django.db import connection
from django.http import JsonResponse




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

    response = requests.get(url, headers=headers)

    return response.json()['contents']['jokes'][0]['joke']

def joke_api(request):

    response = JsonResponse({'status': 'bad_request'}, status=500, safe=False)
    print(request.method)
    if request.method == "GET":  # if get request sent
        try:
            form = request.GET  # acquire json
            token = form.get("token")
            joke = get_joke()

            # validity control
            if verify_token(token=token):
                # Successful request
                response = JsonResponse({'status' : 'success','token': token,'title' : joke['title'],'joke' : joke['text']},status=200, safe=False)
            else:
                # Unauthorized client
                response = JsonResponse({'status' : 'invalid_token'},status=401, safe=False)
        except:
            # Internal error
            response = JsonResponse({'status': 'bad_request'}, status=500, safe=False)
    else:
        print(request.method)

    return response
