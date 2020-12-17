import React, { Component } from "react";
import Navbar from "../../NavBar/NavBar";
import colors from "../../../utils/colors";
import config from "../../../utils/config";
import WorkspaceStepper from "../WorkspaceStepper/WorkspaceStepper";
import axios from "axios";
import Spinner from "../../Spinner/Spinner";
import MuiAlert from "@material-ui/lab/Alert";
import Snackbar from "@material-ui/core/Snackbar";
import jwt_decode from "jwt-decode";
import './WorkspaceListContainer.css'
function Alert(props) {
  return <MuiAlert elevation={6} variant="filled" {...props} />;
}

class WorkspaceList extends Component {
    constructor(props) {
        super(props);
        this.state = { success: false, error: false, loaded: true }
    }
    componentDidMount(){
        const token = localStorage.getItem("jwtToken");
        const decoded = jwt_decode(token);
        //const profileId = this.props.match.params.profileId

    }
    render() {
        return (
          <div className="WorkspaceListContainer">
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
export default WorkspaceList;