import React, { Component } from "react";
import Navbar from "../../NavBar/NavBar";
import colors from "../../../utils/colors";
import config from "../../../utils/config";
import axios from "axios";
import Spinner from "../../Spinner/Spinner";
import MuiAlert from "@material-ui/lab/Alert";
import Snackbar from "@material-ui/core/Snackbar";
import "./WorkspaceList.css";
import { withStyles } from "@material-ui/core/styles";

import Card from "@material-ui/core/Card";
import CardHeader from "@material-ui/core/CardHeader";
import CardContent from "@material-ui/core/CardContent";
import CardActions from "@material-ui/core/CardActions";

import IconButton from "@material-ui/core/IconButton";
import Typography from "@material-ui/core/Typography";
import { red } from "@material-ui/core/colors";

import PageviewIcon from "@material-ui/icons/Pageview";
import DeleteIcon from "@material-ui/icons/Delete";
import AddCircleOutlineIcon from "@material-ui/icons/AddCircleOutline";
import { Link } from "react-router-dom";
import jwt_decode from "jwt-decode";
import CancelOutlinedIcon from "@material-ui/icons/CancelOutlined";
import CheckCircleOutlinedIcon from "@material-ui/icons/CheckCircleOutlined";
import WorkspaceRecommendation from "../../Recommendation/WorkspaceRecommendation/WorkspaceRecommendation";

const useStyles = (theme) => ({
  root: {
    flexGrow: 1,
    marginBottom: theme.spacing(2),
    backgroundColor: colors.secondary,
  },
  demo: {
    marginTop: theme.spacing(3),
    backgroundColor: theme.palette.background.paper,
  },
  title: {
    margin: theme.spacing(4, 0, 2),
  },
  media: {
    height: 0,
    paddingTop: "56.25%", // 16:9
  },
  expand: {
    transform: "rotate(0deg)",
    marginLeft: "auto",
    transition: theme.transitions.create("transform", {
      duration: theme.transitions.duration.shortest,
    }),
  },
  expandOpen: {
    transform: "rotate(180deg)",
  },
  avatar: {
    backgroundColor: red[500],
  },
});

function Alert(props) {
  return <MuiAlert elevation={6} variant="filled" {...props} />;
}

class WorkspaceList extends Component {
  constructor(props) {
    super(props);
    this.state = {
      workspaces: [],
      success: false,
      error: false,
      loaded: false,
      profileId: null,
      inviter: [],
    };
  }
  componentDidMount() {
    this.promise();
  }
  promise = () => {
    Promise.all([
      this.fetchWorkspaces(),
      this.fetchWorkspaceInvitations(),
    ]).then(() => {
      this.setState({
        loaded: true,
      });
    });
  };
  fetchWorkspaces = () => {
    const token = localStorage.getItem("jwtToken");
    const decoded = jwt_decode(token);
    axios.defaults.headers.common["auth_token"] = `${token}`;
    const url = config.BASE_URL;
    this.setState({
      profileId: decoded.id,
    });
    axios
      .get(url + "/api/workspaces/self")
      .then((response) => {
        if (response.status === 200) {
          this.setState({
            workspaces: response.data.workspaces,
          });
        }
      })
      .catch((err) => {
        console.log(err);
      });
  };

  fetchWorkspaceInvitations = () => {
    const token = localStorage.getItem("jwtToken");
    axios.defaults.headers.common["auth_token"] = `${token}`;
    const url = config.BASE_URL;
    return axios
      .get(url + "/api/workspaces/invitations")
      .then((response) => {
        if (response.status === 200) {
          this.setState({
            inviter: response.data,
          });
        }
      })
      .catch((err) => {
        console.log(err);
      });
  };
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

  handleAcceptInvitation = (id) => {
    const token = localStorage.getItem("jwtToken");
    const url = config.BASE_URL;
    let formData = new FormData();
    formData.append("is_accepted", "1");
    formData.append("invitation_id", id);
    axios
      .delete(url + "/api/workspaces/invitations", {
        headers: {
          auth_token: token, //the token is a variable which holds the token
        },
        data: formData,
      })
      .then((response) => {
        if (response.status === 200) {
          this.setState({
            success: "Successfully accepted.",
          });
          this.promise();
        }
      })
      .catch((err) => {
        this.setState({
          error: "Error occured. " + err.response.data.error,
        });
      });
  };
  handleRejectInvitation = (id) => {
    const token = localStorage.getItem("jwtToken");
    const url = config.BASE_URL;
    let formData = new FormData();
    formData.append("is_accepted", "0");
    formData.append("invitation_id", id);
    axios
      .delete(url + "/api/workspaces/invitations", {
        headers: {
          auth_token: token, //the token is a variable which holds the token
        },
        data: formData,
      })
      .then((response) => {
        if (response.status === 200) {
          this.setState({
            success: "Successfully rejected.",
          });
          this.fetchWorkspaceInvitations();
        }
      })
      .catch((err) => {
        console.log(err);
        this.setState({
          error: "Error occured. " + err.response.data.error,
        });
      });
  };

