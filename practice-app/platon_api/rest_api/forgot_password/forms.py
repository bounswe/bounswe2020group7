from django import forms

class ForgotForm(forms.Form):
    e_mail = forms.CharField(label='E-mail', max_length=250)
    forget_password_ans = forms.CharField(label='Answer', max_length=50)     
    password = forms.CharField(label='Password', max_length=50)
    password_again = forms.CharField(label='Password again', max_length=50)
