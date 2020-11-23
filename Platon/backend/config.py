import datetime
# Statement for enabling the development environment
DEBUG = True

# Define the application directory
import os
BASE_DIR = os.path.abspath(os.path.dirname(__file__))

FRONTEND_HOSTNAME = "http://ec2-52-59-254-130.eu-central-1.compute.amazonaws.com/"

# Define the database - we are working with
mysql_user = os.getenv('MYSQL_USER')
mysql_password = os.getenv('MYSQL_PASSWORD')
mysql_host = os.getenv('MYSQL_HOST')
mysql_port = os.getenv('MYSQL_PORT')
mysql_database = os.getenv('MYSQL_DATABASE')

SQLALCHEMY_DATABASE_URI = "mysql+pymysql://{}:{}@{}:{}/{}".format(mysql_user, mysql_password, mysql_host, mysql_port, mysql_database)
SQLALCHEMY_TRACK_MODIFICATIONS = False

# Application threads. A common general assumption is
# using 2 per available processor cores - to handle
# incoming requests using one and performing background
# operations using the other.
THREADS_PER_PAGE = 2

JWT_SESSION_KEY = "secret" 
JWT_ALGORITHM = 'HS256'
SESSION_DURATION = datetime.timedelta(minutes=24*365*60)

LINK_DURATION = datetime.timedelta(minutes=5)

MAIL_SERVER = 'smtp.gmail.com'
MAIL_PORT = 465
MAIL_USE_SSL = True
MAIL_USERNAME = 'platon.group7@gmail.com'
MAIL_PASSWORD = 'hddyynymjbtbaxaf'
MAIL_DEFAULT_SENDER = ('Platon','platon.group7@gmail.com')

# Secret key for signing cookies
SECRET_KEY = "secret"