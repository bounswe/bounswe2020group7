from flask import make_response,jsonify,request
from flask_restplus import Resource,Namespace, fields
from sqlalchemy import func

from app.search_engine.forms import UserSearchForm, user_search_parser,SearchHistoryForm,search_history_parser
from app.search_engine.helpers import SearchEngine, SearchType
from app.search_engine.models import SearchHistoryItem
from app.profile_management.models import Jobs,Skills,UserSkills
from app.auth_system.models import User
from app.auth_system.helpers import decode_token,login_required,allowed_file
from app.search_engine.forms import WorkspaceSearchForm,ws_search_parser
from app.workspace_system.models import Contribution

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

search_history_model = api.model('Search History Item',{
    "query": fields.String,
    "number_of_use": fields.Integer
})

search_history_list_model = api.model('Search History',{
    "search_history": fields.Nested(search_history_model)
})

contributor_model = api.model("Contributor", {
    "id": fields.Integer,
    "name": fields.String,
    "surname": fields.String,
    }
)

workspace_model = api.model('Worksapce', {
    "id": fields.Integer,
    "title": fields.String,
    "is_private": fields.Integer,
    "description": fields.String,
    "state": fields.Integer,
    "deadline": fields.String,
    "creation_time": fields.String,
    "max_contibutors": fields.Integer,
    "contributors": fields.List(
        fields.Nested(contributor_model)
    ),
})

