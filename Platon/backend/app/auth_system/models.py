from app import db

class User(db.Model):
    __tablename__ = 'users'
    id = db.Column(db.Integer,primary_key=True,autoincrement=True)
    e_mail = db.Column(db.String(50),unique=True,nullable=False)
    is_valid = db.Column(db.Boolean,nullable=False)
    password_hashed = db.Column(db.String(256),nullable=False)
    timestamp = db.Column(db.DateTime,default=db.func.now(),nullable=False)
    rate = db.Column(db.Float,nullable=False)
    name = db.Column(db.String(50),nullable=False)
    surname = db.Column(db.String(50),nullable=False)
    is_private = db.Column(db.Boolean,nullable=False)
    profile_photo = db.Column(db.String(50))
    google_scholar_name = db.Column(db.String(50))
    researchgate_name = db.Column(db.String(50))