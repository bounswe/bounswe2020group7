from django.shortcuts import render, HttpResponse
from rest_framework.views import APIView
from rest_framework.response import Response

from rest_api.forgot_password.forgot_password import Forgot_Password

class ForgotPassword(APIView):
    
    def get(self,request):
        try:
            return Response(Forget_Password.forgot_password(request))
        except:
            return Response("Please give an a appropriate input!")

# Create your views here.
