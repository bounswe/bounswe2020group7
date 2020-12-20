import React from 'react';
import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';
import colors from "../../../../../utils/colors";

export default function WorkspaceViewFileSectionDeleteConfirmation(props) {
const handleDeleteFolder = () =>{
    props.handleDeleteDialogClose(props.index);
    props.deleteFolder(props.element);
}

const handleDeleteFile = () =>{
  console.log("içerde")
  props.handleDeleteDialogClose(props.index);
  props.deleteFile(props.element);
}
  return (
    <div>
      <Dialog
        open={props.deleteDialog}
        onClose={props.handleDeleteDialogClose}
        aria-labelledby="alert-dialog-title"
        aria-describedby="alert-dialog-description"
      >
        <DialogTitle id="alert-dialog-title">{"Are you sure?"}</DialogTitle>
        <DialogContent>
          <DialogContentText id="alert-dialog-description">
            {props.element} {props.type} will be deleted permanently!
          </DialogContentText>
        </DialogContent>
        <DialogActions>

          <Button style={{color: colors.primary}} onClick={() => props.handleDeleteDialogClose(props.index)} color="primary">
            Cancel
          </Button>
          <Button style={{color: colors.secondaryLight, backgroundColor: colors.quinary}} onClick={props.type === "folder" ? handleDeleteFolder: handleDeleteFile} color="primary" autoFocus>
            Delete
          </Button>
        </DialogActions>
      </Dialog>
    </div>
  );
}