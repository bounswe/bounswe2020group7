"""platon_api URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/3.0/topics/http/urls/
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


from rest_api import views

urlpatterns = [
<<<<<<< HEAD
    path('api/register/', views.register, name="register"),
    path('api/register/fe', views.register_fe, name="register_fe"),
=======
    path('admin/', admin.site.urls),
    path('api/search/',views.Search.as_view())
>>>>>>> origin/practice-app-development
]
