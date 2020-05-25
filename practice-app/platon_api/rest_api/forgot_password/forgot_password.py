from rest_api.forgot_password.forms import ForgotForm
import hashlib, string
from django.shortcuts import get_object_or_404
from django.http import HttpResponse
from rest_api.models import RegisteredUser



def forgot_password(request):
    
    # create a form instance and populate it with data from the request:
    form = ForgotForm(request.POST)
    # check whether it's valid:
    if form.is_valid():
      
        ru = get_object_or_404(RegisteredUser,e_mail = form.cleaned_data['e_mail'])
        if form.cleaned_data['forget_password_ans'] == ru.forget_password_ans:
            if form.cleaned_data['password'] == form.cleaned_data['password_again']:
                ru.password_hashed = hashlib.sha256(form.cleaned_data['password'].encode("utf-8")).hexdigest()
                ru.save()
                return HttpResponse("Your password is changed",status=201)
            return HttpResponse("Passwords did not match",status=400)
        return HttpResponse("Your answer for your registered secret question is false",status=400)
    return HttpResponse("You must give appropriate input format",status=400)
  
     

        
