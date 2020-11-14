from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from flask_migrate import Migrate
from flask_restful import Api
 
db = SQLAlchemy()
migrate = Migrate()
api = Api()

def create_app(config_class='config'):
    app = Flask(__name__)
    app.config.from_object(config_class)
    api.init_app(app)
    api.prefix = '/api'
    db.init_app(app)
    migrate.init_app(app,db)
    
    from app import models

    @app.errorhandler(404)
    def not_found(error):
        return "404 Error", 404
    
    # Import a module / component using its blueprint handler variable (mod_auth)
    from app.auth_system.views import register_resources as auth_module
    from app.follow_system.views import register_resources as follow_module
    from app.profile_management.views import register_resources as profile_module

    # Register blueprint(s)
    auth_module()
    follow_module()
    profile_module()

    with app.app_context():
        # This will create the database file using SQLAlchemy
        db.create_all()
    
    return app





