import React, { Component } from "react";
import Navbar from "../../NavBar/NavBar";
import colors from "../../../utils/colors";
import config from "../../../utils/config";
import axios from "axios";
import Typography from "@material-ui/core/Typography";
import jwt_decode from "jwt-decode";
import Spinner from "../../Spinner/Spinner";
import { withStyles } from "@material-ui/core/styles";
import MuiAlert from "@material-ui/lab/Alert";
import Snackbar from "@material-ui/core/Snackbar";
import Button from "@material-ui/core/Button";
import WorkspaceInputOptional from "../WorkspaceInput/WorkspaceInputOptional/WorkspaceInputOptional";
import WorkspaceInputRequired from "../WorkspaceInput/WorkspaceInputRequired/WorkspaceInputRequired";
import WorkspaceInputDate from "../WorkspaceInput/WorkspaceInputDate/WorkspaceInputDate";
import WorkspaceInputState from "../WorkspaceInput/WorkspaceInputState/WorkspaceInputState";
import {Link} from 'react-router-dom'

import "./WorkspaceEdit.css";

const StyledButton = withStyles({
  root: {
    background: colors.tertiary,
    color: colors.secondary,

    "&:hover": {
      backgroundColor: colors.tertiaryDark,
    },
  },
})(Button);
function Alert(props) {
  return <MuiAlert elevation={6} variant="filled" {...props} />;
}
class WorkspaceEdit extends Component {
  constructor(props) {
    super(props);
    this.state = {
      success: false,
      error: false,
      loaded: false,
      profileId: null,
      workspace: {},
      skillsList: [],
    };
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
  fetchWorkspace = () => {
    const token = localStorage.getItem("jwtToken");
    const decoded = jwt_decode(token);
    const workspaceId = this.props.match.params.workspaceId;
    this.setState({
      profileId: decoded.id,
    });
    const url = config.BASE_URL;
    axios.defaults.headers.common["auth_token"] = `${token}`;
    Promise.all([
      axios
        .get(url + "/api/profile/skills")
        .then((response) => {
          if (response) {
            this.setState({
              skillsList: response.data,
              profileId: decoded.id,
            });
          }
        })
        .catch((err) => {
          console.log(err);
        }),
      axios
        .get(url + "/api/workspaces", {
          params: {
            workspace_id: workspaceId,
          },
        })
        .then((response) => {
          if (response.status === 200) {
            this.setState({
              workspace: response.data,
              loaded: true,
            });
          }
        })
        .catch((err) => {
          this.setState({
            error: "Error occured. " + err.message,
          });
          console.log(err);
        }),
    ]).then(() => this.setState({ loaded: true }));
  }
  componentDidMount() {
    this.fetchWorkspace();
  }
  handleTitle = (value) => {
    this.setState({
      workspace: { ...this.state.workspace, title: value },
    });
  };
  handleDescription = (value) => {
    this.setState({
      workspace: { ...this.state.workspace, description: value },
    });
  };
  handleIsPrivate = (value) => {
    this.setState({
      workspace: { ...this.state.workspace, is_private: value },
    });
  };
  handleMaxCollaborators = (value) => {
    this.setState({
      workspace: { ...this.state.workspace, max_collaborators: value },
    });
  };
  handleDeadline = (value) => {
    this.setState({
      workspace: { ...this.state.workspace, deadline: value },
    });
  };
  handleRequirements = (value) => {
    this.setState({
      workspace: { ...this.state.workspace, requirements: value },
    });
  };
  handleState = (value) => {
    this.setState({
      workspace: { ...this.state.workspace, state: value },
    });
  };
  handleSkills = (value) => {
    this.setState({
      workspace: { ...this.state.workspace, skills: value },
    });
  };
  handleSubmit = () => {
    if (this.state.title === "" || this.state.description === "") {
      this.setState({ error: "Title and description are required" });
      return;
    }

    let formData = new FormData();
    formData.append("workspace_id", this.props.match.params.workspaceId)
    formData.append("title", this.state.workspace.title);
    formData.append("description", this.state.workspace.description);
    formData.append("state", this.state.workspace.state);
    formData.append("max_collaborators",this.state.workspace.max_collaborators);
    formData.append("skills", JSON.stringify(this.state.workspace.skills));

    // is_private handler
    let isPrivateEdited = this.state.workspace.is_private
    if(typeof this.state.workspace.is_private !== 'number'){
      isPrivateEdited = isPrivateEdited ? 1 : 0;
    }
    formData.append("is_private", isPrivateEdited);

    // requirements handler
    let requirementsEdited = this.state.workspace.requirements
    if(typeof this.state.workspace.requirements !== 'string'){
      requirementsEdited = requirementsEdited.toString();
    }
    requirementsEdited = requirementsEdited.split(",");
    formData.append("requirements", JSON.stringify(requirementsEdited));

    // deadline handler
    let deadlineEdited = this.state.workspace.deadline

    if(typeof this.state.workspace.deadline == 'string'){
      deadlineEdited = new Date(this.state.workspace.deadline).toISOString().slice(0, 10);
    }
    formData.append("deadline", deadlineEdited);

    const url = config.BASE_URL;
    const token = localStorage.getItem("jwtToken");
    axios
      .put(url + "/api/workspaces", formData, {
        headers: {
          auth_token: token,
        },
      })
      .then((response) => {
        if (response.status === 200 || response.status === 202) {
          this.setState({
            success: "Successfully changed.",
          });
          this.fetchWorkspace();
        }
      })
      .catch((err) => {
        this.setState({
          error: "Error occured. " + err.message,
        });
        console.log(err);
      });
  };
  render() {
    return (
      <div className="WorkspaceEditContainer">
        <Navbar />
        <div
          className="container"
          style={{
            marginTop: "40px",
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            paddingBottom: "50px"
          }}
        >
          {this.state.loaded ? (

              <div style={{width:"100%"}}>
                <div style={{display:"flex", justifyContent: "space-between"}}>
                <Typography
                  style={{ color: colors.secondary, marginBottom: "20px" }}
                  component="h1"
                  variant="h5"
                  align="center"
                >
                  Edit Workspace
                </Typography>
                <Link to = {`/${this.state.profileId}/workspace/${this.props.match.params.workspaceId}`} style={{textDecoration: "none"}}>
                  Back to workspace page
                </Link>
                </div>
                <WorkspaceInputRequired
                  title={this.state.workspace.title}
                  handleTitle={this.handleTitle}
                  description={this.state.workspace.description}
                  handleDescription={this.handleDescription}
                />
                <WorkspaceInputState
                  state={this.state.workspace.state}
                  handleState={this.handleState}
                />
                <WorkspaceInputOptional
                  skillsList={this.state.skillsList}
                  is_private={this.state.workspace.is_private}
                  handleIsPrivate={this.handleIsPrivate}
                  max_collaborators={this.state.workspace.max_collaborators}
                  handleMaxCollaborators={this.handleMaxCollaborators}
                  requirements={this.state.workspace.requirements}
                  handleRequirements={this.handleRequirements}
                  skills={this.state.workspace.skills}
                  handleSkills={this.handleSkills}
                />
                <WorkspaceInputDate
                  deadline={new Date(this.state.workspace.deadline)
                    .toISOString()
                    .slice(0, 10)}
                  handleDeadline={this.handleDeadline}
                />
                <hr/>
                <div style={{display:"flex", justifyContent: "center", alignItems: "center"}}>
            <StyledButton
              variant="contained"
              onClick={this.handleSubmit}
            >
              Save
            </StyledButton></div>
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

export default WorkspaceEdit;
