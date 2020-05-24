from django.shortcuts import render
from rest_api.delete_user_t.delete_user_f import DeleteUser
# Create your views here.
from rest_framework.views import APIView
from rest_framework.response import Response

class Delete(APIView):

    def post(self,request):
        try:
            return Response(DeleteUser.deleteUser2(request))
        except:
            return Response("Please give an appropriate input!!")
