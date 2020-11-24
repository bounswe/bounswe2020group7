import requests
import json
import atexit
from apscheduler.schedulers.background import BackgroundScheduler
from app import db
from bs4 import BeautifulSoup
from enum import IntEnum

from app.auth_system.models import User
from app.profile_management.models import ResearchInformation,Notification,NotificationRelatedUser


class ResearchType(IntEnum):
    HAND_WRITTEN = 0
    FETCHED = 1

class ResearchInfoFetch():
    
    @staticmethod
    def fetch_google_scholar_info(username):
        """
            Takes Google Scholar account id as input and returns fetches the works from Google Scholar
        """
        if username is None:
            return []

        api_url = "http://cse.bth.se/~fer/googlescholar-api/googlescholar.php?user={}".format(username)
        try:
            response = requests.get(api_url)
            response = json.loads(response.text)
            return [{'title':research['title'],'description':'','year':research['year']}for research in response['publications']]
        except:
            return []

    @staticmethod
    def extract_google_scholar_id(URL):
        '''
        Extracts Google Scholar account ID from Google Scholar profile page URL.
        '''
        if URL is None:
            return
        return URL.split("?user=", 1)[1].split("&",1)[0]
    
    @staticmethod
    def fetch_research_gate_info(username):
        """
            Takes Research Gate profile URL as input and returns fetches the works from Research Gate
        """
        if username is None:
            return []

        api_url = "{}/research".format(username)
        try:
            response = requests.get(api_url)
            soup = BeautifulSoup(response.text, 'html.parser')
            research_list = []
            for research in soup.find_all("div",{"class":"nova-e-text nova-e-text--size-l nova-e-text--family-sans-serif nova-e-text--spacing-none nova-e-text--color-inherit nova-v-publication-item__title"}):
                research_list.append(research.a.string)
            date_list = []
            for date in soup.find_all("li",{"class":"nova-e-list__item nova-v-publication-item__meta-data-item"}):
                date_list.append(date.string.split()[-1])
            return [{'title':research,'description':'','year':int(year)}for research,year in zip(research_list,date_list)]
        except:
            return []
    @staticmethod
    def update_research_info_all():
        """
            Updates the Google Scholar and ResearchGate information of all users in the system
        """
        try:
            all_users = User.query.all()
        except:
            return
        else:
            for user in all_users:
                update_research_info(user.id)

    @staticmethod
    def update_research_info(user_id):
        '''
        Updates the research information of the user with the given ID.
        '''
        try:
            user = User.query.filter_by(id=user_id).first()
            all_research_of_user = ResearchInformation.query.filter((ResearchInformation.user_id == user.id)&(ResearchInformation.type == int(ResearchType.FETCHED))).all()
            
            google_scholar_id = ResearchInfoFetch.extract_google_scholar_id(user.google_scholar_name)
            all_research_new = ResearchInfoFetch.fetch_google_scholar_info(google_scholar_id) + ResearchInfoFetch.fetch_research_gate_info(user.researchgate_name)
            for research in all_research_new:
                if research['title'].title() not in [i.research_title for i in all_research_of_user]:
                    db.session.add(ResearchInformation(user.id,research['title'].title(),research['description'],research['year'],int(ResearchType.FETCHED)))
            for research in all_research_of_user:
                if research.research_title.title() not in [i['title'].title() for i in all_research_new]:
                    db.session.delete(research)
            db.session.commit()
        except:
            return


class NotificationManager():
    
    @staticmethod
    def add_notification(owner_id,related_user_id_list,text,link=None):
        """
            Creates notification for a user and also adds related user list to the database
            If it creates without any problem it returns True,
            In any problem function returns False
        """
        new_notification = Notification(owner_id,text,link)
        try:
            db.session.add(new_notification)
            db.session.commit()
        except:
            return False

        related_user_records = [NotificationRelatedUser(new_notification.id,id) for id in related_user_id_list]
        try:
            db.session.add_all(related_user_records)
            db.session.commit()
        except:
            return False

        return True


def schedule_regularly():
    scheduler = BackgroundScheduler()
    scheduler.add_job(func=ResearchInfoFetch.update_research_info_all, trigger="interval",seconds=60*60)
    scheduler.start()
    # Shut down the scheduler when exiting the app
    atexit.register(lambda: scheduler.shutdown())