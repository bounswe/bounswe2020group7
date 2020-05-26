FROM python:3
ENV PYTHONUNBUFFERED 1
EXPOSE 8000

ADD . /platon_api

WORKDIR /platon_api

RUN pip install -r requirements.txt

RUN python manage.py makemigrations

RUN python manage.py makemigrations rest_api

RUN python manage.py migrate

CMD [ "python", "manage.py", "runserver", "0.0.0.0:8000" ]