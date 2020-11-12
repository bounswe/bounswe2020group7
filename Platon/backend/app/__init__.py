from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from flask_migrate import Migrate

app = Flask(__name__)

app.config.from_object('config')

db = SQLAlchemy(app)
migrate = Migrate(app,db)

from app import models

@app.errorhandler(404)
def not_found(error):
    return "404 Error", 404

# Import a module / component using its blueprint handler variable (mod_auth)
from app.auth_system.views import mod_auth as auth_module
from app.follow_system.views import mod_follow as follow_module

# Register blueprint(s)
app.register_blueprint(auth_module)
app.register_blueprint(follow_module)

# This will create the database file using SQLAlchemy
db.create_all()