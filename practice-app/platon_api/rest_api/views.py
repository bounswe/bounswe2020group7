
from rest_api.joke.joke import joke_api
from rest_api.update_user.update_user import updateUser
from rest_api.register.register import register_api,register_page
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import permissions
from rest_framework.decorators import api_view,permission_classes
from rest_api.search_engine.search_engine import searchEngine as engine
from rest_api.delete_user_t.delete_user_f import DeleteUser
from django.http import HttpResponse
from rest_api.news.news import news_api
from django.shortcuts import render
from rest_api.logout import logout as lg
from rest_api.translation.translation import translate

def translation(response,token):
    #takes http response and returns it
    context = translate(response,token)
    return context
     
# Create your views here.

def logout(request):
    return lg.logout(request)


@api_view(["GET"])
def joke(request):
    return joke_api(request)
  
  
def update(request):
    return updateUser(request)

def news(request, token):
    return HttpResponse(news_api(request, token))
  
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
