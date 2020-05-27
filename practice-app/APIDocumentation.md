# 1. Registration Endpoint Documentation


___
### **DESCRIPTION**

This collection of endpoints is designed to give registration authorization. It takes necessary parameters from user and if valid it inserts into database.BELOW YOU CAN FIND AS TEXT FORMAT, BUT FROM [**HERE**](https://github.com/bounswe/bounswe2020group7/blob/practice-app-register/practice-app/platon_api/rest_api/register/rest_api.register.register.html) A HTML FORMAT
DONT FORGET YOU NEED TO DONWLOAD TO VIEW HTML

#### **Endpoint /api/register**

    This script controls the registration api of PLATON_API, using django&mysql backend.
        http://localhost:8000/api/register/
        
        'GET':
            Returns a random poem using an external API as JSON
        'POST':
            Gets dictionary from body.
            JSON Format : { 'name': "",                 string, Name parameter given by user
                            'surname': "",              string, Surname parameter given by user
                            'password1': "",            string, Password1 given by user
                            'password2':"",             string, Password2 given by user to check whether Password1 is matched.
                            'e_mail':"",                string, Email parameter given by user
                            'about_me':"",              string, About me parameter given by user
                            'job_name':"",              string, one word job description
                            'forget_password_ans':"",   string, forget password answer parameter given by user
                            'field_of_study':"" }       string, field of study parameter given by user
    
* **Example Requests**
   * POST: "http://localhost:8000/api/register", content-type: "application/json"

         {
               'name' : 'burak', 'surname':'ömür', 'e_mail':'burak.omur@gmail.com', 'about_me':'a student only', 'job_name':"engineer",'forget_password_ans':"father" , 'field_of_study':"science",'password1': "123456",'password2':"123456" 
         }

   * GET: "http://localhost:8000/api/register"

         {
         }

* **Response Status Codes**
    * POST:

          HTTP_201_CREATED : If successfully inserted into database.
          HTTP_400_BAD_REQUEST : If given parameters not appropriate to insert into database.
          HTTP_403_FORBIDDEN: If some error occurred due to api/server/database.
    * GET:

          HTTP_200_OK : Supports get request and returns a random poem in body in application/json format.

#### **Endpoint /api/register/fe**

    This script controls the registration api of PLATON_API, using django&mysql backend.
        http://localhost:8000/api/register/fe
        
        'GET':
            Renders a html page that includes form and random poem.
        'POST':
            Gets dictionary from body.
            JSON Format : { 'name': "",                 string, Name parameter given by user
                            'surname': "",              string, Surname parameter given by user
                            'password1': "",            string, Password1 given by user
                            'password2':"",             string, Password2 given by user to check whether Password1 is matched.
                            'e_mail':"",                string, Email parameter given by user
                            'about_me':"",              string, About me parameter given by user
                            'job_name':"",              string, one word job description
                            'forget_password_ans':"",   string, forget password answer parameter given by user
                            'field_of_study':"" }       string, field of study parameter given by user
    
* **Using form of our known HTML it takes parameters and posts into above described endpoint.**
* **Output:**
    * Always a random poem.
    * Redirects into itself.
    * If fail to insert, it shows an error message.
___

# 2.Search Endpoint Documentation

This module makes a search in the registered users of the platform. You can give a search string and filters about the job and field of study of the user that you want to find. Also, a sorting criteria can be given as parameter. It searches and finds a list of users according to your criteria.

## Input Format

This endpoint has no POST request. So if you send a post request it will give an error. The output is the following:

    {
        "detail": "Method \"POST\" not allowed."
    }

GET request parameters:

    {   
        token = "Your token will be here!!",
        search_string = "The string that you want to search will be here",
        filter = {"job" = "job_filter", "field_of_study" = "field_of_study_filter" },
        sorting_criteria = "Sorting criteria will be here"
    }

* `token` parameter is explained in the **Authentication** part.
* `search_string` parameter can be any string that you want to search. To see all of the users give an empty string`(like search_string="" not search_string=)` as the value of this parameter. You have to give the search_string parameter.
* `filter` parameter is set of filters in the system. `job` can be any job in our system. `field_of_study` can be any string. `filter` parameter and its subparameters are not obligatory parameters. If you don' give this as a parameter system makes a search without filtering.
* `sorting_criteria`is the sorting criteria of the result list. If any parameter is given the default sorting is made according to the semantic points of the results. **name_increasing**, **name_decreasing**, **surname_increasing**, **surname_decreasing** can be given as a parameter. It sorts the result list according to the parameter given.

## Authentication

This module uses the tokens of the system for authentication. To use this endpoint, firstly register to the system with `../api/register/` endpoint and get a token using `../api/login/` endpoint. And give this token as an input to the request as:

    { 
        token = "Your token will be here!!" 
    }

## Sample Output

### Request 

> GET http://127.0.0.1:8000/api/search/?token=824514797890967718&search_string=trial&filter={"job":"student"}

### Output

    [
        {
            "name": "Test1",
            "surname": "Test1",
            "e-mail": "test@test.com",
            "about_me": "I am a test user and I will be deleted when API is deployed!!",
            "job": "Student",
            "field_of_study": "Test Field1"
        },
        {
            "name": "Test1",
            "surname": "Test1",
            "e-mail": "test@test.com",
            "about_me": "I am a test user and I will be deleted when API is deployed!!",
            "job": "Student",
            "field_of_study": "Test Field1"
        },
        {
            "name": "Test1",
            "surname": "Test1",
            "e-mail": "test@test.com",
            "about_me": "I am a test user and I will be deleted when API is deployed!!",
            "job": "Student",
            "field_of_study": "Test Field1"
        },
        {
            "name": "Test1",
            "surname": "Test1",
            "e-mail": "test@test.com",
            "about_me": "I am a test user and I will be deleted when API is deployed!!",
            "job": "Student",
            "field_of_study": "Test Field1"
        }
    ]

## Error Codes

* Token Error:

> GET http://127.0.0.1:8000/api/search/?search_string=trial&filter={"job":"student"}

    "You have to give your token" Status Code: 401

* 3rd Party API Error:

> GET http://127.0.0.1:8000/api/search/?token=824514797890967718&search_string=trial

    "There is a problem about the 3rd party APIs that I used. Please try again!!" , Status Code: 500

* Input Error:

> GET http://127.0.0.1:8000/api/search/?token=824514797890967718&search_string=trial&filter={"job":"dent"}

    "You give your input in wrong format. Please check the API documentation for the appropriate input format!!" Status Code: 400

# 3.Delete User Endpoint Documentation

This module deletes the logged in users account from the database. It returns a best seller book recommendation for the user who deleted their account.

## Input Format

This endpoint has no GET request. So if you send a get request it will give an error. The output is the following:

    {
        "detail": "Method \"GET\" not allowed."
    }

POST request parameters:

    {   
        token = "Token given by the system when you log in",
        email = "Logged in users email"
    }

* `token` parameter is explained in the **Authentication** part.
* `email` parameter is the e-mail address of the logged in user

## Authentication

This module uses the tokens of the system for authentication. To use this endpoint, firstly register to the system with `../api/register/` endpoint and get a token using `../api/login/` endpoint. And give this token as an input to the request as:

    { 
        token = "Token given by the system when you log in" 
    }

## Sample Output

### Request 

> POST http://127.0.0.1:8000/api/deleteuser/ -- with valid token and email

### Output

    "Not interested in research anymore? Maybe a book can help you refresh yourself.Here is a bestseller book recommendation by New York TimesWALK THE WIRE by David BaldacciThe sixth book in the Memory Man series. Decker and Jamison investigate a murder in a North Dakota town in a fracking boom."

## Error Codes

* Token Error:

> POST http://127.0.0.1:8000/api/deleteuser/ -- without token but with valid email

    "No token found."

* Mail Error:

> POST http://127.0.0.1:8000/api/deleteuser/ -- without email but with valid token

    "No e-mail address found."

* Input Error:

> POST http://127.0.0.1:8000/api/deleteuser/ -- with email and token but they do not belong to a spesific user

    "No user found."

* Input Error:

> POST http://127.0.0.1:8000/api/deleteuser/ -- with email and token but something goes wrong

    "Cannot retrieve your token or your mail at the moment, sorry."


    




# 4.Update User Endpoint Documentation

This module allows registered users to update their "name", "surname", "about me", "job name", "forget password answer" information. User needs to enter the correct email address and password to update this information. All information changes are made by filling a single page. Fields left blank are preserved as before.

## Input Format

This endpoint has no GET request. So if you send a post request it will give an error. The output is the following:

    {
        "detail": "Method \"GET\" not allowed."
    }

POST request parameters:

    {   
        token = "Token given by the system when user logged in",
        name = "Logged in user's name",
        surname = "Logged in user's surname",
        password1 = "Logged in user's password",
        e_mail = "Logged in user's email",
        about_me = "Logged in user's about-me part",
        job_name = "Logged in user's jobs",
        forget_pw_ans = "Logged in user's forgot password answer",
        field_of_study = "Logged in user's field of study"
    }

* `token` parameter is explained in the **Authentication** part.
* `name` parameter is a string. It is the saved name given by the registered user during registration.
* `surname` parameter is a string. It is the saved surname given by the registered user during registration.
* `password1` parameter is a string. It is the saved password given by the registered user during registration.
* `e_mail` parameter is a string. It is the saved e-mail given by the registered user during registration.
* `about_me` parameter is a string. It is the saved about-me part given by the registered user during registration.
* `job_name` parameter is a string. It is the saved job name given by the registered user during registration.
* `forgot_pw_ans` parameter is a string. It is the saved forget password answer given by the registered user during registration.
* `field_of_study` parameter is a string. It is the saved field_of_study given by the registered user during registration.

## Authentication

This module uses the tokens of the system for authentication. To use this endpoint, firstly register to the system with `../api/updateUser/` endpoint and get a token using `../api/login/` endpoint. And give this token as an input to the request as:

    { 
        token = "Token given by the system when user logged in" 
    }

## Sample Output

### Request 

> POST http://127.0.0.1:8000/api/updateUser/ -- with valid password and email.

### Output

    "User information is updated!" Status Code: 201

## Error Codes

* Mail & Password Error:

> POST http://127.0.0.1:8000/api/updateUser/ -- with wrong password or email.

    "Not valid input. Check password or give valid inputs to the fields!", Code Status: 403

* Password & Mail Mismatch Error:

> POST http://127.0.0.1:8000/api/updateUser/ -- with a password that does not match the email given.

    "Not valid input. Check password or give valid inputs to the fields!", Code Status: 403


* 3rd Party API Error:

> POST http://127.0.0.1:8000/api/updateUser/ with valid input or not valid input

    "Error! Information could not be changed." Code Status: 403


    



# 5. Recommended News Api Documentation

This API provides user specific news functionality. It checks user's field of study then provide recommended articles for user

## Input Format

This endpoint has GET request.

    {
        token = "Token given by the system when you log in",
    }

This endpoint has no POST request. So if you send a get request it will give an error. The output is the following:

    {
        "content": "Bad Request"
    }


* `token` parameter is explained in the **Authentication** part.

## Authentication

This module uses the tokens of the system for authentication. To use this endpoint, firstly register to the system with `../api/register/` endpoint and get a token using `../api/login/` endpoint. And give this token as an input to the request as:

    {
        token = "Token given by the system when you log in"
    }

## Sample Output

### Request

> GET http://127.0.0.1:8000/api/news/ -- with valid token

### Example Output

    {'url': ['http://www.bbc.co.uk/news/uk-politics-52800595', 'http://www.bbc.co.uk/news/business-52800611', 'http://www.bbc.co.uk/news/world-latin-america-52796519', 'http://www.bbc.co.uk/news/world-europe-52796699', 'http://www.bbc.co.uk/news/entertainment-arts-52796964', 'http://www.bbc.co.uk/news/business-52795376', 'http://www.bbc.co.uk/news/world-us-canada-52795447', 'http://www.bbc.co.uk/news/world-asia-52794434', 'http://www.bbc.co.uk/news/live/world-52793960', 'http://www.bbc.co.uk/news/world-middle-east-52790864'], 'imageUrl': ['https://ichef.bbci.co.uk/images/ic/1024x576/p08f4d8g.jpg', 'https://ichef.bbci.co.uk/news/1024/branded_news/A40B/production/_112459914_reuters.jpg', 'https://ichef.bbci.co.uk/news/1024/branded_news/2A15/production/_112437701_gettyimages-980341738.jpg', 'https://ichef.bbci.co.uk/news/1024/branded_news/14F7B/production/_112438858_mediaitem112438857.jpg', 'https://ichef.bbci.co.uk/news/1024/branded_news/77DB/production/_112438603_pa-28516614.jpg', 'https://ichef.bbci.co.uk/news/1024/branded_news/05F6/production/_101162510_gettyimages-109422673.jpg', 'https://ichef.bbci.co.uk/news/1024/branded_news/E8B5/production/_112437595_b6cc5fa4-328c-4b0a-b4c3-456f42316541.jpg', 'https://ichef.bbci.co.uk/news/1024/branded_news/10723/production/_112436376_p08f3b9b.jpg', 'https://m.files.bbci.co.uk/modules/bbc-morph-news-waf-page-meta/4.1.2/bbc_news_logo.png', 'https://ichef.bbci.co.uk/news/1024/branded_news/95AD/production/_111171383_hi054737257.jpg'], 'title': ["'I don't regret what I did,' says Cummings", 'Spain to stop quarantining arrivals from 1 July', 'Where a convicted murderer could win re-election', 'Fancy a pint? Five ways Europe is easing lockdown', "Brian May 'could have died' after heart attack", "Volkswagen loses landmark German 'dieselgate' case", 'Americans flock to beaches on Memorial Day weekend', 'NZ earthquake hits during PM Ardern TV interview', 'Coronavirus updates: US bars travellers from Brazil as outbreak worsens', "Top exiled Saudi officer 'targeted though family'"], 'token': '5b764b7b3b9d36005f2a9e1d88ffa24ee5ffa56dd3661fb20e6c3e5f277c8999'}

## Error Codes

* Token Error:

> GET http://127.0.0.1:8000/api/news/ -- with no valid token

    "Unauthorized"

> POST http://127.0.0.1:8000/api/news/ --with no valid request

    "Bad Request"



# 6.Joke of the Day Endpoint Documentation

This module returns the joke of the day from an external api. It takes token and check if the token is valid or not then if valid it returns the joke.

## Input Format

This endpoint has no POST request. So if you send a post request it will give an error. The output is the following:

    {
        "detail": "Method \"POST\" not allowed."
    }

GET request parameters:

    {   
        token = "Token of user",
    
    }

* `token` parameter is explained in the **Authentication** part.


## Authentication

This module uses the tokens of the system for authentication. To use this endpoint, firstly register to the system with `../api/register/` endpoint and get a token using `../api/login/` endpoint. And give this token as an input to the request as:

    { 
        token = "Token of user" 
    }

## Sample Output

### Request 

> GET http://127.0.0.1:8000/api/joke/?token=824514797890967718

### Output

    {
        "status": "success",
        "token": "824514797890967718",
        "title": "Courtship Signals",
        "joke": "Q. Why shouldn't you marry a tennis player?\r\nA. Because Love means nothing to them.",
       
    }
        

## Error Codes

* Token Error:

> GET http://127.0.0.1:8000/api/joke/

    {
        "status": "invalid_token",
       
    }


# 7.Forgot Password Endpoint Documentation

This module for used to change password if you forget your password. You have to write your e-mail and answer correctly the secret question query. After that you can create your new password.

## Input Format

This endpoint has no GET request. So if you send a post request it will give an error. The output is the following:

    {
        "detail": "Method \"GET\" not allowed."
    }
 

POST request parameters:

    {   
        e_mail = "E-mail parameter given by user",
        forgot_password_ans = "forget password answer parameter given by user",
        
    }

* `e_mail` parameter is the e-mail address of the user

* `forgot_password_ans` is the secret question query answer(Who is your favourite teacher?)


## Sample Output

### Request 

> POST http://127.0.0.1:8000/api/forgotpassword/ -- with valid e-mail

### Output

    [
        {"Your password is changed"}
        {"Passwords did not match"}
        {"Your answer for your registered secret question is false"}
        {"You must give appropriate input format"}

    ]

## Error Codes

* Mail Error:

> POST http://127.0.0.1:8000/api/forgotpassword/ -- with invalid e-mail

    "404 pages not found"

* Secret Question Answer Error:

> POST http://127.0.0.1:8000/api/forgotpassword/ -- with valid e-mail and wrong secret question answer

    "Your answer for your registered secret question is false"

* Incompatible Password Error:

> POST http://127.0.0.1:8000/api/forgotpassword/ -- with valid e-mail and secret question answer but ıncompatible password

    "Passwords did not match"

# 8.Translation Endpoint Documentation

This module translates about me part of a registered user according to the given token. The translation is from English to Yoda.

## Input Format

This endpoint has no POST request. So if you send a post request it will give an error. The output is the following:

    {
        "detail": "Method \"POST\" not allowed."
    }

GET request parameters:

    {   
        token = "Your token will be in URL",
    }

* `token` parameter is explained in the **Authentication** part.

## Authentication

This module uses the tokens of the system for authentication. To use this endpoint, firstly register to the system with `../api/register/` endpoint and get a token using `../api/login/` endpoint. And give this token as an input to the request as:

    { 
        token = "Your token will be in the URL" 
    }

## Sample Output

### Request 

> GET http://127.0.0.1:8000/api/translation/b2a52d9e497071389f750333b4376e920d6e6c80b6fa84a43c4684ee614f5ee9

### Output

    [
      {
    "translated": "A junior student at bogazici university and my major degree is in computer engineering,  I am,Also I study a minor degree in electrics and electronics engineering.Finally,I like everything about basketball."
      }
    ]

## Error Codes

* Invalid Token Error: When one input invalid token

> GET http://127.0.0.1:8000/api/translation/invalidtoken

    "error": "Unauthorized access" Status Code: 404

* Key Error: When one requests more than 5 in an hour.

> GET http://127.0.0.1:8000/api/translation/b2a52d9e497071389f750333b4376e920d6e6c80b6fa84a43c4684ee614f5ee9

    {
    "error": {
        "code": 429,
        "message": "Too Many Requests: Rate limit of 5 requests per hour exceeded. Please wait for 59 minutes and 56 seconds."
             }
    }




# 9.Log In Endpoint Documentation

This module allows a registered user to log in to the platform by returning the user a unique token.

## Input Format

This endpoint has no POST request. So if you send a post request it will give an error. The output is the following:

    {
        "detail": "Method \"POST\" not allowed."
    }

GET request parameters:

    {   
        e_mail = "Your email will be here.",
        password = "Your password will be here, hashed for protection."
    }

* `e_mail` parameter is the e-mail address user provides while registering to the system via `../api/register/` endpoint and then uses everytime they want to login to the platform. It must be a string in a e-mail address form, as indicated in the implementation.
* `password` parameter is the password users set while registering to the to the system via `../api/register/` endpoint and can be changed via `../api/forgot_password/`. It is hashed for extra protection.


## Sample Output

### Request 

> GET http://127.0.0.1:8000/api/login/?e_mail=dummytest@hotmail.com&password=677g43e38y9608hs327604gp49j69

### Output

    "Welcome back!"

## Error Codes

* Wrong Password:

> GET http://127.0.0.1:8000/api/login/?e_mail=dummytest@hotmail.com&password=38glm2t1r22sue3oytkt7p8hti38m

    "Invalid e-mail or password!"

* Incorrect Email Format:

> GET http://127.0.0.1:8000/api/login/?e_mail=dummytesthotmail.com&password=677g43e38y9608hs327604gp49j69

    "Please use a valid e-mail address."

* Empty Input:

> GET http://127.0.0.1:8000/api/login/?e_mail=dummytest@hotmail.com&password=

    "Please fill all the required fields."


# 10. **Logout Endpoint Documentation**
----
  When a user sends a request to this endpoint, the user gets logged out from the app.

* **URL**

  /api/logout/?token=[user_session_token]

* **Method:**

  `GET` 
  
*  **URL Params**

   **Required:**
 
   `token=[string]`

* **Success Response:**

  * **Code:** 200, **Content:** `"You have succesfully logged out. Goodbye, see you soon!"`
 
* **Error Response:**

  * **Code:** 401 Unauthorized, **Content:** `"401 Unauthorized"`

  OR

  * **Code:** 400 Bad Request, **Content:** `"400 Bad Request"`

* **Sample Call:**

  curl http://127.0.0.1:8000/api/logout/?token=7dec2beca7530a60223035e5928bc00e68aad71b2288c9afb32784a70ce4e518










