from django.shortcuts import render, HttpResponse
from rest_api.forgot_password.forgot_password import forgot_password
from rest_api.forgot_password.forms import ForgotForm



# Create your views here.
def forgotpassword(request):
    if request.method == 'POST':
        # create a form instance and populate it with data from the request:
        form = forgot_password(request)
        
    # if a GET (or any other method) we'll create a blank form
    else:
        form = ForgotForm()

    return render(request, 'forgot.html', {'form': form})
    

