from flask import Flask, redirect, url_for
from flask_sqlalchemy import SQLAlchemy
from flask_migrate import Migrate
from flask_restplus import Api
from flask_mail import Mail
from flask_cors import CORS

import os

db = SQLAlchemy()
migrate = Migrate()
api = Api()
mail = Mail()
app = None

def create_app(config_class='config'):
    global api,app

    app = Flask(__name__)
    app.config.from_object(config_class)
    api = Api(app,
              version="0.5",
              title = "Platon RESTful API",
              description = "An Academic Collaboration Platform Backend",
              default="Platon API Endpoints",
              doc = "/documentation",
              prefix = '/api',
              contact='Platon',
              contact_url="http://ec2-3-120-98-39.eu-central-1.compute.amazonaws.com")
    db.init_app(app)
    migrate.init_app(app,db)
    mail.init_app(app)
    CORS(app)

    from app import models

    @app.errorhandler(404)
    def not_found(error):
        return "404 Error", 404

    # REdirect index to Documentation
    @app.route("/")
    def index():
        return redirect(url_for("doc"))

    # Import a module / component using its blueprint handler variable (mod_auth)
    from app.auth_system.views import register_resources as auth_module
    from app.follow_system.views import register_resources as follow_module
    from app.profile_management.views import register_resources as profile_module
    from app.search_engine.views import register_resources as search_engine
    from app.file_system.views import register_resources as file_system
    from app.workspace_system.views import register_resources as workspace_module
    from app.upcoming_events.views import register_resources as upcoming_events

    # Register blueprint(s)
    auth_module(api)
    follow_module(api)
    profile_module(api)
    search_engine(api)
    workspace_module(api)
    file_system(api)
    upcoming_events(api)
    
    if not os.path.exists(app.config['WORKSPACE_FILE_PATH']):
        os.makedirs(app.config['WORKSPACE_FILE_PATH'])

    if not os.path.exists(app.config['PROFILE_PHOTO_PATH']):
        os.makedirs(app.config['PROFILE_PHOTO_PATH'])

    with app.app_context():
        # This will create the database file using SQLAlchemy
        db.create_all()    
    return app