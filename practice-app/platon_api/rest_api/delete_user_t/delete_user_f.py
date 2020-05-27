"""
Created on May 23, 2020

This script controls the delete user api of PLATON_API, using django&mysql backend.
Endpoint description:
    http://localhost:8000/api/deleteuser/
    
    Anything other than 'POST':
        Returns error
    'POST':
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

        #initialize message to return
        mes = ""
        try:
            #url of the best seller book api
            url = "https://api.nytimes.com/svc/books/v3/lists/current/hardcover-fiction.json"
            res = requests.get(url, {"api-key" : BS_BOOK_KEY})
            #turn this into json form
            allList = json.loads(res.text)
            #get the books dictionary from the result
            books = allList["results"]["books"]
            #if there is no books returned from the API return empty string
            if(len(books) == 0):
                return ""
            #randomly select a book from the list
            book = random.choice(list(books))
            #create the message
            mes = "Not interested in research anymore? Maybe a book can help you refresh yourself."
            mes += " Here is a bestseller book recommendation by New York Times "
            mes += book["title"] + " by " + book["author"] + " "
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
        #return false if either parameters is empty
        if token is None or email is None:
            return False
        #create the sql query
        stmt = "SELECT `e_mail` FROM " + USER_TABLENAME 
        stmt += " WHERE `token` = \"" +  token + "\"" 
        #execute the query
        cursor = connection.cursor()
        cursor.execute(stmt)
        records = cursor.fetchall()
        #if there is more than one user whith the same token it should return false
        if len(records) != 1:
            return False
        #returns true if a user has the given token and mail adress, returns false otherwise
        return records[0][0] == email
    
    @staticmethod   
    def deleteUser2(request):
        """
            where 'request': HTTP request that is from the view class

            returns nothing if everything goes right, returns an error message otherwise.

            This function deletes the logged in user.

        """



        #initialize sql query
        stmt = ""
        #returns an error message if the request != "POST"
        try:
            if request.POST.get("token"):
                token = request.POST.get("token")
            else:
                return Response("No token found.", 403)
            if request.POST.get("email"):
                email = request.POST.get("email")
            else:
                return Response("No e-mail address found.", 403)
            if not DeleteUser.verifyTokenAndMail(token, email):
                return Response("No user found.", 403)
            else:
                stmt = "DELETE FROM `" + USER_TABLENAME + "` WHERE token = \"" + token +"\""
                cursor = connection.cursor()
                cursor.execute(stmt)
                return Response(DeleteUser.bestsellers(), 200)
        except:
            return Response("Cannot retrieve your token or your mail at the moment, sorry.", 403)
