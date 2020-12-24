import React from "react";
import Button from "@material-ui/core/Button";
import Dialog from "@material-ui/core/Dialog";
import DialogActions from "@material-ui/core/DialogActions";
import DialogContent from "@material-ui/core/DialogContent";
import DialogContentText from "@material-ui/core/DialogContentText";
import DialogTitle from "@material-ui/core/DialogTitle";
import colors from "../../../../../utils/colors";
import TextField from "@material-ui/core/TextField";

export default function WorkspaceViewFileSectionPreviewConfirmation(props) {

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
                value={props.body}
                disabled
                multiline
                fullWidth
                rows={15}
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
