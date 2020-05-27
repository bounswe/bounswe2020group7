This method provides the logout functionality to the app.

1. It retrieves the token from the query string in the URL. (If the request was not sent with GET method or if there is no "token" parameter in the query string, a "400 Bad Request" error is returned.)
2. Whether there is a currently logged in user with the token in the request gets checked by a database query. If found, the token for the user in the database gets nullified. (If not, -namely, when there is no currently logged in user with the token- a "401 Unauthorized" error is returned.)
3. After the token for the user gets nullified, "You have succesfully logged out. Goodbye, see you soon!" message is returned with the 200 status code.
