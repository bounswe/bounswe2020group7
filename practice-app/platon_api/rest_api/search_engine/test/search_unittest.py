import unittest

# TODO: Add new tests when the functionality of the search function is determined completely

class TestSearch(unittest.TestCase):

    # Test if the all of the results returned from the search function has the following fields:
    # Name, Surname, About_Me, Job, Date_of_Birth, Email  
    def test_result_format(self):
        # TODO: Make it search string that returns a list of users
        search_string = "Umut"
        # TODO: Call Function Here With a search string 
        search_result = {'Name':"Umut"}
        expected_fields = ['Name', 'Surname', 'About_Me', 'Job', 'Date_of_Birth', 'Email']
        # Check fields for all users in the search list
        for user in search_result:
            # Take the field list
            fields = list(user.keys())
            # Control if it has the expected fields
            self.assertEqual(expected_fields,fields,"Result format is different from expected")


    # Test If search function is called with a name in the Database,
    # function returns the user with this name in the first place of the list.
    def test_name_search(self):
        # TODO: Make it a name in the database
        search_string = "Umut"
        # TODO: Call Function Here With a Name in the Database 
        search_result = [{'Name':'Umut'}]
        # Check the name field of the result
        self.assertIn(search_string,search_result[0]['Name'],"It doesn't return the user with the name {} at the first place of the list".format(search_string))
    
    # Test If search function is called with an empty string,
    # function returns an empty list.
    def test_empty_search(self):
        # TODO: Call Function Here With an Empty String 
        search_result = []
        # Expected output is empty list
        expected_output = []
        # Check for Empty List
        self.assertEqual(search_result,expected_output,"Output is not empty list for empty string")
    
    # Test If a job filter is given to function as a filter
    # all result values will have this filter as a job
    def test_job_filter(self):
        job_filter = "Engineer"
        # TODO: make it a search string which will return an engineer user
        search_string = "Umut"
        # TODO: Call Function Here With search string and filter
        search_result = []
        for user in search_result:
            self.assertIn(job_filter,user['Job'],"Job filter doesn't run correctly")


if __name__ == '__main__':
    unittest.main()