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
import WorkspaceViewFileSectionEditConfirmation from "./WorkspaceViewFileSectionEditConfirmation/WorkspaceViewFileSectionEditConfirmation.js";
import WorkspaceViewFileSectionPreviewConfirmation from "./WorkspaceViewFileSectionPreviewConfirmation/WorkspaceViewFileSectionPreviewConfirmation.js";
import config from "../../../../utils/config";
import colors from "../../../../utils/colors";
import GetAppIcon from "@material-ui/icons/GetApp";
import EditIcon from "@material-ui/icons/Edit";
import Button from "@material-ui/core/Button";
import VisibilityIcon from "@material-ui/icons/Visibility";
import MuiAlert from "@material-ui/lab/Alert";
import Snackbar from "@material-ui/core/Snackbar";
import jwt_decode from "jwt-decode";

function Alert(props) {
  return <MuiAlert elevation={6} variant="filled" {...props} />;
}

const StyledButton = withStyles({
  root: {
    background: colors.tertiary,
    color: colors.secondary,

    "&:hover": {
      backgroundColor: colors.tertiaryDark,
    },
  },
})(Button);
const useStyles = (theme) => ({
  root: {
    width: '620px',
    marginBottom: theme.spacing(3),
  },
  demo: {
    minWidth: "400px",
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
      profileId: null,
      success: false,
      error: false,
      cwd: ".",
      folders: [],
      files: [],
      fileName: "",
      folderName: "",
      selectedFile: null,
      deleteFileDialogArray: [],
      deleteFolderDialogArray: [],
      renameFolderDialogArray: [],
      editFileDialogArray: [],
      previewFileDialogArray: [],
      flag: "",
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
            previewFileDialogArray: Array(response.data.files.length).fill(
              false
            ),
            isFileEditedArray: Array(response.data.files.length).fill(false),
          });
        }
      })
      .catch((err) => {
        this.setState({
          error: "Error occured. " + err.response.data.error,
        });
        console.log(err);
      });
  };
  componentDidMount() {
    const token = localStorage.getItem("jwtToken");
    const decoded = jwt_decode(token);
    this.setState({
      profileId: decoded.id,
    });
    this.fetchFileStructure();
  }

  handleCloseError = (event, reason) => {
    if (reason === "clickaway") {
      return;
    }

    this.setState({ error: false });
  };

  handleCloseSuccess = (event, reason) => {
    if (reason === "clickaway") {
      return;
    }

    this.setState({ success: false });
  };
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
        this.setState({
          error: "Error occured. " + err.message,
        });
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
        this.setState({
          error: "Error occured. " + err.message,
        });
        console.log(err);
      });
  };
  uploadFile = () => {
    if (this.state.selectedFile === null || this.state.fileName === "") {
      return;
    }
    const token = localStorage.getItem("jwtToken");
    axios.defaults.headers.common["auth_token"] = `${token}`;
    const url = config.BASE_URL;
    const c_workspace_id = this.props.workspaceId;
    let formData = new FormData();
    formData.append("new_file", this.state.selectedFile);
    formData.append("filename", this.state.fileName);
    formData.append("path", this.state.cwd);
    formData.append("workspace_id", c_workspace_id);
    axios
      .post(url + "/api/file_system/file", formData)
      .then((response) => {
        if (response.status === 201) {
          this.setState({
            success: "Uploaded successfully",
            fileName: "",
            selectedFile: null,
          });
          this.fetchFileStructure();
        }
      })
      .catch((err) => {
        this.setState({
          error: "Error occured.",
        });
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
        this.setState({
          error: "Error occured.",
        });
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
  downloadFile = (element) => {
    const token = localStorage.getItem("jwtToken");
    axios.defaults.headers.common["auth_token"] = `${token}`;
    const url = config.BASE_URL;
    let c_workspace_id = this.props.workspaceId;

    axios
      .get(url + "/api/file_system/file", {
        params: {
          path: this.state.cwd,
          workspace_id: c_workspace_id,
          filename: element,
        },
      })
      .then((response) => {
        if (response.status === 200) {
          const downloadUrl = window.URL.createObjectURL(
            new Blob([response.data])
          );
          const link = document.createElement("a");
          link.href = downloadUrl;
          link.setAttribute("download", element);
          document.body.appendChild(link);
          link.click();
        }
      })
      .catch((err) => {
        console.log(err);
      });
  };
  editFile = (name, body, index) => {
    const token = localStorage.getItem("jwtToken");
    axios.defaults.headers.common["auth_token"] = `${token}`;
    const url = config.BASE_URL;
    const c_workspace_id = this.props.workspaceId;
    let fileArray = body.split("\n");
    fileArray.forEach((row, i) => {
      fileArray[i] += "\r\n";
    });
    let new_file = new File(fileArray, "filename");

    let formData = new FormData();
    formData.append("filename", name);
    formData.append("new_file", new_file);
    formData.append("path", this.state.cwd);
    formData.append("workspace_id", c_workspace_id);
    axios
      .put(url + "/api/file_system/file", formData, {
        headers: {
          auth_token: token, //the token is a variable which holds the token
        },
      })
      .then((response) => {
        if (response.status === 200) {
          let oldIsFileEdited = this.state.isFileEditedArray;
          oldIsFileEdited[index] = !this.state.isFileEditedArray[index];
          this.setState({
            success: "Successfully updated.",
            isFileEditedArray: oldIsFileEdited,
          });
        }
      })
      .catch((err) => {
        this.setState({ error: "Error occured. " + err.message });
        console.log(err);
      });
  };
  renameFolder = (element, rename) => {
    if (rename==="") {
      return
    }
    console.log("rename", rename==="", rename)
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
          this.setState({
            success: "Succesfully deleted.",
          });
          this.fetchFileStructure();
        }
      })
      .catch((err) => {
        this.setState({
          error: "Error occurred.",
        });
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

  handleEditFileDialogOpen = (index) => {
    let prevState = this.state.editFileDialogArray;
    prevState[index] = true;
    this.setState({ editFileDialogArray: prevState });
  };
  handleEditFileDialogClose = (index) => {
    let prevState = this.state.editFileDialogArray;
    prevState[index] = false;
    this.setState({ editFileDialogArray: prevState });
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
  handlePreviewFileDialogOpen = (index) => {
    let prevState = this.state.previewFileDialogArray;
    prevState[index] = true;
    this.setState({ previewFileDialogArray: prevState });
  };
  handlePreviewFileDialogClose = (index) => {
    let prevState = this.state.previewFileDialogArray;
    prevState[index] = false;
    this.setState({ previewFileDialogArray: prevState });
  };

  render() {
    const { classes } = this.props;
    const nothingToShow =
      this.state.folders.length === 0 && this.state.files.length === 0;
    return (
      <div className={classes.root}>
        <div className={classes.demo}>
          <List>
            {this.state.cwd !== "." ? (
              <div>
                <IconButton
                  onClick={this.moveUp}
                  edge="end"
                  aria-label="moveup"
                >
                  <ArrowBackIcon />
                </IconButton>
              </div>
            ) : null}
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
                  <IconButton
                    onClick={() => this.moveDown(element)}
                    edge="end"
                    aria-label="movedown"
                  >
                    <ArrowForwardIcon />
                  </IconButton>
                  {this.props.collaboratorIds.includes(this.state.profileId) ? (
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
                  ) : null}
                  {this.props.collaboratorIds.includes(this.state.profileId) ? (
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
                  ) : null}
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
                  {this.props.collaboratorIds.includes(this.state.profileId) ? (
                    <IconButton edge="end" aria-label="download">
                      <GetAppIcon onClick={() => this.downloadFile(element)} />
                    </IconButton>
                  ) : null}
                  <IconButton edge="end" aria-label="download">
                    <VisibilityIcon
                      style={{ color: colors.tertiary }}
                      onClick={() => this.handlePreviewFileDialogOpen(index)}
                    />

                    <WorkspaceViewFileSectionPreviewConfirmation
                      type="file"
                      element={element}
                      pls={this.state.files.length}
                      shouldMount={this.state.isFileEditedArray[index]}
                      previewDialog={this.state.previewFileDialogArray[index]}
                      handlePreviewFileDialogClose={
                        this.handlePreviewFileDialogClose
                      }
                      index={index}
                      cwd={this.state.cwd}
                      c_workspace_id={this.props.workspaceId}
                    />
                  </IconButton>
                  {this.props.collaboratorIds.includes(this.state.profileId) ? (
                    <IconButton edge="end" aria-label="download">
                      <EditIcon
                        style={{ color: colors.quaternary }}
                        onClick={() => this.handleEditFileDialogOpen(index)}
                      />
                      <WorkspaceViewFileSectionEditConfirmation
                        type="file"
                        element={element}
                        editFile={this.editFile}
                        pls={this.state.files.length}
                        editDialog={this.state.editFileDialogArray[index]}
                        handleEditFileDialogClose={
                          this.handleEditFileDialogClose
                        }
                        index={index}
                        cwd={this.state.cwd}
                        c_workspace_id={this.props.workspaceId}
                      />
                    </IconButton>
                  ) : null}
                  {this.props.collaboratorIds.includes(this.state.profileId) ? (
                    <IconButton edge="end" aria-label="delete">
                      <DeleteIcon
                        style={{ color: colors.quinary }}
                        onClick={() => this.handleDeleteFileDialogOpen(index)}
                      />
                      <WorkspaceViewFileSectionDeleteConfirmation
                        type="file"
                        deleteDialog={this.state.deleteFileDialogArray[index]}
                        handleDeleteDialogClose={
                          this.handleDeleteFileDialogClose
                        }
                        element={element}
                        index={index}
                        deleteFile={this.deleteFile}
                      />
                    </IconButton>
                  ) : null}
                </ListItemSecondaryAction>
              </ListItem>
            ))}

            {nothingToShow ? (
              <div
                style={{
                  display: "flex",
                  justifyContent: "space-around",
                  alignItems: "center",
                  color: colors.primary,
                }}
              >
                Nothing to show
              </div>
            ) : null}
          </List>
          <hr />
          <div>
            {this.props.collaboratorIds.includes(this.state.profileId) ? (
              <div>
                <div
                  style={{
                    display: "flex",
                    justifyContent: "center",
                    alignItems: "center",
                  }}
                >
                  <input
                    style={{ marginRight: "8px" }}
                    value={this.state.folderName}
                    onChange={(e) =>
                      this.setState({ folderName: e.target.value })
                    }
                    name="folderName"
                    label="FolderName"
                  />

                  <StyledButton onClick={this.createFolder}>
                    Create folder
                  </StyledButton>
                </div>
                <hr />

                <div
                  style={{
                    display: "flex",
                    justifyContent: "center",
                    alignItems: "center",
                  }}
                >
                  <input
                    type="text"
                    placeholder="Rename"
                    disabled={this.state.selectedFile === null}
                    value={this.state.fileName}
                    onChange={(e) =>
                      this.setState({ fileName: e.target.value })
                    }
                    name="fileuploadedrename"
                    label="fileuploadedrename"
                  />
                  <input
                    type="file"
                    onClick={(e) => {
                      e.target.value = null;
                      this.setState({ selectedFile: null, fileName: "" });
                    }}
                    onChange={(e) =>
                      this.setState({
                        selectedFile: e.target.files[0],
                        fileName: e.target.files[0].name,
                      })
                    }
                    name="fileuploaded"
                    label="fileuploaded"
                  />
                  <StyledButton onClick={this.uploadFile}>
                    Upload File
                  </StyledButton>
                </div>
              </div>
            ) : null}
            {this.state.error && (
              <Snackbar
                open={this.state.error}
                autoHideDuration={3000}
                onClose={this.handleCloseError}
              >
                <Alert
                  style={{ backgroundColor: colors.quinary }}
                  severity="error"
                  onClose={this.handleCloseError}
                >
                  {this.state.error}
                </Alert>
              </Snackbar>
            )}
            {this.state.success && (
              <Snackbar
                open={this.state.success}
                autoHideDuration={3000}
                onClose={this.handleCloseSuccess}
              >
                <Alert
                  style={{ backgroundColor: colors.quaternary }}
                  severity="success"
                  onClose={this.handleCloseSuccess}
                >
                  {this.state.success}
                </Alert>
              </Snackbar>
            )}

          </div>
        </div>
      </div>
    );
  }
}

export default withStyles(useStyles)(WorkspaceViewFileSection);
