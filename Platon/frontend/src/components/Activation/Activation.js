import React, { Component } from "react";
import AppBar from "../AppBar/AppBar";
import Avatar from "@material-ui/core/Avatar";
import Button from "@material-ui/core/Button";
import CssBaseline from "@material-ui/core/CssBaseline";
import VpnKeyIcon from "@material-ui/icons/VpnKey";
import Typography from "@material-ui/core/Typography";
import { withStyles } from "@material-ui/core/styles";
import Container from "@material-ui/core/Container";
import colors from "../../utils/colors";
import MuiAlert from "@material-ui/lab/Alert";
import HomeIcon from "@material-ui/icons/Home";
import { Link as RouteLink } from "react-router-dom";
import "./Activation.css";
import Snackbar from "@material-ui/core/Snackbar";
import config from "../../utils/config";
import axios from "axios";

const StyledButton = withStyles({
  root: {
    background: colors.tertiary,
    color: colors.secondary,
    "&:hover": {
      backgroundColor: colors.tertiaryDark,
    },
  },
})(Button);

const useStyles = (theme) => ({
  paper: {
    marginTop: theme.spacing(8),
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
  },

  typography: {
    color: colors.secondary,
  },
  avatar: {
    margin: theme.spacing(1),
    backgroundColor: colors.tertiary,
    color: colors.secondary,
  },
  form: {
    width: "100%",
    marginTop: theme.spacing(1),
  },
  submit: {
    margin: theme.spacing(3, 0, 2),
  },
});

function Alert(props) {
  return <MuiAlert elevation={6} variant="filled" {...props} />;
}

class Activation extends Component {
  constructor(props) {
    super(props);
    this.state = {
      showSuccess: false,
      fieldEmptyError: false,
    };
  }
  handleCloseFieldEmpty = (event, reason) => {
    if (reason === "clickaway") {
      return;
    }

    this.setState({ fieldEmptyError: false });
  };
  handleSubmit = () => {
    const url = config.BASE_URL;
    let formData = new FormData();

    formData.append("is_valid", 1);
    var urlParams = new URLSearchParams(this.props.location.search);
    const token= urlParams.get('token')

    axios.put(url + "/api/auth_system/user", formData, {
        headers: {
          'auth_token': token, //the token is a variable which holds the token
        },
      })
      .then((response) => {
        console.log(response)
        if (response.status === 200) {
          this.setState({
            showSuccess: "Your account is verified.",
          });
        }
        return response.json();
      })
      .catch((err) => {

        this.setState({
            fieldEmptyError: "Link is invalid.",
        });
        console.log(err);
      });
  };
  render() {
    const { classes } = this.props;
    return (
      <div className="LandingActivation">
        <div className="AppBar">
          <AppBar />
        </div>
        <div>
          <Container component="main" maxWidth="xs">
            <CssBaseline />
            <div className={classes.paper}>
              <Avatar className={classes.avatar}>
                <VpnKeyIcon />
              </Avatar>
              <Typography
                component="h1"
                variant="h5"
                className={classes.typography}
              >
                Verify Account
              </Typography>
              <div className={classes.paper}>
                <StyledButton
                  type="submit"
                  fullWidth
                  variant="contained"
                  color="primary"

                  className={classes.submit}
                  onClick={this.handleSubmit}
                >
                  Verify my account
                </StyledButton>
              </div>

              {this.state.fieldEmptyError && (
                <Snackbar
                  open={this.state.fieldEmptyError}
                  autoHideDuration={3000}
                  onClose={this.handleCloseFieldEmpty}
                >
                  <Alert
                    style={{ backgroundColor: colors.quinary }}
                    severity="error"
                    onClose={this.handleCloseFieldEmpty}
                  >
                    {this.props.fieldEmptyError || this.state.fieldEmptyError}
                  </Alert>
                </Snackbar>
              )}
            </div>
          </Container>

          <div className={classes.paper}>
            <RouteLink to="/" style={{ textDecoration: "none" }}>
              <Button
                variant="contained"
                style={{ backgroundColor: colors.secondary }}
                className={classes.button}
                startIcon={<HomeIcon />}
              >
                GO TO MAIN PAGE
              </Button>
            </RouteLink>
            <Snackbar
              open={this.state.showSuccess}
              autoHideDuration={3000}
              onClose={this.handleCloseSuccess}
            >
              <Alert
                style={{ backgroundColor: colors.quaternary }}
                severity="success"
                onClose={this.handleCloseSuccess}
              >
                {this.props.showSuccess || this.state.showSuccess}
              </Alert>
            </Snackbar>
          </div>
        </div>
      </div>
    );
  }
}

export default withStyles(useStyles)(Activation);
