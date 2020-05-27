NAME
    rest_api.forgot_password.forgot_password

DESCRIPTION
    Created on MAY 24, 2020
    This script controls the forgot password endpoint of PLATON_API, using django&mysql backend.
    Endpoint description:
        http://localhost:8000/api/forgotpassword>

        GET':
            Produces error
        'POST':
            Changes the registered user password.
            JSON Format : {
                            e_mail = "E-mail parameter given by user",
                            forgot_password_ans = "forget password answer parameter given by user",
                            }
@author: Mehmet Temizel
@company: Group7

FUNCTIONS
    forgot_password(request)
        returns the response of whether password change succesfully or not  

DATA
    USER_TABLENAME = 'rest_api_registereduser'
    connection = <django.db.DefaultConnectionProxy object>

FILE
    D:\Python\bounswe2020group7\practice-app\platon_api\rest_api\forgot_password\forgot_password.py
