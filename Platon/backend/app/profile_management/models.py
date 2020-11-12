from app import db

class ResearchInformation(db.Model):
    __tablename__ = 'research_information'
    id = db.Column(db.Integer,primary_key=True,autoincrement=True)
    user_id = db.Column(db.Integer,db.ForeignKey('users.id',ondelete="CASCADE"))
    research_title = db.Column(db.String(250),nullable=False)
    description = db.Column(db.String(500))
    type = db.Column(db.SmallInteger,nullable=False)

class Jobs(db.Model):
    __tablename__ = 'jobs'
    id = db.Column(db.Integer,primary_key=True,autoincrement=True)
    name = db.Column(db.String(50),nullable=False,unique=True)

class Affiliate(db.Model):
    __tablename__ = 'affiliates'
    id = db.Column(db.Integer,primary_key=True,autoincrement=True)
    user_id = db.Column(db.Integer,db.ForeignKey('users.id',ondelete="CASCADE"))
    position = db.Column(db.Integer,db.ForeignKey('jobs.id',ondelete="CASCADE"))
    start_date = db.Column(db.Date,nullable=False)
    end_date = db.Column(db.Date,nullable=False)
    country = db.Column(db.String(50),nullable=False)
    city = db.Column(db.String(50),nullable=False)

class Skills(db.Model):
    __tablename__ = 'skills'
    id = db.Column(db.Integer,primary_key=True,autoincrement=True)
    name = db.Column(db.String(50),nullable=False,unique=True)

class UserSkills(db.Model):
    __tablename__ = 'user_skills'
    user_id = db.Column(db.Integer,db.ForeignKey('users.id',ondelete="CASCADE"),primary_key=True)
    skill_id = db.Column(db.Integer,db.ForeignKey('skills.id',ondelete="CASCADE"),primary_key=True)