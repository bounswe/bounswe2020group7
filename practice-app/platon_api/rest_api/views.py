from rest_api.forgot_password.forgot_password import forgot_password
from rest_api.forgot_password.forms import ForgotForm
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
from rest_api.login.login import login


def translation(response,token):
    #takes http response and returns it
    context = translate(response,token)
    return context

# Create your views here.

def forgotpassword(request):
    if request.method == 'POST':
        # create a form instance and populate it with data from the request:
        form = forgot_password(request)

    # if a GET (or any other method) we'll create a blank form
    else:
        form = ForgotForm()

    return render(request, 'forgot.html', {'form': form})




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

class Login(APIView):
    def get(self,request):
        return login.login(request)

######## Frontend starts
from django.shortcuts import render, redirect
from rest_api.frontend.forms import RegisterForm, LoginForm, ForgotForm

def index(request):
    register_form = RegisterForm()
    login_form = LoginForm()
    context =  {'register_form': register_form, 'login_form': login_form}
    return render(request, 'index.html', context)

@api_view(["POST", "GET"])
@permission_classes((permissions.AllowAny,))
def register_f(request):
    resp =  register_api(request)
    print("h", resp.status_code)
    register_form = RegisterForm()
    login_form = LoginForm()
    context =  {'register_form': register_form, 'login_form': login_form}
    context["resp"] =  resp
    context["status_code"] =  resp.status_code
    return render(request, 'index.html', context)

def login_f(request):
    resp = login.login(request)
    return redirect('../home/'+ resp.content.decode("UTF-8"))

import copy, json

def home(request, token):
    context = json.loads(news_api(request, token))
    context["token"] = token
    if context != 404:
        context['token'] = token
        return render(request, 'home.html', context)
    else:
        return {'error_message': 'Invalid Token'}

def about(request, token):
    context = translate(request, token)
    my_str = context.content.decode('utf-8').replace("'","\"")
    data = json.loads(my_str)
    if('error' in data):
        translated = data["error"]["message"]
    elif('translated' in data):
        translated = data["translated"]
    else:
        translated = "Your about me context is not written in English"

    return render(request, "about.html", {'token': token, 'translated': translated})

import copy

def logout_f(request, token):
    request.GET = request.GET.copy()
    request.GET["token"] = token
    lg.logout(request)
    return redirect('index')

def resetpassword_f(request):
    forgot_password_message = "Your password has been changed successfully!"
    forgot_password_form= ForgotForm()
    return render(request, 'password_reset.html', {'forgot_password_message': forgot_password_message, 'forgot_password_form':forgot_password_form})

def forgotpassword_f(request):
    forgot_password(request)
    return redirect('index')

def joke_f(request, token):
    request.GET = request.GET.copy()
    request.GET["token"] = token
    resp = joke_api(request)
    return render(request, "joke.html", {'token': token, 'title': resp.data["title"], 'joke': resp.data["joke"]})

