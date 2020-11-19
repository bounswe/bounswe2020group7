from app import db

class Follow(db.Model):
    __tablename__ = 'follows'
    follower_id = db.Column(db.Integer,db.ForeignKey('users.id',ondelete="CASCADE"),primary_key=True)
    following_id = db.Column(db.Integer,db.ForeignKey('users.id',ondelete="CASCADE"),primary_key=True)
    timestamp = db.Column(db.DateTime,default=db.func.now(),nullable=False)

class FollowRequests(db.Model):
    __tablename__ = 'follow_requests'
    follower_id = db.Column(db.Integer,db.ForeignKey('users.id',ondelete="CASCADE"),primary_key=True)
    following_id = db.Column(db.Integer,db.ForeignKey('users.id',ondelete="CASCADE"),primary_key=True)
    timestamp = db.Column(db.DateTime,default=db.func.now(),nullable=False)

class Comments(db.Model):
    id = db.Column(db.Integer,primary_key=True,autoincrement=True)
    owner_id = db.Column(db.Integer,db.ForeignKey('users.id',ondelete="CASCADE"))
    commented_user_id = db.Column(db.Integer,db.ForeignKey('users.id',ondelete="CASCADE"))
    timestamp = db.Column(db.DateTime,default=db.func.now(),nullable=False)
    rate = db.Column(db.SmallInteger,nullable=False)
    text = db.Column(db.String(300))