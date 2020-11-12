from flask import Blueprint
from app import db

mod_follow = Blueprint('follow_system', __name__, url_prefix='/follow_system')

@mod_follow.route('/', methods=['GET'])
def index():
    return "PlatonAPI Follow System is in the development process!!!"