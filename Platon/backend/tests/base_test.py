from flask_testing import TestCase
from app import create_app, db
import unittest
import datetime

class TestConfig:

    TESTING = True

    # Statement for enabling the development environment
    DEBUG = True

    # Define the application directory
    import os
    BASE_DIR = os.path.abspath(os.path.dirname(__file__))  

    # Define the database - we are working with
    mysql_user = "user"
    mysql_password = "rootpassword"
    mysql_host = "52.59.254.130"
    mysql_port = "3306"
    mysql_database = "platondb_test"

    SQLALCHEMY_DATABASE_URI = "mysql+pymysql://{}:{}@{}:{}/{}".format(mysql_user, mysql_password, mysql_host, mysql_port, mysql_database)
    SQLALCHEMY_TRACK_MODIFICATIONS = False

    # Application threads. A common general assumption is
    # using 2 per available processor cores - to handle
    # incoming requests using one and performing background
    # operations using the other.
    THREADS_PER_PAGE = 2

    # Use a secure, unique and absolutely secret key for
    # signing the data. 
    JWT_SESSION_KEY = "35c55c78ea5f90c0087020ab49ba78f96eedbec3a04640234719b6dad8849769"
    
    JWT_ALGORITHM = 'HS256'
    
    SESSION_DURATION = datetime.timedelta(minutes=10)

    LINK_DURATION = datetime.timedelta(minutes=5)

    MAIL_SERVER = 'smtp.gmail.com'
    MAIL_PORT = 465
    MAIL_USE_SSL = True
    MAIL_USERNAME = 'platon.group7@gmail.com'
    MAIL_PASSWORD = 'hddyynymjbtbaxaf'
    MAIL_DEFAULT_SENDER = ('Platon','platon.group7@gmail.com')

    # Secret key for signing cookies
    SECRET_KEY = "secret"

class BaseTest(TestCase):

    def create_app(self):
        # pass in test configuration
        return create_app(TestConfig)
    
    def setUp(self):
        db.create_all()
    
    def tearDown(self):

        db.session.remove()
        db.drop_all()