Help on module rest_api.translation.translation in rest_api.translation:

NAME

    rest_api.translation.translation

DESCRIPTION

    Created on MAY 18, 2020
    This script controls the translate functionality api of PLATON_API, using django mvc model.
    Endpoint description:
        http://localhost:8000/api/translate/<str:token>
        
        'GET':
            Gets token from url.
            token = "Your token will be in url",
                        
    @author: Hasan Ramazan Yurt
    @company: Group7

FUNCTIONS

    translate(request, token)
        This function returns a translated about me part of user whose token is given by using an external api.

FILE

    c:\users\halil\desktop\ders\cmpe 352\repository\bounswe2020group7\practice-app\platon_api\rest_api\translation\translation.py


