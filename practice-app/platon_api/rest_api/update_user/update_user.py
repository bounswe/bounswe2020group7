
from django.shortcuts import render, redirect
from django.db import connection, transaction
from platon_api.settings import DATABASES, USER_TABLENAME, WEBSITE_URL, JOB_LIST_API_URL
import requests as req
import re,copy, hashlib, random, json
from rest_api.update_user.forms import RegisteredUserForm
from rest_api.models import RegisteredUser
from django.http import HttpResponse



def isValid(name, surname, password1, token, e_mail, about_me, job_name, forget_pw_ans, field_of_study):
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
    #finds user from database with primary key, token
    user = RegisteredUser.objects.get(token = token)

    # checks if the user entered the password or not
    if len(password1) == 0:
        return False

    # hashes the given password to update user information
    hash_password = hashlib.sha256(password1.encode("utf-8")).hexdigest()

    # check if the given password is equal the password on the database or not (in hashed form)
    if(user.password_hashed != hash_password):
        return False
    
    # regular expression for texts
    regex = r"[A-Za-zöçşüığİÖÇĞÜŞ]{0,50}( [A-Za-zöçşüığİÖÇĞÜŞ]{0,50})?"
    
    # Text type validity check with regex
    if re.match(regex, name) == None or re.match(regex, surname) == None or re.match(regex, field_of_study) == None  or re.match(regex, forget_pw_ans) == None:
        return False

    # Mail type validity check with regex
    if len(e_mail) != 0  and re.match(r"(^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$)", email) == None:
        return False
    
    return True
    
      
def updateUser(request):
    
    if request.method == "POST":
    
        try:
           
            form = copy.deepcopy(request.POST)       # acquire json
            # get fields from json
            name, surname, password1,e_mail, about_me, job_name, forget_pw_ans, field_of_study = (form.get("name"), form.get("surname"), form.get("password1"),
                                                                                                            form.get("e_mail") ,form.get("about_me") , form.get("job_name"), 
                                                                                                            form.get("forget_password_ans") ,form.get("field_of_study") )      

            token = form.get("token")
           
            
            if not isValid(name, surname, password1,token, e_mail, about_me, job_name, forget_pw_ans, field_of_study):
                return HttpResponse("Not valid input. Check password or give valid inputs to the fields!")

            
            user = RegisteredUser.objects.get(token = token)
            
            #Checks the inputs one by one. 
            #If any input is given to the fields, updates associated fields on the database, 
            #else, the field on the database remains the same
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
            #If program reaches at this point, returns confirmation message.
            return HttpResponse("User information is updated!")
        except:
            #If program fails, it returns this message.
            return HttpResponse("Error! Information could not be changed.")    
