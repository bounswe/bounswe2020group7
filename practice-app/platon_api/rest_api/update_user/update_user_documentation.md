Help on module update_user:

NAME
    update_user

DESCRIPTION

    Created on MAY 27, 2020
    This script controls the user information update functionality api of PLATON_API, using django&mysql backend.
    Endpoint description:
        http://localhost:8000/api/updateUser/

        'GET':
            Produces error
        'POST':
            Gets dictionary from body.
            JSON Format : { 'token': "",                string, token parameter given by the system when user logged in
                            'name': "",                 string, Name parameter given by user
                            'surname': "",              string, Surname parameter given by user
                            'password1': "",            string, Password1 given by user
                            'e_mail':"",                string, Email parameter given by user
                            'about_me':"",              string, About me parameter given by user
                            'job_name':"",              int, job id parameter chosen by user
                            'forget_password_ans':"",   string, forget password answer parameter given by user
                            'field_of_study':"" }       string, field of study parameter given by user
    
    @author: Meltem Arslan
    @company: Group7

FUNCTIONS

    getJobName(name)
           where name is the job name taken from user while registration
            
        Returns uuid of normalized job, or empty string
            
        This function takes a string and using an external api it normalizes and returns the uuid of that job.
    
    updateUser(request)
           where 'request': HTTP request that is from the view class
        Accepts only "POST" requests and returns a response with status.
        This function updates the information of logged in user.
    
    isValid(name, surname, password1, email, about_me, job_name, forget_pw_ans, field_of_study, user)
            where 'name': string, Name parameter given by user
            where 'surname': string, Surname parameter given by user
            where 'password1': string, Password1 given by user
            where 'email': string, Email parameter given by user
            where 'about_me': string, About me parameter given by user
            where 'job_id': string, job id parameter chosen by user
            where 'forget_pw_ans': string, forget password answer parameter given by user
            where 'field_of_study': string, field of study parameter given by user
            where 'user': object, user object in database which's token was matched
          
            returns True if given values appropriate to insert in database and the values correctly matches with a user in the database, else False
        
        This function takes input parameters and checks them if they are valid and return the boolean result.
    


DATA

    JOB_LIST_API_URL = 'http://api.dataatwork.org/v1/jobs/normalize?job_ti...
    USER_TABLENAME = 'rest_api_registereduser'
    WEBSITE_URL = 'http://localhost:8000'
    connection = <django.db.DefaultConnectionProxy object>

FILE

    C:\Users\Meltem Arslan\django\mysite2\bounswe2020group7\practice-app\platon_api\rest_api\update_user\update_user.py


