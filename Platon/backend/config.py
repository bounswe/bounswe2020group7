import datetime
# In the production please make False both DEVELOPMENT and DEBUG flags
DEBUG = False
DEVELOPMENT = False

# Define the application directory
import os
BASE_DIR = os.path.abspath(os.path.dirname(__file__))

FRONTEND_HOSTNAME = "http://ec2-3-120-98-39.eu-central-1.compute.amazonaws.com"

if DEVELOPMENT and DEBUG:
    # Define the database - we are working with
    mysql_user = "root"
    mysql_password = "rootpassword"
    mysql_host = "18.185.75.161"
    mysql_port = "3306"
    mysql_database = "platondb"
elif DEVELOPMENT:
    # Define the database - we are working with
    mysql_user = "admin"
    mysql_password = "platon1234"
    mysql_host = "platondbms.cchkgthejclc.eu-central-1.rds.amazonaws.com"
    mysql_port = "3306"
    mysql_database = "platondb"
else:
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

LINK_DURATION = datetime.timedelta(minutes=20)

MAIL_SERVER = 'smtp.gmail.com'
MAIL_PORT = 465
MAIL_USE_SSL = True
MAIL_USERNAME = 'platon.group7@gmail.com'
MAIL_PASSWORD = 'hddyynymjbtbaxaf'
MAIL_DEFAULT_SENDER = ('Platon','platon.group7@gmail.com')

# Secret key for signing cookies
SECRET_KEY = "secret"

PROFILE_PHOTO_PATH = BASE_DIR + os.path.sep + "data" + os.path.sep +"profile_photos"