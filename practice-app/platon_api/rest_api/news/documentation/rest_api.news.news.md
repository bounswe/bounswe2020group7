NAME

    rest_api.news.news

DESCRIPTION

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

FUNCTIONS

    news_api(request, token)
        returns featured news article for the user's field of study

    verify_token(token=None)
        where 'token': string, 64 characther string that can be token

        returns True if token exists in the DB

        This function verifies the token if there exits.

DATA

    NEWS_API_KEY = '1d2f2e6696174ffca8668a0f26322f41'
    USER_TABLENAME = 'rest_api_registereduser'
    connection = <django.db.DefaultConnectionProxy object>

FILE

    c:\users\ahmet\desktop\fe\practice-app\platon_api\rest_api\news\news.py