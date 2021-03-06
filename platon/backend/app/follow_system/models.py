from app import db

# e.g.User(follower_id) ----follows----> User(following_id)
class Follow(db.Model):
    __tablename__ = 'follows'
    follower_id = db.Column(db.Integer,db.ForeignKey('users.id',ondelete="CASCADE"),primary_key=True)
    following_id = db.Column(db.Integer,db.ForeignKey('users.id',ondelete="CASCADE"),primary_key=True)
    timestamp = db.Column(db.DateTime,default=db.func.now(),nullable=False)
    
    def __init__(self,follower_id,following_id):
        self.follower_id = follower_id
        self.following_id = following_id

class FollowRequests(db.Model):
    __tablename__ = 'follow_requests'
    follower_id = db.Column(db.Integer,db.ForeignKey('users.id',ondelete="CASCADE"),primary_key=True)
    following_id = db.Column(db.Integer,db.ForeignKey('users.id',ondelete="CASCADE"),primary_key=True)
    timestamp = db.Column(db.DateTime,default=db.func.now(),nullable=False)

    def __init__(self, follower_id, following_id):
        self.follower_id = follower_id
        self.following_id = following_id

class Comments(db.Model):
    id = db.Column(db.Integer,primary_key=True,autoincrement=True)
    owner_id = db.Column(db.Integer,db.ForeignKey('users.id',ondelete="CASCADE"))
    commented_user_id = db.Column(db.Integer,db.ForeignKey('users.id',ondelete="CASCADE"))
    timestamp = db.Column(db.DateTime,default=db.func.now(),nullable=False)
    rate = db.Column(db.SmallInteger,nullable=False)
    text = db.Column(db.String(300))
    
    def __init__(self, owner_id, commented_user_id, rate, text):
        self.owner_id = owner_id
        self.commented_user_id = commented_user_id
        self.rate = rate
        self.text = text

class Reports(db.Model):
    id = db.Column(db.Integer,primary_key=True,autoincrement=True)
    owner_id = db.Column(db.Integer,db.ForeignKey('users.id',ondelete="CASCADE"))
    reported_user_id = db.Column(db.Integer,db.ForeignKey('users.id',ondelete="CASCADE"))
    text = db.Column(db.String(300))

    def __init__(self, owner_id, reported_user_id, text):
        self.owner_id = owner_id
        self.reported_user_id = reported_user_id
        self.text = text