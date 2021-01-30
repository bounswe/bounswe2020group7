import React, { useState, useEffect } from 'react'
import { withStyles } from '@material-ui/core/styles'
import colors from '../../../../utils/colors'
import List from '@material-ui/core/List'
import ListItem from '@material-ui/core/ListItem'
import { getMilestones } from './utils'
import ListItemAvatar from '@material-ui/core/ListItemAvatar'
import Avatar from '@material-ui/core/Avatar'
import DoneOutline from '@material-ui/icons/DoneOutline'
import ListItemText from '@material-ui/core/ListItemText'
import ListItemSecondaryAction from '@material-ui/core/ListItemSecondaryAction'
import IconButton from '@material-ui/core/IconButton'
import EditIcon from '@material-ui/icons/Edit'
import DeleteIcon from '@material-ui/icons/Delete'
import CreateUpdateModal from './modals/createupdate'
import Button from '@material-ui/core/Button'
import DeleteModal from './modals/delete'
import { getTime } from 'date-fns'

const useStyles = (theme) => ({
  root: {
    width: '620px',
    marginBottom: theme.spacing(3),
  },
  demo: {
    backgroundColor: colors.secondary,
  },
  title: {
    margin: theme.spacing(4, 0, 2),
  },
})

const StyledButton = withStyles({
  root: {
    background: colors.tertiary,
    color: colors.secondary,

    '&:hover': {
      backgroundColor: colors.tertiaryDark,
    },
  },
})(Button)


const MilestoneSection = ({ classes, workspaceId }) => {
  const [milestones, setMilestones] = useState([])
  const [editOpen, setEditOpen] = useState(false)
  const [createOpen, setCreateOpen] = useState(false)
  const [currentMilestone, setCurrentMilestone] = useState(null)
  const [deleteOpen, setDeleteOpen] = useState(false)

  useEffect(() => {
    loadMilestones()
  }, [workspaceId])

  const openEditPopup = (ms) => {
    setCurrentMilestone(ms)
    setEditOpen(true)
  }

  const openDeletePopup = (ms) => {
    setCurrentMilestone(ms)
    setDeleteOpen(true)
  }


  const loadMilestones = () => {
    return getMilestones({ workspaceId })
      .then((res) => {
        if (res.status === 200) {
          setMilestones(res.data.result.sort((a, b) => {
            return getTime(new Date(a.deadline)) - getTime(new Date(b.deadline))
          }))
        }
      })
  }

  return (
    <div className={classes.root}>
      <div className={classes.demo}>
        <List>
          {milestones.map((milestone) => (
            <ListItem>
              <ListItemAvatar>
                <Avatar style={{ backgroundColor: colors.primary }}>
                  <DoneOutline style={{ color: colors.tertiary }} />
                </Avatar>
              </ListItemAvatar>
              <ListItemText
                primary={milestone && milestone.title}
                secondary={(<div
                  style={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    paddingRight: '40px',
                  }}
                >
                  <div style={{ wordBreak: 'break-word', marginRight: '16px' }}>{milestone &&
                                                            milestone.description}</div>
                  <div>{milestone && milestone.deadline}</div>
                </div>)}
              />
              <ListItemSecondaryAction>
                <IconButton edge="end">
                  <EditIcon
                    style={{ color: colors.quaternary }}
                    onClick={() => openEditPopup(milestone)}
                  />
                  {/*component*/}
                </IconButton>
                <IconButton edge="end">
                  <DeleteIcon
                    style={{ color: colors.quinary }}
                    onClick={() => openDeletePopup(milestone)}
                  />
                  {/*component*/}
                </IconButton>
              </ListItemSecondaryAction>
            </ListItem>))}
        </List>
        <div>
          <div
            style={{
              display: 'flex',
              justifyContent: 'center',
              alignItems: 'center',
            }}
          >
            <StyledButton onClick={() => setCreateOpen(true)}>
              Create Milestone
            </StyledButton>
          </div>
        </div>
        <CreateUpdateModal
          open={editOpen}
          isCreate={false}
          workspaceId={workspaceId}
          closePopup={() => setEditOpen(false)}
          currentMilestone={currentMilestone}
          loadMilestones={() => loadMilestones()}
        />
        <CreateUpdateModal
          open={createOpen}
          isCreate
          workspaceId={workspaceId}
          closePopup={() => setCreateOpen(false)}
          loadMilestones={() => loadMilestones()}
        />
        <DeleteModal
          closePopup={() => setDeleteOpen(false)}
          milestone={currentMilestone}
          open={deleteOpen}
          loadMilestones={() => loadMilestones()}
        />
      </div>
    </div>
  )
}


export default withStyles(useStyles)(MilestoneSection)
