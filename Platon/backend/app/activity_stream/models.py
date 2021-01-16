from app import db

class ActivityStreamItem(db.Model):
    __tablename__ = 'activity_stream'
    activity_id = db.Column(db.Integer,primary_key=True,autoincrement=True)
    activity_context_vocab = db.Column(db.String(200), nullable=False)
    activity_context_ext = db.Column(db.String(200), nullable=True)
    activity_summary = db.Column(db.String(200), nullable=False)
    activity_type = db.Column(db.String(50), nullable=False)
    activity_actor_type = db.Column(db.String(50), nullable=False)
    activity_actor_id = db.Column(db.Integer,db.ForeignKey('users.id',ondelete="CASCADE"), nullable=False)
    activity_actor_name = db.Column(db.String(50), nullable=False)
    activity_actor_image_url = db.Column(db.String(100), nullable=False)
    timestamp = db.Column(db.DateTime,default=db.func.now(),nullable=False)
    activity_object_type = db.Column(db.String(50), nullable=False)
    activity_object_id = db.Column(db.Integer, nullable=False)
    activity_object_name = db.Column(db.String(50), nullable=False)
    activity_object_image_url = db.Column(db.String(100), nullable=True)
    activity_object_content = db.Column(db.String(250), nullable=True)
    activity_object_rating_value = db.Column(db.Integer, nullable=True)
    activity_target_type = db.Column(db.String(50), nullable=True)
    activity_target_id = db.Column(db.Integer, nullable=True)
    activity_target_name = db.Column(db.String(50), nullable=True)
    activity_target_image_url = db.Column(db.String(100), nullable=True)
