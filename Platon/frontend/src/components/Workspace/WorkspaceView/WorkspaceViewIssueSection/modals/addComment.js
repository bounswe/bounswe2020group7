import React, { useState } from "react";
import Button from "@material-ui/core/Button";
import TextField from "@material-ui/core/TextField";
import Dialog from "@material-ui/core/Dialog";
import DialogActions from "@material-ui/core/DialogActions";
import DialogContent from "@material-ui/core/DialogContent";
import DialogContentText from "@material-ui/core/DialogContentText";
import DialogTitle from "@material-ui/core/DialogTitle";
import axios from "axios";
import { getIssueComments } from "../utils";
import config from "../../../../../utils/config";
const { BASE_URL } = config;

const COMMENT_URL = `${BASE_URL}/api/workspaces/issue/comment`;
axios.defaults.headers.common["auth_token"] = localStorage.getItem("jwtToken");

const AddCommentModal = ({
  closePopup,
  issue,
  workspaceId,
  open,
  loadIssues,
  loadComments,
  issues
}) => {
  const [comment, setComment] = useState("");

  const handleSubmit = () => {
    if (comment === "") return;

    const { workspace_id: workspaceId, issue_id: issueId } = issue;
    let formData = new FormData();
    formData.append("workspace_id", workspaceId);
    formData.append("issue_id", issueId);
    formData.append("comment", comment);

    const options = {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    };
    axios
      .post(COMMENT_URL, formData, options)
      .then((response) => {
        if(response.status===200){
            //TODO
            setComment("")
            let whichIssue = 0
            for(var key in issues){
              if(issues[key].issue_id===issueId){
                whichIssue = key
                break
              }
            }
            loadComments(workspaceId, issueId, whichIssue)
            closePopup()
        }
    })
      .catch((err) => {
        console.log(err);
      });
  };

  return (
    <div>
      <Dialog
        open={open}
        fullWidth={true}
        maxWidth={"sm"}
        onClose={closePopup}
        aria-labelledby="form-dialog-title"
      >
        <DialogTitle id="form-dialog-title">Add Comment</DialogTitle>
        <DialogContent>
          <TextField
            autoFocus
            value={comment}
            onChange={(e) => setComment(e.target.value)}
            margin="dense"
            id="comment"
            label="Comment"
            fullWidth
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
    </div>
  );
};
export default AddCommentModal;
