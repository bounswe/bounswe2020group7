from django.http import HttpResponse
from rest_api.models import RegisteredUser
from django.utils.datastructures import MultiValueDictKeyError
from django.core.exceptions import ObjectDoesNotExist
from rest_framework.response import Response

def logout(request):
	# Checks whether the request method is GET, as the logout request should be sent using the GET method.
	try:
		# Retrieves the token from the query string.
		token = request.GET["token"]
		# From the database, retrieves the user who is currently logged in with the "token".
		logged_in_user = RegisteredUser.objects.get(token=token)
		# Sets the "token" field of the user to NULL.
		logged_in_user.token = None
		logged_in_user.save()
		# Returns the response to the client.
		return Response("You have succesfully logged out. Goodbye, see you soon!", status=200)
	except MultiValueDictKeyError:
		# If the client sends the request with a token parameter, then the server returns a "400 Bad Request" error.
		return Response("400 Bad Request", status=400)
	except ObjectDoesNotExist:
		# If the client sends the request with a token nonexistent in the database,
		# (namely, when there is no currently logged-in user with the token sent)
		# then the server returns a "401 Unauthorized" error.
		return Response("401 Unauthorized", status=401)


