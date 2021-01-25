import React, { useState, useEffect } from "react";
import Button from "@material-ui/core/Button";
import Dialog from "@material-ui/core/Dialog";
import DialogActions from "@material-ui/core/DialogActions";
import DialogContent from "@material-ui/core/DialogContent";
import DialogContentText from "@material-ui/core/DialogContentText";
import DialogTitle from "@material-ui/core/DialogTitle";
import TextField from '@material-ui/core/TextField'
import { reportUserPost } from "./utils";
import Snackbar from "@material-ui/core/Snackbar";
import MuiAlert from "@material-ui/lab/Alert";

export default function ReportDialog({ user, closeDialog, open }) {
  const [text, setText] = useState("");
  const [alert, setAlert] = useState({
    open: false,
    message: "",
    severity: "error",
  });

  useEffect(() => {}, []);

  const reportUser = () => {
    reportUserPost({ reported_user_id: user.id, text: text })
      .then((res) => {
        console.log(res)
        if (res.status === 200) {
          setAlert({
            open: true,
            message: res.data.message,
            severity: "success",
          });
        } else {
          setAlert({
            open: true,
            message: res.data.message,
            severity: "success",
          });
        }
      })
      .catch((error) => {
        setAlert({
          open: true,
          message: error.response.data.error,
          severity: "error",
        });
      });
  };

  const closeAlert = () => {
    setAlert({ open: false, message: "", severity: "error" });
  };

  function Alert(props) {
    return <MuiAlert elevation={6} variant="filled" {...props} />;
  }

  const updateText = (event) => {
    setText(event.target.value);
  };

  if (!user) return null;

  return (
    <>
      <Snackbar open={alert.open} autoHideDuration={6000} onClose={closeAlert}>
        <Alert onClose={closeAlert} severity={alert.severity}>
          {alert.message}
        </Alert>
      </Snackbar>
      <Dialog
        open={open}
        onClose={closeDialog}
        aria-labelledby="form-dialog-title"
      >
        <DialogTitle id="form-dialog-title">Report</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Please write a reson why you report {user.name}
          </DialogContentText>
          <TextField
            autoFocus
            margin="dense"
            id="reportUser"
            label="Report Reason"
            type="text"
            onChange={updateText}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={reportUser} color="primary">
            Report
          </Button>
          <Button onClick={closeDialog} color="primary">
            Exit
          </Button>
        </DialogActions>
      </Dialog>
    </>
  );
}
