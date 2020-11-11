import os
basedir = os.path.abspath(os.path.dirname(__file__))

class Config(object):

    DATABASE_URL = "write-here-database-path"
    SQLALCHEMY_DATABASE_URI = 'mysql://' + DATABASE_URL + '/platon_db'
    SQLALCHEMY_TRACK_MODIFICATIONS = False