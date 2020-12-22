from app import db

class Workspace(db.Model):
    __tablename__ = "workspaces"
    id = db.Column(db.Integer,primary_key=True,autoincrement=True)
    creator_id = db.Column(db.Integer,db.ForeignKey('users.id', ondelete="SET NULL"))
    is_private = db.Column(db.Boolean,default=False,nullable=False)
    title = db.Column(db.String(256),nullable=False)
    state = db.Column(db.SmallInteger,default=0,nullable=False)
    timestamp = db.Column(db.DateTime,default=db.func.now(),nullable=False)
    description = db.Column(db.String(2000))
    deadline = db.Column(db.DateTime,nullable=True)
    max_collaborators = db.Column(db.SmallInteger,default=10)
    trending_score = db.Column(db.Float, default = 0.0)
    view_count = db.Column(db.Integer,default=0)

class WorkspaceSkill(db.Model):
    __tablename__ = "workspace_skills"
    workspace_id = db.Column(db.Integer,db.ForeignKey('workspaces.id',ondelete="CASCADE"),primary_key=True)
    skill_id = db.Column(db.Integer,db.ForeignKey('skills.id',ondelete="CASCADE"),primary_key=True)

class WorkspaceRequirement(db.Model):
    __tablename__ = "workspace_requirements"
    workspace_id = db.Column(db.Integer,db.ForeignKey('workspaces.id',ondelete="CASCADE"),primary_key=True)
    requirement_id = db.Column(db.Integer,db.ForeignKey('requirements.id',ondelete="CASCADE"),primary_key=True)

class Contribution(db.Model):
    __tablename__ = "contributions"
    workspace_id = db.Column(db.Integer,db.ForeignKey('workspaces.id', ondelete="CASCADE"),primary_key=True)
    user_id = db.Column(db.Integer,db.ForeignKey('users.id',ondelete="CASCADE"),primary_key=True)
    is_active = db.Column(db.Boolean,nullable=False)

class Collaboration(db.Model):
    __tablename__ = "collaborations"
    user_1_id = db.Column(db.Integer,db.ForeignKey('users.id',ondelete="CASCADE"),primary_key=True)
    user_2_id = db.Column(db.Integer,db.ForeignKey('users.id',ondelete="CASCADE"),primary_key=True)

class CollaborationInvitation(db.Model):
    __tablename__ = "collaboration_invitations"
    workspace_id = db.Column(db.Integer,db.ForeignKey('workspaces.id',ondelete="CASCADE"),primary_key=True)
    invitee_id = db.Column(db.Integer,db.ForeignKey('users.id',ondelete="CASCADE"),primary_key=True)
    invitor_id = db.Column(db.Integer,db.ForeignKey('users.id',ondelete="CASCADE"))
    is_accepted = db.Column(db.Boolean)

class CollaborationApplication(db.Model):
    __tablename__ = "collaboration_applications"
    workspace_id = db.Column(db.Integer,db.ForeignKey('workspaces.id',ondelete="CASCADE"),primary_key=True)
    applicant_id = db.Column(db.Integer,db.ForeignKey('users.id',ondelete="CASCADE"),primary_key=True)
    is_accepted = db.Column(db.Boolean)

class Milestone(db.Model):
    __tablename__ = "milestones"
    id = db.Column(db.Integer,primary_key=True,autoincrement=True)
    creator_id = db.Column(db.Integer,db.ForeignKey('users.id'))
    workspace_id = db.Column(db.Integer,db.ForeignKey('workspaces.id',ondelete="CASCADE"))
    title = db.Column(db.String(100),nullable=False)
    description = db.Column(db.String(500),nullable=False)
    deadline = db.Column(db.DateTime, nullable=False)

    def __init__(self, creator_id_, workspace_id_, title_, description_, deadline_):
        self.creator_id = creator_id_
        self.workspace_id = workspace_id_
        self.title = title_
        self.description = description_
        self.deadline = deadline_

class Issue(db.Model):
    __tablename__ = "issues"
    id = db.Column(db.Integer,primary_key=True,autoincrement=True)
    creator_id = db.Column(db.Integer,db.ForeignKey('users.id'))
    workspace_id = db.Column(db.Integer,db.ForeignKey('workspaces.id',ondelete="CASCADE"))
    title = db.Column(db.String(100),nullable=False)
    description = db.Column(db.String(500),nullable=False)
    deadline = db.Column(db.DateTime)
    is_open = db.Column(db.Boolean)

    def __init__(self, creator_id_, workspace_id_, title_, description_, deadline_=None, is_open_=True):
        self.creator_id = creator_id_
        self.workspace_id = workspace_id_
        self.title = title_
        self.description = description_
        self.deadline = deadline_
        self.is_open = is_open_

class IssueAssignee(db.Model):
    __tablename__ = "issue_assignees"
    issue_id = db.Column(db.Integer,db.ForeignKey('issues.id',ondelete="CASCADE"),primary_key=True)
    assignee_id = db.Column(db.Integer,db.ForeignKey('users.id',ondelete="CASCADE"),primary_key=True)

    def __init__(self, issue_id_, assignee_id_):
        self.issue_id = issue_id_
        self.assignee_id = assignee_id_

class IssueComment(db.Model):
    __tablename__ = "issue_comments"
    id = db.Column(db.Integer,primary_key=True,autoincrement=True)
    issue_id = db.Column(db.Integer,db.ForeignKey('issues.id',ondelete="CASCADE"))
    commenter_id = db.Column(db.Integer,db.ForeignKey('users.id'))
    comment = db.Column(db.String(512))

    def __init__(self, issue_id_, commenter_id_, comment_):
        self.issue_id = issue_id_
        self.commenter_id = commenter_id_
        self.comment = comment_

class Requirement(db.Model):
    __tablename__ = "requirements"
    id = db.Column(db.Integer,primary_key=True,autoincrement=True)
    text = db.Column(db.String(200))

