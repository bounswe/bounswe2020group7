from django.db import models

class RegisteredUser(models.Model):
    name = models.CharField(max_length=30)
    surname = models.CharField(max_length=30)
    password_hashed = models.CharField(max_length=64)
    e_mail = models.EmailField(max_length=250, unique= True)
    token = models.CharField(max_length=64, null = True, unique = True)
    about_me = models.TextField(max_length=330)
    job_uuid = models.CharField(max_length=32)
    field_of_study = models.CharField(max_length=50)
<<<<<<< HEAD
    forget_password_ans = models.CharField(max_length=50)
    
# Create your models here.
=======
    forget_password_ans = models.CharField(max_length=50)    
>>>>>>> origin/practice-app-development
