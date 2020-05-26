"""
Created on May 24, 2020
This script controls the update user api of PLATON_API, using django&mysql backend.
Endpoint description:
    http://localhost:8000/api/updateUser/


@author: Meltem Arslan
@company: Group7
"""

from django.shortcuts import render, redirect
from django.db import connection, transaction
from platon_api.settings import DATABASES, USER_TABLENAME, WEBSITE_URL, JOB_LIST_API_URL
import requests as req
import re,copy, hashlib, random, json
from rest_api.update_user.forms import RegisteredUserForm
from rest_api.models import RegisteredUser
from rest_framework.response import Response

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

def isValid(name, surname, password1, token, e_mail, about_me, job_name, forget_pw_ans, field_of_study, user):
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

    # checks if the user entered the password or not
    if len(password1) == 0:
        return False

    # hashes the given password to update user information
    hash_password = hashlib.sha256(password1.encode("utf-8")).hexdigest()

    if user.e_mail != e_mail:
        return False

    # check if the given password is equal the password on the database or not (in hashed form)
    if(user.password_hashed != hash_password):
        return False

    # regular expression for texts
    regex = r"[A-Za-zöçşüığİÖÇĞÜŞ]{0,50}( [A-Za-zöçşüığİÖÇĞÜŞ]{0,50})?"

    # Text type validity check with regex
    if re.match(regex, name) == None or re.match(regex, surname) == None or re.match(regex, field_of_study) == None  or re.match(regex, forget_pw_ans) == None:
        return False

    # Mail type validity check with regex
    if len(e_mail) != 0  and re.match(r"(^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$)", e_mail) == None:
        return False
    return True


def updateUser(request):
    """
        where 'request': HTTP request that is from the view class
        This function updates the information of logged in user.
    """

    try:
        form = request.POST                                                 # acquire json
        # get fields from json
        token, name, surname, password1,e_mail, about_me, job_name, forget_pw_ans, field_of_study = (form.get("token"), form.get("name"), form.get("surname"), form.get("password1"),
                                                                                                        form.get("e_mail") ,form.get("about_me") , form.get("job_name"),
                                                                                                        form.get("forget_password_ans") ,form.get("field_of_study") )
        user = RegisteredUser.objects.get(token = token)
        
        if not isValid(name, surname, password1,token, e_mail, about_me, job_name, forget_pw_ans, field_of_study, user):
            return Response("Not valid input. Check password or give valid inputs to the fields!", 403)

        job_uuid = getJobName(job_name)
        #Checks the inputs one by one.
        #If any input is given to the fields, updates associated fields on the database,
        #else, the field on the database remains the same
        if(len(name) != 0):
            user.name = name
            user.save()
        if(len(surname) != 0):
            user.surname = surname
            user.save()
        if(len(about_me) != 0):
            user.about_me = about_me
            user.save()
        if(len(forget_pw_ans) != 0):
            user.forget_password_ans = forget_pw_ans
            user.save()
        if(len(field_of_study)!=0):
            user.field_of_study = field_of_study
            user.save()
        if(len(job_uuid) != 0):
            user.job_uuid = job_uuid
            user.save()
        #If program reaches at this point, returns confirmation message.
        return Response("User information is updated!", status=201)
    except:
        #If program fails, it returns this message.
        return Response("Error! Information could not be changed.", 403)
