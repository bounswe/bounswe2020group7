from django.shortcuts import render, get_object_or_404
from django.http import HttpResponse, Http404, HttpResponseRedirect
from django.urls import reverse

from rest_api.models import RegisteredUser

def translate(request, token):
    regUser = get_object_or_404(RegisteredUser,token = token)

    #url ="https://api.funtranslations.com/translate/yoda.json?text=" + regUser.about_me
    #rq = req.get(url).json()

    context = {
        'ru': regUser,
    }
    return render(request,"translation.html",context)