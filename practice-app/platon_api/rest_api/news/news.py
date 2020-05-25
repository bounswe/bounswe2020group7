"""
Created on MAY 15, 2020
Controls recommended news api of PLATON_API, using django&mysql backend.
Endpoint description:
    http://localhost:8000/api/news/<str:token>

    'POST':
        Produces error
    'GET':
        Gets dictionary from body.
        JSON Format : {
                        url = "url of the news",
                        imageUrl = "image's url of the news",
                        title = "title of the news"
                        }
@author: Ahmet Dadak
@company: Group7
"""

from newsapi import NewsApiClient
from django.db import connection
from platon_api.settings import NEWS_API_KEY
import json
from platon_api.settings import USER_TABLENAME


def news_api(request, token):
    """
        returns featured news article for the user's field of study
    """
    if request.method == 'GET':
        # this sql queries the user's field of study by user's token
        sql = "SELECT field_of_study FROM `" + USER_TABLENAME + "` WHERE token='{}'".format(token)
        cursor = connection.cursor()
        cursor.execute(sql)
        field_of_study = cursor.fetchall()[0][0]
        newsapi = NewsApiClient(api_key=NEWS_API_KEY)
        all_articles = newsapi.get_everything(q=field_of_study,
                                                sources='bbc-news',
                                                language='en',
                                                sort_by='relevancy')
        title_list = []
        url_list = []
        url_to_image_list = []
        feed = all_articles["articles"]
        if len(all_articles["articles"]) < 10:
            top_headlines = newsapi.get_top_headlines(sources='bbc-news')
            feed = top_headlines["articles"]
        for key in feed:
            title_list.append(key["title"])
            url_list.append(key["url"])
            url_to_image_list.append(key["urlToImage"])

        context = {'url': url_list, 'imageUrl': url_to_image_list, 'title': title_list}
        return json.dumps(context)
