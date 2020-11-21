FROM python:3.8.0-slim
RUN pip install --upgrade pip
WORKDIR /platon_backend
COPY backend/requirements.txt requirements.txt
RUN pip install -r requirements.txt
COPY ./backend .
CMD ["/bin/sh", "backend/platon_backend_entrypoint.sh"]