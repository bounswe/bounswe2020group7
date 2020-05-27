from rest_api.forgot_password.forms import ForgotForm
import hashlib, string
from rest_api.models import RegisteredUser
from rest_framework.response import Response


def forgot_password(request):
    try:
        # create a form instance and populate it with data from the request:
        form = ForgotForm(request.data)
        # check whether it's valid:
        if form.is_valid():
        
            ru = RegisteredUser.objects.get(e_mail = form.cleaned_data['e_mail'])
            if form.cleaned_data['forget_password_ans'] == ru.forget_password_ans:
                if form.cleaned_data['password'] == form.cleaned_data['password_again']:
                    ru.password_hashed = hashlib.sha256(form.cleaned_data['password'].encode("utf-8")).hexdigest()
                    ru.save()
                    return Response("Your password is changed",status=201)
                return Response("Passwords did not match",status=400)
            return Response("Your answer for your registered secret question is false",status=400)
        return Response("You must give appropriate input format",status=400)
    except:
        return Response("Error Occurred",status=400)
  
     

        
