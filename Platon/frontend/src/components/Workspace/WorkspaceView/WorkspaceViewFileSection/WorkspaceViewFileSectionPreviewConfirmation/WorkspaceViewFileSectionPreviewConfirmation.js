import React, { useState, useEffect } from "react";
import Button from "@material-ui/core/Button";
import Dialog from "@material-ui/core/Dialog";
import DialogActions from "@material-ui/core/DialogActions";
import DialogContent from "@material-ui/core/DialogContent";
import DialogContentText from "@material-ui/core/DialogContentText";
import DialogTitle from "@material-ui/core/DialogTitle";
import colors from "../../../../../utils/colors";
import TextField from "@material-ui/core/TextField";
import config from "../../../../../utils/config";
import axios from "axios";
export default function WorkspaceViewFileSectionEditConfirmation(props) {
  const [body, setBody] = useState(props.element);
  useEffect(() => {
    const token = localStorage.getItem("jwtToken");
    axios.defaults.headers.common["auth_token"] = `${token}`;
    const url = config.BASE_URL;
    axios
      .get(url + "/api/file_system/file", {
        params: {
          path: props.cwd,
          workspace_id: props.c_workspace_id,
          filename: props.element,
        },
      })
      .then((response) => {
        if (response.status === 200) {
          setBody(response.data);
        }
      })
      .catch((err) => {
        console.log(err);
      });
  }, []);


  return (
    <div>
      <Dialog
        fullWidth
        maxWidth={"lg"}
        open={props.previewDialog}
        onClose={props.handlePreviewFileDialogClose}
        aria-labelledby="alert-dialog-title"
        aria-describedby="alert-dialog-description"
      >
        <DialogTitle id="alert-dialog-title">
          {"Preview " + props.element + " " + props.type}
        </DialogTitle>
        <DialogContent>
          <DialogContentText id="alert-dialog-description">
            <div
              style={{
                display: "flex",
                flexDirection: "column",
                alignItems: "center",
              }}
            >
              <TextField
                variant="outlined"
                value={body}
                disabled
                multiline
                fullWidth
                rows={15}
                onChange={(e) => setBody(e.target.value)}
              />
            </div>
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button
            style={{ color: colors.primary }}
            onClick={() => props.handlePreviewFileDialogClose(props.index)}
            color="primary"
          >
            Cancel
          </Button>

        </DialogActions>
      </Dialog>
    </div>
  );
}
