from django.shortcuts import render
from rest_framework.views import APIView
from rest_framework.response import Response

from rest_api.search_engine.search_engine import searchEngine as engine

class Search(APIView):

    def get(self,request):
        try:
            return Response(engine.search(request))
        except:
            return Response("Wrong input. Please give an appropriate input!!")
 

# Create your views here.
