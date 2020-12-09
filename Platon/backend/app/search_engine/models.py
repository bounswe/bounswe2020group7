from app import db

class SearchHistoryItem(db.Model):
    __tablename__ = "search_history_item"
    id = db.Column(db.Integer,primary_key=True,autoincrement=True)
    user_id = db.Column(db.Integer,db.ForeignKey('users.id',ondelete="CASCADE"))
    query = db.Column(db.String(100),nullable=False)
    type = db.Column(db.SmallInteger,nullable=False)

    def __init__(self,user_id,query,type):
        self.user_id = user_id
        self.query = query
        self.type = type