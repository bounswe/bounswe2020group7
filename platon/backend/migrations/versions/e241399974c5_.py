"""empty message

Revision ID: e241399974c5
Revises: e14b21bfe56d
Create Date: 2021-01-21 21:03:13.402858

"""
from alembic import op
import sqlalchemy as sa
from sqlalchemy.dialects import mysql

# revision identifiers, used by Alembic.
revision = 'e241399974c5'
down_revision = 'e14b21bfe56d'
branch_labels = None
depends_on = None


def upgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    op.alter_column('activity_stream', 'activity_object_name',
               existing_type=mysql.VARCHAR(length=50),
               nullable=True)
    op.alter_column('activity_stream', 'activity_object_type',
               existing_type=mysql.VARCHAR(length=50),
               nullable=True)
    # ### end Alembic commands ###


def downgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    op.alter_column('activity_stream', 'activity_object_type',
               existing_type=mysql.VARCHAR(length=50),
               nullable=False)
    op.alter_column('activity_stream', 'activity_object_name',
               existing_type=mysql.VARCHAR(length=50),
               nullable=False)
    # ### end Alembic commands ###
