from rest_api.joke.joke import joke_api
from rest_framework.decorators import api_view

def joke(response):
    return joke_api(response)


@api_view(["GET"])
def joke(request):
    return joke_api(request)