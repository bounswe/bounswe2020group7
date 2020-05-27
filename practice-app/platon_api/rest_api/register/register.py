"""
Created on MAY 16, 2020

This script controls the registration api of PLATON_API, using django&mysql backend.
Endpoint description:
    http://localhost:8000/api/register/

    'GET':
        Returns a random poem using external api
    'POST':
        Gets dictionary from body.
        JSON Format : { 'name': "",                 string, Name parameter given by user
                        'surname': "",              string, Surname parameter given by user
                        'password1': "",            string, Password1 given by user
                        'password2':"",             string, Password2 given by user to check whether Password1 is matched.
                        'e_mail':"",                string, Email parameter given by user
                        'about_me':"",              string, About me parameter given by user
                        'job_name':"",              int, job id parameter chosen by user
                        'forget_password_ans':"",   string, forget password answer parameter given by user
                        'field_of_study':"" }       string, field of study parameter given by user

@author: Burak Omur, darktheorys
@company: Group7

"""
from django.shortcuts import render, redirect
from django.db import connection, transaction
from platon_api.settings import DATABASES, USER_TABLENAME, WEBSITE_URL, JOB_LIST_API_URL,HOME_PAGE
import requests as req
import re,copy, hashlib, random, json
from .forms import RegisteredUserForm
from rest_framework.response import Response
from rest_api.serializers import RegisteredUserSerializer

def getPoem():
    """

        return a python dictionary, json that includes random poem

    This function returns a random poet using an external api.

    """

    resp = {}
    try:
        url ="https://www.poemist.com/api/v1/randompoems"                   # url of external api
        rq = req.get(url).json()                                            # requesting it
        poem = copy.deepcopy(random.choice(list(rq)))                       # random choice
        resp["Wow"] = "You should not be using GET for registration."       # message to user
        resp["Luck"] = "Well, Here a poem for you."                  # message to user

        if "content" in poem:                                               # arrangements
            poem["poem"] = poem["content"]
            del poem["content"]

        if "url" in poem:                                                   # arrangements
            del poem["url"]

        if "poet" in poem:                                                  # arrangements
            pt = poem["poet"]
            poem["poet"] = pt["name"]

        resp["Content"] = poem
    except:
        pass
    return resp


def getJobName(name):
    """
        where name is the job name taken from user while registration

        returns uuid of normalized job, or empty string

    This function takes a string and using an external api it normalizes and returns the uuid of that job.
    """
    try:
        resp = req.get(JOB_LIST_API_URL + name).json()
        if "error" not in resp:
            return resp[0].get("uuid")         # getting uuid field
    except:
        pass
    return ""


def isValid(name, surname, password1,password2, email, about_me, job_name, forget_pw_ans, field_of_study):
    """
        where 'name': string, Name parameter given by user
        where 'surname': string, Surname parameter given by user
        where 'password1': string, Password1 given by user
        where 'password2': string, Password2 given by user to check whether Password1 is matched.
        where 'email': string, Email parameter given by user
        where 'about_me': string, About me parameter given by user
        where 'job_id': string, job id parameter chosen by user
        where 'forget_pw_ans': string, forget password answer parameter given by user
        where 'field_of_study': string, field of study parameter given by user

        returns True if given values appropriate to insert in database, else False

    This function takes input parameters and checks them if they are valid and return the boolean result.
    """
    try:
        if len(name) == 0 or len(surname) == 0 or len(email) == 0 or len(about_me) == 0 or len(forget_pw_ans) == 0 or len(field_of_study)==0 or len(job_name) == 0:
            return False

        # Password match control
        if password1 != password2 or len(password1) == 0:
            return False

        # regular expression for texts
        regex = r"[A-Za-zöçşüığİÖÇĞÜŞ]{2,50}( [A-Za-zöçşüığİÖÇĞÜŞ]{2,50})?"

        # Text type validity check with regex
        if re.match(regex, name) == None or re.match(regex, surname) == None or re.match(regex, field_of_study) == None  or re.match(regex, forget_pw_ans) == None:
            return False

        # Mail type validity check with regex
        if re.match(r"(^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$)", email) == None:
            return False

        return True
    except:
        return False


def register_api(request):
    """
        where 'response': rest_framework response

        returns response from rest_framework

        This function only accepts POST requests, and if valid input is necessary inserts into database
    """
    json_response = {"response":""}
    resp = Response()              # create response object
    resp.status_code = 200              # set status to 200 as default
    resp["Content-type"] = "application/json"

    if request.method == "POST":
        try:
            form = copy.deepcopy(request.data)        # acquire json
            # get fields from json
            name, surname, password1,password2, email, about_me, job_name, forget_pw_ans, field_of_study = (form.get("name"), form.get("surname"), form.get("password1"),form.get("password2"),
                                                                                                            form.get("e_mail") , form.get("about_me") , form.get("job_name"),
                                                                                                            form.get("forget_password_ans") ,form.get("field_of_study") )

            job_uuid, password = getJobName(job_name) , hashlib.sha256(password1.encode("utf-8")).hexdigest()           # obtaining pw and job name
            form["job_uuid"], form["password_hashed"] =  job_uuid, password                                             # setting values
            serializer = RegisteredUserSerializer(data = form)                                                          # defining serializer for modal

            if isValid(name, surname, password1, password2, email, about_me, job_uuid, forget_pw_ans, field_of_study) and serializer.is_valid():     # validity control
                serializer.save()
                resp.status_code = 201                                                                  # if successfull, response code 201 CREATED
                json_response["response"] = "SUCCESSFULL"
            else:
                resp.status_code = 400
                json_response["response"] = "NOT_VALID_INPUT"
        except:
            resp.status_code = 403
            json_response["response"] = "VERY_BAD_THING_HAPPENED"                                                              # return ERROR AS json
    else:
        json_response = getPoem()
    resp.data = json_response
    return resp


def register_page(request):
    """
        where request HttpRequest to carry post and get requests

        This function takes a request and if POST, then it registers into system using another endpoint of this api
        If get, it renders a form to register and also a random poem.
    """
    form, resp_api, error =  {}, {}, ""                                     # initialization
    if request.method == "POST":                                            # if post
        form = RegisteredUserForm(request.POST)                             # acquire form
        try:
            resp_api = req.post(WEBSITE_URL + "/api/register/", request.data )             # send post request to another endpoint to register into system
            if resp_api.status_code == 201:                                                # if successful
                return redirect(HOME_PAGE)                                                    # redirect home
            else:
                error = "FAIL TO REGISTER - CHECK YOUR FIELDS"                                  # error
        except:
            pass
    try:
        form = RegisteredUserForm()                                                         # create empty form
        resp_apis = dict(req.get(WEBSITE_URL + "/api/register/").json())                    # get poem
        resp_api = []
        for key,value in resp_apis.items():                                                 # parse poem
            if key == "Content":
                dt = dict(value)
                if "title" in dt:
                    resp_api.append(dt["title"])
                if "poem" in dt:
                    poem = str(dt["poem"])
                    for par in poem.split("\n\n"):
                        resp_api.append("-")
                        for each in par.split("\n"):
                            resp_api.append(each)

                if "poet" in dt:
                    resp_api.append("-------------")
                    resp_api.append(dt["poet"])
            if key != "Wow":
                resp_api.append(key + " : " + value)
                resp_api.append("-------------")
    except:
        pass
    return render(request, 'register/register_form.html', {"error":error, "resp": resp_api, 'form':form})