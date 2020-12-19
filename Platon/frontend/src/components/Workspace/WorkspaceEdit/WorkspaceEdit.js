import React, { Component } from 'react';
import Navbar from "../../NavBar/NavBar";
import colors from "../../../utils/colors";
import Typography from "@material-ui/core/Typography";
import jwt_decode from "jwt-decode";
import Spinner from "../../Spinner/Spinner";
import "./WorkspaceEdit.css";
import MuiAlert from "@material-ui/lab/Alert";
import Snackbar from "@material-ui/core/Snackbar";
function Alert(props) {
    return <MuiAlert elevation={6} variant="filled" {...props} />;
  }
class WorkspaceEdit extends Component {
    constructor(props) {
        super(props);
        this.state = {
          success: false,
          error: false,
          loaded: true,
          profileId: null
        };
      }
      componentDidMount() {
        const token = localStorage.getItem("jwtToken");
        const decoded = jwt_decode(token);
        this.setState({
            profileId: decoded.id
          })
      }
      render() {
        const { classes } = this.props;
        return (
          <div className="WorkspaceEditContainer">
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
                  <div>
                    <Typography
                      style={{ color: colors.secondary, marginBottom: "20px" }}
                      component="h1"
                      variant="h5"
                      align="center"
                    >
                      Edit Workspace
                    </Typography>
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

export default WorkspaceEdit;