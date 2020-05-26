"""platon_api URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/2.2/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""


from django.urls import path, include
from rest_api import views
from rest_api import views as v_rest_api

urlpatterns = [
    path('api/forgotpassword/', views.forgot_password),
    path('api/joke/', views.joke, name="joke"),
    path('api/updateUser/', views.updateUser),
    path('api/register/', views.register, name="register"),
    path('api/register/fe', views.register_fe, name="register_fe"),
    path('api/search/',views.Search.as_view()),
    path('api/deleteuser/', views.Delete.as_view()),
    path('api/news/<str:token>/', v_rest_api.news, name="news"),
    path('api/logout/', views.logout, name="logout"),
    path('api/translation/<str:token>', views.translation),
    path('api/login/',views.Login.as_view()),

    #Frontend starts
    path('', views.index, name='index'),
    path('register/', views.register_f, name='register_f'),
    path('login/', views.login_f, name='login_f'),
    path('home/<str:token>/', views.home, name="home"),
    path('about/<str:token>/', views.about, name="about"),
    path('logout/<str:token>/', views.logout_f, name="logout_f"),
    path('resetpassword/', views.resetpassword_f, name="resetpassword_f"),
    path('joke/<str:token>/', views.joke_f, name="joke_f"),
    path('search/', views.search_f, name="search_f"),
    path('error/', views.error_f, name="error_f"),
    path('update/<str:token>/', views.update_f, name="update_f"),
    path('delete/<str:token>/', views.delete_f, name="delete_f"),
]
