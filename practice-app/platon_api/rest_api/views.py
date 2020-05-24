from rest_api.joke.joke import joke_api
from rest_framework.decorators import api_view

@api_view(["GET"])
def joke(request):
    return joke_api(request)