  deleteWorkspace = (element) => {
    const token = localStorage.getItem("jwtToken");
    const url = config.BASE_URL;
    let formData = new FormData();
    formData.append("workspace_id", element);
    axios
      .delete(url + "/api/workspaces", {
        headers: {
          auth_token: token, //the token is a variable which holds the token
        },
        data: formData,
      })
      .then((response) => {
        if (response.status === 200) {
          this.setState({
            success: "Successfully deleted.",
          });
          this.fetchWorkspaces();
        }
      })
      .catch((err) => {
        this.setState({
          error: "Error occured. " + err.response.data.error,
        });
      });
  };
  render() {
    const { classes } = this.props;
    return (
      <div className="WorkspaceListContainer">
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
          {this.state.loaded ? (
            <div
              style={{
                display: "flex",
                flexDirection: "row",
                justifyContent: "space-between",
                alignItems: "baseline",
                width: "100%",
              }}
            >
              <div style={{ width: "55%" }}>
                <Typography
                  style={{ color: colors.secondary, marginBottom: "20px" }}
                  component="h1"
                  variant="h5"
                  align="center"
                >
                  Workspaces
                </Typography>

                {this.state.workspaces.length !== 0 ? (
                  this.state.workspaces.map((item, index) => {
                    return (
                      <Card className={classes.root}>
                        <CardHeader
                          style={{ color: colors.primaryDark }}
                          title={item.title}
                        />

                        <CardContent>
                          <Typography
                            variant="body2"
                            style={{ color: colors.primary }}
                            component="p"
                          >
                            {item.description}
                          </Typography>
                        </CardContent>
                        <CardActions
                          style={{
                            display: "flex",
                            justifyContent: "space-between",
                          }}
                        >
                          <Link
                            to={`/${this.state.profileId}/workspace/${item.id}`}
                          >
                            <IconButton aria-label="view">
                              <PageviewIcon
                                style={{ color: colors.tertiary }}
                              />
                            </IconButton>
                          </Link>

                          <IconButton
                            onClick={() => this.deleteWorkspace(item.id)}
                            aria-label="delete"
                          >
                            <DeleteIcon style={{ color: colors.quinary }} />
                          </IconButton>
                        </CardActions>
                      </Card>
                    );
                  })
                ) : (
                  <Typography
                    style={{ color: colors.secondary, marginBottom: "20px" }}
                    component="h1"
                    variant="h6"
                    align="center"
                  >
                    You have not created any workspace yet.
                  </Typography>
                )}
              </div>
              <div style={{ width: "35%" }}>
                <div
                  style={{
                    display: "flex",
                    flexDirection: "column",
                    alignItems: "center",
                  }}
                >
                  <Link
                    to={`/${this.state.profileId}/workspace/new`}
                    style={{ textDecoration: "none" }}
                  >
                    <IconButton
                      disableRipple
                      aria-label="create"
                      style={{
                        border: "none",
                        borderRadius: "0.5em",
                        backgroundColor: colors.tertiary,
                      }}
                    >
                      <AddCircleOutlineIcon
                        style={{ color: colors.secondary }}
                      />
                      <Typography
                        style={{
                          color: colors.secondaryLight,
                          paddingLeft: "5px",
                        }}
                        component="h1"
                        variant="h5"
                        align="center"
                      >
                        New Workspace
                      </Typography>
                    </IconButton>
                  </Link>
                </div>
                <div style={{ marginTop: "40px" }}>
                  <Typography
                    gutterBottom
                    variant="body1"
                    align="center"
                    style={{ color: colors.tertiary }}
                  >
                    Incoming Workspace Invitations
                  </Typography>
                  {this.state.inviter && this.state.inviter.length === 0 ? (
                    <div
                      style={{ textAlign: "center", color: colors.secondary }}
                    >
                      Nothing to show
                    </div>
                  ) : null}
                  {this.state.inviter.map((inviter, index) => (
                    <div>
                      <div style={{ display: "flex" }}>
                        <h6
                          style={{
                            margin: "auto 0px",
                            color: colors.secondary,
                          }}
                        >
                          {inviter.invitor_fullname} invited you to join{" "}
                          {inviter.workspace_title}
                        </h6>
                        <div>
                          <IconButton
                            onClick={() =>
                              this.handleRejectInvitation(inviter.invitation_id)
                            }
                          >
                            <CancelOutlinedIcon
                              style={{ color: colors.quinary }}
                            />
                          </IconButton>
                          <IconButton
                            onClick={() =>
                              this.handleAcceptInvitation(inviter.invitation_id)
                            }
                          >
                            <CheckCircleOutlinedIcon
                              style={{ color: colors.quaternary }}
                            />
                          </IconButton>
                        </div>
                      </div>
                      <hr style={{ backgroundColor: colors.primaryDark }} />
                    </div>
                  ))}
                </div>

                <div style={{ marginTop: "40px" }}>
                  <Typography
                    gutterBottom
                    variant="body1"
                    align="center"
                    style={{ color: colors.quaternary }}
                  >
                    Recommended Workspaces to You
                  </Typography>
                  <WorkspaceRecommendation />
                </div>
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
export default withStyles(useStyles)(WorkspaceList);
