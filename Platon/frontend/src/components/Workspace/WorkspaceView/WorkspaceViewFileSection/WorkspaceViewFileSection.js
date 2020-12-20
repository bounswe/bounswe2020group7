import React, { Component } from "react";
import { withStyles } from "@material-ui/core/styles";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemAvatar from "@material-ui/core/ListItemAvatar";
import ListItemSecondaryAction from "@material-ui/core/ListItemSecondaryAction";
import ListItemText from "@material-ui/core/ListItemText";
import Avatar from "@material-ui/core/Avatar";
import IconButton from "@material-ui/core/IconButton";
import FolderIcon from "@material-ui/icons/Folder";
import InsertDriveFileIcon from "@material-ui/icons/InsertDriveFile";
import DeleteIcon from "@material-ui/icons/Delete";
import TextFieldsIcon from "@material-ui/icons/TextFields";
import axios from "axios";
import ArrowBackIcon from "@material-ui/icons/ArrowBack";
import ArrowForwardIcon from "@material-ui/icons/ArrowForward";
import WorkspaceViewFileSectionDeleteConfirmation from "./WorkspaceViewFileSectionDeleteConfirmation/WorkspaceViewFileSectionDeleteConfirmation";
import WorkspaceViewFileSectionRenameConfirmation from "./WorkspaceViewFileSectionRenameConfirmation/WorkspaceViewFileSectionRenameConfirmation";
import config from "../../../../utils/config";
import colors from "../../../../utils/colors";
const useStyles = (theme) => ({
  root: {
    flexGrow: 1,
    width: 752,
  },
  demo: {
    backgroundColor: colors.secondary,
  },
  title: {
    margin: theme.spacing(4, 0, 2),
  },
});

