import React from "react";
import Button from "@material-ui/core/Button";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import DialogTitle from "@material-ui/core/DialogTitle";
import Dialog from "@material-ui/core/Dialog";
import { TextField } from "@material-ui/core";
import axios from 'axios';
import MuiAlert from "@material-ui/lab/Alert";
import config from "../../../utils/config";
import Snackbar from "@material-ui/core/Snackbar";
import colors from "../../../utils/colors";

function Alert(props) {
  return <MuiAlert elevation={6} variant="filled" {...props} />;
}
function SimpleDialog(props) {
  const { onClose, open } = props;
  const { id, title, description, year } = props;
  const { type, dialogTitle } = props;
  const [newTitle, setTitle] = React.useState(title);
  const [newDescription, setDescription] = React.useState(description);
  const [newYear, setYear] = React.useState(year);
  const [showError, setShowError] = React.useState(false);
  const [showSuccess, setShowSuccess] = React.useState(false);

  const handleClose = () => {
    onClose(true);
  };

  const handleCloseSuccess = (event, reason) => {
    if (reason === "clickaway") {
      return;
    }

    setShowSuccess(false);
  };

  const handleCloseError = (event, reason) => {
    if (reason === "clickaway") {
      return;
    }

    setShowError(false);
  };
  const url = config.BASE_URL
  const handleUpdate = () => {
    const token = localStorage.getItem("jwtToken");

    let formData = new FormData();

    if(type==="EDIT") {
      formData.append("research_id", id)
      formData.append("research_title", newTitle)
      formData.append("description", newDescription)
      formData.append("year", newYear)

      axios.put(url + "/api/profile/research_information", formData, {
          headers: {
            'auth_token': token, //the token is a variable which holds the token
          },
        })
        .then((response) => {
          console.log(response)
          if (response.status === 201) {
          setShowSuccess("Your research is successfully updated.");
          document.location.reload()
          }

        })
        .catch((err) => {
          setShowError("A problem is occurred");
          console.log(err);
        });
    }
    else if(type==="NEW"){
      if(newTitle === "" || newYear === "") {
        setShowError("Title and year fields are required");
        return
      }
      formData.append("research_title", newTitle)
      formData.append("description", newDescription)
      formData.append("year", newYear)
      axios.post(url + "/api/profile/research_information", formData, {
        headers: {
          'auth_token': token, //the token is a variable which holds the token
        },
      })
      .then((response) => {
        console.log(response)
        if (response.status === 201) {
        setShowSuccess("Your research is successfully updated.");
        document.location.reload()
        }

      })
      .catch((err) => {
        setShowError("A problem is occurred");
        console.log(err);
      });
    }
  }
  return (
    <Dialog
      onClose={handleClose}
      aria-labelledby="simple-dialog-title"
      open={open}
    >
      <DialogTitle id="simple-dialog-title">{dialogTitle}</DialogTitle>
      <List>
        <ListItem>
          <TextField
            defaultValue={title}
            id="outlined-basic"
            label="Title"
            variant="outlined"
            fullWidth
            onChange={(e) => setTitle(e.target.value)}
          />
        </ListItem>
        <ListItem>
          <TextField
            defaultValue={description}
            id="outlined-basic"
            label="Description"
            variant="outlined"
            fullWidth
            onChange={(e) => setDescription(e.target.value)}
          />
        </ListItem>
        <ListItem>

          <TextField
            defaultValue={year}
            id="outlined-basic"
            label="Year"
            variant="outlined"
            fullWidth
            onChange={(e) => setYear(e.target.value)}
          />
        </ListItem>
        <ListItem>
    <Button onClick={handleUpdate}>{type==="NEW" ? "Create" : "Update"}</Button>
</ListItem>
      </List>
      {showError && (
              <Snackbar
                open={showError}
                autoHideDuration={3000}
                onClose={handleCloseError}
              >
                <Alert
                  style={{ backgroundColor: colors.quinary }}
                  severity="error"
                  onClose={handleCloseError}
                >
                  {showError}
                </Alert>
              </Snackbar>
            )}
            {showSuccess && (
              <Snackbar
                open={showSuccess}
                autoHideDuration={3000}
                onClose={handleCloseSuccess}
              >
                <Alert
                  style={{ backgroundColor: colors.quaternary }}
                  severity="success"
                  onClose={handleCloseSuccess}
                >
                  {showSuccess}
                </Alert>
              </Snackbar>
            )}
    </Dialog>

  );
}



export default function SimpleDialogDemo(props) {
  const [open, setOpen] = React.useState(false);

  const handleClickOpen = () => {
    setOpen(true);
  };

  const handleClose = (value) => {
    setOpen(false);
  };

  return (
    <div>

      <Button variant="outlined" style={{color: colors.primary, backgroundColor:colors.secondary}} onClick={handleClickOpen}>
      {props.type==="NEW" ? "Create Research" : "Update Research"}
      </Button>
      <SimpleDialog
        dialogTitle={props.dialogTitle}
        type={props.type}
        id={props.id}
        title={props.title}
        description={props.description}
        year={props.year}
        open={open}
        onClose={handleClose}
      />

    </div>
  );
}
