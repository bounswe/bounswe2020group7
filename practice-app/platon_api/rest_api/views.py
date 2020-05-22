from django.shortcuts import render

from rest_api.translation.translation import translate

def translation(response,token):
    context = translate(response,token)
    return render(response, "translation.html", context)
     
