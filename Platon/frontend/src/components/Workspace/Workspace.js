import React, { Component } from "react";
import Navbar from "../NavBar/NavBar";
import WorkspaceInput from "./WorkspaceInput/WorkspaceInput";
import colors from "../../utils/colors";
import config from "../../utils/config";
import "./Workspace.css";
import WorkspaceStepper from "./WorkspaceStepper/WorkspaceStepper";
import axios from "axios";
import Spinner from "../Spinner/Spinner";
class Workspace extends Component {
  constructor(props) {
    super(props);
    this.state = {
      title: "",
      description: "",
      is_private: 0,
      max_collaborators: "",
      deadline: null,
      requirements: "",
      skills: [],
      loaded: false,
      skillsList: [],
    };
  }
  componentDidMount() {
    const url = config.BASE_URL;
    axios
      .get(url + "/api/profile/skills")
      .then((response) => {
        if (response) {
          this.setState({
            skillsList: response.data,
            loaded: true,
          });
        }
        return response;
      })
      .catch((err) => {
        console.log(err);
        return err.response;
      });
  }

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
      skills: value
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
            <WorkspaceStepper
              title={this.state.title}
              description={this.state.description}
              skillsList={this.state.skillsList}
              is_private={this.state.is_private}
              max_collaborators={this.state.max_collaborators}
              deadline={this.state.deadline}
              requirements={this.state.requirements}
              skills={this.state.skills}
              handleTitle={this.handleTitle}
              handleDescription={this.handleDescription}
              handleIsPrivate = {this.handleIsPrivate}
              handleMaxCollaborators={this.handleMaxCollaborators}
              handleDeadline = {this.handleDeadline}
              handleRequirements = {this.handleRequirements}
              handleSkills={this.handleSkills}
            />
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

export default Workspace;
