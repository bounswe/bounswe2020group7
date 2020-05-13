from django.db import connection, transaction
import hashlib
from django.http import HttpResponse, HttpRequest
from platon_api.settings import DATABASES, USER_TABLENAME, JOB_CHOICES, WEBSITE_URL
from rest_api.register import forms
import requests as req
from django.shortcuts import redirect
from django.shortcuts import render
import re,copy
from django.contrib import messages

def isValid(name, surname, password1,password2, email, about_me, job_id, forget_pw_ans, field_of_study):
    try:
        if len(name) > 30 or len(surname) > 30 or len(email) > 255 or len(about_me) > 330  or len(forget_pw_ans) > 50  or len(field_of_study) > 50  or int(job_id) > len(JOB_CHOICES):
            return False
        
        if password1 != password2:
            return False
        
        regex = r"[A-Za-zöçşüığİÖÇĞÜŞ]{2,50}( [A-Za-zöçşüığİÖÇĞÜŞ]{2,50})?"
        
        if re.match(regex, name) == None or re.match(regex, surname) == None or re.match(regex, field_of_study) == None  or re.match(regex, forget_pw_ans) == None:
            return False

        if re.match(r"(^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$)", email) == None:
            return False
        
        return True
    except:
        return False

def register_api(response):
    resp = HttpResponse()
    resp.status_code = 200
    if response.method == "POST":
        try:
            form = response.POST
            name, surname, password1,password2, email, about_me, job_id, forget_pw_ans, field_of_study = form.get("name"), form.get("surname"), form.get("password1"),form.get("password2"), form.get("e_mail") , form.get("about_me") , form.get("job_id"), form.get("forget_password_ans") ,form.get("field_of_study") 
            if isValid(name, surname, password1,password2, email, about_me, job_id, forget_pw_ans, field_of_study):
                try:
                    cursor = connection.cursor()
                    db_name = DATABASES.get("default").get("NAME")
                    password = hashlib.sha256(password1.encode("utf-8")).hexdigest()
                    token = hashlib.sha256(name.encode("utf-8")).hexdigest()
                    query = "INSERT INTO `" + db_name + "`.`" + USER_TABLENAME + "` (`id`, `name`, `surname`, `password_hash`, `e-mail`, `token`, `about_me`, `job_id`, `field_of_study`, `forget_password_ans`) VALUES ("
                    query += "NULL,'" + name + "','" +  surname + "','" + password + "','" + email + "','" + token + "','"  + about_me + "','"+ job_id + "','" + field_of_study + "','" +  forget_pw_ans + "');"
                    cursor.execute(query)
                    resp.status_code = 201
                except:
                    resp.status_code = 501
        except:
            pass
    if resp.status_code == 200:
        resp["Content-type"] = "application/json"
        resp.write({"error":"SOME_ERROR_OCCURRED"})
    return resp


def register_form(response):
    error = ""
    form = ""
    if response.method == "POST": 
        form = forms.RegisterForm(response.POST)
        url = WEBSITE_URL + "/api/register/"
        resp = register_api(response)
        if resp.status_code == 201:
            return redirect("/api/index")
        error = "Some Error Occurred" 
    else:    
        form = forms.RegisterForm()
    return render(response, "register/register.html", {"form": form, "error":error})
            
    