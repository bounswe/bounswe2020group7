import React, { useEffect, useState } from 'react'
import Button from '@material-ui/core/Button'
import TextField from '@material-ui/core/TextField'
import Dialog from '@material-ui/core/Dialog'
import DialogActions from '@material-ui/core/DialogActions'
import DialogContent from '@material-ui/core/DialogContent'
import DialogTitle from '@material-ui/core/DialogTitle'
import { createMilestone, updateMilestone } from '../utils'
import { format } from 'date-fns'
import Snackbar from '@material-ui/core/Snackbar'
import MuiAlert from '@material-ui/lab/Alert'

const CreateUpdateModal = ({ isCreate, open, closePopup, workspaceId, currentMilestone, loadMilestones }) => {
  const [milestone, setMilestone] = useState({})
  const [errorSnackbar, setErrorSnackbar] = useState(false)
  const [errorText, setErrorText] = useState('')

  function Alert(props) {
    return <MuiAlert elevation={6} variant="filled" {...props} />
  }

  useEffect(() => {
    if (currentMilestone) {
      setMilestone({
        ...currentMilestone,
        deadline: format(new Date(currentMilestone.deadline), 'yyyy-MM-dd\'T\'HH:mm'),
      })
    }
  }, [currentMilestone])

  const handleSubmit = () => {
    const dateObject = new Date(milestone.deadline)
    let formattedDateString
    try {
      formattedDateString = format(dateObject, 'yyyy-MM-dd HH:mm:ss')
    } catch {
      setErrorText('Please select a date.')
      setErrorSnackbar(true)
      return
    }

    if (isCreate) {
      createMilestone({
        workspaceId,
        title: milestone.title,
        description: milestone.description,
        deadline: formattedDateString,
      }).then((res) => {
        if (res.status === 200) {
          loadMilestones()
          closePopup()
        }
        else {
          setErrorText("Couldn't create milestone.")
          setErrorSnackbar(true)
        }
      }).catch((e) => {
        setErrorText("Couldn't create milestone.")
        setErrorSnackbar(true)
      })
    } else {
      updateMilestone({
        milestoneId: milestone.milestone_id,
        workspaceId,
        title: milestone.title,
        description: milestone.description,
        deadline: formattedDateString,
      }).then((res) => {
        if (res.status === 200) {
          loadMilestones()
          closePopup()
        }
        else {
          setErrorText("Couldn't update milestone.")
          setErrorSnackbar(true)
        }
      }).catch((e) => {
        setErrorText("Couldn't update milestone.")
        setErrorSnackbar(true)
      })
    }
  }

  const updateTitle = (event) => {
    setMilestone({ ...milestone, title: event.target.value })
  }

  const updateDescription = (event) => {
    setMilestone({ ...milestone, description: event.target.value })
  }

  const updateDeadline = (event) => {
    setMilestone({ ...milestone, deadline: event.target.value })
  }

  return (
    <>
      <Snackbar
        open={errorSnackbar}
        autoHideDuration={6000}
        onClose={() => setErrorSnackbar(false)}
      >
        <Alert onClose={() => setErrorSnackbar(false)} severity="error">
          {errorText}
        </Alert>
      </Snackbar>
      <Dialog open={open} onClose={closePopup} aria-labelledby="form-dialog-title">
        <DialogTitle id="form-dialog-title">
          {isCreate ? 'Create Milestone' : 'Update Milestone'}
        </DialogTitle>
        <DialogContent>
          <TextField
            autoFocus
            margin="dense"
            id="title"
            label="Milestone Title"
            type="text"
            onChange={updateTitle}
            value={milestone && milestone.title}
          />
          <TextField
            autoFocus
            margin="dense"
            id="description"
            label="Milestone Description"
            type="text"
            fullWidth
            onChange={updateDescription}
            value={milestone && milestone.description}
          />
          <TextField
            id="datetime-local"
            label="Next appointment"
            type="datetime-local"
            // defaultValue="2017-05-24T10:30"
            value={milestone && milestone.deadline}
            onChange={updateDeadline}
            InputLabelProps={{
              shrink: true,
            }}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={closePopup} color="primary">
            Cancel
          </Button>
          <Button onClick={handleSubmit} color="primary">
            Submit
          </Button>
        </DialogActions>
      </Dialog>
    </>
  )
}

export default CreateUpdateModal
