import React, { useState, useEffect } from "react";
import { withStyles } from "@material-ui/core/styles";
import colors from "../../../../utils/colors";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import { getIssues } from "./utils";
import ListItemAvatar from "@material-ui/core/ListItemAvatar";
import Avatar from "@material-ui/core/Avatar";
import ListItemText from "@material-ui/core/ListItemText";
import ListItemSecondaryAction from "@material-ui/core/ListItemSecondaryAction";
import IconButton from "@material-ui/core/IconButton";
import EditIcon from "@material-ui/icons/Edit";
import DeleteIcon from "@material-ui/icons/Delete";
import ErrorIcon from "@material-ui/icons/Error";
import CommentIcon from "@material-ui/icons/Comment";
import GroupAddIcon from "@material-ui/icons/GroupAdd";
import CreateUpdateModal from "./modals/createupdate";
import Button from "@material-ui/core/Button";
import DeleteModal from "./modals/delete";
import AssigneeModal from "./modals/assignee";
import CommentsModal from "./modals/comments";
import { Divider } from "@material-ui/core";

const useStyles = (theme) => ({
  root: {
    width: "620px",
    marginBottom: theme.spacing(3),
  },
  demo: {
    backgroundColor: colors.secondary,
  },
  title: {
    margin: theme.spacing(4, 0, 2),
  },
});

const StyledButton = withStyles({
  root: {
    background: colors.tertiary,
    color: colors.secondary,

    "&:hover": {
      backgroundColor: colors.tertiaryDark,
    },
  },
})(Button);

const IssueSection = ({ classes, workspaceId, members }) => {
  const [issues, setIssues] = useState([]);
  const [editOpen, setEditOpen] = useState(false);
  const [commentOpen, setCommentOpen] = useState(false);
  const [assigneeOpen, setAssigneeOpen] = useState(false);
  const [createOpen, setCreateOpen] = useState(false);
  const [currentIssue, setCurrentIssue] = useState(null);
  const [deleteOpen, setDeleteOpen] = useState(false);

  useEffect(() => {
    getIssues({ workspaceId })
      .then((res) => {
        if (res.status === 200) {
          setIssues(res.data.result);
        }
      })
      .catch((error) => {
        console.log(error);
      });
  }, [workspaceId]);

  const openCommentPopup = (ms) => {
    setCurrentIssue(ms);
    setCommentOpen(true);
  };


  const openAssigneePopup = (ms) => {
    setCurrentIssue(ms);
    setAssigneeOpen(true);
  };

  const openEditPopup = (ms) => {
    setCurrentIssue(ms);
    setEditOpen(true);
  };

  const openDeletePopup = (ms) => {
    setCurrentIssue(ms);
    setDeleteOpen(true);
  };
  //TODO

  const loadIssues = () => {
    return getIssues({ workspaceId }).then((res) => {
      if (res.status === 200) {
        setIssues(res.data.result);
      }
    });
  };

  return (
    <div className={classes.root}>
      <div className={classes.demo}>
        <List>
          {issues &&
            issues.length !== 0 &&
            issues.map((issue, index) => (
              <div>
                <ListItem>
                  <ListItemAvatar>
                    <Avatar style={{ backgroundColor: colors.primary }}>
                      <ErrorIcon style={{ color: colors.quinary }} />
                    </Avatar>
                  </ListItemAvatar>
                  <ListItemText
                    primary={issue && issue.title}
                    secondary={
                      <div
                        style={{
                          display: "flex",
                          flexDirection:"column",
                          justifyContent: "space-between",
                        }}
                      >
                        <div
                          style={{
                            wordBreak: "break-word",
                            marginRight: "16px",
                          }}
                        >
                          {issue && issue.description}
                        </div>
                        <div>Deadline: {(issue && issue.deadline) ? issue.deadline : "Not specified"}</div>
                      </div>
                    }
                  />
                  <ListItemSecondaryAction>
                    <IconButton edge="end">
                      <GroupAddIcon
                        style={{ color: colors.primary }}
                        onClick={() => openAssigneePopup(issue)}
                      />
                    </IconButton>
                    <IconButton edge="end">
                      <CommentIcon
                        style={{ color: colors.tertiary }}
                        onClick={() => openCommentPopup(issue)}
                      />
                    </IconButton>
                    <IconButton edge="end">
                      <EditIcon
                        style={{ color: colors.quaternary }}
                        onClick={() => openEditPopup(issue)}
                      />
                    </IconButton>
                    <IconButton edge="end">
                      <DeleteIcon
                        style={{ color: colors.quinary }}
                        onClick={() => openDeletePopup(issue)}
                      />
                    </IconButton>
                  </ListItemSecondaryAction>
                </ListItem>
                <Divider/>
              </div>
            ))}
        </List>

        <div>
          <div
            style={{
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
            }}
          >
            <StyledButton onClick={() => setCreateOpen(true)}>
              Create Issue
            </StyledButton>
          </div>
        </div>
        <CreateUpdateModal
          open={editOpen}
          isCreate={false}
          workspaceId={workspaceId}
          closePopup={() => setEditOpen(false)}
          currentIssue={currentIssue}
          loadIssues={() => loadIssues()}
          members={members}
        />
        <CreateUpdateModal
          open={createOpen}
          isCreate
          workspaceId={workspaceId}
          closePopup={() => setCreateOpen(false)}
          loadIssues={() => loadIssues()}
          members={members}
        />
        <AssigneeModal
          closePopup={() => setAssigneeOpen(false)}
          issue={currentIssue}
          workspaceId={workspaceId}
          open={assigneeOpen}
          loadIssues={() => loadIssues()}
          members={members}
        />
        <CommentsModal
          closePopup={() => setCommentOpen(false)}
          issue={currentIssue}
          workspaceId={workspaceId}
          open={commentOpen}
          loadIssues={() => loadIssues()}
          issues={issues}
        />
        <DeleteModal
          closePopup={() => setDeleteOpen(false)}
          issue={currentIssue}
          open={deleteOpen}
          loadIssues={() => loadIssues()}
        />
      </div>
    </div>
  );
};

export default withStyles(useStyles)(IssueSection);
