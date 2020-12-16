from app import db

class Workspace(db.Model):
    __tablename__ = "workspaces"
    id = db.Column(db.Integer,primary_key=True,autoincrement=True)
    creator_id = db.Column(db.Integer,db.ForeignKey('users.id'))
    is_private = db.Column(db.Boolean,nullable=False)
    title = db.Column(db.String(256),nullable=False)
    state = db.Column(db.SmallInteger,nullable=False)
    timestamp = db.Column(db.DateTime,default=db.func.now(),nullable=False)
    description = db.Column(db.String(2000))
    deadline = db.Column(db.DateTime)
    max_collaborators = db.Column(db.SmallInteger,default=10)
    has_any_file = db.Column(db.Boolean)
    trending_score = db.Column(db.Float, default = 0.0)
    view_count = db.Column(db.Integer,default=0)

class WorkspaceSkills(db.Model):
    __tablename__ = "workspace_skills"
    workspace_id = db.Column(db.Integer,db.ForeignKey('workspaces.id',ondelete="CASCADE"),primary_key=True)
    skill_id = db.Column(db.Integer,db.ForeignKey('skills.id',ondelete="CASCADE"),primary_key=True)

class WorkspaceRequirements(db.Model):
    __tablename__ = "workspace_requirements"
    workspace_id = db.Column(db.Integer,db.ForeignKey('workspaces.id',ondelete="CASCADE"),primary_key=True)
    requirements = db.Column(db.String(200))

class Contributions(db.Model):
    __tablename__ = "contributions"
    workspace_id = db.Column(db.Integer,db.ForeignKey('workspaces.id'),primary_key=True)
    user_id = db.Column(db.Integer,db.ForeignKey('users.id',ondelete="CASCADE"),primary_key=True)
    is_active = db.Column(db.Boolean,nullable=False)

class CollaborationInvitations(db.Model):
    __tablename__ = "collaboration_invitations"
    workspace_id = db.Column(db.Integer,db.ForeignKey('workspaces.id',ondelete="CASCADE"),primary_key=True)
    invitee_id = db.Column(db.Integer,db.ForeignKey('users.id',ondelete="CASCADE"),primary_key=True)
    invitor_id = db.Column(db.Integer,db.ForeignKey('users.id',ondelete="CASCADE"))

class CollaborationApplications(db.Model):
    __tablename__ = "collaboration_applications"
    workspace_id = db.Column(db.Integer,db.ForeignKey('workspaces.id',ondelete="CASCADE"),primary_key=True)
    applicant_id = db.Column(db.Integer,db.ForeignKey('users.id',ondelete="CASCADE"),primary_key=True)

class Milestones(db.Model):
    __tablename__ = "milestones"
    id = db.Column(db.Integer,primary_key=True,autoincrement=True)
    creator_id = db.Column(db.Integer,db.ForeignKey('users.id'))
    workspace_id = db.Column(db.Integer,db.ForeignKey('workspaces.id',ondelete="CASCADE"))
    title = db.Column(db.String(100),nullable=False)
    description = db.Column(db.String(500),nullable=False)
    deadline = db.Column(db.DateTime, nullable=False)

class Issues(db.Model):
    __tablename__ = "issues"
    id = db.Column(db.Integer,primary_key=True,autoincrement=True)
    creator_id = db.Column(db.Integer,db.ForeignKey('users.id'))
    workspace_id = db.Column(db.Integer,db.ForeignKey('workspaces.id',ondelete="CASCADE"))
    title = db.Column(db.String(100),nullable=False)
    description = db.Column(db.String(500),nullable=False)
    deadline = db.Column(db.DateTime)
    is_open = db.Column(db.Boolean)

class IssueAssignees(db.Model):
    __tablename__ = "issue_assignees"
    issue_id = db.Column(db.Integer,db.ForeignKey('issues.id',ondelete="CASCADE"),primary_key=True)
    assignee_id = db.Column(db.Integer,db.ForeignKey('users.id',ondelete="CASCADE"),primary_key=True)

class IssueComments(db.Model):
    __tablename__ = "issue_comments"
    id = db.Column(db.Integer,primary_key=True,autoincrement=True)
    issue_id = db.Column(db.Integer,db.ForeignKey('issues.id',ondelete="CASCADE"))
    commenter_id = db.Column(db.Integer,db.ForeignKey('users.id'))
    comment = db.Column(db.String(512))