from rest_framework.test import APIClient, APITestCase
from django.urls import reverse
from rest_framework import status
from rest_framework.authtoken.models import Token

from rest_api.serializers import RegisteredUserSerializer
from rest_api.models import RegisteredUser



class TestRegisterApi(APITestCase):
    # Testing for creation of user with valid input
    def test_validRegistration(self):
        data = {"name":"testcase", "surname":"testcase_sur", "e_mail":"testcase@rest.api",
                "password1":"testcaseValid", "password2":"testcaseValid", "about_me":"right now testing vie unittest",
                "forget_password_ans":"testingisgood", "job_name":"tester", "field_of_study":"test"}
        response = self.client.post("/api/register/", data)
        self.assertEqual(response.status_code, status.HTTP_201_CREATED)
    
    # Testing for creation of user with valid input, and one more item that is irrelevant
    def test_validRegistrationExtraItem(self):
        data = {"test":"helo", "name":"testcase", "surname":"testcase_sur", "e_mail":"testcase@rest.api",
                "password1":"testcaseValid", "password2":"testcaseValid", "about_me":"right now testing vie unittest",
                "forget_password_ans":"testingisgood", "job_name":"tester", "field_of_study":"test"}
        response = self.client.post("/api/register/", data)
        self.assertEqual(response.status_code, status.HTTP_201_CREATED)
    
    # Testing for creation of user with valid input, except that passwords doesnot match  
    def test_unmatchingPassword(self):
        data = {"name":"testcase", "surname":"testcase_sur", "e_mail":"testcase@rest.api",
                "password1":"testcaseValid1", "password2":"testcaseValid2", "about_me":"right now testing vie unittest",
                "forget_password_ans":"testingisgood", "job_name":"tester", "field_of_study":"test"}
        response = self.client.post("/api/register/", data)
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
    
    # Testing for creation of user with valid input, except that email is wrong in terms of regex   
    def test_wrongMail(self):
        data = {"name":"testcase", "surname":"testcase_sur", "e_mail":"testcaserestapi",
                "password1":"testcaseValid", "password2":"testcaseValid", "about_me":"right now testing vie unittest",
                "forget_password_ans":"testingisgood", "job_name":"tester", "field_of_study":"test"}
        response = self.client.post("/api/register/", data)
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
    
    # Testing for creation of user with empty input  
    def test_empty(self):
        data = {"name":"", "surname":"", "e_mail":"",
                "password1":"", "password2":"", "about_me":"",
                "forget_password_ans":"", "job_name":"", "field_of_study":""}
        response = self.client.post("/api/register/", data)
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        
    # Testing for creation of user with valid input except that one field is missing   
    def test_deficientPost(self):
        data = {"surname":"testcase_sur", "e_mail":"testcase@rest.api",
                "password1":"testcaseValid", "password2":"testcaseValid", "about_me":"right now testing vie unittest",
                "forget_password_ans":"testingisgood", "job_name":"tester", "field_of_study":"test"}
        response = self.client.post("/api/register/", data)
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        
     # Testing for creation of user with valid input except that one field is wrongly named 
    def test_wrongPostItemName(self):
        data = {"nema":"testcase", "surname":"testcase_sur", "e_mail":"testcase@rest.api",
                "password1":"testcaseValid1", "password2":"testcaseValid2", "about_me":"right now testing vie unittest",
                "forget_password_ans":"testingisgood", "job_name":"tester", "field_of_study":"test"}
        response = self.client.post("/api/register/", data)
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
       
       # testing for get request
    def test_getRequest(self):
        response = self.client.get("/api/register/")
        self.assertEqual(response.status_code, status.HTTP_200_OK)
