import datetime
# Statement for enabling the development environment
DEBUG = True

# Define the application directory
import os
BASE_DIR = os.path.abspath(os.path.dirname(__file__))  

# Define the database - we are working with
DATABASE_URL = "write-db-url-here"
SQLALCHEMY_DATABASE_URI = 'mysql://' + DATABASE_URL + '/platon_db'
SQLALCHEMY_TRACK_MODIFICATIONS = False

# Application threads. A common general assumption is
# using 2 per available processor cores - to handle
# incoming requests using one and performing background
# operations using the other.
THREADS_PER_PAGE = 2

JWT_SESSION_KEY = "secret"
    
JWT_ALGORITHM = 'HS256'
    
SESSION_DURATION = datetime.timedelta(minutes=10)

# Secret key for signing cookies
SECRET_KEY = "secret"