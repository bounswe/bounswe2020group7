from django.shortcuts import render
from rest_api.register.register import register_form, register_api

def register(response):
    return register_api(response)

def register_page(response):
    return register_form(response)
            
    
    
    
    
    