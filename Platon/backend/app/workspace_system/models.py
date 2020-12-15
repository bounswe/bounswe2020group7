from app import db

class Workspace(db.Model):
    __tablename__ = "workspaces"
    id = db.Column(db.Integer,primary_key=True,autoincrement=True)
    creator_id = db.Column(db.Integer,db.ForeignKey('users.id',ondelete="CASCADE"))
    is_private = db.Column(db.Boolean,nullable=False)
    title = db.Column(db.String(256),nullable=False)
    state = db.Column(db.SmallInteger,nullable=False)
    timestamp = db.Column(db.DateTime,default=db.func.now(),nullable=False)
    description = db.Column(db.String(2000))
    deadline = db.Column(db.DateTime)
    max_collaborators = db.Column(db.SmallInteger,nullable=False)
    has_any_file = db.Column(db.Boolean)
    trending_score = db.Column(db.Float, default = 0.0)
    view_count = db.Column(db.Integer,default=0)