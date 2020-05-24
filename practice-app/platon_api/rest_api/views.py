from django.shortcuts import render

from rest_api.register.register import register_api,register_page
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import permissions
from rest_framework.decorators import api_view,permission_classes
from rest_api.search_engine.search_engine import searchEngine as engine
from rest_api.delete_user_t.delete_user_f import DeleteUser

class Delete(APIView):

    def post(self,request):
        try:
            return Response(DeleteUser.deleteUser2(request))
        except:
            return Response("Please give an appropriate input!!")
          
          

class Search(APIView):

    def get(self,request):
        try:
            return Response(engine.search(request))
        except:
            return Response("Please give an appropriate input!!")

@api_view(["POST", "GET"])
@permission_classes((permissions.AllowAny,))
def register(request):
    return register_api(request)

@api_view(["GET", "POST"])
@permission_classes((permissions.AllowAny,))
def register_fe(request):
    return register_page(request)

