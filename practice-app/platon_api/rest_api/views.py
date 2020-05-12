from django.shortcuts import render
from rest_framework.views import APIView
from rest_framework.response import Response

from rest_api.search_engine.search_engine import searchEngine as engine

class Search(APIView):

    def get(self,request):
        return Response(engine.search(request))

# Create your views here.
