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
from rest_api.models import RegisteredUser

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
from rest_api.frontend.forms import RegisterForm, LoginForm, ForgotForm, UpdateForm

def index(request):
    register_form = RegisterForm()
    login_form = LoginForm()
    context =  {'register_form': register_form, 'login_form': login_form}
    return render(request, 'index.html', context)

@api_view(["POST", "GET"])
@permission_classes((permissions.AllowAny,))
def register_f(request):
    resp =  register_api(request)
    register_form = RegisterForm()
    login_form = LoginForm()
    context =  {'register_form': register_form, 'login_form': login_form}
    context["resp"] =  resp
    context["status_code"] =  resp.status_code
    return render(request, 'index.html', context)

def login_f(request):
    resp = login.login(request)
    if resp.status_code == 200:
        return redirect('../home/'+ resp.content.decode("UTF-8"))
    else:
        register_form = RegisterForm()
        login_form = LoginForm()
        context =  {'register_form': register_form, 'login_form': login_form}
        context["login_fail"] = True
        return render(request, 'index.html', context)

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
    if request.method == 'POST':
        resp = forgot_password(request)
        if resp.status_code == 201:
            return redirect('index')
        else:
            forgot_password_form = ForgotForm()
            return render(request, 'password_reset.html', {'reset_fail': resp.status_code, 'forgot_password_form':forgot_password_form})
    forgot_password_form = ForgotForm()
    return render(request, 'password_reset.html', {'forgot_password_form':forgot_password_form})

def joke_f(request, token):
    request.GET = request.GET.copy()
    request.GET["token"] = token
    resp = joke_api(request)
    return render(request, "joke.html", {'token': token, 'title': resp.data["title"], 'joke': resp.data["joke"]})

def search_f(request):
    token = request.GET["token"]
    if "job" and "field_of_study" in request.GET:
        request.GET = request.GET.copy()
        if request.GET["job"] != "" and request.GET["field_of_study"]!="":
            request.GET["filter"] = json.dumps({'job': request.GET["job"], 'field_of_study': request.GET["field_of_study"] })
        elif request.GET["job"] != "":
            request.GET["filter"] = json.dumps({'job': request.GET["job"]})
        elif request.GET["field_of_study"] != "":
            request.GET["filter"] = json.dumps({'field_of_study': request.GET["field_of_study"]})
    resp = engine.search(request)
    return render(request, "search_result.html", {'search_result': resp[0], 'token': token})

def error_f(request):
    return render(request, 'error.html')

def update_f(request, token):
    if request.method == 'POST':
        request.POST = request.POST.copy()
        request.POST["token"] = token
        request.method = "POST"
        resp = updateUser(request)
        if resp.status_code == 201:
            update_form = UpdateForm()
            return render(request, 'update.html', {'updated': resp.status_code, 'update_form': update_form, 'token': token})
        else:
            update_form = UpdateForm()
            return render(request, 'update.html', {'updated': resp.status_code, 'update_form': update_form, 'token': token})
    update_form = UpdateForm()
    return render(request, 'update.html', {'update_form': update_form, 'token': token})

def delete_f(request, token):
    request.POST = request.POST.copy()
    request.POST["token"] = token
    request.method = "POST"
    user = RegisteredUser.objects.get(token=token)
    request.POST["email"] = user.e_mail
    DeleteUser.deleteUser2(request)
    return redirect('index')