import requests
import string
import json

class SearchEngine():

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

    sorting_criteria_list: list
        a list of sorting criteria that can be used in the search functionality
    """
    
    exact_match_score = 1000000

    @staticmethod
    def semantic_related_list(search_tokens,max_num_of_related=8):
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
            'x-rapidapi-key': "afa95044b8msha957cc9dbf304f4p1c6e46jsn49772da509a5"
        }
        try: 
            response = requests.request("GET", url, headers=headers, params=querystring)
            resp = json.loads(response.text)
            # If API gives different message tha expected return None to give an error message
            if not isinstance(resp,list):
                return None
            # Return the set of stopwords and punctuation
            return set([res["word"].lower() for res in resp])
        except:
            # If there is an error in the request return a None object
            return None
    
    @staticmethod
    def remove_punctuation(search_query):
        """
            Removes the punctuation of a given query
        """
        search_query = search_query.lower()
        for punctuation in string.punctuation:
           search_query = search_query.replace(punctuation," ",search_query)
        return search_query

    @staticmethod
    def sort_ids(related_id_score_list):
        return sorted(related_id_score_list, key=lambda tup: tup[1])