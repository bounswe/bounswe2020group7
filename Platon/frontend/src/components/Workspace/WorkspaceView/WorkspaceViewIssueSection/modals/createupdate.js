import React, { useEffect, useState } from 'react'
import Button from '@material-ui/core/Button'
import TextField from '@material-ui/core/TextField'
import Dialog from '@material-ui/core/Dialog'
import DialogActions from '@material-ui/core/DialogActions'
import DialogContent from '@material-ui/core/DialogContent'
import DialogTitle from '@material-ui/core/DialogTitle'
import { createIssue, updateIssue} from '../utils'
import { format } from 'date-fns'
import Snackbar from '@material-ui/core/Snackbar'
import MuiAlert from '@material-ui/lab/Alert'

const CreateUpdateModal = ({ isCreate, open, closePopup, workspaceId, currentIssue, loadIssues }) => {
  const [issue, setIssue] = useState({})
  const [errorSnackbar, setErrorSnackbar] = useState(false)
  const [errorText, setErrorText] = useState('')

  function Alert(props) {
    return <MuiAlert elevation={6} variant="filled" {...props} />
  }

  useEffect(() => {
    if (currentIssue) {
      setIssue({
        ...currentIssue,
        deadline: currentIssue.deadline ? format(new Date(currentIssue.deadline), 'yyyy-MM-dd\'T\'HH:mm') : "",
      })
    }
  }, [currentIssue])

  const handleSubmit = () => {
    const dateObject = new Date(issue.deadline)
    let formattedDateString
    if(issue.deadline)
      formattedDateString = format(dateObject, 'yyyy-MM-dd HH:mm:ss')

    if (isCreate) {
      createIssue({
        workspaceId,
        title: issue.title,
        description: issue.description,
        deadline: formattedDateString,
      }).then((res) => {
        if (res.status === 200) {
          setIssue({})
          loadIssues()
          closePopup()
        }
        else {
          setErrorText("Couldn't create issue.")
          setErrorSnackbar(true)
        }
      }).catch((e) => {
        setErrorText("Couldn't create issue.")
        setErrorSnackbar(true)
      })
    } else {
      updateIssue({
        issueId: issue.issue_id,
        workspaceId,
        title: issue.title,
        description: issue.description,
        deadline: formattedDateString,
      }).then((res) => {
        if (res.status === 200) {
          loadIssues()
          closePopup()
        }
        else {
          setErrorText("Couldn't update issue.")
          setErrorSnackbar(true)
        }
      }).catch((e) => {
        setErrorText("Couldn't update issue.")
        setErrorSnackbar(true)
      })
    }
  }

  const updateTitle = (event) => {
    setIssue({ ...issue, title: event.target.value })
  }

  const updateDescription = (event) => {
    setIssue({ ...issue, description: event.target.value })
  }

  const updateDeadline = (event) => {
    setIssue({ ...issue, deadline: event.target.value })
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
          {isCreate ? 'Create Issue' : 'Update Issue'}
        </DialogTitle>
        <DialogContent>
          <TextField
            autoFocus
            margin="dense"
            id="title"
            label="Issue Title"
            type="text"
            onChange={updateTitle}
            value={issue && issue.title}
          />
          <TextField
            autoFocus
            margin="dense"
            id="description"
            label="Issue Description"
            type="text"
            fullWidth
            onChange={updateDescription}
            value={issue && issue.description}
          />
          <TextField
            id="datetime-local"
            label="Deadline"
            type="datetime-local"
            // defaultValue="2017-05-24T10:30"
            value={issue && issue.deadline}
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
