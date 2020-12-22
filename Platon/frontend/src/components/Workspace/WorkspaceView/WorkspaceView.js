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
import { Link } from "react-router-dom";

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
    backgroundColor: colors.secondary,
  },
  chip: {
    color: colors.primary,
    backgroundColor: colors.tertiary,
    margin: theme.spacing(0.5),
  },
  section1: {
    margin: theme.spacing(0, 2),
  },
  section2: {
    margin: theme.spacing(2),
  },
  section3: {
    margin: theme.spacing(3, 1, 1),
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

    axios.defaults.headers.common["auth_token"] = `${token}`;
    const url = config.BASE_URL;

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
        /*this.setState({
        isSending: false,
        error: "Error occured. " + err.message,
      });*/
        console.log(err);
      });
  }
  handleChange = (event, newValue) => {
    this.setState({
      value: newValue,
    });
  };
  render() {
    const { classes } = this.props;
    return (
      <div
        className="WorkspaceViewContainer"

      >
        <Navbar />
        <div style={{ backgroundColor: colors.primary}}>
        <div className="container">
          <div style={{ display: "flex", justifyContent: "space-between",paddingTop: "40px", paddingBottom: "10px" }}>
            <Typography
              style={{ color: colors.secondary }}
              component="h1"
              variant="h5"
              align="center"
            >
              View Workspace
            </Typography>
            <Link
              to={`/${this.state.profileId}/workspace`}
              style={{ textDecoration: "none" }}
            >
              Back to workspaces
            </Link>
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
              <Tab
                style={{
                  backgroundColor: colors.secondary,
                  borderRadius: "5px 5px 0px 0px",
                }}
                label="Issues"
                {...a11yProps(2)}
              />
              <Tab
                style={{
                  backgroundColor: colors.secondary,
                  borderRadius: "5px 5px 0px 0px",
                }}
                label="Milestones"
                {...a11yProps(3)}
              />
            </Tabs>
            <div style={{backgroundColor: colors.secondary, width: "100%"}}>
            <div
              className="container"
              style={{
                display: "flex",
                flexDirection: "column",
                alignItems: "center",
                backgroundColor: colors.secondary,
              }}
            >
              {this.state.loaded ? (
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
                        <Grid container alignItems="center">
                          <Grid item xs>
                            <Typography gutterBottom variant="h4">
                              {this.state.workspace.title}
                            </Typography>
                          </Grid>
                        </Grid>
                        <Typography color="textSecondary" variant="h6">
                          {this.state.workspace.description}
                        </Typography>
                      </div>
                      <Divider variant="middle" />
                      <div className={classes.section2}>
                        <div
                          style={{
                            display: "flex",
                            justifyContent: "space-between",
                          }}
                        >
                          <div>
                            <Typography gutterBottom variant="body1">
                              Workspace Privacy
                            </Typography>
                            <div>
                              {this.state.workspace.is_private
                                ? "Private"
                                : "Public"}
                            </div>
                          </div>
                          <div>
                            <Typography gutterBottom variant="body1">
                              Maximum Collaborator Number
                            </Typography>
                            <div>{this.state.workspace.max_collaborators}</div>
                          </div>
                        </div>
                        <Typography gutterBottom variant="body1">
                          Skills
                        </Typography>
                        {this.state.workspace.skills.length !== 0 ? (
                          <div>
                            {this.state.workspace.skills.map((element) => (
                              <Chip className={classes.chip} label={element} />
                            ))}
                          </div>
                        ) : (
                          "Not specified"
                        )}
                        <Typography gutterBottom variant="body1">
                          Requirements
                        </Typography>
                        {this.state.workspace.requirements ? (
                          <div>
                            {this.state.workspace.requirements.join(", ")}
                          </div>
                        ) : (
                          "Not specified"
                        )}
                        <Typography gutterBottom variant="body1">
                          Deadline
                        </Typography>
                        {this.state.workspace.deadline ? (
                          <div>
                            {new Date(this.state.workspace.deadline)
                              .toLocaleString()
                              .substring(0, 10)}
                          </div>
                        ) : (
                          "Not specified"
                        )}
                        <Typography gutterBottom variant="body1">
                          Collaborators
                        </Typography>
                        <div>
                          {this.state.workspace.active_contributors.map(
                            (element) => (
                              <div>
                                {element.name} {element.surname}
                              </div>
                            )
                          )}
                        </div>
                        <Typography gutterBottom variant="body1">
                          State
                        </Typography>
                        <WorkspaceViewStateTimeline
                          state={this.state.workspace.state}
                        />
                      </div>

                      <div className={classes.section3}>
                        <Link
                          to={`/${this.state.profileId}/workspace/${this.props.match.params.workspaceId}/edit`}
                          style={{ textDecoration: "none" }}
                        >
                          <Button color="primary">Edit Workspace</Button>
                        </Link>
                      </div>
                    </div>
                  </TabPanel>
                  <TabPanel value={this.state.value} index={1}>
                    <WorkspaceViewFileSection
                      workspaceId={this.props.match.params.workspaceId}
                    />
                  </TabPanel>
                  <TabPanel value={this.state.value} index={2}>
                    Issues
                    {/*<Issues workspaceId={this.props.match.params.workspaceId} members={this.state.workspace.active_contributors} />*/}
                  </TabPanel>
                  <TabPanel value={this.state.value} index={3}>
                    Milestones
                    {/*<Milestone workspaceId={this.props.match.params.workspaceId} members={this.state.workspace.active_contributors} />*/}
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
