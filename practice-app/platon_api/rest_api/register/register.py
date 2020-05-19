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
from django.http import HttpResponse, HttpRequest
from platon_api.settings import DATABASES, USER_TABLENAME, WEBSITE_URL, JOB_LIST_API_URL
import requests as req
from django.shortcuts import redirect,render
import re,copy, requests, hashlib


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
        
        # Length control of input parameters
        if len(name) > 30 or len(surname) > 30 or len(email) > 255 or len(about_me) > 330  or len(forget_pw_ans) > 50  or len(field_of_study) > 50:
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

def register_api(response):
    """
        where 'response': HttpResponse, Name parameter given by user

        returns Json object if 'GET' requested else nothing
        
        This function takes Httpresponse object and if 'POST' request used, it inserts into database
    """
    error = ""
    resp = HttpResponse()               # create response object
    resp.status_code = 200              # set status to 200 as default
    if response.method == "POST":       # if post request sent
        try:
            form = response.POST        # acquire json
            # get fields from json
            name, surname, password1,password2, email, about_me, job_name, forget_pw_ans, field_of_study = form.get("name"), form.get("surname"), form.get("password1"),form.get("password2"), form.get("e_mail") , form.get("about_me") , form.get("job_name"), form.get("forget_password_ans") ,form.get("field_of_study") 
    
            job_uuid = getJobName(job_name)   #a short word to describe job
            # validity control
            if isValid(name, surname, password1,password2, email, about_me, job_uuid, forget_pw_ans, field_of_study): 
                try:
                    cursor = connection.cursor()
                    db_name = DATABASES.get("default").get("NAME")                          # get database name from settings
                    password = hashlib.sha256(password1.encode("utf-8")).hexdigest()        # make password hashed
                    # MYsql insertion query
                    query = "INSERT INTO `" + db_name + "`.`" + USER_TABLENAME + "` (`name`, `surname`, `password_hashed`, `e_mail`, `token`, `about_me`, `job_uuid`, `field_of_study`, `forget_password_ans`) VALUES ("
                    query += "'" + name + "','" +  surname + "','" + password + "','" + email + "','','"  + about_me + "','"+ job_uuid+ "','" + field_of_study + "','" +  forget_pw_ans + "');"
                    cursor.execute(query)
                    resp.status_code = 201      # if successfull, response code 201 CREATED
                    error = "SUCCESSFULL"
                except:
                    resp.status_code = 503      # if fail, response code 501 INTERNAL SERVER ERROR
                    error = "SOME_ERROR_WHILE_INSERTION_TO_DB"
            else:
                error = "NOT_VALID_INPUT"
                
        except:
            error = "VERY_BAD_THING_HAPPENED"
    resp["Content-type"] = "application/json"
    resp.write({"response":error})         # return ERROR AS json
    return resp