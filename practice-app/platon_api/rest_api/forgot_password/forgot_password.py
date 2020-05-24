from rest_api.forgot_password.forms import ForgotForm
import re,copy, hashlib, random, json, string, requests
from django.shortcuts import render, get_object_or_404
from django.http import HttpResponseRedirect
from rest_api.models import RegisteredUser
from rest_framework.response import Response



def forgot_password(request):
    
    # create a form instance and populate it with data from the request:
    form = ForgotForm(request.POST)
    # check whether it's valid:
    if form.is_valid():
      
        ru = get_object_or_404(RegisteredUser,e_mail = form.cleaned_data['e_mail'])
        ru.save()
        if form.cleaned_data['forget_password_ans'] == ru.forget_password_ans:
            print(ru.password_hashed)
            if form.cleaned_data['password'] == form.cleaned_data['password_again']:
                ru.password_hashed = hashlib.sha256(form.cleaned_data['password'].encode("utf-8")).hexdigest()
                ru.save()
            print(ru.password_hashed)
           
        return HttpResponseRedirect('/index/')

    return render(request, 'forgot.html', {'form': form})

     

        