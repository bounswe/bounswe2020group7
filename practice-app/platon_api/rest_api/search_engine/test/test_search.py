from django.test import TestCase,TransactionTestCase,Client
from django.db import connection,transaction
from platon_api.settings import USER_TABLENAME 
from rest_api.search_engine.search_engine import searchEngine
import json
import hashlib

# Create your tests here.
class SearchTest(TransactionTestCase):

    """
    In this class, the unit tests of the search engine is implemented.

    ...

    Attributes
    ----------
    client : Client Object
        a client that will make the requests in the test cases

    user_list: list
        This is a list of tuples. The tuples in the list includes the users that I added to the test database for test cases
    
    valid_token: str
        A token that exists in the test database
    """

    client = Client()

    user_list = [
            ("Umut","Ozdemir",hashlib.sha256("123456".encode("utf-8")).hexdigest(),"umut@gmail.com",hashlib.sha256("umut@gmail.com".encode("utf-8")).hexdigest(),"I am a junior student at Bogazici University and my major degree is in Computer Engineering, also I study a minor degree in Electrics and Electronics Engineering. Finally, I like everything about basketball.","0aa64c1575f51c91930095311b536477","Computer Engineering","Umut"),
            ("Öykü","Yilmaz",hashlib.sha256("1111111".encode("utf-8")).hexdigest(),"oyku@gmail.com",hashlib.sha256("oyku@gmail.com".encode("utf-8")).hexdigest(),"A CmpE student in Bogazici University. Loves to learn new things, swimming, and also doing nothing.","0e78a3833926aa1f49dd9f4bb86b7386","Computer Engineering","Oyku"),
            ("Hasan Ramazan","Yurt",hashlib.sha256("hfaonfsln".encode("utf-8")).hexdigest(),"ramazan@gmail.com",hashlib.sha256("ramazan@gmail.com".encode("utf-8")).hexdigest(),"I am a junior Computer Engineering student at Bogazici University. I can describe myself as creative, sociable and an engineer ready to learn. Also, I am a passionate esports follower.","0e78a3833926aa1f49dd9f4bb86b7386","Computer Engineering","Yurt"),
            ("Ahmet","Dadak",hashlib.sha256("bsfuaıbsbf".encode("utf-8")).hexdigest(),"ahmet@gmail.com",hashlib.sha256("ahmet@gmail.com".encode("utf-8")).hexdigest(),"Junior computer engineering student at Bogazici University. Writes code, scripts, and lyrics. Interested in Web Applications and ML. Interested in sports, literature, design, and music.","0e78a3833926aa1f49dd9f4bb86b7386","Computer Engineering","Ahmet")
    ]
    
    valid_token = "03684b7c7efc7ba7ffe53f32f3c9346d8383fcc13eddd1272705c746ba986f2f"

    def setUp(self):
        """
            This function creates the initial test database with users in the user_list above
        """
        with connection.cursor() as cursor:
            # Add some test users
            sql = 'INSERT INTO `'+ USER_TABLENAME +'` (`name`, `surname`, `password_hashed`, `e_mail`, `token`, `about_me`, `job_uuid`, `field_of_study`, `forget_password_ans`) VALUES'
            for test_user in SearchTest.user_list:
                cursor.execute(sql + "('"+"','".join([str(x) for x in test_user])+"');")

    def test_no_input(self):
        """
            Test for if there is no input given to the request. In must give Wrong input. Please give an appropriate input!! error
        """
        resp = SearchTest.client.get('/api/search/')
        self.assertEqual(json.loads(resp.content),"You give your input in wrong format. Please check the API documentation for the appropriate input format!!","No Input Test Error")

    def test_no_search_string(self):
        """
            Test for if there is no search string given to the request   
        """
        resp = SearchTest.client.get('/api/search/',{'token':SearchTest.valid_token})
        self.assertEqual(json.loads(resp.content),"You give your input in wrong format. Please check the API documentation for the appropriate input format!!","No Search String Test Error")

    def test_wrong_search_criteria(self):
        """
            Test the input control of the sorting criteria parameter
        """
        # Name Decreasing sorting criteria is selected
        sorting_criteria = "surname_decrease"
        # Search string that returns all of the users
        search_string = "''"
        # Search For all users
        resp = SearchTest.client.get('/api/search/',{"token":SearchTest.valid_token,"search_string":search_string,"sorting_criteria":sorting_criteria})
        search_result = json.loads(resp.content)
        self.assertEqual(json.loads(resp.content),"You give your input in wrong format. Please check the API documentation for the appropriate input format!!","Sorting Critera Input Control Doesn't Work")
    
    def test_wrong_token(self):
        """
            Test for if there is wrong token given to the request. It must give You have to give your token error
        """
        wrong_token = "1"*64
        expected_result = "You have to give your token"
        resp = SearchTest.client.get('/api/search/',{"token":wrong_token,"search_string":"Umut"})
        self.assertEqual(json.loads(resp.content),expected_result,"Token Control is not True")
    
    def test_result_format(self):
        """
            Test if the all of the results returned from the search function has the following fields:
            Name, Surname, About_Me, Job, Field_of_study, Email 
        """
        # Search String that returns all users in the system
        search_string = "''"
        # Search For list of all users
        resp = SearchTest.client.get('/api/search/',{"token":SearchTest.valid_token,"search_string":search_string})
        search_result = json.loads(resp.content)
        expected_fields = ['name', 'surname','e_mail', 'about_me', 'job', 'field_of_study']
        # Check fields for all users in the search list
        for user in search_result:
            # Take the field list
            fields = list(user.keys())
            # Control if it has the expected fields
            self.assertEqual(expected_fields,fields,"Result format is different from expected")
    

    def test_name_search(self):
        """
            Test If search function is called with a name in the Database,
            function returns the user with this name.
        """
        # A name in the database
        search_string = "Umut"
        # Search For Umut
        resp = SearchTest.client.get('/api/search/',{"token":SearchTest.valid_token,"search_string":search_string})
        search_result = json.loads(resp.content)
        # Check the name field of the result
        self.assertEqual(search_string,search_result[0]['name'],"It doesn't return the user with the name {}".format(search_string))


    def test_email_search(self):
        """
            Test If search function is called with a e_mail in the Database,
            function returns the user with this e_mail.
        """
        # A name in the database
        search_string = "umut@gmail.com"
        # Search For Umut
        resp = SearchTest.client.get('/api/search/',{"token":SearchTest.valid_token,"search_string":search_string})
        search_result = json.loads(resp.content)
        # Check the e_mail field of the result
        self.assertEqual(search_string,search_result[0]['e_mail'],"It doesn't return the user with the email {}".format(search_string))

    def test_semantic_search(self):
        """
            This test case controls if the semantic search functionality works or not
            Search string is football and the word basketball is in the about me of the user Umut. It must give Umut as result
            Because basketball and football are semanticly related
        """
        # A name in the database
        search_string = "football"
        # Search For Umut
        resp = SearchTest.client.get('/api/search/',{"token":SearchTest.valid_token,"search_string":search_string})
        search_result = json.loads(resp.content)
        # Umut must be in the results because basketball and football are semanticly related
        self.assertEqual("Umut",search_result[0]['name'],"Semantic Search functionality doesn't work.")
    

    def test_job_filter(self):
        """
            Test If a job filter is given to function as a filter
            all result values will have this filter as a job
        """
        # A Job in the database
        job_filter = {"job" : "EnGiNeEr"}
        # Search string that returns all of the users
        search_string = "''"
        # Search For all users with given filter
        resp = SearchTest.client.get('/api/search/',{"token":SearchTest.valid_token,"search_string":search_string,"filter":json.dumps(job_filter)})
        search_result = json.loads(resp.content)
        for user in search_result:

            self.assertIn(job_filter.title(),user['job'],"Job filter doesn't run correctly")


    def test_filed_of_study_filter(self):
        """
            Test If a field of study filter is given to function as a filter
            all result values will have this filter as a field of study
        """
        # A Job in the database
        job_filter = {"field_of_study" : "computer ENGINEERING"}
        # Search string that returns all of the users
        search_string = "''"
        # Search For all users with given filter
        resp = SearchTest.client.get('/api/search/',{"token":SearchTest.valid_token,"search_string":search_string,"filter":json.dumps(job_filter)})
        search_result = json.loads(resp.content)
        for user in search_result:
            self.assertIn(job_filter.title(),user['field_of_study'],"Field of study filter doesn't run correctly")

    def test_sorting_name(self):
        """
            Test if sorting functionality works correctly or not
        """
        # Name Decreasing sorting criteria is selected
        sorting_criteria = "name_decreasing"
        # Search string that returns all of the users
        search_string = "''"
        # Search For all users
        resp = SearchTest.client.get('/api/search/',{"token":SearchTest.valid_token,"search_string":search_string,"sorting_criteria":sorting_criteria})
        search_result = json.loads(resp.content)
        for i in range(len(search_result)-1):
            self.assertGreaterEqual(search_result[i]["name"],search_result[i+1]["name"],"{} sorting criteria doesn't work".format(sorting_criteria))
    
    def test_sorting_name2(self):
        """
            Test if sorting functionality works correctly or not
        """
        # Name Decreasing sorting criteria is selected
        sorting_criteria = "name_increasing"
        # Search string that returns all of the users
        search_string = "''"
        # Search For all users
        resp = SearchTest.client.get('/api/search/',{"token":SearchTest.valid_token,"search_string":search_string,"sorting_criteria":sorting_criteria})
        search_result = json.loads(resp.content)
        for i in range(len(search_result)-1):
            self.assertLessEqual(search_result[i]["name"],search_result[i+1]["name"],"{} sorting criteria doesn't work".format(sorting_criteria))

    def test_sorting_surname(self):
        """
            Test if sorting functionality works correctly or not
        """
        # Name Decreasing sorting criteria is selected
        sorting_criteria = "surname_increasing"
        # Search string that returns all of the users
        search_string = "''"
        # Search For all users
        resp = SearchTest.client.get('/api/search/',{"token":SearchTest.valid_token,"search_string":search_string,"sorting_criteria":sorting_criteria})
        search_result = json.loads(resp.content)
        for i in range(len(search_result)-1):
            self.assertLessEqual(search_result[i]["name"],search_result[i+1]["name"],"{} sorting criteria doesn't work".format(sorting_criteria))
    
    def test_sorting_surname2(self):
        """
            Test if sorting functionality works correctly or not
        """
        # Name Decreasing sorting criteria is selected
        sorting_criteria = "surname_decreasing"
        # Search string that returns all of the users
        search_string = "''"
        # Search For all users
        resp = SearchTest.client.get('/api/search/',{"token":SearchTest.valid_token,"search_string":search_string,"sorting_criteria":sorting_criteria})
        search_result = json.loads(resp.content)
        for i in range(len(search_result)-1):
            self.assertGreaterEqual(search_result[i]["name"],search_result[i+1]["name"],"{} sorting criteria doesn't work".format(sorting_criteria))
    
    def test_semantic_related_list(self):
        """
            Test the tokenization is true or not
        """
        search_tokens = ["basketball", "computer"]
        number_of_related = 2
        expected_output = [(token,1000000) for token in search_tokens] + [("basketball game",87833),("football",73595),("calculator",87500),("figurer",81849)]
        # Use the function
        result =  searchEngine.semantic_related_list(search_tokens,number_of_related)
        self.assertEqual(expected_output,result,"Semantic related list isn't generated correctly")
