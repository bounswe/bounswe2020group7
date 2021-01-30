import React, {useState} from "react";
import Button from "@material-ui/core/Button";
import Dialog from "@material-ui/core/Dialog";
import DialogActions from "@material-ui/core/DialogActions";
import DialogContent from "@material-ui/core/DialogContent";
import DialogContentText from "@material-ui/core/DialogContentText";
import DialogTitle from "@material-ui/core/DialogTitle";
import colors from "../../../../../utils/colors";
import TextField from "@material-ui/core/TextField";

export default function WorkspaceViewFileSectionRenameConfirmation(props) {
  const [rename, setRename] = useState("");
  const handleRenameFolder = () => {
    props.handleRenameDialogClose(props.index);
    props.renameFolder(props.element, rename);
  };
  return (
    <div>
      <Dialog
        open={props.renameDialog}
        onClose={props.handleRenameDialogClose}
        aria-labelledby="alert-dialog-title"
        aria-describedby="alert-dialog-description"
      >
        <DialogTitle id="alert-dialog-title">
          {"Rename " + props.element + " " + props.type}
        </DialogTitle>
        <DialogContent>
          <DialogContentText id="alert-dialog-description">
            <TextField

              onChange={(e) => setRename(e.target.value)}
            />
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button
            style={{ color: colors.primary }}
            onClick={() => props.handleRenameDialogClose(props.index)}
            color="primary"
          >
            Cancel
          </Button>
          <Button
          onClick={handleRenameFolder}
            style={{
              color: colors.secondaryLight,
              backgroundColor: colors.quaternary,
            }}
            color="primary"
            autoFocus
          >
            Rename
          </Button>
        </DialogActions>
      </Dialog>
    </div>
  );
}
