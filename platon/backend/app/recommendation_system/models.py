from app import db

class FollowRecommendationItem(db.Model):
    __tablename__ = 'follow_recommendations'
    owner_id = db.Column(db.Integer,db.ForeignKey('users.id',ondelete="CASCADE"),primary_key=True)
    recommendation_id = db.Column(db.Integer,db.ForeignKey('users.id',ondelete="CASCADE"),primary_key=True)
    score = db.Column(db.Float,nullable=False)
    timestamp = db.Column(db.DateTime,default=db.func.now(),nullable=False)

    def __init__(self,owner_id,recommendation_id,score):
        self.owner_id = owner_id
        self.recommendation_id = recommendation_id
        self.score = score

class WorkspaceRecommendationItem(db.Model):
    __tablename__ = 'workspace_recommendations'
    owner_id = db.Column(db.Integer,db.ForeignKey('users.id',ondelete="CASCADE"),primary_key=True)
    recommendation_id = db.Column(db.Integer,db.ForeignKey('workspaces.id',ondelete="CASCADE"),primary_key=True)
    score = db.Column(db.Float,nullable=False)
    timestamp = db.Column(db.DateTime,default=db.func.now(),nullable=False)

    def __init__(self,owner_id,recommendation_id,score):
        self.owner_id = owner_id
        self.recommendation_id = recommendation_id
        self.score = score

class CollaboratorRecommendationItem(db.Model):
    __tablename__ = 'collaborator_recommendations'
    owner_id = db.Column(db.Integer,db.ForeignKey('workspaces.id',ondelete="CASCADE"),primary_key=True)
    recommendation_id = db.Column(db.Integer,db.ForeignKey('users.id',ondelete="CASCADE"),primary_key=True)
    score = db.Column(db.Float,nullable=False)
    timestamp = db.Column(db.DateTime,default=db.func.now(),nullable=False)

    def __init__(self,owner_id,recommendation_id,score):
        self.owner_id = owner_id
        self.recommendation_id = recommendation_id
        self.score = score