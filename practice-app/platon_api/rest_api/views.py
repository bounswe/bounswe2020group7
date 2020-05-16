from django.shortcuts import render
from rest_api.register.register import register_api

def register(response):
    return register_api(response)
    
    
    
    
    