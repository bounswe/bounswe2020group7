from app import db

class Follow(db.Model):
    __tablename__ = 'follows'
    follower_id = db.Column(db.Integer,db.ForeignKey('users.id',ondelete="CASCADE"),primary_key=True)
    following_id = db.Column(db.Integer,db.ForeignKey('users.id',ondelete="CASCADE"),primary_key=True)
    timestamp = db.Column(db.DateTime,default=db.func.now(),nullable=False)

class FollowRequests(db.Model):
    __tablename__ = 'followrequests'
    follower_id = db.Column(db.Integer,db.ForeignKey('users.id',ondelete="CASCADE"),primary_key=True)
    following_id = db.Column(db.Integer,db.ForeignKey('users.id',ondelete="CASCADE"),primary_key=True)
    timestamp = db.Column(db.DateTime,default=db.func.now(),nullable=False)