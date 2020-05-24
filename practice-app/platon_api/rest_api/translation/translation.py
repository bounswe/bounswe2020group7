from django.shortcuts import render, get_object_or_404
from django.http import HttpResponse, Http404, HttpResponseRedirect
from django.urls import reverse

from rest_api.models import RegisteredUser

import requests as req

def translate(request, token):
    #find user with respect to token or send 404 error
    regUser = get_object_or_404(RegisteredUser,token = token)

    url ="https://api.funtranslations.com/translate/yoda.json?text=" + regUser.about_me
    rq = req.get(url).json()

    context = {
        'translated': rq["contents"]["translated"],
    }
    return context