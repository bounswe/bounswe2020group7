import React, { useState, useEffect } from 'react'
import { withStyles } from '@material-ui/core/styles'
import colors from '../../../../utils/colors'
import List from '@material-ui/core/List'
import ListItem from '@material-ui/core/ListItem'
import { getIssues } from './utils'
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


const IssueSection = ({ classes, workspaceId }) => {
  const [issues, setIssues] = useState([])
  const [editOpen, setEditOpen] = useState(false)
  const [createOpen, setCreateOpen] = useState(false)
  const [currentIssue, setCurrentIssue] = useState(null)
  const [deleteOpen, setDeleteOpen] = useState(false)

  useEffect(() => {
    loadIssues()
  }, [workspaceId])

  const openEditPopup = (ms) => {
    setCurrentIssue(ms)
    setEditOpen(true)
  }

  const openDeletePopup = (ms) => {
    setCurrentIssue(ms)
    setDeleteOpen(true)
  }


  const loadIssues = () => {
    return getIssues({ workspaceId })
      .then((res) => {
        if (res.status === 200) {
          setIssues(res.data.result.sort((a, b) => {
            return getTime(new Date(a.deadline)) - getTime(new Date(b.deadline))
          }))
        }
      })
  }

  return (
    <div className={classes.root}>
      <div className={classes.demo}>
        <List>
          {issues.map((issue) => (
            <ListItem>
              <ListItemAvatar>
                <Avatar style={{ backgroundColor: colors.primary }}>
                  <DoneOutline style={{ color: colors.tertiary }} />
                </Avatar>
              </ListItemAvatar>
              <ListItemText
                primary={issue && issue.title}
                secondary={(<div
                  style={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    paddingRight: '40px',
                  }}
                >
                  <div style={{ wordBreak: 'break-word', marginRight: '16px' }}>{issue &&
                                                            issue.description}</div>
                  <div>{issue && issue.deadline}</div>
                </div>)}
              />
              <ListItemSecondaryAction>
                <IconButton edge="end">
                  <EditIcon
                    style={{ color: colors.quaternary }}
                    onClick={() => openEditPopup(issue)}
                  />
                  {/*component*/}
                </IconButton>
                <IconButton edge="end">
                  <DeleteIcon
                    style={{ color: colors.quinary }}
                    onClick={() => openDeletePopup(issue)}
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
              Create Issue
            </StyledButton>
          </div>
        </div>
        <CreateUpdateModal
          open={editOpen}
          isCreate={false}
          workspaceId={workspaceId}
          closePopup={() => setEditOpen(false)}
          currentIssue={currentIssue}
          loadIssues={() => loadIssues()}
        />
        <CreateUpdateModal
          open={createOpen}
          isCreate
          workspaceId={workspaceId}
          closePopup={() => setCreateOpen(false)}
          loadIssues={() => loadIssues()}
        />
        <DeleteModal
          closePopup={() => setDeleteOpen(false)}
          issue={currentIssue}
          open={deleteOpen}
          loadIssues={() => loadIssues()}
        />
      </div>
    </div>
  )
}


export default withStyles(useStyles)(IssueSection)
