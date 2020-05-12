from django.contrib.auth import login, authenticate
from django.contrib.auth.forms import UserCreationForm
from django.db import models
from django.contrib.auth.models import User
from django import forms
from platon_api.settings import JOB_CHOICES

class RegisterForm(UserCreationForm):
    name = forms.CharField(label='Name:', max_length=30)
    surname = forms.CharField(label='Surname:', max_length=30)
    e_mail = forms.EmailField(label='E-Mail:', max_length=250)
    field_of_study = forms.CharField(label='Field Of Study:', max_length=50)
    forget_password_ans = forms.CharField(label='Best teacher at school ( secret question ):', max_length=50)
    about_me = forms.CharField(widget=forms.Textarea)
    job_id = forms.ChoiceField(choices=JOB_CHOICES)
    
    
    class Meta:
        model = User
        fields = ["name","surname", "e_mail", "password1", "password2", "forget_password_ans", "field_of_study", "job_id", "about_me"]
    