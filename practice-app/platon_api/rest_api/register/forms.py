from django import forms
from crispy_forms.helper import FormHelper
from crispy_forms.layout import Layout, Submit, Row, Column
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
        
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.helper = FormHelper()
        self.helper.layout = Layout(
            Row(
                
                Column('name', css_class='form-group col-md-6'),
                Column('surname', css_class='form-group col-md-6'),
                css_class='form-row'
            ),
            Row(
                Column('e_mail', css_class='form-group col-sm-6'),
                Column('password1', css_class='form-group col-sm-3'),
                Column('password2', css_class='form-group col-sm-3'),
                css_class='form-row'
            ),
            Row(
                Column('forget_password_ans', css_class='form-group col-md-4'),
                Column('field_of_study', css_class='form-group col-md-4'),
                Column('job_name', css_class='form-group col-md-4'),
                css_class='form-row'
            ),
            Row(
                Column('about_me', css_class='form-group col-md-12'),
                css_class='form-row'
            ),
            Row(
                Submit('submit', 'Register', css_class='form-group col-md-12'),
                css_class='form-row'
            ),
            
        )    