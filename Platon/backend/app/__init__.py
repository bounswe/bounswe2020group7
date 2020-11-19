from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from flask_migrate import Migrate
from flask_restplus import Api
from flask_mail import Mail

db = SQLAlchemy()
migrate = Migrate()
api = Api()
mail = Mail()

def create_app(config_class='config'):
    global api

    app = Flask(__name__)
    app.config.from_object(config_class)
    api = Api(app,
              version="0.1",
              title = "Platon RESTful API",
              description = "An Academic Collaboration Platform Backend",
              default="Platon API Endpoints",
              doc = "/documentation",
              prefix = '/api')
    db.init_app(app)
    migrate.init_app(app,db)
    mail.init_app(app)
    
    from app import models

    @app.errorhandler(404)
    def not_found(error):
        return "404 Error", 404
    
    # Import a module / component using its blueprint handler variable (mod_auth)
    from app.auth_system.views import register_resources as auth_module
    from app.follow_system.views import register_resources as follow_module
    from app.profile_management.views import register_resources as profile_module

    # Register blueprint(s)
    auth_module(api)
    follow_module(api)
    profile_module(api)

    # Run Scheduled Functions
    from app.profile_management.views import schedule_regularly
    schedule_regularly()
    with app.app_context():
        # This will create the database file using SQLAlchemy
        db.create_all()    
    return app