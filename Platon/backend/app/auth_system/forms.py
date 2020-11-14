from wtforms import Form, StringField, validators

class LoginForm(Form):
    e_mail = StringField("e_mail",validators=[validators.DataRequired()])
    password = StringField("password",validators=[validators.DataRequired()])