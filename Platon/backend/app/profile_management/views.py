from flask import Blueprint
from app import db

mod_profile = Blueprint('profile_management', __name__, url_prefix='/profile')

@mod_profile.route('/', methods=['GET'])
def index():
    return "PlatonAPI Profile Management System is in the development process!!!"