import React, { useState, useEffect } from 'react'
import Button from '@material-ui/core/Button'
import Dialog from '@material-ui/core/Dialog'
import DialogActions from '@material-ui/core/DialogActions'
import DialogContent from '@material-ui/core/DialogContent'
import DialogContentText from '@material-ui/core/DialogContentText'
import DialogTitle from '@material-ui/core/DialogTitle'
import List from '@material-ui/core/List'
import ListItem from '@material-ui/core/ListItem'
import ListItemAvatar from '@material-ui/core/ListItemAvatar'
import ListItemSecondaryAction from '@material-ui/core/ListItemSecondaryAction'
import ListItemText from '@material-ui/core/ListItemText'
import Avatar from '@material-ui/core/Avatar'
import IconButton from '@material-ui/core/IconButton'
import WorkIcon from '@material-ui/icons/Work'
import AddCircleIcon from '@material-ui/icons/AddCircleOutlined'
import {
  getSelfWorkspaces,
  inviteUserToWorkspace, isInContributors,
} from './utils'
import Snackbar from '@material-ui/core/Snackbar'
import MuiAlert from '@material-ui/lab/Alert'

export default function InviteDialog({ user, closeDialog, open }) {
  const [workspaces, setWorkspaces] = useState([])
  const [alert, setAlert] = useState({
    open: false,
    message: '',
    severity: 'error',
  })

  useEffect(() => {
    getSelfWorkspaces().then((res) => {
      if (res.status === 200) {
        setWorkspaces(res.data.workspaces)
      } else {
        setWorkspaces([])
      }
    }).catch(() => {
      setWorkspaces([])
    })
  }, [])

  const inviteUser = (workspaceId) => {
    inviteUserToWorkspace({ workspaceId, inviteeId: user.id }).then((res) => {
      if (res.status === 200) {
        setAlert({ open: true, message: res.data.message, severity: 'success' })
      } else {
        setAlert({ open: true, message: res.data.message, severity: 'success' })
      }
    }).catch((error) => {
      setAlert({ open: true, message: error.response.data.error, severity: 'error' })
    })
  }

  const closeAlert = () => {
    setAlert({ open: false, message: '', severity: 'error' })
  }

  function Alert(props) {
    return <MuiAlert elevation={6} variant="filled" {...props} />
  }

  if (!user) return null

  return (
    <>
      <Snackbar
        open={alert.open}
        autoHideDuration={6000}
        onClose={closeAlert}
      >
        <Alert onClose={closeAlert} severity={alert.severity}>
          {alert.message}
        </Alert>
      </Snackbar>
      <Dialog open={open} onClose={closeDialog} aria-labelledby="form-dialog-title">
        <DialogTitle id="form-dialog-title">Invitation</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Please invite {user.name} to workspaces using plus button
          </DialogContentText>
          <div>
            <List>
              {workspaces.map((workspace) => (
                <ListItem>
                  <ListItemAvatar>
                    <Avatar>
                      <WorkIcon />
                    </Avatar>
                  </ListItemAvatar>
                  <ListItemText
                    primary={workspace.title}
                  />
                  {!isInContributors({ workspace, inviteeId: user.id }) &&
                   (<ListItemSecondaryAction>
                     <IconButton edge="end" onClick={() => inviteUser(workspace.id)}>
                       <AddCircleIcon />
                     </IconButton>
                   </ListItemSecondaryAction>)}
                </ListItem>
              ))}
            </List>
          </div>
        </DialogContent>
        <DialogActions>
          <Button onClick={closeDialog} color="primary">
            Done
          </Button>
        </DialogActions>
      </Dialog>
    </>
  )
}

