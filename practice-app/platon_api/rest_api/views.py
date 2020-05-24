from django.shortcuts import render

from rest_api.translation.translation import translate

def translation(response,token):
    #takes http response and returns it
    context = translate(response,token)
    return context
     
