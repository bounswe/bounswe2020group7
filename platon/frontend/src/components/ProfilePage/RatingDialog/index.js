import React, { useState } from 'react'
import Button from '@material-ui/core/Button'
import Dialog from '@material-ui/core/Dialog'
import DialogActions from '@material-ui/core/DialogActions'
import DialogContent from '@material-ui/core/DialogContent'
import DialogTitle from '@material-ui/core/DialogTitle'
import colors from '../../../utils/colors'
import Snackbar from '@material-ui/core/Snackbar'
import MuiAlert from '@material-ui/lab/Alert'
import { commentUser } from './utils'
import TextField from '@material-ui/core/TextField'
import Rating from '@material-ui/lab/Rating'


const SNACKBAR_INITIAL_STATE = {
  severity: 'success',
  open: false,
  message: '',
}

const RatingDialogue = ({ closePopup, open, user, reloadProfile }) => {
  const [snackbar, setSnackbar] = useState(SNACKBAR_INITIAL_STATE)
  const [commentText, setCommentText] = useState('')
  const [rating, setRating] = useState(null)

  function Alert(props) {
    return <MuiAlert elevation={6} variant="filled" {...props} />
  }

  const handleSubmit = () => {
    commentUser({ userId: user.id, commentText, rating }).then((res) => {
      if (res.status === 201) {
        setSnackbar({
          open: true,
          severity: 'success',
          message: 'Comment submitted.',
        })
        closePopup()
      } else {
        setSnackbar({
          open: true,
          severity: 'error',
          message: 'Something went wrong.',
        })
      }
    }).catch((error) => {
      setSnackbar({
        open: true,
        severity: 'error',
        message: error.response.data.error,
      })
    })
  }

  return (
    <div> {
      snackbar?.open && (<Snackbar
        open={snackbar}
        autoHideDuration={6000}
        onClose={() => setSnackbar()}
      >
        <Alert onClose={() => setSnackbar(SNACKBAR_INITIAL_STATE)} severity={snackbar?.severity}>
          {snackbar?.message}
        </Alert>
      </Snackbar>)
    }
      <Dialog
        open={open}
        onClose={closePopup}
        aria-labelledby="alert-dialog-title"
        aria-describedby="alert-dialog-description"
        fullWidth
        maxWidth={'sm'}
      >
        <DialogTitle id="alert-dialog-title">Comment & Rate User</DialogTitle>
        <DialogContent>
          <Rating
            name="half-rating-read"
            value={rating}
            onChange={(event, newValue) => {
              setRating(newValue)
            }}
            precision={1}
            size="large"
            style={{ display: 'block' }}
          />
          <TextField
            autoFocus
            margin="dense"
            id="comment"
            fullWidth
            label="Comment (optional)"
            type="text"
            onChange={(e) => setCommentText(e.target.value)}
            value={commentText}
          />
        </DialogContent>
        <DialogActions>
          <Button style={{ color: colors.primary }} onClick={closePopup} color="primary">
            Cancel
          </Button>
          <Button
            style={{ color: colors.secondaryLight, backgroundColor: colors.quinary }}
            onClick={handleSubmit}
            color="primary"
            autoFocus
          >
            Submit
          </Button>
        </DialogActions>
      </Dialog>
    </div>
  )
}

export default RatingDialogue
