import React, { useState } from "react";
import Button from "@material-ui/core/Button";
import Dialog from "@material-ui/core/Dialog";
import DialogActions from "@material-ui/core/DialogActions";
import DialogContent from "@material-ui/core/DialogContent";
import DialogContentText from "@material-ui/core/DialogContentText";
import DialogTitle from "@material-ui/core/DialogTitle";
import colors from "../../../../../utils/colors";
import { deleteIssue } from "../utils";
import Snackbar from "@material-ui/core/Snackbar";
import MuiAlert from "@material-ui/lab/Alert";

const DeleteModal = ({ closePopup, open, issue, loadIssues }) => {
  const [errorSnackbar, setErrorSnackbar] = useState(false);

  function Alert(props) {
    return <MuiAlert elevation={6} variant="filled" {...props} />;
  }

  const handleSubmit = () => {
    const { workspace_id: workspaceId, issue_id: issueId } = issue;

    deleteIssue({ workspaceId, issueId })
      .then((res) => {
        if (res.status === 200) {
          closePopup();
          loadIssues();
        } else {
          setErrorSnackbar(true);
        }
      })
      .catch(() => {
        setErrorSnackbar(true);
      });
  };

  return (
    <div>
      <Snackbar
        open={errorSnackbar}
        autoHideDuration={6000}
        onClose={() => setErrorSnackbar(false)}
      >
        <Alert onClose={() => setErrorSnackbar(false)} severity="error">
          Can't delete issue.
        </Alert>
      </Snackbar>
      <Dialog
        open={open}
        onClose={closePopup}
        aria-labelledby="alert-dialog-title"
        aria-describedby="alert-dialog-description"
      >
        <DialogTitle id="alert-dialog-title">{"Are you sure?"}</DialogTitle>
        <DialogContent>
          <DialogContentText id="alert-dialog-description">
            It will be deleted permanently!
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button
            style={{ color: colors.primary }}
            onClick={closePopup}
            color="primary"
          >
            Cancel
          </Button>
          <Button
            style={{
              color: colors.secondaryLight,
              backgroundColor: colors.quinary,
            }}
            onClick={handleSubmit}
            color="primary"
            autoFocus
          >
            Delete
          </Button>
        </DialogActions>
      </Dialog>
    </div>
  );
};

export default DeleteModal;
