"""
Created on May 23, 2020

This script controls the delete user api of PLATON_API, using django&mysql backend.
Endpoint description:
    http://localhost:8000/api/deleteuser/
    
    Anything other then 'DELETE':
        Returns error
    'DELETE':
        Deletes the logged in user.
        JSON Format : {   
                        token = "Token given by the system when you log in",
                        email = "Logged in users email"
                        }
@author: Oyku Yilmaz
@company: Group7
"""



from platon_api.settings import USER_TABLENAME, BS_BOOK_KEY
from django.db import connection
import requests, json, random
from rest_framework.response import Response

class DeleteUser:
    """
    In this class, delete user endpoint is implemented
    """

    
    @staticmethod
    def bestsellers():
        """
            returns a message including a book recommendation in the format "NAME OF THE BOOK " by "AUTHOR". "DESCRIPTION".

            This function returns a message including a book recomendation taken by NYT bestseller list.
        """


        mes = ""
        try:
            url = "https://api.nytimes.com/svc/books/v3/lists/current/hardcover-fiction.json"
            res = requests.get(url, {"api-key" : BS_BOOK_KEY})
            allList = json.loads(res.text)
            books = allList["results"]["books"]
            if(len(books) == 0):
                return ""
            book = random.choice(list(books))
            mes = "Not interested in research anymore? Maybe a book can help you refresh yourself."
            mes += "Here is a bestseller book recommendation by New York Times"
            mes += book["title"] + " by " + book["author"] 
            if("description" in book):
                mes += book["description"]
        except:
            pass
        return mes

    @staticmethod
    def verifyTokenAndMail(token=None, email=None):
        """
            where 'token': string, 64 characther string that can be token

            where 'email': string

            returns TRUE if the token and the email belongs to a specific user in the database

            This function verifies if both the mail and token exist in the database.

        """





        if token is None or email is None:
            return False
        
        stmt = "SELECT `e_mail` FROM " + USER_TABLENAME 
        stmt += " WHERE `token` = \"" +  token + "\"" 
        cursor = connection.cursor()
        cursor.execute(stmt)
        records = cursor.fetchall()
        if len(records) != 1:
            return False
        return records[0][0] == email
    
    @staticmethod   
    def deleteUser2(request):
        """
            where 'request': HTTP request that is from the view class

            returns nothing if everything goes right, returns an error message otherwise.

            This function deletes the logged in user.
            
        """




        stmt = ""
        if request.method == 'DELETE':
            try:
                if request.GET.get("token"):
                    token = request.GET.get("token")
                else:
                    return "No token found."
                if request.GET.get("email"):
                    email = request.GET.get("email")
                else:
                    return "No e-mail address found."
                if not DeleteUser.verifyTokenAndMail(token, email):
                    return "No user found."
                else:
                    stmt = "DELETE FROM `" + USER_TABLENAME + "` WHERE token = \"" + token +"\""
                    cursor = connection.cursor()
                    cursor.execute(stmt)
                    return DeleteUser.bestsellers()
            except:
                return "Cannot perform the request."
        else:
            return "Wrong Request."


        



