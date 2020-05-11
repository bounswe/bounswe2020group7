import json
from django.db import connection

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
    
    mydb: mysql_connection
        mysql connection to the mysql server of our api

    Methods
    -------
    get_parameter_list(request=None)
        This function is used to split the arguments of the endpoint in the correct order.

    verify_token(token=None)
        This function verifies the token if there exits.
    
    search(request=None)
        This is the function that searchs the user in our database
    """

    """ TODO: Update the hardcoded list when it is finished"""

    job_list = ["engineer","teacher","doctor","a","b","c","d","f","e","g","c","h","j","k"]
    
    exact_match_score = 1000000

    num_of_semantically_related = 3

    @staticmethod
    def semantic_related_list(search_tokens,max_num_of_related):
        result_list = [(token,searchEngine.exact_match_score) for token in search_tokens]
        url = "https://api.datamuse.com/words"
        try:
            for token in search_tokens:
                querystring = {"ml":token}
                response = requests.request("GET", url, params=querystring)
                resp = json.loads(response.text)
                resp = resp[:max_num_of_related]
                result_list += [(related["word"],related["score"]) for related in resp]
            return result_list
        except:
            return []


    @staticmethod
    def get_parameter_list(request=None):
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
                # Take the index of the job
                job_id = searchEngine.job_list.index(filter["job"].lower())
                # Change job with job id
                del filter["job"]
                filter["job_id"] = job_id
            # Change field of study to lower case
            if "field_of_study" in filter:
                filter["field_of_study"] = filter["field_of_study"].lower()
        else:
            filter = {}
        if request.GET.get("search_string"):
            # Take the search string from URL
            search_string = request.GET["search_string"].lower()
        else:
            search_string = ""
        # Return parameters as a dictionary
        return {"filter":filter,"search_string":search_string,"token":token}
    
    @staticmethod
    def verify_token(token=None):
        # If there is no token we cannot validate
        if token is None:
            return False
        # SQL Query to verify token from database
        sql = "SELECT token FROM users WHERE token='{}'".format(token)
        # Create a MySQL cursor
        cursor = connection.cursor()
        # Execute SQL query
        cursor.execute(sql)
        result = cursor.fetchall()
        # Validate the token if there is
        return len(result)!=0

    @staticmethod
    def get_stopwords():
        # GET request for the list of stopwords in English
        url = "https://stopwords.p.rapidapi.com/stopwords"
        querystring = {"langs":"en","details":"false"}
        headers = {
            'x-rapidapi-host': "stopwords.p.rapidapi.com",
            'x-rapidapi-key': "a5cb4aeb98msh29ffc5d7378f3d2p136d6cjsn7e882dc52f2a"
        }
        try: 
            response = requests.request("GET", url, headers=headers, params=querystring)
            resp = json.loads(response.text)
            # Return the set of stopwords and punctuation
            return set([res["word"] for res in resp] + list(string.punctuation))
        except:
            # If there is an error in the request return a None object
            return None

    @staticmethod
    def search(request):
        # Generate parameters from URL
        param_dict = searchEngine.get_parameter_list(request)
        # Control the token of the GET request
        if not searchEngine.verify_token(param_dict["token"]):
            return "You have to give your token"
        # TODO: Ask what will be returned if search string is empty to the group
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
        base_sql = "SELECT id,name,surname,`e-mail`,about_me,job_id,field_of_study FROM users WHERE "
        filter_list = []
        if "job_id" in param_dict["filter"]:
            filter_list.append("job_id=" + str(param_dict["filter"]["job_id"]))
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
            sql = base_sql + '(LOWER(name) REGEXP ".*{0}.*" OR LOWER(surname) REGEXP ".*{0}.*" OR LOWER(`e-mail`) REGEXP ".*{0}.*" OR LOWER(about_me) REGEXP ".*{0}.*")'.format(search_token)
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
                    search_result.append({"name":result[1],"surname":result[2],"e-mail":result[3],"about_me":result[4],"job":searchEngine.job_list[result[5]],"field_of_study":result[6]})
                    break
            continue
        # Return the search result list
        return search_result
             

         