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
            This {props.type} will be deleted permanently! {props.element}
          </DialogContentText>
        </DialogContent>
        <DialogActions>

          <Button style={{color: colors.primary}} onClick={() => props.handleDeleteDialogClose(props.index)} color="primary">
            Cancel
          </Button>
          <Button style={{color: colors.secondaryLight, backgroundColor: colors.quinary}} onClick={handleDeleteFolder} color="primary" autoFocus>
            Delete
          </Button>
        </DialogActions>
      </Dialog>
    </div>
  );
}