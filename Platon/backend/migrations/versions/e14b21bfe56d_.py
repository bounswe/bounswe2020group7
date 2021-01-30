"""empty message

Revision ID: e14b21bfe56d
Revises: 3c39990a78d6
Create Date: 2021-01-21 19:15:21.772944

"""
from alembic import op
import sqlalchemy as sa
from sqlalchemy.dialects import mysql

# revision identifiers, used by Alembic.
revision = 'e14b21bfe56d'
down_revision = '3c39990a78d6'
branch_labels = None
depends_on = None


def upgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    op.alter_column('activity_stream', 'activity_object_id',
               existing_type=mysql.INTEGER(),
               nullable=True)
    # ### end Alembic commands ###


def downgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    op.alter_column('activity_stream', 'activity_object_id',
               existing_type=mysql.INTEGER(),
               nullable=False)
    # ### end Alembic commands ###
