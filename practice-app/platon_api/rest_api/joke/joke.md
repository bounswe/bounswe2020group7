Help on module rest_api.joke.joke in rest_api.joke:

NAME
    rest_api.joke.joke - Created on MAY 22, 2020

DESCRIPTION
    
    This script controls the joke api of PLATON_API, using django&mysql backend.
    Endpoint description:
        http://localhost:8000/api/joke/

        'GET':
            Returns a joke from https://api.jokes.one/jod api.
            Get token from request header.
            JSON Format : { 'token': "" }   #token of the user


    @author: Ertugrul Bulbul, ertbulbul
    @company: Group7


FUNCTIONS
   
   verify_token(token=None)
           where 'token': string, 64 characther string that can be token

           returns True if token exists in the DB

        This function verifies the token if there exits.

    joke_api(request)
            where 'response': rest_framework response

            returns response from rest_framework

        This function only accepts GET requests, and valid token is neccessary to return joke


    get_joke():

            return a python dictionary, json that includes joke of the day

        This function returns a joke using an external api.



DATA
    
    USER_TABLENAME = 'rest_api_registereduser'
    connection = <django.db.DefaultConnectionProxy object>

FILE
    
    C:\Users\asus\Documents\bounswe2020group7\practice-app\platon_api\rest_api\joke\joke.py
