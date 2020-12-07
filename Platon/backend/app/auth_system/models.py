from app import db

class User(db.Model):
    __tablename__ = 'users'
    id = db.Column(db.Integer,primary_key=True,autoincrement=True)
    e_mail = db.Column(db.String(50),unique=True,nullable=False)
    is_valid = db.Column(db.Boolean,nullable=False)
    password_hashed = db.Column(db.String(64),nullable=False)
    timestamp = db.Column(db.DateTime,default=db.func.now(),nullable=False)
    rate = db.Column(db.Float,nullable=False)
    name = db.Column(db.String(50),nullable=False)
    surname = db.Column(db.String(50),nullable=False)
    is_private = db.Column(db.Boolean,nullable=False)
    profile_photo = db.Column(db.String(50))
    google_scholar_name = db.Column(db.String(256))
    researchgate_name = db.Column(db.String(256))
    position_id = db.Column(db.Integer,db.ForeignKey('jobs.id',ondelete="CASCADE"))
    institution = db.Column(db.String(100))

    def __init__(self,e_mail,is_valid,password_hashed,rate,name,surname,is_private,profile_photo,google_scholar_name,researchgate_name,positon_id,institution,skill_id):
        self.e_mail = e_mail
        self.is_valid = is_valid
        self.password_hashed = password_hashed
        self.rate = rate
        self.name = name
        self.surname = surname
        self.is_private = is_private
        self.profile_photo = profile_photo
        self.google_scholar_name = google_scholar_name
        self.researchgate_name = researchgate_name
        self.position_id = positon_id
        self.institution = institution
        self.skill_id = skill_id