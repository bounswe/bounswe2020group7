from django.shortcuts import render, HttpResponse
#from rest_api.forgot_password import forgot_password

#def forgot_password(request):
 #     return forgot_password(request)
 if request.GET.get("token"):
    token = request.GET["token"]



# Create your views here.
