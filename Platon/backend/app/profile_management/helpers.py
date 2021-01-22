from flask_mail import Message 
from flask import render_template

import requests
import json
import atexit
from apscheduler.schedulers.background import BackgroundScheduler
from app import db,app,mail
from bs4 import BeautifulSoup
from enum import IntEnum
from flask import current_app as config_app

from app.auth_system.models import User
from app.profile_management.models import ResearchInformation,Notification,NotificationRelatedUser,NotificationStatus


class ResearchType(IntEnum):
    HAND_WRITTEN = 0
    FETCHED = 1

class ResearchInfoFetch():
    
    @staticmethod
    def fetch_google_scholar_info(username):
        """
            Takes Google Scholar account id as input and returns fetches the works from Google Scholar
        """
        if username is None or username == '':
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
        if URL is None or URL == '':
            return
        return URL.split("?user=", 1)[1].split("&",1)[0]
    
    @staticmethod
    def fetch_research_gate_info(username):
        """
            Takes Research Gate profile URL as input and returns fetches the works from Research Gate
        """
        if username is None or username == '':
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
        with app.app_context():
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

class EMailManager():

    @staticmethod
    def send_account_activation_e_mail(recipient_email,activation_token):
        msg = Message(subject='Account Activation',recipients = [recipient_email])
        e_mail_context = {
            "activation_link": "{}/activate_account?token={}".format(config_app.config["FRONTEND_HOSTNAME"],activation_token),
            "activation_token": activation_token
        }
        msg.html = render_template('account_verification.html',activation_link = e_mail_context["activation_link"],activation_token = e_mail_context["activation_token"])
        try:
            mail.send(msg)
            return True
        except:
            return False
    
    @staticmethod
    def send_reset_password_e_mail(recipient_email,token):
        msg = Message(subject='Reset Password',recipients = [recipient_email])
        e_mail_context = {
            "reset_password_link": "{}/resetpassword/{}".format(config_app.config["FRONTEND_HOSTNAME"],token),
            "reset_password_token": token
        }
        msg.html = render_template('reset_password.html',reset_password_link = e_mail_context["reset_password_link"],reset_password_token = e_mail_context["reset_password_token"])
        try:
            mail.send(msg)
            return True
        except:
            return False

    @staticmethod
    def send_admin_report_e_mail(reported_user_id,report_text):
        msg = Message(subject='Report',recipients = ["bounswegroup7@gmail.com"])
        msg.html = render_template('report_admin.html',report_text = report_text,user_id = reported_user_id)
        try:
            mail.send(msg)
            return True
        except:
            return False
    
    @staticmethod
    def send_notification_e_mail(recipient_email,notification):
        msg = Message(subject='Notification',recipients = [recipient_email])
        msg.html = render_template('notification.html',notification = notification)
        try:
            mail.send(msg)
            return True
        except:
            return False

class NotificationManager():
    
    @staticmethod
    def add_notification(owner_id,related_user_id_list,text,link=None):
        """
            Creates notification for a user and also adds related user list to the database
            If it creates without any problem it returns True,
            In any problem function returns False
        """
        try:
            notification_status = NotificationStatus.query.get(owner_id)
        except:
            return False
        # Add Notification if it is allowed    
        if notification_status is None or int(notification_status.is_notification_allowed) == 1:
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
        # Send Notification E-Mial if it is allowed
        if int(notification_status.is_email_allowed) == 1:
            try:
                user = User.query.get(owner_id)
            except:
                return False
            EMailManager.send_notification_e_mail(user.e_mail,text)
        return True

def schedule_regularly():
    scheduler = BackgroundScheduler()
    scheduler.add_job(func=ResearchInfoFetch.update_research_info_all, trigger="interval",seconds=60*60)
    scheduler.start()
    # Shut down the scheduler when exiting the app
    atexit.register(lambda: scheduler.shutdown())