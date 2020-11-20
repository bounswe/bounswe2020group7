import requests
import json
import atexit
from apscheduler.schedulers.background import BackgroundScheduler
from app import db

from app.profile_management.models import ResearchInformation

class ResearchInfoFetch():
    
    @staticmethod
    def fetch_google_scholar_info(username):
        """
            Takes Google Scholar account id as input and returns fetches the works from Google Scholar
        """
        api_url = "http://cse.bth.se/~fer/googlescholar-api/googlescholar.php?user={}".format(username)
        try:
            response = requests.get(api_url)
            response = json.loads(response.text)
            return [{'title':research['title'],'description':'','year':research['year']}for research in response['publications']]
        except:
            return []
    
    @staticmethod
    def fetch_research_gate_info(username):
        """
            Takes ResearchGate name as input and returns fetches the works from ResearchGate
        """
        api_url = "https://dblp.org/search/publ/api?q=author%3A{}%3A&format=json".format(username.replace(' ','_'))
        try:
            response = requests.get(api_url)
            response = json.loads(response.text)
            research_list = response['result']['hits']['hit']
            return [{'title':i['info']['title'],'description':'','year':int(i['info']['year'])}for i in research_list]
        except:
            return []
    
    @staticmethod
    def update_research_info():
        """
            Updates the Google Scholar and ResearchGate information of all users in the system
        """
        try:
            all_users = User.query.all()
        except:
            return
        try:
            for user in all_users:
                all_research_of_user = ResearchInformation.query.filter((ResearchInformation.user_id == user.id)&(ResearchInformation.type == ResearchType.FETCHED)).all()
                all_research_new = ResearchInfoFetch.fetch_google_scholar_info(user.google_scholar_name) + ResearchInfoFetch.fetch_research_gate_info(user.researchgate_name)
                for research in all_research_new:
                    if research['title'] not in [i.research_title for i in all_research_of_user]:
                        db.seesion.add(ResearchInformation(user.id,research['title'],research['description'],research['year'],ResearchType.FETCHED))
                for research in all_research_of_user:
                    if research.research_title not in [i['title'] for i in all_research_new]:
                        db.session.delete(research)
                db.session.commit()
        except:
            return


def schedule_regularly():
    scheduler = BackgroundScheduler()
    scheduler.add_job(func=ResearchInfoFetch.update_research_info, trigger="interval",seconds=60*60)
    scheduler.start()
    # Shut down the scheduler when exiting the app
    atexit.register(lambda: scheduler.shutdown())