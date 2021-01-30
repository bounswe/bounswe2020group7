import React, { Component } from "react";
import Navbar from "../../NavBar/NavBar";
import { withStyles } from "@material-ui/core/styles";
import Chip from "@material-ui/core/Chip";
import Button from "@material-ui/core/Button";
import Grid from "@material-ui/core/Grid";
import Divider from "@material-ui/core/Divider";
import colors from "../../../utils/colors";
import Typography from "@material-ui/core/Typography";
import jwt_decode from "jwt-decode";
import Spinner from "../../Spinner/Spinner";
import Tab from "@material-ui/core/Tab";
import "./WorkspaceView.css";
import MuiAlert from "@material-ui/lab/Alert";
import Snackbar from "@material-ui/core/Snackbar";
import WorkspaceViewFileSection from "./WorkspaceViewFileSection/WorkspaceViewFileSection";
import config from "../../../utils/config";
import axios from "axios";
import WorkspaceViewStateTimeline from "./WorkspaceViewStateTimeline/WorkspaceViewStateTimeline";
import Tabs from "@material-ui/core/Tabs";
import { Link, Redirect } from "react-router-dom";
import CancelOutlinedIcon from "@material-ui/icons/CancelOutlined";
import CheckCircleOutlinedIcon from "@material-ui/icons/CheckCircleOutlined";
import IconButton from "@material-ui/core/IconButton";
import WorkspaceViewMilestoneSection from "./WorkspaceViewMilestoneSection";
import WorkspaceViewIssueSection from "./WorkspaceViewIssueSection";
import CollaboratorRecommendationDialog from '../../Recommendation/CollaboratorRecommendation/CollaboratorRecommendationDialog/CollaboratorRecommendationDialog'
import WorkspaceViewTagSearch from "./WorkspaceViewTagSearch/WorkspaceViewTagSearch"
const colorsDark = [
  colors.tertiaryDark,
  colors.quaternaryDark,
  colors.quinaryDark,
  colors.senaryDark,
  colors.septenaryDark
];

const StyledButton = withStyles({
  root: {
    background: colors.tertiary,
    color: colors.secondary,

    "&:hover": {
      backgroundColor: colors.tertiaryDark,
    },
  },
})(Button);

const StyledButtonApply = withStyles({
  root: {
    background: colors.quaternaryDark,
    color: colors.secondary,

    "&:hover": {
      backgroundColor: colors.quaternary,
    },
  },
})(Button);

const StyledButtonQuit = withStyles({
  root: {
    background: colors.quinary,
    color: colors.secondary,

    "&:hover": {
      backgroundColor: colors.quinaryDark,
    },
  },
})(Button);

function TabPanel(props) {
  const { children, value, index, ...other } = props;

  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`simple-tabpanel-${index}`}
      aria-labelledby={`simple-tab-${index}`}
      {...other}
    >
      {value === index && <div>{children}</div>}
    </div>
  );
}

function a11yProps(index) {
  return {
    id: `simple-tab-${index}`,
    "aria-controls": `simple-tabpanel-${index}`,
  };
}

const useStyles = (theme) => ({
  root: {
    margin: theme.spacing(2, 0),
    backgroundColor: colors.secondary,
    minWidth: "480px",
    width: "720px",
  },
  divider: {
    margin: theme.spacing(2),
  },
  chip: {
    color: colors.secondary,
    margin: theme.spacing(0.5),
  },
  section1: {
    margin: theme.spacing(0, 2),
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
  },
  section2: {
    margin: theme.spacing(2),
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
  },

  section3: {
    margin: theme.spacing(3, 1, 1),
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
  },
});

function Alert(props) {
  return <MuiAlert elevation={6} variant="filled" {...props} />;
}

class WorkspaceView extends Component {
  constructor(props) {
    super(props);
    this.state = {
      success: false,
      error: false,
      loaded: false,
      profileId: null,
      workspaceId: null,
      value: 0,
      workspace: {},
      collaboratorIds: [],
      applicants: [],
      unauthorized: false,
      quited: false,
      collaboratorDialog: false,
      tagSearchDialog: false,
      tag: "",
      tagColor: colors.primary
    };
  }

  componentDidMount() {
    const token = localStorage.getItem("jwtToken");
    const decoded = jwt_decode(token);
    const workspaceId = this.props.match.params.workspaceId;
    this.setState({
      profileId: decoded.id,
      workspaceId: workspaceId,
    });

    this.promise();
  }

