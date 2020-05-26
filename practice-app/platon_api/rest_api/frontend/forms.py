from django.contrib.auth import login, authenticate
from django.contrib.auth.forms import UserCreationForm, AuthenticationForm
from django.db import models
from django.contrib.auth.models import User
from django import forms

class RegisterForm(UserCreationForm):
    name = forms.CharField(label='Name', max_length=30)
    surname = forms.CharField(label='Surname', max_length=30)
    e_mail = forms.EmailField(label='E-Mail', max_length=250)
    field_of_study = forms.CharField(label='Field Of Study', max_length=50)
    forget_password_ans = forms.CharField(label='Best teacher at school ( secret question )', max_length=50)
    about_me = forms.CharField(widget=forms.Textarea, max_length=5000)
    job_name = forms.CharField(label='Job',max_length=32)

    class Meta:
        model = User
        fields = ["name","surname", "e_mail", "password1", "password2", "forget_password_ans", "field_of_study", "job_name", "about_me"]

class LoginForm(forms.Form):
    e_mail = forms.CharField(label='E-mail', max_length=250)
    password = forms.CharField(label='Password', max_length=50, widget=forms.PasswordInput)


class ForgotForm(forms.Form):
    e_mail = forms.CharField(label='E-mail', max_length=250)
    forget_password_ans = forms.CharField(label='Answer', max_length=50)
    password = forms.CharField(label='Password', max_length=50, widget=forms.PasswordInput)
    password_again = forms.CharField(label='Password again', max_length=50, widget=forms.PasswordInput)