import React, { useState, useEffect} from "react";
import Button from "@material-ui/core/Button";
import TextField from "@material-ui/core/TextField";
import Dialog from "@material-ui/core/Dialog";
import DialogActions from "@material-ui/core/DialogActions";
import DialogContent from "@material-ui/core/DialogContent";
import DialogTitle from "@material-ui/core/DialogTitle";
import axios from "axios";
import { getIssueComments } from "../utils";
import config from "../../../../../utils/config";
import colors from "../../../../../utils/colors";
import Spinner from '../../../../Spinner/Spinner'
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import Divider from '@material-ui/core/Divider';
import ListItemText from '@material-ui/core/ListItemText';
import ListItemAvatar from '@material-ui/core/ListItemAvatar';
import Avatar from '@material-ui/core/Avatar';
import ListItemSecondaryAction from "@material-ui/core/ListItemSecondaryAction";
import SpeakerNotesOffIcon from "@material-ui/icons/SpeakerNotesOff";
import IconButton from "@material-ui/core/IconButton";
import AddCommentModal from "./addComment";

const { BASE_URL } = config;
const COMMENT_URL = `${BASE_URL}/api/workspaces/issue/comment`;
axios.defaults.headers.common["auth_token"] = localStorage.getItem("jwtToken");

const CommentsModal = ({
  closePopup,
  issue,
  workspaceId,
  open,
  loadIssues,
  loadComments,
  issues,
  openPopupAddComment,
  closePopupAddComment,
  openAddComment
}) => {
  const [comments, setComments] = useState([]);
  const [loaded, setLoaded] = React.useState(false);
  const [comment, setComment] = useState("");

  useEffect(() => {
    setLoaded(false)
    if(issue){
      const issueId = issue.issue_id
      getIssueComments({ workspaceId, issueId })
      .then((res) => {
        if (res.status === 200) {
            setComments(res.data.result)
            setLoaded(true)
        }
      }).catch((err)=>console.log(err.response.data.error))
    }

  }, [issue])
  const handleDeleteComment = (commentId) =>{
    const issueId = issue.issue_id
    let formData = new FormData()
    formData.append('workspace_id', workspaceId)
    formData.append('issue_id', issueId)
    formData.append('comment_id', commentId)
    const options = {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
      data: formData
    }
    axios.delete(COMMENT_URL, options).then((res)=>{
        if(res.status===200){
            getIssueComments({ workspaceId, issueId })
      .then((res) => {
        if (res.status === 200) {
            setComments(res.data.result)
            setLoaded(true)
        }
      }).catch((err)=>console.log(err.response.data.error))
    }
    }).catch((err)=>console.log(err))
  }
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
            setComment("")

        getIssueComments({ workspaceId, issueId })
          .then((res) => {
            if (res.status === 200) {
                setComments(res.data.result)
                setLoaded(true)
            }
          }).catch((err)=>console.log(err.response.data.error))

        }
        }).catch((err) => {
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
        <DialogTitle id="form-dialog-title">Comments</DialogTitle>
        {loaded ? (
        <DialogContent>
           <List >
               {comments && comments.length === 0 ? "No comments": null}
                {comments && comments.map((comment)=>
               <div>
                <ListItem alignItems="flex-start">
                    <ListItemAvatar>
                    <Avatar src={BASE_URL + "/api" + comment.owner_photo} />
                    </ListItemAvatar>
                    <ListItemText
                    primary={`${comment.owner_name} ${comment.owner_surname}`}
                    secondary={comment.comment
                    }
                    />
                <ListItemSecondaryAction>
                    <IconButton onClick={()=>handleDeleteComment(comment.comment_id)}>
                        <SpeakerNotesOffIcon style={{color: colors.quinary }}/>
                    </IconButton>
                    </ListItemSecondaryAction>
                </ListItem>
                <Divider variant="inset" component="li" />
                </div>
               )}
        </List> <TextField
            autoFocus
            value={comment}

            onChange={(e) => setComment(e.target.value)}
            margin="dense"
            id="comment"
            label="Comment"
            fullWidth
          />
        </DialogContent>
        ): <div style={{display:"flex", flexDirection: "column", alignItems: "center", width:"300px", paddingLeft: "300px"}}><Spinner/></div>}
        <DialogActions>
          <Button onClick={closePopup} color="primary">
            Close
          </Button>
          <Button onClick= {handleSubmit} color="secondary">
            Add Comment
          </Button>
        </DialogActions>
      </Dialog>
      <AddCommentModal
          closePopup={() => openPopupAddComment(false)}
          issue={issue}
          workspaceId={workspaceId}
          open={openAddComment}
          loadIssues={() => loadIssues()}
          issues={issues}
          comment={comment}
          setComment={setComment}
          handleSubmit={handleSubmit}
        />
    </div>
  );
};
export default CommentsModal;
