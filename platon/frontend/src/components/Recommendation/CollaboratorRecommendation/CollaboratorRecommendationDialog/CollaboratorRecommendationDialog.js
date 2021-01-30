import React, { useState, useEffect } from "react";
import PropTypes from "prop-types";
import { makeStyles } from "@material-ui/core/styles";
import Avatar from "@material-ui/core/Avatar";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemAvatar from "@material-ui/core/ListItemAvatar";
import ListItemText from "@material-ui/core/ListItemText";
import DialogTitle from "@material-ui/core/DialogTitle";
import Dialog from "@material-ui/core/Dialog";
import PersonIcon from "@material-ui/icons/Person";
import { blue } from "@material-ui/core/colors";
import PersonAddIcon from "@material-ui/icons/PersonAdd";
import IconButton from "@material-ui/core/IconButton";
import colors from "../../../../utils/colors";
import ListItemSecondaryAction from "@material-ui/core/ListItemSecondaryAction";
import AddCircleIcon from '@material-ui/icons/AddCircle';
import Spinner from "../../../Spinner/Spinner";
import config from "../../../../utils/config";
import axios from "axios";

const { BASE_URL } = config;
const REC_URL = `${BASE_URL}/api/recommendation_system/collaboration`;
axios.defaults.headers.common["auth_token"] = localStorage.getItem("jwtToken");

const useStyles = makeStyles({
  avatar: {
    backgroundColor: blue[100],
    color: blue[600],
  },
});

const NUMBER_OF_RECOMMENDATIONS = 5;
function SimpleDialog(props) {
  const classes = useStyles();
  const { onClose, selectedValue, open } = props;
  const handleClose = () => {
    onClose(selectedValue);
  };
  const handleInvite = (userId) =>{
    let formData = new FormData()
    formData.append("workspace_id", props.workspaceId)
    formData.append("invitee_id", userId)

    axios.post(BASE_URL + "/api/workspaces/invitations", formData).then((response)=>{
      if(response.status===201){
        props.handleSuccessText("Invitation has been sent successfully!")
      }
    }).catch((error)=>{
      props.handleErrorText(error.response.data.error)
    })
  }
  return (
    <Dialog
      onClose={handleClose}
      aria-labelledby="simple-dialog-title"
      open={open}
    >{props.loaded ? (
      <div>
      <DialogTitle
        style={{ backgroundColor: colors.secondary }}
        id="simple-dialog-title"
      >
        Recommended Users for Collaboration
      </DialogTitle>
      <List style={{ backgroundColor: colors.secondaryLight }}>
        {props.recommendations.map((user) => (
          <ListItem key={user}>
            <ListItemAvatar>
            <a href={`/${user.id}`} style={{textDecoration: "none"}}>
              <Avatar className={classes.avatar} src={BASE_URL + "/api" + user.profile_photo} >
                <PersonIcon />
              </Avatar>
              </a>
            </ListItemAvatar>
            <a href={`/${user.id}`} style={{textDecoration: "none"}}>
            <ListItemText primary={`${user.name} ${user.surname}`} secondary={user.institution === "" ? `${user.job}` : `${user.job} @${user.institution}`}/>
            </a>
            <ListItemSecondaryAction>
              <IconButton onClick={() => handleInvite(user.id)}>
                  <AddCircleIcon style={{color: colors.septenaryDark }}/>
              </IconButton>
            </ListItemSecondaryAction>

          </ListItem>
        ))}

      </List>
      </div>):<div style={{display: "flex", alignItems: "center", height: "500px", width: "500px"}}><div style={{margin:"200px"}}><Spinner/></div></div>}
    </Dialog>
  );
}

SimpleDialog.propTypes = {
  onClose: PropTypes.func.isRequired,
  open: PropTypes.bool.isRequired,
};

export default function SimpleDialogDemo(props) {
  const [open, setOpen] = React.useState(false);
  const [recommendations, setRecommendations] = React.useState([]);
  const [loaded, setLoaded] = React.useState(false);
  const options = {
    params: {
      workspace_id: props.workspaceId,
      number_of_recommendations: NUMBER_OF_RECOMMENDATIONS,
    },
  };
  useEffect(() => {
    axios
      .get(REC_URL, options)
      .then((response) => {
        setRecommendations(response.data.recommendation_list);
        setLoaded(true);
      })
      .catch((error) => {
        console.log(error);
      });
  }, []);
  const handleClickOpen = () => {
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
  };
  return (
    <div>
      <IconButton style={{ padding: "0px 16px" }} onClick={handleClickOpen}>
        <PersonAddIcon style={{ color: colors.primary }} />
      </IconButton>
      <SimpleDialog
        loaded={loaded}
        recommendations={recommendations}
        workspaceId={props.workspaceId}
        open={open}
        onClose={handleClose}
        handleCloseError={props.handleCloseError}
        handleCloseSuccess={props.handleCloseSuccess}
        handleErrorText={props.handleErrorText}
        handleSuccessText={props.handleSuccessText}
      />
    </div>
  );
}