class WorkspaceViewFileSection extends Component {
  constructor(props) {
    super(props);
    this.state = {
      cwd: ".",
      folders: [],
      files: [],
      folderName: "",
      selectedFile: null,
      deleteFileDialogArray: [],
      deleteFolderDialogArray: [],
      renameFileDialogArray: [],
      renameFolderDialogArray: [],
    };
  }
  fetchFileStructure = () => {
    const token = localStorage.getItem("jwtToken");
    axios.defaults.headers.common["auth_token"] = `${token}`;
    const url = config.BASE_URL;
    let c_workspace_id = this.props.workspaceId;
    axios
      .get(url + "/api/file_system/folder", {
        params: {
          path: this.state.cwd,
          workspace_id: c_workspace_id,
        },
      })
      .then((response) => {
        if (response.status === 200) {
          this.setState({
            files: response.data.files,
            folders: response.data.folders,
            cwd: response.data.cwd,
            deleteFileDialogArray: Array(response.data.files.length).fill(
              false
            ),
            deleteFolderDialogArray: Array(response.data.folders.length).fill(
              false
            ),
            renameFileDialogArray: Array(response.data.files.length).fill(
              false
            ),
            renameFolderDialogArray: Array(response.data.folders.length).fill(
              false
            ),
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
  };
  componentDidMount() {
    this.fetchFileStructure();
  }

  moveUp = (element) => {
    const token = localStorage.getItem("jwtToken");
    axios.defaults.headers.common["auth_token"] = `${token}`;
    const url = config.BASE_URL;
    let c_workspace_id = this.props.workspaceId;
    const changePath = this.state.cwd.split("/").slice(0, -1).join("/");
    axios
      .get(url + "/api/file_system/folder", {
        params: {
          path: changePath,
          workspace_id: c_workspace_id,
        },
      })
      .then((response) => {
        if (response.status === 200) {
          this.setState({
            files: response.data.files,
            folders: response.data.folders,
            cwd: response.data.cwd,
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
  };
  moveDown = (element) => {
    const token = localStorage.getItem("jwtToken");
    axios.defaults.headers.common["auth_token"] = `${token}`;
    const url = config.BASE_URL;
    let c_workspace_id = this.props.workspaceId;
    const changePath = this.state.cwd + "/" + element;

    axios
      .get(url + "/api/file_system/folder", {
        params: {
          path: changePath,
          workspace_id: c_workspace_id,
        },
      })
      .then((response) => {
        if (response.status === 200) {
          this.setState({
            files: response.data.files,
            folders: response.data.folders,
            cwd: response.data.cwd,
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
  };
  uploadFile = () => {
    if(this.state.selectedFile===null){
      return;
    }
    const token = localStorage.getItem("jwtToken");
    axios.defaults.headers.common["auth_token"] = `${token}`;
    const url = config.BASE_URL;
    const c_workspace_id = this.props.workspaceId;
    let formData = new FormData();
    formData.append("new_file", this.state.selectedFile);
    formData.append("filename", this.state.selectedFile.name);
      formData.append("path", this.state.cwd);
      formData.append("workspace_id", c_workspace_id);
      axios
        .post(url + "/api/file_system/file", formData)
        .then((response) => {
          if (response.status === 201) {

            this.fetchFileStructure();
          }
        })
        .catch((err) => {
          /*this.setState({
            showError: "Error occured. Check your credientials.",
          });*/
          console.log(err);
        });
  };
  createFolder = () => {
    if (this.state.folderName === "") {
      return;
    }
    const token = localStorage.getItem("jwtToken");
      axios.defaults.headers.common["auth_token"] = `${token}`;
      const url = config.BASE_URL;
      const c_workspace_id = this.props.workspaceId;
      let formData = new FormData();
      formData.append("new_folder_name", this.state.folderName);
      formData.append("path", this.state.cwd);
      formData.append("workspace_id", c_workspace_id);
      axios
        .post(url + "/api/file_system/folder", formData)
        .then((response) => {
          if (response.status === 201) {
            this.setState({ folderName: "" });
            this.fetchFileStructure();
          }
        })
        .catch((err) => {
          /*this.setState({
            showError: "Error occured. Check your credientials.",
          });*/
          console.log(err);
        });
  };
  renameFolder = (element, rename) => {
    const token = localStorage.getItem("jwtToken");
    const url = config.BASE_URL;
    const c_workspace_id = this.props.workspaceId;
    const changePath = this.state.cwd + "/" + element;
    let formData = new FormData();
    formData.append("workspace_id", c_workspace_id);
    formData.append("path", changePath);
    formData.append("new_folder_name", rename);

    axios
      .put(url + "/api/file_system/folder", formData, {
        headers: {
          auth_token: token, //the token is a variable which holds the token
        },
      })
      .then((response) => {
        if (response.status === 200) {
          this.fetchFileStructure();
        }
      })
      .catch((err) => {
        console.log(err);
      });
  };
  deleteFolder = (element) => {
    const token = localStorage.getItem("jwtToken");

    const url = config.BASE_URL;

    const changePath = this.state.cwd + "/" + element;
    let formData = new FormData();
    formData.append("workspace_id", this.props.workspaceId);
    formData.append("path", changePath);
    axios
      .delete(url + "/api/file_system/folder", {
        headers: {
          auth_token: token, //the token is a variable which holds the token
        },
        data: formData,
      })
      .then((response) => {
        if (response.status === 200) {
          this.fetchFileStructure();
        }
      })
      .catch((err) => {
        console.log(err);
      });
  };
  deleteFile = (element) => {
    const token = localStorage.getItem("jwtToken");

    const url = config.BASE_URL;

    const changePath = this.state.cwd;
    let formData = new FormData();
    formData.append("workspace_id", this.props.workspaceId);
    formData.append("path", changePath);
    formData.append("filename", element);
    axios
      .delete(url + "/api/file_system/file", {
        headers: {
          auth_token: token, //the token is a variable which holds the token
        },
        data: formData,
      })
      .then((response) => {
        if (response.status === 200) {
          this.fetchFileStructure();
        }
      })
      .catch((err) => {
        console.log(err);
      });
  };
  handleDeleteFileDialogOpen = (index) => {
    let prevState = this.state.deleteFileDialogArray;
    prevState[index] = true;
    this.setState({ deleteFileDialogArray: prevState });
  };
  handleDeleteFileDialogClose = (index) => {
    let prevState = this.state.deleteFileDialogArray;
    prevState[index] = false;
    this.setState({ deleteFileDialogArray: prevState });
  };
  handleDeleteFolderDialogOpen = (index) => {
    let prevState = this.state.deleteFolderDialogArray;
    prevState[index] = true;
    this.setState({ deleteFolderDialogArray: prevState });
  };
  handleDeleteFolderDialogClose = (index) => {
    let prevState = this.state.deleteFolderDialogArray;
    prevState[index] = false;
    this.setState({ deleteFolderDialogArray: prevState });
  };

  handleRenameFileDialogOpen = () => {
    this.setState({ deleteFileDialog: true });
  };
  handleRenameFileDialogClose = () => {
    this.setState({ deleteFileDialog: false });
  };
  handleRenameFolderDialogOpen = (index) => {
    let prevState = this.state.renameFolderDialogArray;
    prevState[index] = true;
    this.setState({ renameFolderDialogArray: prevState });
  };
  handleRenameFolderDialogClose = (index) => {
    let prevState = this.state.renameFolderDialogArray;
    prevState[index] = false;
    this.setState({ renameFolderDialogArray: prevState });
  };

  render() {
    const { classes } = this.props;

    const nothingToShow =
      this.state.folders.length === 0 && this.state.files.length === 0;
    return (
      <div className={classes.root}>
        <div className={classes.demo}>
          <List>
            {this.state.folders.map((element, index) => (
              <ListItem>
                <ListItemAvatar>
                  <Avatar style={{ backgroundColor: colors.primary }}>
                    <FolderIcon style={{ color: colors.tertiary }} />
                  </Avatar>
                </ListItemAvatar>
                <ListItemText
                  style={{ color: colors.primary }}
                  primary={element}
                />
                <ListItemSecondaryAction>
                  {this.state.cwd !== "." ? (
                    <IconButton
                      onClick={() => this.moveUp(element)}
                      edge="end"
                      aria-label="moveup"
                    >
                      <ArrowBackIcon />
                    </IconButton>
                  ) : null}
                  <IconButton
                    onClick={() => this.moveDown(element)}
                    edge="end"
                    aria-label="movedown"
                  >
                    <ArrowForwardIcon />
                  </IconButton>
                  <IconButton edge="end" aria-label="renameFolder">
                    <TextFieldsIcon
                      style={{ color: colors.quaternary }}
                      onClick={() => this.handleRenameFolderDialogOpen(index)}
                    />
                    <WorkspaceViewFileSectionRenameConfirmation
                      type="folder"
                      element={element}
                      renameFolder={this.renameFolder}
                      renameDialog={this.state.renameFolderDialogArray[index]}
                      handleRenameDialogClose={
                        this.handleRenameFolderDialogClose
                      }
                      index={index}
                    />
                  </IconButton>
                  <IconButton edge="end" aria-label="delete">
                    <DeleteIcon
                      style={{ color: colors.quinary }}
                      onClick={() => this.handleDeleteFolderDialogOpen(index)}
                    />
                    <WorkspaceViewFileSectionDeleteConfirmation
                      type="folder"
                      element={element}
                      deleteFolder={this.deleteFolder}
                      deleteDialog={this.state.deleteFolderDialogArray[index]}
                      handleDeleteDialogClose={
                        this.handleDeleteFolderDialogClose
                      }
                      index={index}
                    />
                  </IconButton>
                </ListItemSecondaryAction>
              </ListItem>
            ))}
            {this.state.files.map((element, index) => (
              <ListItem>
                <ListItemAvatar>
                  <Avatar style={{ backgroundColor: colors.primary }}>
                    <InsertDriveFileIcon style={{ color: colors.quaternary }} />
                  </Avatar>
                </ListItemAvatar>
                <ListItemText
                  style={{ color: colors.primary }}
                  primary={element}
                />
                <ListItemSecondaryAction>
                  <IconButton edge="end" aria-label="delete">
                    <DeleteIcon
                      style={{ color: colors.quinary }}
                      onClick={() => this.handleDeleteFileDialogOpen(index)}
                    />
                    <WorkspaceViewFileSectionDeleteConfirmation
                      type="file"
                      deleteDialog={this.state.deleteFileDialogArray[index]}
                      handleDeleteDialogClose={this.handleDeleteFileDialogClose}
                      element={element}
                      index={index}
                      deleteFile={this.deleteFile}
                    />
                  </IconButton>
                </ListItemSecondaryAction>
              </ListItem>
            ))}

            {nothingToShow ? (
              <div>
                <IconButton
                  onClick={this.moveUp}
                  edge="end"
                  aria-label="moveup"
                >
                  <ArrowBackIcon />
                </IconButton>
                <span style={{ marginLeft: "20px", color: colors.primary }}>
                  Nothing to show
                </span>
              </div>
            ) : null}
          </List>
          <hr />
          <input
            value={this.state.folderName}
            onChange={(e) => this.setState({ folderName: e.target.value })}
            name="folderName"
            label="FolderName"
          />
          <button onClick={this.createFolder}>Create folder</button>
          <input
            type="file"
            onClick={e => (e.target.value = null)}
            onChange={(e) => this.setState({ selectedFile: e.target.files[0] })}
            name="fileuploaded"
            label="fileuploaded"
          />
          <button onClick={this.uploadFile}>Upload File</button>
        </div>
      </div>
    );
  }
}

export default withStyles(useStyles)(WorkspaceViewFileSection);
