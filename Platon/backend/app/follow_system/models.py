from app import db

# e.g.User(follower_id) ----follows----> User(following_id)
class Follow(db.Model):
    __tablename__ = 'follows'
    follower_id = db.Column(db.Integer,db.ForeignKey('users.id',ondelete="CASCADE"),primary_key=True)
    following_id = db.Column(db.Integer,db.ForeignKey('users.id',ondelete="CASCADE"),primary_key=True)
    timestamp = db.Column(db.DateTime,default=db.func.now(),nullable=False)

    def __init__(self, follower_id, following_id):
        self.follower_id = follower_id
        self.following_id = following_id

# e.g.User(follower_id) ----sent Follow Request----> User(following_id)
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
