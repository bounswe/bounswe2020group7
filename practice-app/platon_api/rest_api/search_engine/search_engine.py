"""
Created on MAY 17, 2020
This script controls the search functionality api of PLATON_API, using django&mysql backend.
Endpoint description:
    http://localhost:8000/api/search/
    
    'POST':
        Produces error
    'GET':
        Gets dictionary from body.
        JSON Format : {   
                        token = "Your token will be here!!",
                        search_string = "The string that you want to search will be here",
                        filter = {"job" = "job_filter", "field_of_study" = "field_of_study_filter" },
                        sorting_criteria = "Sorting criteria will be here"
                        }
@author: Halil Umut Ozdemir
@company: Group7
"""

from django.db import connection
from platon_api.settings import JOB_LIST_API_URL, STOPWORDS_API_KEY, USER_TABLENAME

import json
import string
import re
import requests

class searchEngine():

    """
    In this class the Search Engine endpoint is implemented.

    ...

    Attributes
    ----------
    job_list : list
        a list of jobs that whose index is written in the database. 
    
    exact_match_score: int
        Semantic score of an exact match in search operation
    
    num_of_semantically_related: int
        Number of words that search engine uses in semantice search for each token

    sotring_criteria_list: list
        a list of sorting criteria that can be used in the search functionality
    """

    """ TODO: Update the hardcoded list for the future use of this module"""

    sotring_criteria_list = ["name_increasing", "name_decreasing", "surname_increasing", "surname_decreasing"]
    
    exact_match_score = 1000000

    num_of_semantically_related = 5

    @staticmethod
    def semantic_related_list(search_tokens,max_num_of_related):
        """
            where 'search_tokens': List of tokens that are given in a search string
            where 'max_num_of_related': Number of words that will be chosen for a token

            returns a list of tuples which contails (word,semantic relation point)

            This functions takes a list of tokens and creates the list of semanticly related words for the list of tokens

        """
        result_list = [(token,searchEngine.exact_match_score) for token in search_tokens]
        url = "https://api.datamuse.com/words"
        try:
            for token in search_tokens:
                querystring = {"ml":token}
                response = requests.request("GET", url, params=querystring)
                resp = json.loads(response.text)
                result_list += [(related["word"],related["score"]) for related in resp[:max_num_of_related]]
            return result_list
        except:
            return [(token,searchEngine.exact_match_score) for token in search_tokens]


    @staticmethod
    def get_parameter_list(request=None):
        """
            where 'request': HTTP request that comes from the view class

            returns a dictionary of the paramters if there is no problem about the paramters

            This function is used to split the arguments of the endpoint in the correct order.

        """
        # Take token from URL
        if request.GET.get("token"):
            token = request.GET["token"]
        else:
            token = None
        if request.GET.get("filter"):
            # Take Parameter from the URL in the JSON format
            filter_json = request.GET["filter"]
            # Change JSON to Python dictionary
            filter = json.loads(filter_json)
            if "job" in filter:
                try:
                    # Get job uuid from the Job list API
                    response = requests.request("GET",JOB_LIST_API_URL+filter["job"].lower())
                    job  = json.loads(response.text)
                    job_uuid = job[0]["uuid"]
                except:
                    return "There is a problem about an API. Please try again!!"
                # Change job with job uuid
                del filter["job"]
                filter["job_uuid"] = job_uuid
            # Change field of study to lower case
            if "field_of_study" in filter:
                filter["field_of_study"] = filter["field_of_study"].lower()
        else:
            filter = {}
        if request.GET.get("search_string"):
            # Take the search string from URL
            search_string = request.GET["search_string"].lower()
            # Make search string only a empty string to return all users
            if search_string == "":
                search_string = " "
        else:
            return "Wrong input. Please give an appropriate input!!"
        if request.GET.get("sorting_criteria",False):
            # Take sorting criteria from URL
            sotring_criteria = request.GET.get("sorting_criteria",False).replace('"',"")
            if sotring_criteria not in searchEngine.sotring_criteria_list:
                return "Wrong input. Please give an appropriate input!!"
        else:
            # Default sorting criteria
            sotring_criteria = ""
        # Return parameters as a dictionary
        return {"filter":filter,"search_string":search_string,"token":token,"sorting_criteria":sotring_criteria}
    
    @staticmethod
    def verify_token(token=None):
        """
            where 'token': string, 64 characther string that can be token

            returns True if token exists in the DB

            This function verifies the token if there exits.

        """
        # If there is no token we cannot validate
        if token is None:
            return False
        # SQL Query to verify token from database
        sql = "SELECT token FROM "+ USER_TABLENAME +" WHERE token='{}'".format(token)
        # Create a MySQL cursor
        cursor = connection.cursor()
        # Execute SQL query
        cursor.execute(sql)
        result = cursor.fetchall()
        # Validate the token if there is
        return len(result)!=0
    
    @staticmethod
    def sort_output(output_list,sorting_type):
        """
            where 'output_list': the list of the results that come without sorting
            where 'sorting_type': sorting type parameter

            returns sorted version of the search result

            This function sorts the output with a given sotring criteria

        """
        # This function sorts the output list according to the given sorting type
        if sorting_type == "name_increasing":
            return sorted(output_list, key=lambda x: x["name"],reverse = False)
        elif sorting_type == "name_decreasing":
            return sorted(output_list, key=lambda x: x["name"],reverse = True)
        elif sorting_type == "surname_increasing":
            return sorted(output_list, key=lambda x: x["surname"],reverse = False)
        elif sorting_type == "surname_decreasing":
            return sorted(output_list, key=lambda x: x["surname"],reverse = True)
        else:
            return output_list

    @staticmethod
    def get_stopwords():
        """
            returns list of stopwords + punctuation in English

            This function returns a list of stopwords for English

        """
        # GET request for the list of stopwords in English
        url = "https://stopwords.p.rapidapi.com/stopwords"
        querystring = {"langs":"en","details":"false"}
        headers = {
            'x-rapidapi-host': "stopwords.p.rapidapi.com",
            'x-rapidapi-key': STOPWORDS_API_KEY
        }
        try: 
            response = requests.request("GET", url, headers=headers, params=querystring)
            resp = json.loads(response.text)
            # If API gives different message tha expected return None to give an error message
            if not isinstance(resp,list):
                return None
            # Return the set of stopwords and punctuation
            return set([res["word"] for res in resp] + list(string.punctuation))
        except:
            # If there is an error in the request return a None object
            return None
    
    @staticmethod
    def get_job_name(job_uuid):
        """
            where 'job_uuid': job uuid of a job

            returns job name of given uuid

            This function converts a job uuid to its name
        """
        response = requests.request("GET","http://api.dataatwork.org/v1/jobs/"+job_uuid)
        job = json.loads(response.text)
        return job["title"].title()

    @staticmethod
    def search(request):
        """
            where 'request' : HTTP Request that comes from the view class

            returns a list of search results.

            This is the function that searchs the user in our database

        """
        # Generate parameters from URL
        param_dict = searchEngine.get_parameter_list(request)
        # Control input if there is an error return error message
        if not isinstance(param_dict,dict): 
            return "Wrong input. Please give an appropriate input!!"
        # Control the token of the GET request
        if not searchEngine.verify_token(param_dict["token"]):
            return "You have to give your token"
        # If there is no search string return empty list
        if param_dict["search_string"] == "":
            return []
        # Tokenize the search string without stopwords and punctuation
        stopwords = searchEngine.get_stopwords()
        # If there is a problem return an information about the problem
        if stopwords is None:
            return "There is a problem about an API. Please try again!!"
        # Tokenize the search string and remove stopwords from it
        search_tokens = set(re.findall(r"[\w']+|[.,!?;}{)(\"]", param_dict["search_string"].lower())).difference(stopwords)
        # Store teh scores of the results and result list
        result_score = {}
        result_list = []
        base_sql = "SELECT id,name,surname,`e_mail`,about_me,job_uuid,field_of_study FROM " + USER_TABLENAME + " WHERE "
        filter_list = []
        if "job_uuid" in param_dict["filter"]:
            filter_list.append("job_uuid='{}'".format(param_dict["filter"]["job_uuid"]))
        if "field_of_study" in param_dict["filter"]:
            filter_list.append('field_of_study="{}"'.format(param_dict["filter"]["field_of_study"]))
        # Add filters to the base SQL Query
        base_sql += " AND ".join(filter_list) + (" AND " if len(filter_list)>0 else "")
        # Create MySQL cursor
        cursor = connection.cursor()
        # If search token is empty then search for all strings
        if len(search_tokens) == 0:
            search_tokens.add("")
        for search_tuple in searchEngine.semantic_related_list(search_tokens,searchEngine.num_of_semantically_related):
            search_token = search_tuple[0]
            # Search for the name
            sql = base_sql + '(LOWER(name) REGEXP ".*{0}.*" OR LOWER(surname) REGEXP ".*{0}.*" OR LOWER(`e_mail`) REGEXP ".*{0}.*" OR LOWER(about_me) REGEXP ".*{0}.*")'.format(search_token)
            cursor.execute(sql)
            for result in cursor:
                # Increase the point of the result
                if result in result_list:
                    result_score[result[0]] += search_tuple[1]
                # Add to the dictionary if it is not found before
                else:
                    result_list.append(result)
                    result_score[result[0]] = search_tuple[1]
        # Sort result according to their total points
        sorted_index = sorted(result_score.items(), key=lambda x: x[1],reverse=True)
        search_result = []
        # Create the search list with the decreasing order according to total semantic points
        for id,score in sorted_index:
            for result in result_list:
                if result[0]==id:
                    search_result.append({"name":result[1],"surname":result[2],"e_mail":result[3],"about_me":result[4],"job":searchEngine.get_job_name(result[5]),"field_of_study":result[6].title()})
                    break
            continue
        # Return the search result list
        return searchEngine.sort_output(search_result,param_dict["sorting_criteria"])