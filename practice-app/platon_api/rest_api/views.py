from django.shortcuts import render
from rest_api.register.register import register_api

from rest_framework import permissions
from rest_framework.decorators import api_view,permission_classes

@api_view(["POST", "GET"])
@permission_classes((permissions.AllowAny,))
def register(request):
    return register_api(request)
