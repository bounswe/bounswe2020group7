from app import db

class UpcomingEvent(db.Model):
    __tablename__ = "upcoming_events"
    id = db.Column(db.Integer,primary_key=True,autoincrement=True)
    title = db.Column(db.String(200),nullable=False)
    acronym = db.Column(db.String(30),nullable=False)
    location = db.Column(db.String(100))
    date = db.Column(db.String(100))
    deadline = db.Column(db.String(100))
    link = db.Column(db.String(150))

    def __init__(self,title,acronym,location,date,deadline,link):
        self.title = title
        self.acronym = acronym
        self.location = location
        self.date = date
        self.deadline = deadline
        self.link = link