Help on module search_engine:

NAME
    search_engine

DESCRIPTION

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

CLASSES

    builtins.object
        searchEngine

    class searchEngine(builtins.object)
     |  In this class the Search Engine endpoint is implemented.
     |
     |  ...
     |
     |  Attributes
     |  ----------
     |  job_list : list
     |      a list of jobs that whose index is written in the database.
     |
     |  exact_match_score: int
     |      Semantic score of an exact match in search operation
     |
     |  num_of_semantically_related: int
     |      Number of words that search engine uses in semantice search for each token
     |
     |  sotring_criteria_list: list
     |      a list of sorting criteria that can be used in the search functionality
     |
     |  Static methods defined here:
     |
     |  get_job_name(job_uuid)
     |      where 'job_uuid': job uuid of a job
     |
     |      returns job name of given uuid
     |
     |      This function converts a job uuid to its name
     |
     |  get_parameter_list(request=None)
     |      where 'request': HTTP request that comes from the view class
     |
     |      returns a dictionary of the paramters if there is no problem about the paramters
     |
     |      This function is used to split the arguments of the endpoint in the correct order.
     |
     |  get_stopwords()
     |      returns list of stopwords + punctuation in English
     |
     |      This function returns a list of stopwords for English
     |
     |  search(request)
     |      where 'request' : HTTP Request that comes from the view class
     |
     |      returns a list of search results.
     |
     |      This is the function that searchs the user in our database
     |
     |  semantic_related_list(search_tokens, max_num_of_related)
     |      where 'search_tokens': List of tokens that are given in a search string
     |      where 'max_num_of_related': Number of words that will be chosen for a token
     |
     |      returns a list of tuples which contails (word,semantic relation point)
     |
     |
     |  sort_output(output_list, sorting_type)
     |      where 'output_list': the list of the results that come without sorting
     |      where 'sorting_type': sorting type parameter
     |
     |      returns sorted version of the search result
     |
     |      This function sorts the output with a given sotring criteria
     |
     |  verify_token(token=None)
     |      where 'token': string, 64 characther string that can be token
     |
     |      returns True if token exists in the DB
     |
     |      This function verifies the token if there exits.
     |
     |  ----------------------------------------------------------------------
     |  Data descriptors defined here:
     |
     |  __dict__
     |      dictionary for instance variables (if defined)
     |
     |  __weakref__
     |      list of weak references to the object (if defined)
     |
     |  ----------------------------------------------------------------------
     |  Data and other attributes defined here:
     |
     |  exact_match_score = 1000000
     |
     |  num_of_semantically_related = 5
     |
     |  sotring_criteria_list = ['name_increasing', 'name_decreasing', 'surnam...

DATA

    JOB_LIST_API_URL = 'http://api.dataatwork.org/v1/jobs/normalize?job_ti...
    USER_TABLENAME = 'rest_api_registereduser'
    connection = <django.db.DefaultConnectionProxy object>

FILE

    c:\users\halil\desktop\ders\cmpe 352\repository\bounswe2020group7\practice-app\platon_api\rest_api\search_engine\search_engine.py