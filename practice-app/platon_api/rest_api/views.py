from django.shortcuts import render, HttpResponse
from rest_framework.views import APIView
from rest_framework.response import Response

from rest_api.forgot_password.forgot_password import Forgot_Password

class ForgotPassword(APIView):
    
    def get(self,request):
        return Response(Forget_Password.forgot_password(request))
 
if request.GET.get("token"):
    token = request.GET["token"]



# Create your views here.
