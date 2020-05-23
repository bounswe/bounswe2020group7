from platon_api.settings import USER_TABLENAME
from django.db import connection

class deleteUser:


    @staticmethod
    def verifyTokenAndMail(token=None, email=None):
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
        stmt = ""
        if request.method == 'POST':
            try:
                if request.GET.get("token"):
                    token = request.GET.get("token")
                else:
                    return "No token found."
                if request.GET.get("email"):
                    email = request.GET.get("email")
                else:
                    return "No e-mail address found."
                if not deleteUser.verifyTokenAndMail(token, email):
                    return "No user found."
                else:
                    stmt = "DELETE FROM `" + USER_TABLENAME + "` WHERE token = \"" + token +"\""
                    cursor = connection.cursor()
                    cursor.execute(stmt)
            except:
                return "Cannot perform the request."
        else:
            return "Wrong Request."