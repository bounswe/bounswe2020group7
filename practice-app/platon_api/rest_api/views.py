from django.shortcuts import render

from rest_api.translation.translation import translate

def translation(response,token):
    return translate(response,token)
     
