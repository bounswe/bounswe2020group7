import React, { Component } from "react";
import Navbar from "../../NavBar/NavBar";
import colors from "../../../utils/colors";
import Typography from "@material-ui/core/Typography";
import jwt_decode from "jwt-decode";
import Spinner from "../../Spinner/Spinner";
import "./WorkspaceView.css";
import MuiAlert from "@material-ui/lab/Alert";
import Snackbar from "@material-ui/core/Snackbar";
import WorkspaceViewFileSection from "./WorkspaceViewFileSection/WorkspaceViewFileSection";
import config from "../../../utils/config"
import axios from "axios";

function Alert(props) {
  return <MuiAlert elevation={6} variant="filled" {...props} />;
}
class WorkspaceView extends Component {
  constructor(props) {
    super(props);
    this.state = {
      success: false,
      error: false,
      loaded: true,
      profileId: null,
      workspaceId: null,
      workspace: {}
    };
  }
  componentDidMount() {
    const token = localStorage.getItem("jwtToken");
    const decoded = jwt_decode(token);
    const workspaceId = this.props.match.params.workspaceId
    this.setState({
      profileId: decoded.id,
      workspaceId: workspaceId
    });

    axios.defaults.headers.common["auth_token"] = `${token}`;
    const url = config.BASE_URL

    axios.get(url+'/api/workspaces', {
      params: {
        workspace_id: workspaceId
      }
    }).then((response) => {
      console.log(response.data)
      if (response.status === 200) {
        this.setState({
          workspace: response.data,
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
    const { classes } = this.props;
    return (
      <div className="WorkspaceViewContainer">
        <Navbar />
        <div
          className="container"
          style={{
            marginTop: "50px",
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
          }}
        >
          <Typography
            style={{ color: colors.secondary, marginBottom: "20px" }}
            component="h1"
            variant="h5"
            align="center"
          >
            View Workspace
          </Typography>
          {this.state.loaded ? (
            <div
              style={{
                display: "flex",
                flexDirection: "row",
                justifyContent: "space-around",
                width: "100%",
              }}
            >
              <div>
                <Typography
                  style={{ color: colors.secondary, marginBottom: "20px" }}
                  component="h1"
                  variant="h6"
                  align="center"
                >
                  Workspace Details
                </Typography>
                <Typography
                  style={{ color: colors.secondary, marginBottom: "20px" }}
                  component="h1"
                  variant="subtitle1"
                  align="center"
                >
                  Title: {this.state.workspace ? this.state.workspace.title : "Loading..."}
                </Typography>
                <Typography
                  style={{ color: colors.secondary, marginBottom: "20px" }}
                  component="h1"
                  variant="subtitle1"
                  align="center"
                >
                  Description: {this.state.workspace ? this.state.workspace.description : "Loading..."}
                </Typography>
                <Typography
                  style={{ color: colors.secondary, marginBottom: "20px" }}
                  component="h1"
                  variant="subtitle1"
                  align="center"
                >
                  Deadline: {this.state.workspace ? (this.state.workspace.deadline ? this.state.workspace.deadline : "Not specified") : "Loading..."}
                </Typography>
                <Typography
                  style={{ color: colors.secondary, marginBottom: "20px" }}
                  component="h1"
                  variant="subtitle1"
                  align="center"
                >
                  Workspace Privacy: {this.state.workspace ? (this.state.workspace.is_private ? "Private" : "Public") : "Loading..."}
                </Typography>
                <Typography
                  style={{ color: colors.secondary, marginBottom: "20px" }}
                  component="h1"
                  variant="subtitle1"
                  align="center"
                >
                  Number of Maximum Collaborator: {this.state.workspace ? (this.state.workspace.max_collaborators ? this.state.workspace.max_collaborators : "Not specified"): "Loading..."}
                </Typography>
                <Typography
                  style={{ color: colors.secondary, marginBottom: "20px" }}
                  component="h1"
                  variant="subtitle1"
                  align="center"
                >
                  Requirements: {this.state.workspace ? (this.state.workspace.requirements ? this.state.workspace.requirements  : "Not specified") : "Loading..."}
                </Typography>
                <Typography
                  style={{ color: colors.secondary, marginBottom: "20px" }}
                  component="h1"
                  variant="subtitle1"
                  align="center"
                >
                  Skills: {this.state.workspace ? (this.state.workspace.skills ? this.state.workspace.skills : "Not specified") : "Loading..."}
                </Typography>
                <Typography
                  style={{ color: colors.secondary, marginBottom: "20px" }}
                  component="h1"
                  variant="subtitle1"
                  align="center"
                >

                  State: {this.state.workspace ? (this.state.workspace.state !== null ? (this.state.workspace.state === 0 ? "Search For Collaborators": (this.state.workspace.state === 1 ? "Ongoing" : "Published"))  : "Not specified") : "Loading..."}
                </Typography>

                <Typography
                  style={{ color: colors.secondary, marginBottom: "20px" }}
                  component="h1"
                  variant="h6"
                  align="center"
                >
                  Workspace Files
                </Typography>
                <WorkspaceViewFileSection workspaceId={this.props.match.params.workspaceId} />
              </div>
            </div>
          ) : (
            <div style={{ marginTop: "150px" }}>
              <Spinner />
            </div>
          )}
        </div>
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
    );
  }
}

export default WorkspaceView;
