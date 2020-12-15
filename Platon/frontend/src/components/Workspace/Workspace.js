import React, { Component } from "react";
import Navbar from "../NavBar/NavBar";
import Container from "@material-ui/core/Container";
import Typography from "@material-ui/core/Typography";
import WorkspaceInput from './WorkspaceInput/WorkspaceInput';

import "./Workspace.css";
class Workspace extends Component {
  constructor(props) {
    super(props);
    this.state = {};
  }
  render() {
    return (
      <div className="WorkspaceContainer">
        <Navbar />
        <div className="container" style={{ display: "flex", flexDirection:"column", alignItems: "center"}}>
            <Typography variant="h5" gutterBottom>
                Create a new workspace
            </Typography>
            <WorkspaceInput/>

        </div>
      </div>
    );
  }
}

export default Workspace;
