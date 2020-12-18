"""empty message

Revision ID: 9868dd7416dc
Revises: 62527399a925
Create Date: 2020-12-17 20:21:12.810573

"""
from alembic import op
import sqlalchemy as sa
from sqlalchemy.dialects import mysql

# revision identifiers, used by Alembic.
revision = '9868dd7416dc'
down_revision = '62527399a925'
branch_labels = None
depends_on = None


def upgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    op.alter_column('workspaces', 'max_collaborators',
               existing_type=mysql.SMALLINT(),
               nullable=True)
    op.drop_constraint('workspaces_ibfk_1', 'workspaces', type_='foreignkey')
    op.create_foreign_key(None, 'workspaces', 'users', ['creator_id'], ['id'])
    op.drop_column('workspaces', 'has_any_file')
    # ### end Alembic commands ###


def downgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    op.add_column('workspaces', sa.Column('has_any_file', mysql.TINYINT(display_width=1), autoincrement=False, nullable=True))
    op.drop_constraint(None, 'workspaces', type_='foreignkey')
    op.create_foreign_key('workspaces_ibfk_1', 'workspaces', 'users', ['creator_id'], ['id'], ondelete='CASCADE')
    op.alter_column('workspaces', 'max_collaborators',
               existing_type=mysql.SMALLINT(),
               nullable=False)
    # ### end Alembic commands ###