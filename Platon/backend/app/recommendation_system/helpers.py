from rank_bm25 import BM25Okapi
from app.recommendation_system import *

from app import db,app

from app.recommendation_system.models import *
from app.profile_management.models import UserSkills
from app.workspace_system.models import Workspace,WorkspaceSkill
from app.auth_system.models import User

import atexit
from apscheduler.schedulers.background import BackgroundScheduler

class RecommendationSystem():

    number_of_recommendations = 8

    @staticmethod
    def calculate_recommendations(tokenized_corpus,id_list,tokenized_query):
        # Calcualte BM-25 Model
        bm25 = BM25Okapi(tokenized_corpus)
        # Calculate document scores
        doc_scores = bm25.get_scores(tokenized_query)
        # Return sorted list of recommendations
        return sorted(zip(id_list,doc_scores),key= lambda x:x[1],reverse=True)
    
    @staticmethod
    def update_follow_recommendation(user_id,skill_array,id_list):
        # Take the skills of the user
        try:
            skills = UserSkills.query.filter_by(user_id=user_id).all()
        except:
            return
        # Tokenized Query
        tokenized_query = [skill.skill_id for skill in skills]
        # Calculate Recommendations
        recommendations = RecommendationSystem.calculate_recommendations(skill_array,id_list,tokenized_query)[:RecommendationSystem.number_of_recommendations+1]
        # Delete all of the recommendations of this user
        try:
            FollowRecommendationItem.query.filter(FollowRecommendationItem.owner_id == user_id).delete()
        except:
            return
        recommendation_records = []
        for recommendation in recommendations:
            if recommendation[0] != user_id:
                recommendation_records.append(FollowRecommendationItem(user_id,recommendation[0],float(recommendation[1])))
        try:
            db.session.add_all(recommendation_records)
            db.session.commit()
        except:
            return
    
    @staticmethod
    def update_all_follow_recommendations():
        with app.app_context():
            # Take all of the user skills
            try:
                all_user_skills = UserSkills.query.all()
            except:
                return
            skills = {}
            for user_skill in all_user_skills:
                if user_skill.user_id not in skills:
                    skills[user_skill.user_id] = [user_skill.skill_id]
                else:
                    skills[user_skill.user_id].append(user_skill.skill_id)
            try:
                all_users = User.query.all()
            except:
                return
            id_list = [user.id for user in all_users]
            tokenized_corpus = []
            for id in id_list:
                if id in skills:
                    tokenized_corpus.append(skills[id])
                else:
                    tokenized_corpus.append([])
            # Update all follow recommendations
            for user in all_users:
                RecommendationSystem.update_follow_recommendation(user.id,tokenized_corpus,id_list)

    @staticmethod
    def update_workspace_recommendation(user_id,skill_array,id_list):
        # Take the skills of the user
        try:
            skills = UserSkills.query.filter_by(user_id=user_id).all()
        except:
            return
        # Tokenized Query
        tokenized_query = [skill.skill_id for skill in skills]
        # Calculate Recommendations
        recommendations = RecommendationSystem.calculate_recommendations(skill_array,id_list,tokenized_query)[:RecommendationSystem.number_of_recommendations+1]
        # Delete all of the recommendations of this user
        try:
            WorkspaceRecommendationItem.query.filter(WorkspaceRecommendationItem.owner_id == user_id).delete()
        except:
            return
        recommendation_records = []
        for recommendation in recommendations:
            recommendation_records.append(WorkspaceRecommendationItem(user_id,recommendation[0],float(recommendation[1])))
        try:
            db.session.add_all(recommendation_records)
            db.session.commit()
        except:
            return

    @staticmethod
    def update_all_workspace_recommendations():
        with app.app_context():
            # Take all of the user skills
            try:
                all_ws_skills = WorkspaceSkill.query.all()
            except:
                return
            skills = {}
            for ws_skill in all_ws_skills:
                if ws_skill.workspace_id not in skills:
                    skills[ws_skill.workspace_id] = [ws_skill.skill_id]
                else:
                    skills[ws_skill.workspace_id].append(ws_skill.skill_id)
            try:
                all_ws = Workspace.query.all()
            except:
                return
            id_list = [ws.id for ws in all_ws]
            tokenized_corpus = []
            for id in id_list:
                if id in skills:
                    tokenized_corpus.append(skills[id])
                else:
                    tokenized_corpus.append([])
            try:
                all_users = User.query.all()
            except:
                return
            # Update all follow recommendations
            for user in all_users:
                RecommendationSystem.update_workspace_recommendation(user.id,tokenized_corpus,id_list)    
    
    @staticmethod
    def update_collaboration_recommendation(workspace_id,skill_array,id_list):
        # Take the skills of the user
        try:
            skills = WorkspaceSkill.query.filter_by(workspace_id=workspace_id).all()
        except:
            return
        # Tokenized Query
        tokenized_query = [skill.skill_id for skill in skills]
        # Calculate Recommendations
        recommendations = RecommendationSystem.calculate_recommendations(skill_array,id_list,tokenized_query)[:RecommendationSystem.number_of_recommendations+1]
        # Delete all of the recommendations of this user
        try:
            CollaboratorRecommendationItem.query.filter(CollaboratorRecommendationItem.owner_id == workspace_id).delete()
        except:
            return
        recommendation_records = []
        for recommendation in recommendations:
            recommendation_records.append(CollaboratorRecommendationItem(workspace_id,recommendation[0],float(recommendation[1])))
        try:
            db.session.add_all(recommendation_records)
            db.session.commit()
        except:
            return

    @staticmethod
    def update_all_collaboration_recommendations():
        with app.app_context():
            # Take all of the user skills
            try:
                all_user_skills = UserSkills.query.all()
            except:
                return
            skills = {}
            for user_skill in all_user_skills:
                if user_skill.user_id not in skills:
                    skills[user_skill.user_id] = [user_skill.skill_id]
                else:
                    skills[user_skill.user_id].append(user_skill.skill_id)
            try:
                all_users = User.query.all()
            except:
                return
            id_list = [user.id for user in all_users]
            tokenized_corpus = []
            for id in id_list:
                if id in skills:
                    tokenized_corpus.append(skills[id])
                else:
                    tokenized_corpus.append([])
            try:
                all_ws = Workspace.query.all()
            except:
                return
            # Update all follow recommendations
            for ws in all_ws:
                RecommendationSystem.update_collaboration_recommendation(ws.id,tokenized_corpus,id_list)

def schedule_regularly():
    scheduler = BackgroundScheduler()
    scheduler.add_job(func=RecommendationSystem.update_all_follow_recommendations, trigger="interval",seconds=60*10)
    scheduler.add_job(func=RecommendationSystem.update_all_workspace_recommendations, trigger="interval",seconds=60*10)
    scheduler.add_job(func=RecommendationSystem.update_all_collaboration_recommendations, trigger="interval",seconds=60*10)
    scheduler.start()
    # Shut down the scheduler when exiting the app
    atexit.register(lambda: scheduler.shutdown())   