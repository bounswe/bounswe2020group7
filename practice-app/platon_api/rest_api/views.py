from django.shortcuts import render
from django.http import HttpResponse
from rest_api.news.news import news_api

# Create your views here.

def news(request, token):
    return HttpResponse(news_api(request, token))