"""
Created on MAY 18, 2020
This script controls the translate functionality api of PLATON_API, using django mvc model.
Endpoint description:
    http://localhost:8000/api/translate/<str:token>
    
    'GET':
        Gets token from url.
        token = "Your token will be in url",
                    
@author: Hasan Ramazan Yurt
@company: Group7
"""

from django.shortcuts import get_object_or_404
from rest_framework.response import Response
from rest_api.models import RegisteredUser
import requests as req

def translate(request, token):
    """
    This function returns a translated about me part of user whose token is given by using an external api.
    
    """
    try:
        #find user with respect to token
        regUser = RegisteredUser.objects.get(token = token)

        #general response
        resp = Response()
        resp["content-type"] = "application/json"

        #url for request
        url ="https://api.funtranslations.com/translate/yoda.json?text=" + regUser.about_me
        rq = req.get(url).json()

        if("error" in rq):
            #if there is a key error
            resp.data = rq
            resp.status_code = 200
        else:
            #no error
            context = {
                'translated': rq["contents"]["translated"],
            }
            resp.data = context
            resp.status_code = 200
    except:
        resp.data = {"error": "Unauthorized access"}
        resp.status_code = 403
    return resp