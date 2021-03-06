import React, { Component } from "react";
import Navbar from "../../NavBar/NavBar";
import colors from "../../../utils/colors";
import config from "../../../utils/config";
import "./WorkspaceCreate.css";
import WorkspaceStepper from "../WorkspaceStepper/WorkspaceStepper";
import axios from "axios";
import Spinner from "../../Spinner/Spinner";
import MuiAlert from "@material-ui/lab/Alert";
import Snackbar from "@material-ui/core/Snackbar";

function Alert(props) {
  return <MuiAlert elevation={6} variant="filled" {...props} />;
}

class WorkspaceCreate extends Component {
  constructor(props) {
    super(props);
    this.state = {
      title: "",
      description: "",
      is_private: 0,
      max_collaborators: "",
      deadline: "",
      requirements: "",
      skills: [],
      upcomingEvents:[],
      loaded: false,
      skillsList: [],
      upcomingEventsList:[],
      error: false,
      success: false,
      isSending: false,
      created: false,
    };
  }
  componentDidMount() {
    const url = config.BASE_URL;
    Promise.all([axios.get(url + "/api/profile/skills")
      .then((response) => {
        if (response) {
          this.setState({

            skillsList: response.data,
          });
        }
      })
      .catch((err) => {
        console.log(err);
      }),
      axios.get(url +"/api/upcoming_events").then((response)=>{
        if(response){
          this.setState({
            upcomingEventsList: response.data.upcoming_events
          })
        }
      })
    ]).then(()=>{
      this.setState({
        loaded: true
      })
    })
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
  handleTitle = (value) => {
    this.setState({
      title: value,
    });
  };
  handleDescription = (value) => {
    this.setState({
      description: value,
    });
  };

  handleIsPrivate = (value) => {
    this.setState({
      is_private: value,
    });
  };
  handleMaxCollaborators = (value) => {
    this.setState({
      max_collaborators: value,
    });
  };
  handleDeadline = (value) => {
    this.setState({
      deadline: value,
    });
  };
  handleRequirements = (value) => {
    this.setState({
      requirements: value,
    });
  };
  handleSkills = (value) => {
    this.setState({
      skills: value,
    });
  };
  handleUpcomingEvents = (value) =>{
    this.setState({
      upcomingEvents: value
    })
  }

  handleSubmit = () => {
    if (this.state.title === "" || this.state.description === "") {
      this.setState({ error: "Title and description are required" });
      return;
    }

    let formData = new FormData();

    formData.append("title", this.state.title);
    formData.append("description", this.state.description);
    if (this.state.is_private !== 0) {
      formData.append("is_private", this.state.is_private);
    }
    if (this.state.max_collaborators !== "") {
      formData.append("max_collaborators", this.state.max_collaborators);
    }
    if (this.state.requirements !== "") {
      let requirementsSent = this.state.requirements.split(",")
      formData.append("requirements", JSON.stringify(requirementsSent));
    }
    if (this.state.skills.length !== 0) {

      formData.append("skills", JSON.stringify(this.state.skills));
    }
    if (this.state.deadline !== "") {
      formData.append("deadline", this.state.deadline);
    }
    if (this.state.upcomingEvents.length !== 0) {
      formData.append("upcoming_events", JSON.stringify(this.state.upcomingEvents));
    }
    this.setState({ isSending: true });
    const url = config.BASE_URL
    const token = localStorage.getItem("jwtToken");
    axios
      .post(url + "/api/workspaces", formData, {
        headers: {
          'auth_token': token //the token is a variable which holds the token
        },
      }).then((response) => {

        if (response.status === 201) {
          this.setState({
            isSending: false,
            created: true,
            success: "Successfully created.",
          });
        }
      })
      .catch((err) => {
        this.setState({
          isSending: false,
          error: "Error occured. " + err.message,
        });
        console.log(err);
      });
  };

  render() {
    return (
      <div className="WorkspaceContainer">
        <Navbar />
        <div
          className="container"
          style={{
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
          }}
        >
          {this.state.loaded ? (
            <div>
              <WorkspaceStepper
                title={this.state.title}
                description={this.state.description}
                skillsList={this.state.skillsList}
                is_private={this.state.is_private}
                max_collaborators={this.state.max_collaborators}
                deadline={this.state.deadline}
                requirements={this.state.requirements}
                skills={this.state.skills}
                upcomingEventsList={this.state.upcomingEventsList}
                upcomingEvents={this.state.upcomingEvents}
                error={this.state.error}
                success={this.state.success}
                isSending={this.state.isSending}
                created={this.state.created}
                handleTitle={this.handleTitle}
                handleDescription={this.handleDescription}
                handleIsPrivate={this.handleIsPrivate}
                handleMaxCollaborators={this.handleMaxCollaborators}
                handleDeadline={this.handleDeadline}
                handleRequirements={this.handleRequirements}
                handleSkills={this.handleSkills}
                handleSubmit={this.handleSubmit}
                handleUpcomingEvents={this.handleUpcomingEvents}
              />

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
          ) : (
            <div style={{ marginTop: "150px" }}>
              <Spinner />
            </div>
          )}
        </div>
      </div>
    );
  }
}

export default WorkspaceCreate;
