from django.shortcuts import render
from rest_api.update_user.update_user import updateUser

# Create your views here.
def update(request):
    return updateUser(request)