from flask import make_response,jsonify,request
from flask_restplus import Resource,Namespace, fields
from sqlalchemy import func

from app.search_engine.forms import UserSearchForm, user_search_parser
from app.search_engine.helpers import SearchEngine, SearchType
from app.profile_management.models import Jobs,Skills,UserSkills
from app.auth_system.models import User
from app.auth_system.helpers import decode_token

from app import api, db
import math

search_engine_ns = Namespace("Search Engine",
                            description="Search Engine Endpoints",
                            path = "/search_engine")

search_user_model = api.model('User Data', {
    "id": fields.Integer,
    "name": fields.String,
    "surname": fields.String,
    "profile_photo": fields.String,
    "e_mail": fields.String,
    "job_id": fields.Integer
})

search_user_list_model = api.model('User List(Search)', {
    'number_of_pages': fields.Integer,
    'result_list': fields.List(
        fields.Nested(search_user_model)
    )
})

@search_engine_ns.route("/user")
class UserSearchAPI(Resource):
    
    @api.doc(responses={401 : 'Account Problems', 400 : 'Input Format Error' ,500 : ' Database Connection Error'})
    @api.response(200, 'Valid Search Result', search_user_list_model)
    @api.expect(user_search_parser)
    def get(self):

        form = UserSearchForm(request.args)
        if form.validate():
            search_query = form.search_query.data.lower()
            # Remove the punctuation in the search query
            search_query = SearchEngine.remove_punctuation(search_query)
            # Tokenize the search query
            tokens = search_query.split()
            # Get stopwords form the API
            stopwords = SearchEngine.get_stopwords()
            # Remove stopwords from the list of tokens
            tokens = list(set(tokens) - set(stopwords))
            # Get the semantically related list for our tokens
            tokens = SearchEngine.semantic_related_list(tokens)
            result_list = []
            result_id_score = []
            # Search tokens in Jobs Table
            for token,score in tokens:
                try:
                    related_job = Jobs.query.filter(func.lower(Jobs.name) == token).first()
                except: 
                    return make_response(jsonify({"error": "Database Connection Problem."}), 500)
                if related_job is not None:
                    try:
                        owner_list = User.query.filter(User.job_id == related_job.id).all()
                    except: 
                        return make_response(jsonify({"error": "Database Connection Problem."}), 500)
                    for user in owner_list:
                        id_list = [i[0] for i in result_id_score]
                        if user.id not in id_list:
                            result_list.append({"id":user.id, "name": user.name, "surname": user.surname, 
                                            "profile_photo" : user.profile_photo, "is_private": user.is_private, "job_id" : user.job_id})
                            result_id_score.append((user.id,score))
                        else:
                            id_index = id_list.index(user.id)
                            result_id_score[id_index] =(user.id, result_id_score[id_index][1]+score) 
            # Search tokens in Skills Table
            for token,score in tokens:
                try:
                    related_skill = Skills.query.filter(func.lower(Skills.name) == token).first()
                except: 
                    return make_response(jsonify({"error": "Database Connection Problem."}), 500)
                if related_skill is not None:
                    try:
                        owner_id_list = UserSkills.query.filter(UserSkills.skill_id == related_skill.id).all()
                    except: 
                        return make_response(jsonify({"error": "Database Connection Problem."}), 500)
                    for owner_id in owner_id_list:
                        id_list = [i[0] for i in result_id_score]
                        if owner_id.user_id not in id_list:
                            # Take user information from the DB
                            try:
                                user = User.query.filter(User.id == owner_id.user_id).first()
                            except:
                                return make_response(jsonify({"error": "Database Connection Problem."}), 500)
                            result_list.append({"id":user.id, "name": user.name, "surname": user.surname, 
                                            "profile_photo" : user.profile_photo, "is_private": user.is_private, "job_id" : user.job_id})
                            result_id_score.append((user.id,score))
                        else:
                            id_index = id_list.index(owner_id.user_id)
                            result_id_score[id_index] =(user.id, result_id_score[id_index][1]+score) 
            # Search tokens for name, surname, institution or email match
            for token,score in tokens:
                try:
                    query = '(LOWER(name) REGEXP ".*{0}.*" OR LOWER(surname) REGEXP ".*{0}.*" \
                    OR LOWER(`e_mail`) REGEXP ".*{0}.*" OR LOWER(institution) REGEXP ".*{0}.*")' \
                    .format(token)
                    sql_statement = "SELECT id,name,surname,profile_photo,is_private,job_id FROM users WHERE {}".format(query)
                    result = db.engine.execute(sql_statement)
                    for user in result:
                        id_list = [i[0] for i in result_id_score]
                        if user[0] not in id_list:
                            result_list.append({"id":user[0], "name": user[1], "surname": user[2], 
                                            "profile_photo" : user[3], "is_private": user[4],"job_id" : user.job_id})
                            result_id_score.append((user.id,score))
                        else:
                            id_index = id_list.index(user[0])
                            result_id_score[id_index] =(user.id, result_id_score[id_index][1]+score)
                except:
                    return make_response(jsonify({"error": "Database Connection Problem."}), 500)
            # Sort result ids according to their scores
            sorted_id_list = SearchEngine.sort_ids(result_id_score)
            sorted_result_list = []
            for user_id,score in sorted_id_list:
                index = [user["id"] for user in result_list].index(user_id)
                sorted_result_list.append(result_list[index])
            # Apply given filters
            if form.job_filter.data is not None:
                for i,user in enumerate(sorted_result_list):
                    if user["job_id"] != form.job_filter.data:
                        sorted_result_list.pop(i)
            number_of_pages = 1
            # Apply Pagination
            if form.page.data is not None and form.per_page.data:
                per_page = form.per_page.data
                number_of_pages = math.ceil(len(sorted_id_list)/per_page)
                page = form.page.data if form.page.data < number_of_pages else number_of_pages-1
                sorted_result_list = sorted_result_list[page*per_page:(page+1)*per_page]
            # Add Search History Item
            try:
                auth_token = request.headers.get('auth_token')
                SearchEngine.add_search_history_item(decode_token(auth_token),query,SearchType.USER)
            except:
                pass
            return make_response(jsonify({"number_of_pages": number_of_pages,"result_list": sorted_result_list}))
        return make_response(jsonify({"error": "Missing data fields or invalid data."}), 400)

def register_resources(api):
    api.add_namespace(search_engine_ns) 