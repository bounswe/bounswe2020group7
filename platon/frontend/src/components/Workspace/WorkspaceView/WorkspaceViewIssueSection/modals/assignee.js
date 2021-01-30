import React, { useState, useEffect} from "react";
import Button from "@material-ui/core/Button";
import Dialog from "@material-ui/core/Dialog";
import DialogActions from "@material-ui/core/DialogActions";
import DialogContent from "@material-ui/core/DialogContent";
import DialogContentText from "@material-ui/core/DialogContentText";
import DialogTitle from "@material-ui/core/DialogTitle";
import colors from "../../../../../utils/colors";
import Snackbar from "@material-ui/core/Snackbar";
import MuiAlert from "@material-ui/lab/Alert";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemText from "@material-ui/core/ListItemText";
import ListItemIcon from "@material-ui/core/ListItemIcon";
import Checkbox from "@material-ui/core/Checkbox";
import axios from 'axios'
import config from "../../../../../utils/config"
import { getIssueAssignees } from '../utils'
import Spinner from '../../../../Spinner/Spinner'
const { BASE_URL } = config

const ISSUE_URL = `${BASE_URL}/api/workspaces/issue`
axios.defaults.headers.common['auth_token'] = localStorage.getItem('jwtToken')

const AssigneeModal = ({ closePopup, open, issue, workspaceId, loadIssues, members }) => {
  const [errorSnackbar, setErrorSnackbar] = useState(false);
  const [checked, setChecked] = React.useState([]);
  const [initialChecked, setInitialChecked] = React.useState([]);
  const [loaded, setLoaded] = React.useState(false);

  useEffect(() => {
    setLoaded(false)
    if(issue){
      const issueId = issue.issue_id
      getIssueAssignees({ workspaceId, issueId })
      .then((res) => {
        if (res.status === 200) {
            let ids = []
              for(var key in res.data.result){
                  ids.push(res.data.result[key].assignee_id)
              }
          setChecked(ids)
          setInitialChecked(ids)
          setLoaded(true)
        }
      })
    }

  }, [issue])
  const CheckboxList = ({ members }) => {
    const handleToggle = (value) => () => {
      const currentIndex = checked.indexOf(value);
      const newChecked = [...checked];

      if (currentIndex === -1) {
        newChecked.push(value);
      } else {
        newChecked.splice(currentIndex, 1);
      }

      setChecked(newChecked);
    };



    return (
      <List>
        {members.map((member) => {
          const labelId = `checkbox-list-label-${member.id}`;

          return (
            <ListItem
              key={member.id}
              role={undefined}
              dense
              button
              onClick={handleToggle(member.id)}
            >
              <ListItemIcon>
                <Checkbox
                  edge="start"
                  checked={checked.indexOf(member.id) !== -1}
                  tabIndex={-1}
                  disableRipple
                  inputProps={{ "aria-labelledby": labelId }}
                />
              </ListItemIcon>
              <ListItemText
                id={labelId}
                primary={`${member.name} ${member.surname}`}
              />
            </ListItem>
          );
        })}
      </List>
    );
  };
  function Alert(props) {
    return <MuiAlert elevation={6} variant="filled" {...props} />;
  }

  const handleSubmit = () => {
    const { workspace_id: workspaceId, issue_id: issueId } = issue;

    checked.map((id) => {
      if(!initialChecked.includes(id)){
        let formData = new FormData();
        formData.append("workspace_id", workspaceId);
        formData.append("issue_id", issueId);
        formData.append("assignee_id", id);

        const options = {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        };
        return axios
        .post(ISSUE_URL + "/assignee", formData, options)
        .then((res) => {
          if (res.status === 200) {
            closePopup();
          } else {
            setErrorSnackbar(true);
          }
        })
        .catch((err) => {
          setErrorSnackbar(true);
        });
      }
    });
    initialChecked.map((id)=>{
      if(!checked.includes(id)){
        let formData = new FormData();
        formData.append("workspace_id", workspaceId);
        formData.append("issue_id", issueId);
        formData.append("assignee_id", id);

        const options = {
          headers: {
            "Content-Type": "multipart/form-data",
          },
          data: formData
        };
        return axios
        .delete(ISSUE_URL + "/assignee", options)
        .then((res) => {
          if (res.status === 200) {
            closePopup();
          } else {
            setErrorSnackbar(true);
          }
        })
        .catch((err) => {
          setErrorSnackbar(true);
        });
      }
    })
  };

  return (
    <div>
      <Snackbar
        open={errorSnackbar}
        autoHideDuration={6000}
        onClose={() => setErrorSnackbar(false)}
      >
        <Alert onClose={() => setErrorSnackbar(false)} severity="error">
          Can't assign.
        </Alert>
      </Snackbar>
      <Dialog
        open={open}
        onClose={closePopup}
        aria-labelledby="alert-dialog-title"
        aria-describedby="alert-dialog-description"
      >

        <DialogTitle id="alert-dialog-title">{"Assign"}</DialogTitle>
        {loaded ? (
        <DialogContent>
          <DialogContentText id="alert-dialog-description">
            <CheckboxList members={members} />
          </DialogContentText>
        </DialogContent>
        ): <div style={{display:"flex", flexDirection: "column", alignItems: "center", width:"300px"}}><Spinner/></div>}
        <DialogActions>
          <Button
            style={{ color: colors.primary }}
            onClick={closePopup}
            color="primary"
          >
            Cancel
          </Button>
          <Button
            style={{
              color: colors.secondaryLight,
              backgroundColor: colors.quinary,
            }}
            onClick={handleSubmit}
            color="primary"
            autoFocus
          >
            Assign
          </Button>
        </DialogActions>
      </Dialog>
    </div>
  );
};

export default AssigneeModal;
