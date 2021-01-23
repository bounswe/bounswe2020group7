from app import db

class ResearchInformation(db.Model):
    __tablename__ = 'research_information'
    id = db.Column(db.Integer,primary_key=True,autoincrement=True)
    user_id = db.Column(db.Integer,db.ForeignKey('users.id',ondelete="CASCADE"))
    research_title = db.Column(db.String(250),nullable=False)
    description = db.Column(db.String(500))
    year = db.Column(db.Integer,nullable=False)
    type = db.Column(db.SmallInteger,nullable=False)

    def __init__(self,user_id,research_title,description,year,type):
        self.user_id = user_id
        self.research_title = research_title
        self.description = description
        self.year = year
        self.type = type

class Jobs(db.Model):
    __tablename__ = 'jobs'
    id = db.Column(db.Integer,primary_key=True,autoincrement=True)
    name = db.Column(db.String(50),nullable=False,unique=True)

    def __init__(self, name):
        self.name = name

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

    def __init__(self, name):
        self.name = name

class UserSkills(db.Model):
    __tablename__ = 'user_skills'
    user_id = db.Column(db.Integer,db.ForeignKey('users.id',ondelete="CASCADE"),primary_key=True)
    skill_id = db.Column(db.Integer,db.ForeignKey('skills.id',ondelete="CASCADE"),primary_key=True)

    def __init__(self, user_id, skill_id):
        self.user_id = user_id
        self.skill_id = skill_id

class Notification(db.Model):
    __tablename__ = 'notifications'
    id = db.Column(db.Integer,primary_key=True,autoincrement=True)
    owner_id = db.Column(db.Integer,db.ForeignKey('users.id',ondelete="CASCADE"),primary_key=True)
    text = db.Column(db.String(500),nullable=False)
    link = db.Column(db.String(200),nullable=True)
    timestamp = db.Column(db.DateTime,default=db.func.now(),nullable=False)

    def __init__(self,owner_id,text,link):
        self.owner_id = owner_id
        self.text = text
        self.link = link

class NotificationRelatedUser(db.Model):
    __tablename__ = 'notification_related_users'
    notification_id = db.Column(db.Integer,db.ForeignKey('notifications.id',ondelete="CASCADE"),primary_key=True)
    related_user_id = db.Column(db.Integer,db.ForeignKey('users.id',ondelete="CASCADE"),primary_key=True)

    def __init__(self,notification_id,related_user_id):
        self.notification_id = notification_id
        self.related_user_id = related_user_id

class NotificationStatus(db.Model):
    __tablename__ = 'notification_status'
    owner_id = db.Column(db.Integer,db.ForeignKey('users.id',ondelete="CASCADE"),primary_key=True)
    is_email_allowed = db.Column(db.Boolean,nullable=False)
    is_notification_allowed = db.Column(db.Boolean,nullable=False)