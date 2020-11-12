from flask import Blueprint
from app import db

mod_auth = Blueprint('auth', __name__, url_prefix='/auth')

@mod_auth.route('/', methods=['GET'])
def index():
    return "PlatonAPI Auth is in the development process!!!"
 