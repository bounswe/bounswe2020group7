import React, { Component } from "react";
import AppBar from "../AppBar/AppBar";
import Avatar from "@material-ui/core/Avatar";
import Button from "@material-ui/core/Button";
import CssBaseline from "@material-ui/core/CssBaseline";
import TextField from "@material-ui/core/TextField";
import Typography from "@material-ui/core/Typography";
import { withStyles } from "@material-ui/core/styles";
import Container from "@material-ui/core/Container";
import colors from "../../utils/colors";
import MuiAlert from "@material-ui/lab/Alert";

import AutorenewIcon from "@material-ui/icons/Autorenew";
import "./ResetPassword.css";
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

class ResetPassword extends Component {
  constructor(props) {
    super(props);
    this.state = {
      passwordAgain: "",
      password: "",
      showSuccess: false,
      fieldEmptyError: false,
      showError: false,
    };
  }
  handleCloseFieldEmpty = (event, reason) => {
    if (reason === "clickaway") {
      return;
    }

    this.setState({ fieldEmptyError: false });
  };

  handleCloseError = (event, reason) => {
    if (reason === "clickaway") {
      return;
    }

    this.setState({ showError: false });
  };

  handleCloseSuccess = (event, reason) => {
    if (reason === "clickaway") {
      return;
    }

    this.setState({ showSuccess: false });
  };
  handleSubmit = () => {
    if (this.state.passwordAgain === "" || this.state.password === "") {
      this.setState({ fieldEmptyError: "Fields are required" });
      return;
    }
    if (this.state.passwordAgain!==this.state.password) {
      this.setState({ fieldEmptyError: "Password mismatch" });
      return;
    }
    //TODO error check
    let path = this.props.location.pathname
    const token = path.split('/')[2]

    const url = config.BASE_URL
    const data = { new_password: this.state.password, new_password_repeat: this.state.passwordAgain };

    fetch(url + "/auth_system/reset_password", {
      method: "POST",
      body: JSON.stringify(data),
      headers:{
        'Content-Type': 'application/json',
        'auth_token': token,
      }
    })
      .then((response) => {
        if (response.status === 200) {
          this.setState({
            showSuccess: "We've succesfully reset your password",
          });
          this.setState({

            passwordAgain: "",
            password: "",

          });
        }
        return response.json();
      })
      .catch((err) => {
        console.log(err);
        this.setState({ showError: "Error occured. Check your credientials." });
      });
  };
  render() {
    const { classes } = this.props;

    return (
      <div className="LandingResetPassword">
        <div className="AppBar">
          <AppBar/>
        </div>
        <div>
          <Container component="main" maxWidth="xs">
            <CssBaseline />
            <div className={classes.paper}>
              <Avatar className={classes.avatar}>
                <AutorenewIcon />
              </Avatar>
              <Typography
                component="h1"
                variant="h5"
                className={classes.typography}
              >
                Reset Password
              </Typography>
              <div className={classes.paper}>
                <Typography
                  variant="body1"
                  className={classes.typography}
                  margin="normal"
                >
                  Enter your new password.
                </Typography>

                <CssTextField
                  variant="outlined"
                  margin="normal"
                  required
                  fullWidth
                  name="password"
                  label="New Password"
                  type="password"
                  id="password"
                  value={this.state.password}
                  onChange={(e) => this.setState({ password: e.target.value })}
                  autoComplete="current-password"
                />
                <CssTextField
                  variant="outlined"
                  margin="normal"
                  required
                  fullWidth
                  id="passwordAgain"
                  label="New Password Again"
                  name="passwordAgain"
                  type="password"
                  value={this.state.passwordAgain}
                  onChange={(e) => this.setState({ passwordAgain: e.target.value })}
                  autoComplete="passwordAgain"
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
                  Reset My Password
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
          {this.state.showError && (
            <Snackbar
              open={this.state.showError}
              autoHideDuration={3000}
              onClose={this.handleCloseError}
            >
              <Alert
                style={{ backgroundColor: colors.quinary }}
                severity="error"
                onClose={this.handleCloseError}
              >
                {this.props.showError || this.state.showError}
              </Alert>
            </Snackbar>
          )}
          {this.state.showSuccess && (
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
          )}
            </div>
          </Container>
        </div>
      </div>
    );
  }
}

export default withStyles(useStyles)(ResetPassword);
