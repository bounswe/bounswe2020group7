FROM python:3.8.0-slim
RUN pip install --upgrade pip
WORKDIR /platon_backend
COPY backend/requirements.txt requirements.txt
RUN pip install -r requirements.txt
COPY ./backend .
CMD ["flask","db","init"]
CMD ["flask","db","migrate"]
CMD ["flask","db","upgrade"]
CMD ["flask","run","--host=0.0.0.0"]