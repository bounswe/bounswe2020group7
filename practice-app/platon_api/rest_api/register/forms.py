from django import forms

from rest_api.models import RegisteredUser

class RegisteredUserForm(forms.ModelForm):
    job_name = forms.CharField(max_length=50)
    password1 = forms.CharField(widget=forms.PasswordInput, label="Password:") 
    password2 = forms.CharField(widget=forms.PasswordInput, label = "Password Again:") 
    class Meta:
        model = RegisteredUser
        labels = {"forget_password_ans" : "Your Favourite Teacher:",
                  "job_name":"Job Name:",
                  "field_of_study":"Field Of Study"}
        fields = [  "name",
                    "surname",
                    "e_mail",
                    "password1",
                    "password2",
                    "forget_password_ans",
                    "job_name",
                    "field_of_study",
                    "about_me",]