workspace_list_model = api.model('Worksapce List', {
    "number_of_pages": fields.String,
    'workspaces': fields.List(
        fields.Nested(workspace_model)
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
            # List of user dictionaries that will be send as a search output
            result_list = []
            # Score of each user record
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
                            if user.is_valid == 0:
                                continue
                            profile_photo = ""
                            if allowed_file(user.profile_photo):
                                profile_photo = "/auth_system/profile_photo?user_id={}".format(user.id)
                            result_list.append({"id":user.id, "name": user.name, "surname": user.surname, 
                                            "profile_photo" : profile_photo, "is_private": int(user.is_private), "job_id" : user.job_id})
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
                        # Creates the list of currently found ids
                        id_list = [i[0] for i in result_id_score]
                        if owner_id.user_id not in id_list:
                            # Take user information from the DB
                            try:
                                user = User.query.filter(User.id == owner_id.user_id).first()
                            except:
                                return make_response(jsonify({"error": "Database Connection Problem."}), 500)
                            if user.is_valid == 0:
                                continue
                            profile_photo = ""
                            if allowed_file(user.profile_photo):
                                profile_photo = "/auth_system/profile_photo?user_id={}".format(user.id)
                            result_list.append({"id":user.id, "name": user.name, "surname": user.surname, 
                                            "profile_photo" : profile_photo, "is_private": int(user.is_private), "job_id" : user.job_id})
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
                    sql_statement = "SELECT id,name,surname,profile_photo,is_private,job_id,is_valid FROM users WHERE {}".format(query)
                    result = db.engine.execute(sql_statement)
                    for user in result:
                        id_list = [i[0] for i in result_id_score]
                        if user[0] not in id_list:
                            if user[-1] == 0:
                                continue
                            profile_photo = ""
                            if allowed_file(user[3]):
                                profile_photo = "/auth_system/profile_photo?user_id={}".format(user.id)
                            result_list.append({"id":user[0], "name": user[1], "surname": user[2], 
                                            "profile_photo" : profile_photo, "is_private": int(user[4]),"job_id" : user.job_id})
                            result_id_score.append((user[0],score))
                        else:
                            id_index = id_list.index(user[0])
                            result_id_score[id_index] =(user[0], result_id_score[id_index][1]+score)
                except:
                    return make_response(jsonify({"error": "Database Connection Problem."}), 500)
            sorted_result_list = []
            # Sort result ids according to their scores
            if form.sorting_criteria.data is None:
                sorted_id_list = SearchEngine.sort_ids(result_id_score)
                for user_id,score in sorted_id_list:
                    index = [user["id"] for user in result_list].index(user_id)
                    sorted_result_list.append(result_list[index])
            # Sort Results according to Sorting Criteria
            else:
                reverse = form.sorting_criteria.data == 1
                sorted_result_list = SearchEngine.sort_results(result_list,["name","surname"],reverse)

            # Apply given filters
            if form.job_filter.data is not None:
                for i,user in enumerate(sorted_result_list):
                    if user["job_id"] != form.job_filter.data:
                        sorted_result_list.pop(i)
            number_of_pages = 1
            # Apply Pagination
            if form.page.data is not None and form.per_page.data is not None:
                per_page = form.per_page.data
                number_of_pages = math.ceil(len(sorted_id_list)/per_page)
                page = form.page.data if form.page.data < number_of_pages else number_of_pages-1
                sorted_result_list = sorted_result_list[page*per_page:(page+1)*per_page]
            # Add Search History Item
            try:
                auth_token = request.headers.get('auth_token')
                SearchEngine.add_search_history_item(decode_token(auth_token),search_query,int(SearchType.USER))
            except:
                pass
            # Remove Non-Valid Users
            return make_response(jsonify({"number_of_pages": number_of_pages,"result_list": sorted_result_list}))
        return make_response(jsonify({"error": "Missing data fields or invalid data."}), 400)

@search_engine_ns.route("/search_history")
class SearchHistoryAPI(Resource):
    
    @api.doc(responses={401 : 'Account Problems', 400 : 'Input Format Error' ,500 : ' Database Connection Error'})
    @api.response(200, 'Valid Search History', search_history_list_model)
    @api.expect(search_history_parser)
    @login_required
    def get(user_id,self):
        form = SearchHistoryForm(request.args)
        if form.validate():
            if form.search_type.data > 2 or form.search_type.data < 0:
                return make_response(jsonify({"error": "Search Type must be 0, 1 or 2"}), 400)
            try:
                search_history =  db.engine.execute("SELECT `query` FROM `search_history_item` WHERE `user_id`={} AND `type`={}".format(user_id,form.search_type.data))           
            except:
                return make_response(jsonify({"error": "Database Connection Problem."}), 500)
            search_hist_dict = {}
            for history_item in search_history:
                if history_item[0] not in search_hist_dict:
                    search_hist_dict[history_item[0]] = 1
                else:
                    search_hist_dict[history_item[0]] += 1
            # Sort Search Distioneries according to number of use
            sorted_result = sorted(search_hist_dict.items(), key=lambda x: x[1], reverse=True)
            response = [{"query": item[0], "number_of_use":item[1]} for item in sorted_result]
            return make_response(jsonify({"search_history": response}),200)
        else:
            return make_response(jsonify({"error": "Missing data fields or invalid data."}), 400)

@search_engine_ns.route("/workspace")
class WorkspaceSearchAPI(Resource):

    @api.doc(responses={401 : 'Account Problems', 400 : 'Input Format Error' ,500 : 'Database Connection Error'})
    @api.response(200, 'Valid Search Result', workspace_list_model)
    @api.expect(ws_search_parser)    
    def get(self):
        form = WorkspaceSearchForm(request.args)
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
            # List of user dictionaries that will be send as a search output
            result_list = []
            # Score of each user record
            result_id_score = []
            for token,score in tokens:
                query = '(LOWER(title) REGEXP ".*{0}.*" OR LOWER(description) REGEXP ".*{0}.*")'.format(token)
                sql_statement = "SELECT * FROM workspaces WHERE {}".format(query)
                result = db.engine.execute(sql_statement)
                for workspace in result:
                    id_list = [i[0] for i in result_id_score]
                    if workspace[0] not in id_list:
                        # Remove private worksapces
                        if workspace[2] == 1:
                            continue
                        try:
                            contributors = Contribution.query.filter(Contribution.workspace_id == workspace[0]).all()
                        except:
                            return make_response(jsonify({"err": "Database Connection Error"}),500)
                        contributor_list = []
                        for contributor in contributors:
                            try:
                                user = User.query.get(contributor.user_id)
                            except:
                                return make_response(jsonify({"err": "Database Connection Error"}),500)
                            contributor_list.append({
                                    "id": user.id,
                                    "name": user.name,
                                    "surname": user.surname
                            })
                        result_list.append({"id":workspace[0], "title": workspace[3], "is_private": int(workspace[2]), 
                                            "description" : workspace[6], "state": workspace[4], "deadline" : workspace[7],
                                            "creation_time": workspace[5], "max_contributors": workspace[8],"contributor_list":contributor_list})
                        result_id_score.append((workspace[0],score))
                    else:
                        id_index = id_list.index(workspace[0])
                        result_id_score[id_index] =(workspace[0], result_id_score[id_index][1]+score)
            number_of_pages = 1
            # Apply Pagination
            if form.page.data is not None and form.per_page.data is not None:
                per_page = form.per_page.data
                number_of_pages = math.ceil(len(result_list)/per_page)
                page = form.page.data if form.page.data < number_of_pages else number_of_pages-1
                result_list = result_list[page*per_page:(page+1)*per_page]
            # Add Search History Item
            try:
                auth_token = request.headers.get('auth_token')
                SearchEngine.add_search_history_item(decode_token(auth_token),search_query,int(SearchType.WORKSPACE))
            except:
                pass
            # Remove Non-Valid Users
            return make_response(jsonify({"number_of_pages": number_of_pages,"result_list": result_list}))

        else:
            return make_response(jsonify({"error": "Missing data fields or invalid data."}), 400)



def register_resources(api):
    api.add_namespace(search_engine_ns) 