  promise = () => {
    Promise.all([
      this.fetchWorkspace(),
      this.fetchWorkspaceApplications(),
    ]).then(() => {
      this.setState({
        loaded: true,
      });
    });
  };
  handleChange = (event, newValue) => {
    this.setState({
      value: newValue,
    });
  };
  fetchWorkspace = () => {
    const token = localStorage.getItem("jwtToken");

    axios.defaults.headers.common["auth_token"] = `${token}`;
    const url = config.BASE_URL;
    return axios
      .get(url + "/api/workspaces", {
        params: {
          workspace_id: this.props.match.params.workspaceId,
        },
      })
      .then((response) => {
        if (response.status === 200) {
          let tempArray = [];
          for (var key in response.data.active_contributors) {
            tempArray.push(response.data.active_contributors[key].id);
          }
          this.setState({
            workspace: response.data,
            collaboratorIds: tempArray,
          });
        }
      })
      .catch((err) => {
        if ((err.response.status = 401)) {
          this.setState({
            unauthorized: true,
          });
        }
        this.setState({
          error: "Error occured. " + err.response.data.error,
        });
        console.log(err);
      });
  };
  fetchWorkspaceApplications = () => {
    const token = localStorage.getItem("jwtToken");
    axios.defaults.headers.common["auth_token"] = `${token}`;
    const url = config.BASE_URL;
    return axios
      .get(url + "/api/workspaces/applications", {
        params: {
          workspace_id: this.props.match.params.workspaceId,
        },
      })
      .then((response) => {
        if (response.status === 200) {
          this.setState({
            applicants: response.data,
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
  handleAcceptApplication = (id) => {
    const token = localStorage.getItem("jwtToken");
    const url = config.BASE_URL;
    let formData = new FormData();
    formData.append("is_accepted", "1");
    formData.append("application_id", id);
    axios
      .delete(url + "/api/workspaces/applications", {
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
  handleApply = () => {
    axios.defaults.headers.common["auth_token"] = localStorage.getItem(
      "jwtToken"
    );
    const url = config.BASE_URL;
    let formData = new FormData();
    formData.append("workspace_id", this.props.match.params.workspaceId);
    axios
      .post(url + "/api/workspaces/applications", formData)
      .then((response) => {
        if (response.status === 201) {
          this.setState({ success: "You have successfully applied." });
          this.promise();
        }
      })
      .catch((err) => {
        this.setState({
          error: "Error occured. " + err.response.data.error,
        });
        console.log(err);
      });
  };
  handleQuit = () => {
    const token = localStorage.getItem("jwtToken");
    const url = config.BASE_URL;
    let formData = new FormData();
    formData.append("workspace_id", this.props.match.params.workspaceId);
    axios
      .delete(url + "/api/workspaces/quit", {
        headers: {
          auth_token: token,
        },
        data: formData,
      })
      .then((response) => {
        if (response.status === 201) {
          this.setState({
            success: "Successfully quited.",
            quited: true,
          });
        }
      })
      .catch((err) => {
        this.setState({
          error: "Error occured. " + err.response.data.error,
        });
      });
  };
  handleRejectApplication = (id) => {
    const token = localStorage.getItem("jwtToken");
    const url = config.BASE_URL;
    let formData = new FormData();
    formData.append("is_accepted", "0");
    formData.append("application_id", id);
    axios
      .delete(url + "/api/workspaces/applications", {
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
          this.fetchWorkspaceApplications();
        }
      })
      .catch((err) => {
        this.setState({
          error: "Error occured. " + err.response.data.error,
        });
      });
  };

  handleClickOpen = () => {
    this.setState({collaboratorDialog: true})
  };

  handleClose = () => {
    this.setState({collaboratorDialog: false})
  };

  handleClickOpenTagSearch = (tag, tagColor) => {
    this.setState({tagSearchDialog: true, tag: tag, tagColor: tagColor})
  }
  handleCloseTagSearch = () =>{
    this.setState({tagSearchDialog: false})
  }

  handleErrorText = (text) => {
    this.setState({
      error: text
    })
  }
  handleSuccessText = (text) => {
    this.setState({
      success: text
    })
  }
  render() {
    if (this.state.quited) {
      return <Redirect to={`/${this.state.profileId}/workspace`} />;
    }
    const { classes } = this.props;
    return (
      <div className="WorkspaceViewContainer">
        <Navbar />
        <div style={{ backgroundColor: colors.primary }}>
          <div className="container">
            <div
              style={{
                display: "flex",
                justifyContent: "space-between",
                paddingTop: "40px",
                paddingBottom: "10px",
              }}
            >
              <Typography
                style={{ color: colors.secondary }}
                component="h1"
                variant="h5"
                align="center"
              >
                View Workspace
              </Typography>
              {this.state.collaboratorIds.includes(this.state.profileId) ? (
                <Link
                  to={`/${this.state.profileId}/workspace`}
                  style={{ textDecoration: "none" }}
                >
                  Back to workspaces
                </Link>
              ) : null}
            </div>
          </div>
        </div>
        <div
          style={{
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
          }}
        >
          <Tabs
            value={this.state.value}
            onChange={this.handleChange}
            aria-label="simple tabs example"
            style={{ width: "100%" }}
            centered
            TabIndicatorProps={{ style: { background: colors.secondary } }}
          >
            <Tab
              style={{
                backgroundColor: colors.secondary,
                borderRadius: "5px 5px 0px 0px",
              }}
              label="Details"
              {...a11yProps(0)}
            />
            <Tab
              style={{
                backgroundColor: colors.secondary,
                borderRadius: "5px 5px 0px 0px",
              }}
              label="Files"
              {...a11yProps(1)}
            />
            {this.state.collaboratorIds.includes(this.state.profileId) ? (
              <Tab
                style={{
                  backgroundColor: colors.secondary,
                  borderRadius: "5px 5px 0px 0px",
                }}
                label="Issues"
                {...a11yProps(2)}
              />
            ) : null}
            {this.state.collaboratorIds.includes(this.state.profileId) ? (
              <Tab
                style={{
                  backgroundColor: colors.secondary,
                  borderRadius: "5px 5px 0px 0px",
                }}
                label="Milestones"
                {...a11yProps(3)}
              />
            ) : null}
          </Tabs>
          <div style={{ backgroundColor: colors.secondary, width: "100%" }}>
            <div
              className="container"
              style={{
                display: "flex",
                flexDirection: "column",
                alignItems: "center",
                backgroundColor: colors.secondary,
              }}
            >
              {this.state.loaded && !this.state.unauthorized ? (
                <div
                  style={{
                    display: "flex",
                    justifyContent: "space-around",
                    alignItems: "center",
                    width: "100%",
                  }}
                >
                  <TabPanel value={this.state.value} index={0}>
                    <div className={classes.root}>
                      <div className={classes.section1}>
                        <Typography
                          gutterBottom
                          variant="h4"
                          align="center"
                          style={{ color: colors.tertiaryDark }}
                        >
                          {this.state.workspace.title}
                        </Typography>

                        <Typography
                          color="textSecondary"
                          variant="h6"
                          align="center"
                          style={{ color: colors.tertiary }}
                        >
                          {this.state.workspace.description}
                        </Typography>
                      </div>
                      <Divider
                        className={classes.divider}
                        variant="middle"
                        style={{ backgroundColor: colors.primaryLight }}
                      />
                      <div className={classes.section2}>
                        <Grid container={true} spacing={2}>
                          <Grid
                            item
                            xs
                            container={true}
                            direction="column"
                            alignItems="center"
                          >
                            <Typography
                              gutterBottom
                              variant="body1"
                              align="center"
                              style={{ color: colors.quaternaryDark }}
                            >
                              Workspace Privacy
                            </Typography>
                            <div style={{ color: colors.quinaryDark }}>
                              {this.state.workspace.is_private
                                ? "Private"
                                : "Public"}
                            </div>
                          </Grid>
                          <Grid
                            item
                            xs
                            container={true}
                            direction="column"
                            alignItems="center"
                          >
                            <Typography
                              gutterBottom
                              variant="body1"
                              align="center"
                              style={{ color: colors.quaternaryDark }}
                            >
                              Maximum Collaborator
                            </Typography>

                            {this.state.workspace.max_collaborators !== "" ? (
                              <div style={{ color: colors.quinaryDark }}>
                                {this.state.workspace.max_collaborators}
                              </div>
                            ) : (
                              "Not specified"
                            )}
                          </Grid>
                        </Grid>
                        <Divider
                          className={classes.divider}
                          variant="middle"
                          style={{
                            width: "100%",
                            backgroundColor: colors.primaryLight,
                          }}
                        />
                        <Grid container={true} spacing={2}>
                          <Grid
                            item
                            xs
                            container={true}
                            direction="column"
                            alignItems="center"
                          >
                            <Typography
                              gutterBottom
                              variant="body1"
                              style={{ color: colors.quaternaryDark }}
                            >
                              Requirements
                            </Typography>
                            {this.state.workspace.requirements.length !== 0 &&
                            this.state.workspace.requirements.toString() !==
                              "" ? (
                              <div style={{ color: colors.tertiaryDark }}>
                                {this.state.workspace.requirements.join(", ")}
                              </div>
                            ) : (
                              "Not specified"
                            )}
                          </Grid>

                          <Grid
                            item
                            xs
                            container={true}
                            direction="column"
                            alignItems="center"
                          >
                            <Typography
                              gutterBottom
                              variant="body1"
                              style={{ color: colors.quaternaryDark }}
                            >
                              Tags
                            </Typography>
                            {this.state.workspace.skills &&
                            this.state.workspace.skills.length !== 0 ? (
                              <div>
                                {this.state.workspace.skills.map((element) => {
                                  const tagColor = colorsDark[
                                    Math.floor(
                                      Math.random() * colorsDark.length
                                    )
                                  ]
                                  return  (
                                  <Chip
                                    onClick={()=>this.handleClickOpenTagSearch(element, tagColor)}
                                    className={classes.chip}
                                    style={{
                                      backgroundColor:tagColor

                                    }}
                                    label={element}
                                  />
                                )})}
                            <WorkspaceViewTagSearch tag={this.state.tag} tagColor={this.state.tagColor} workspaceId={this.props.match.params.workspaceId} open={this.state.tagSearchDialog} onClose={this.handleCloseTagSearch}/>

                              </div>
                            ) : (
                              "Not specified"
                            )}
                          </Grid>
                        </Grid>
                        <Divider
                          className={classes.divider}
                          variant="middle"
                          style={{
                            width: "100%",
                            backgroundColor: colors.primaryLight,
                          }}
                        />
                        <Grid container={true} spacing={2}>
                          <Grid
                            item
                            xs
                            container={true}
                            direction="column"
                            alignItems="center"
                          >
                            <Typography
                              gutterBottom
                              variant="body1"
                              align="center"
                              style={{ color: colors.quaternaryDark }}
                            >
                              Deadline
                            </Typography>

                            {this.state.workspace.deadline ? (
                              <div style={{ color: colors.quinaryDark }}>
                                {this.state.workspace.deadline
                                  .split(".")
                                  .reverse()
                                  .join(".")}
                              </div>
                            ) : (
                              "Not specified"
                            )}
                          </Grid>

                          <Grid
                            item
                            xs
                            container={true}
                            direction="column"
                            alignItems="center"
                          >
                            <Typography
                              gutterBottom
                              variant="body1"
                              style={{ color: colors.quaternaryDark }}
                            >
                              Attached Upcoming Events
                            </Typography>
                            {this.state.workspace.upcoming_events &&
                            this.state.workspace.upcoming_events.length !==
                              0 ? (
                              <div>
                                {this.state.workspace.upcoming_events.map(
                                  (element) => (
                                    <div style={{ color: colors.quinaryDark }}>
                                      {element.acronym}
                                    </div>
                                  )
                                )}
                              </div>
                            ) : (
                              "Not specified"
                            )}
                          </Grid>
                        </Grid>
                        <Divider
                          className={classes.divider}
                          variant="middle"
                          style={{
                            width: "100%",
                            backgroundColor: colors.primaryLight,
                          }}
                        />
                        <div style={{ display: "flex", alignItems:"start" }}>
                          <Typography
                            gutterBottom
                            variant="body1"
                            style={{ color: colors.quaternaryDark }}
                          >
                            Collaborators
                          </Typography>
                          {this.state.collaboratorIds.includes(this.state.profileId) ? (
                          <CollaboratorRecommendationDialog handleSuccessText={this.handleSuccessText} handleErrorText={this.handleErrorText} handleCloseError={this.handleCloseError} handleCloseSuccess = {this.handleCloseSuccess} workspaceId={this.props.match.params.workspaceId} open={this.state.collaboratorDialog} onClose={this.handleClose}/>
                          ):null}
                          </div>
                        <div>
                          {this.state.workspace.active_contributors &&
                            this.state.workspace.active_contributors.map(
                              (element) => (
                                <Link
                                  to={`/${element.id}/`}
                                  style={{
                                    textDecoration: "none",
                                    color: colors.quinaryDark,
                                  }}
                                >
                                  <div>
                                    {element.name} {element.surname}
                                  </div>
                                </Link>
                              )
                            )}
                        </div>
                        {!this.state.collaboratorIds.includes(
                          this.state.profileId
                        ) ? (
                          <div>
                            <br />
                            <StyledButtonApply onClick={this.handleApply}>
                              Apply Workspace
                            </StyledButtonApply>
                          </div>
                        ) : null}
                        {this.state.collaboratorIds.includes(
                          this.state.profileId
                        ) &&
                        this.state.profileId !==
                          this.state.workspace.creator_id ? (
                          <div>
                            <br />
                            <StyledButtonQuit onClick={this.handleQuit}>
                              Quit Workspace
                            </StyledButtonQuit>
                          </div>
                        ) : null}
                        {this.state.collaboratorIds.includes(
                          this.state.profileId
                        ) ? (
                          <div style={{ textAlign: "center" }}>
                            <hr />
                            <Typography
                              gutterBottom
                              variant="body1"
                              align="center"
                              style={{ color: colors.quaternaryDark }}
                            >
                              Incoming Requests
                            </Typography>
                            {this.state.applicants &&
                            this.state.applicants.length === 0
                              ? "Nothing to show"
                              : null}
                            {this.state.applicants.map((applicant, index) => (
                              <div style={{ display: "flex" }}>
                                <h6 style={{ margin: "auto 0px" }}>
                                  {applicant.applicant_fullname}
                                </h6>
                                <div>
                                  <IconButton
                                    onClick={() =>
                                      this.handleRejectApplication(
                                        applicant.application_id
                                      )
                                    }
                                  >
                                    <CancelOutlinedIcon
                                      style={{ color: colors.quinary }}
                                    />
                                  </IconButton>
                                  <IconButton
                                    onClick={() =>
                                      this.handleAcceptApplication(
                                        applicant.application_id
                                      )
                                    }
                                  >
                                    <CheckCircleOutlinedIcon
                                      style={{ color: colors.quaternary }}
                                    />
                                  </IconButton>
                                </div>
                              </div>
                            ))}
                          </div>
                        ) : null}
                      </div>
                      <Divider
                        className={classes.divider}
                        variant="middle"
                        style={{
                          width: "100%",
                          backgroundColor: colors.primaryLight,
                        }}
                      />
                      <Typography
                        gutterBottom
                        variant="body1"
                        align="center"
                        style={{ color: colors.quaternaryDark }}
                      >
                        State
                      </Typography>
                      <WorkspaceViewStateTimeline
                        state={this.state.workspace.state}
                      />
                      {this.state.collaboratorIds.includes(
                        this.state.profileId
                      ) ? (
                        <div className={classes.section3}>
                          <Link
                            to={`/${this.props.match.params.profileId}/workspace/${this.props.match.params.workspaceId}/edit`}
                            style={{ textDecoration: "none" }}
                          >
                            <StyledButton color="primary">
                              Edit Workspace
                            </StyledButton>
                          </Link>
                        </div>
                      ) : null}
                    </div>
                  </TabPanel>
                  <TabPanel value={this.state.value} index={1}>
                    <WorkspaceViewFileSection
                      workspaceId={this.props.match.params.workspaceId}
                      collaboratorIds={this.state.collaboratorIds}
                    />
                  </TabPanel>
                  <TabPanel value={this.state.value} index={2}>
                    <WorkspaceViewIssueSection
                      workspaceId={this.props.match.params.workspaceId}
                      members={this.state.workspace.active_contributors}
                    />
                    {/*<Issue workspaceId={this.props.match.params.workspaceId} members={this.state.workspace.active_contributors}/>*/}
                  </TabPanel>
                  <TabPanel value={this.state.value} index={3}>
                    <WorkspaceViewMilestoneSection
                      workspaceId={this.state.workspaceId}
                    />
                  </TabPanel>
                </div>
              ) : (
                <div style={{ marginTop: "150px" }}>
                  <Spinner />
                </div>
              )}
            </div>
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
      </div>
    );
  }
}

export default withStyles(useStyles)(WorkspaceView);
