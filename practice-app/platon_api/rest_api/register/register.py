"""
Created on MAY 16, 2020

This script controls the registration api of PLATON_API, using django&mysql backend.
Endpoint description:
    http://localhost:8000/api/register/
    
    'GET':
        Produces error
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

from django.db import connection, transaction
from platon_api.settings import DATABASES, USER_TABLENAME, WEBSITE_URL, JOB_LIST_API_URL
import requests as req
import re,copy, hashlib

from rest_framework.response import Response
from rest_api.serializers import RegisteredUserSerializer 


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
        if password1 != password2:
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
    try:
        form = copy.deepcopy(request.data)        # acquire json
        # get fields from json
        name, surname, password1,password2, email, about_me, job_name, forget_pw_ans, field_of_study = (form.get("name"), form.get("surname"), form.get("password1"),form.get("password2"), 
                                                                                                        form.get("e_mail") , form.get("about_me") , form.get("job_name"), 
                                                                                                        form.get("forget_password_ans") ,form.get("field_of_study") )
        
        job_uuid, password = getJobName(job_name) , hashlib.sha256(password1.encode("utf-8")).hexdigest()   
        form["job_uuid"], form["password_hashed"] =  job_uuid, password 
        serializer = RegisteredUserSerializer(data = form)
        # validity control
        if isValid(name, surname, password1, password2, email, about_me, job_uuid, forget_pw_ans, field_of_study) and serializer.is_valid(): 
            serializer.save()
            resp.status_code = 201                                                                  # if successfull, response code 201 CREATED
            json_response["response"] = "SUCCESSFULL"
        else:
            resp.status_code = 400   
            errors = serializer.errors
            for each in errors.keys():
                if each not in json_response:
                    json_response[each] = errors[each]
            json_response["response"] = "NOT_VALID_INPUT"    
    except:
        resp.status_code = 400  
        json_response["response"] = "VERY_BAD_THING_HAPPENED"                                                              # return ERROR AS json
        
    resp.data = json_response
    return resp