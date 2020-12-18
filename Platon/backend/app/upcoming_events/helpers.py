import requests
import re
import atexit
from apscheduler.schedulers.background import BackgroundScheduler

from app.upcoming_events.models import UpcomingEvent
from app import db

class UpcomingEventsManager():

    event_regex = '<tr bgcolor="#[ef]6[ef]6[ef]6">[\s\S]+?</tr>'
    title_regex = '<a href="([\s\S]*)">([\s\S]*)</a></td>\s<td align="[\s\S]*" colspan="[\s\S]*?">([\s\S]*?)</td>'
    date_regex = '<td align="[\s\S]*">([\s\S]*?)</td>\s<td align="[\s\S]*">([\s\S]*?)</td>\s<td align="[\s\S]*">([\s\S]*?)</td>'

    @staticmethod
    def parse_event(first_part,second_part):
        # Parse link and title by using regular expressions
        link,acronym,title = re.findall(UpcomingEventsManager.title_regex,first_part)[0]
        # Parse date, deadline and location by uning regular expressions
        date,location,deadline = re.findall(UpcomingEventsManager.date_regex,second_part)[0]
        return {'link': "http://www.wikicfp.com" + link, 'acronym': acronym, 'title': title,
                'date': date, 'location': location, 'deadline': deadline}
    
    @staticmethod
    def get_number_of_pages():
        response = requests.get("http://www.wikicfp.com/cfp/allcfp")
        number_of_pages = max([int(number) for number in re.findall('page=([\d]*)',response.text)])
        return number_of_pages
    
    @staticmethod
    def parse_page(page_id):
        url = "http://www.wikicfp.com/cfp/allcfp?page={}".format(page_id)
        response = requests.get(url)
        event_list = re.findall(UpcomingEventsManager.event_regex,response.text)
        events_of_page = []
        # Parse each event one by one
        for index in range(int(len(event_list)/2)):
            first_part = event_list[2*index]
            second_part = event_list[2*index+1]
            # Parse each event and append to the list
            events_of_page.append(UpcomingEventsManager.parse_event(first_part,second_part))
        return events_of_page

    @staticmethod
    def parse_all_pages():
        number_of_pages = UpcomingEventsManager.get_number_of_pages()
        all_events = []
        for i in range(number_of_pages):
            all_events += UpcomingEventsManager.parse_page(i+1)
    
    @staticmethod
    def update_upcoming_events():
        upcoming_events = UpcomingEventsManager.parse_all_pages()
        try:
            existing_upcoming_events = UpcomingEvent.query.filter().all()
        except:
            pass
        existing_titles = [existing_event.title for existing_event in existing_upcoming_events]
        try:
            for upcoming_event in upcoming_events:
                if upcoming_event["title"] in existing_titles:
                    continue
                new_event = UpcomingEvent(upcoming_event["title"],upcoming_event["acronym"],upcoming_event["location"],
                                        upcoming_event["date"],upcoming_event["deadline"],upcoming_event["link"])
                db.session.add(new_event)
            db.session.commit()
        except:
            return


def schedule_regularly():
    scheduler = BackgroundScheduler()
    scheduler.add_job(func=UpcomingEventsManager.update_upcoming_events, trigger="interval",seconds=10)
    scheduler.start()
    # Shut down the scheduler when exiting the app
    atexit.register(lambda: scheduler.shutdown())    