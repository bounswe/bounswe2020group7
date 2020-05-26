from rest_api.forgot_password.forgot_password import forgot_password as fg
from rest_api.forgot_password.forms import ForgotForm
from rest_api.joke.joke import joke_api
from rest_api.update_user.update_user import updateUser as ud
from rest_api.register.register import register_api,register_page
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import permissions
from rest_framework.decorators import api_view
from rest_api.search_engine.search_engine import searchEngine as engine
from rest_api.delete_user_t.delete_user_f import DeleteUser
from django.http import HttpResponse, HttpRequest
from rest_api.news.news import news_api
from django.shortcuts import render
from rest_api.logout import logout as lg
from rest_api.translation.translation import translate
from rest_api.login.login import login
from rest_api.models import RegisteredUser

@api_view(["GET"])
def translation(response,token):
    #takes http response and returns it
    return translate(response,token)

@api_view(["POST"])
def forgotpassword(request):
    return fg(request)

@api_view(["GET"])
def logout(request):
    return lg.logout(request)

@api_view(["GET"])
def joke(request):
    return joke_api(request)

@api_view(["POST"])
def update(request):
    return ud(request)

@api_view(["GET"])
def news(request, token):
    return news_api(request, token)

@api_view(["POST"])
def delete_user(request):
    return DeleteUser.deleteUser2(request)

@api_view(["GET"])
def search_in(request):
    search_result,status = engine.search(request)
    return Response(search_result,status=status)

@api_view(["POST", "GET"])
def register(request):
    return register_api(request)

@api_view(["GET", "POST"])
def register_fe(request):
    return register_page(request)

@api_view(["POST"])
def login_sys(request):
    return login(request)

######## Frontend starts
from django.shortcuts import render, redirect
from rest_api.frontend.forms import RegisterForm, LoginForm, ForgotForm, UpdateForm

def index(request):
    register_form = RegisterForm()
    login_form = LoginForm()
    context =  {'register_form': register_form, 'login_form': login_form}
    return render(request, 'index.html', context)

@api_view(["POST", "GET"])
def register_f(request):
    resp =  register_api(request)
    register_form = RegisterForm()
    login_form = LoginForm()
    context =  {'register_form': register_form, 'login_form': login_form}
    context["resp"] =  resp
    context["status_code"] =  resp.status_code
    return render(request, 'index.html', context)

def login_f(request):
    resp = login_sys(request)
    if resp.status_code == 200:
        return redirect('../home/'+ resp.data)
    else:
        register_form = RegisterForm()
        login_form = LoginForm()
        context =  {'register_form': register_form, 'login_form': login_form}
        context["login_fail"] = True
        return render(request, 'index.html', context)

import copy, json

def home(request, token):
    try:
        context = json.loads(news_api(request, token).data)
        context["token"] = token
        if context != 404:
            context['token'] = token
            return render(request, 'home.html', context)
        else:
            return {'error_message': 'Invalid Token'}
    except:
        return HttpResponse("Unauthorized Request",status=401)
def about(request, token):
    data = translate(request, token).data
    print(data)
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
        resp = forgotpassword(request)
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
    if resp[1] != 200:
        resp = ([], resp[1])
    return render(request, "search_result.html", {'search_result': resp[0], 'token': token})

def error_f(request):
    return render(request, 'error.html')

def update_f(request, token):
    update_form = UpdateForm()
    if request.method == "POST":
        request.POST = request.POST.copy()
        request.POST["token"] = token
        resp = ud(request)
        if resp.status_code == 201:
            return render(request, 'update.html', {'updated': resp.status_code, 'update_form': update_form, 'token': token})
        else:
            return render(request, 'update.html', {'updated': resp.status_code, 'update_form': update_form, 'token': token})
    return render(request, 'update.html', {'update_form': update_form, 'token': token})

def delete_f(request, token):
    request.POST = request.POST.copy()
    request.POST["token"] = token
    user = RegisteredUser.objects.get(token=token)
    request.POST["email"] = user.e_mail
    DeleteUser.deleteUser2(request)
    return redirect('index')

def book_f(request,token):
    book_recommendation = DeleteUser.bestsellers()
    return render(request,'book.html',{'book_recommendation':book_recommendation, 'token':token})