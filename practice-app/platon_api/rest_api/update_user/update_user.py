from platon_api.settings import USER_TABLENAME
from django.db import connectionfrom django.shortcuts import render, redirect
from django.db import connection, transaction
from platon_api.settings import DATABASES, USER_TABLENAME, WEBSITE_URL, JOB_LIST_API_URL,HOME_PAGE
import requests as req
import re,copy, hashlib, random, json
from .forms import RegisteredUserForm
from rest_framework.response import Response
from rest_api.serializers import RegisteredUserSerializer 
from rest_api.models import RegisteredUser



def isValid(name, surname, password1,password2,password3, token, e_mail, about_me, job_name, forget_pw_ans, field_of_study):
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
        if len(password1) == 0:
            return False
        

        # Password match control
        if password2 != password3:
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
      
def updateUser(request):

    if request.method == "POST":
        try:
            form = copy.deepcopy(request.data)        # acquire json
            # get fields from json
            name, surname, password1,password2, password3, e_mail, about_me, job_name, forget_pw_ans, field_of_study = (form.get("name"), form.get("surname"), form.get("password1"),form.get("password2"), form.get("password3"),
                                                                                                            form.get("e_mail") ,form.get("about_me") , form.get("job_name"), 
                                                                                                            form.get("forget_password_ans") ,form.get("field_of_study") )      
    token = request.user.token
    isValid(name, surname, password1,password2, password3, token, e_mail, about_me, job_name, forget_pw_ans, field_of_study)
    user = RegisteredUser.objects.get(token = token)
    if(len(name) != 0):
        user.name = name
        user.save()
    if(len(surname) != 0):
        user.surname = surname
        user.save()
    if(len(e_mail)!= 0):
        user.e_mail = e_mail
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
    if(len(job_name) != 0):
        user.job_name = job_name
        user.save()
    






























    @staticmethod
    def verifyTokenAndMail(token=None, email=None):
        if token is None or email is None:
            return False
        
        stmt = "SELECT `e_mail` FROM " + USER_TABLENAME 
        stmt += " WHERE `token` = \"" +  token + "\"" 
        cursor = connection.cursor()
        cursor.execute(stmt)
        records = cursor.fetchall()
        if len(records) != 1:
            return False
        return records[0][0] == email
    
    @staticmethod   
    def deleteUser2(request):
        stmt = ""
        if request.method == 'POST':
            try:
                if request.GET.get("token"):
                    token = request.GET.get("token")
                else:
                    return "No token found."
                if request.GET.get("email"):
                    email = request.GET.get("email")
                else:
                    return "No e-mail address found."
                if not deleteUser.verifyTokenAndMail(token, email):
                    return "No user found."
                else:
                    stmt = "DELETE FROM `" + USER_TABLENAME + "` WHERE token = \"" + token +"\""
                    cursor = connection.cursor()
                    cursor.execute(stmt)
            except:
                return "Cannot perform the request."
        else:
            return "Wrong Request."