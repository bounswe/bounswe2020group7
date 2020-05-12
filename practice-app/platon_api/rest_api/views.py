from django.shortcuts import render
from rest_api.register.register import register_view
from django.http import HttpRequest

def register(response):
    return register_view(response)