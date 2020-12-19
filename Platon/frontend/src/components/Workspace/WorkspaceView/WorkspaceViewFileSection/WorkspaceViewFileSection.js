import React, { Component } from "react";
import { withStyles } from "@material-ui/core/styles";
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemAvatar from '@material-ui/core/ListItemAvatar';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemSecondaryAction from '@material-ui/core/ListItemSecondaryAction';
import ListItemText from '@material-ui/core/ListItemText';
import Avatar from '@material-ui/core/Avatar';
import IconButton from '@material-ui/core/IconButton';
import FormGroup from '@material-ui/core/FormGroup';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import Checkbox from '@material-ui/core/Checkbox';
import Grid from '@material-ui/core/Grid';
import Typography from '@material-ui/core/Typography';
import FolderIcon from '@material-ui/icons/Folder';
import InsertDriveFileIcon from '@material-ui/icons/InsertDriveFile';
import DeleteIcon from '@material-ui/icons/Delete';
import axios from "axios";
import jwt_decode from "jwt-decode";
import config from '../../../../utils/config';
import colors from '../../../../utils/colors';
const useStyles = (theme) => ({
  root: {
    flexGrow: 1,
    maxWidth: 752,
  },
  demo: {
    backgroundColor: colors.secondary,
  },
  title: {
    margin: theme.spacing(4, 0, 2),
  },
});

function generate(element) {
  return [0, 1, 2].map((value) =>
    React.cloneElement(element, {
      key: value,
    }),
  );
}

class WorkspaceViewFileSection extends Component {
  constructor(props) {
    super(props);
    this.state = {
      cwd: ".",
      route: ".",
      folders: [],
      files: []
    };
  }
  componentDidMount(){
    const token = localStorage.getItem("jwtToken");
    axios.defaults.headers.common["auth_token"] = `${token}`;
    const url = config.BASE_URL
    let c_workspace_id = this.props.workspaceId
    axios.get(url+'/api/file_system/folder', {
      params: {
        path: this.state.route,
        workspace_id: c_workspace_id
      }
    }).then((response) => {
      console.log(response)
      if (response.status === 200) {
        this.setState({
          files: response.data.files,
          folders: response.data.folders,

        });
      }
    })
    .catch((err) => {
      /*this.setState({
        isSending: false,
        error: "Error occured. " + err.message,
      });*/
      console.log(err);
    });
  }
  render() {
    const {classes} = this.props
    return (
      <div className={classes.root}>



            <div className={classes.demo}>
              <List >
                {this.state.folders.map((element) => (
                  <ListItem>
                    <ListItemAvatar>
                      <Avatar style={{backgroundColor:colors.primary}} >
                        <FolderIcon style={{color:colors.secondary}} />
                      </Avatar>
                    </ListItemAvatar>
                    <ListItemText
                     style={{color: colors.primary}}
                      primary={element}
                    />
                    <ListItemSecondaryAction>
                      <IconButton edge="end" aria-label="delete">
                        <DeleteIcon />
                      </IconButton>
                    </ListItemSecondaryAction>
                  </ListItem>
                ))}
                {this.state.files.map((element) => (
                  <ListItem>
                    <ListItemAvatar>
                      <Avatar  style={{backgroundColor:colors.primary}} >
                        <InsertDriveFileIcon style={{color:colors.secondary}}/>
                      </Avatar>
                    </ListItemAvatar>
                    <ListItemText
                      primary={element}
                    />
                    <ListItemSecondaryAction>
                      <IconButton edge="end" aria-label="delete">
                        <DeleteIcon />
                      </IconButton>
                    </ListItemSecondaryAction>
                  </ListItem>
                ))}
              </List>
            </div>

      </div>
    );
  }
}

export default withStyles(useStyles)(WorkspaceViewFileSection);

