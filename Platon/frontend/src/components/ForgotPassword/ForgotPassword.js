import React, { Component } from "react";
import AppBar from "../AppBar/AppBar";
import Avatar from "@material-ui/core/Avatar";
import Button from "@material-ui/core/Button";
import CssBaseline from "@material-ui/core/CssBaseline";
import TextField from "@material-ui/core/TextField";
import VpnKeyIcon from "@material-ui/icons/VpnKey";
import Typography from "@material-ui/core/Typography";
import { withStyles } from "@material-ui/core/styles";
import Container from "@material-ui/core/Container";
import colors from "../../utils/colors";
import MuiAlert from "@material-ui/lab/Alert";
import HomeIcon from "@material-ui/icons/Home";
import { Link as RouteLink } from "react-router-dom";
import "./ForgotPassword.css";
import Snackbar from "@material-ui/core/Snackbar";
import config from "../../utils/config";

const CssTextField = withStyles({
  root: {
    "& .MuiInputBase-input": {
      color: colors.secondary,
    },
    "& .Mui-required": {
      color: colors.primaryLight,
    },
    "& label.Mui-focused": {
      color: colors.tertiary,
    },
    "& .MuiInput-underline:after": {
      borderBottomColor: colors.tertiary,
    },
    "& .MuiOutlinedInput-root": {
      "& fieldset": {
        borderColor: colors.secondaryLight,
      },
      "&:hover fieldset": {
        borderColor: colors.secondaryDark,
      },
      "&.Mui-focused fieldset": {
        borderColor: colors.tertiary,
      },
    },
  },
})(TextField);

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

class ForgotPassword extends Component {
  constructor(props) {
    super(props);
    this.state = {
      email: "",
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
    if (this.state.email === "") {
      this.setState({ fieldEmptyError: "Fields are required" });
      return;
    }
    if (!/\S+@\S+\.\S+/.test(this.state.email) ) {
      this.setState({ fieldEmptyError: "Invalid email" });
      return;
    }
    const url = config.BASE_URL
    const data = { e_mail: this.state.email };
    fetch(url + "/auth_system/reset_password", {
      method: "GET",
      body: JSON.stringify(data),
    })
      .then((response) => {
        if (response.status === 200) {
          this.setState({
            showSuccess: "We've sent an email to entered email address.",
          });
        }
        return response.json();
      })
      .catch((err) => {
        console.log(err);
      });
  };
  render() {
    const { classes } = this.props;
    return (
      <div className="LandingForgotPassword">
        <div className="AppBar">
          <AppBar/>
        </div>
        <div>
          {!this.state.showSuccess ? (
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
                  Forgot Password
                </Typography>
                <div className={classes.paper}>
                  <Typography
                    variant="body1"
                    className={classes.typography}
                    margin="normal"
                  >
                    Enter your email address. We'll send you an email providing
                    a link to reset your password.
                  </Typography>
                  <CssTextField
                    variant="outlined"
                    margin="normal"
                    required
                    fullWidth
                    id="email"
                    label="Email Address"
                    name="email"
                    value={this.state.email}
                    onChange={(e) => this.setState({ email: e.target.value })}
                    autoComplete="email"
                    autoFocus
                  />
                  <StyledButton
                    type="submit"
                    fullWidth
                    variant="contained"
                    color="primary"
                    className={classes.submit}
                    onClick={this.handleSubmit}
                  >
                    Send Reset Password Link
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
          ) : (
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
          )}
        </div>
      </div>
    );
  }
}

export default withStyles(useStyles)(ForgotPassword);
