import React, { Component } from "react";
import Avatar from "@material-ui/core/Avatar";
import Button from "@material-ui/core/Button";
import CssBaseline from "@material-ui/core/CssBaseline";
import TextField from "@material-ui/core/TextField";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import Checkbox from "@material-ui/core/Checkbox";
import Grid from "@material-ui/core/Grid";
import CreateIcon from "@material-ui/icons/Create";
import Typography from "@material-ui/core/Typography";
import { withStyles } from "@material-ui/core/styles";
import Container from "@material-ui/core/Container";
import colors from "../../utils/colors";
import MuiAlert from "@material-ui/lab/Alert";
import Snackbar from "@material-ui/core/Snackbar";
import AppBar from "../AppBar/AppBar";
import TermsConditions from './TermsConditions/TermsConditions'
import "./Register.css";
import config from "../../utils/config";
import axios from 'axios'

const StyledTextField = withStyles({
  root: {
    "& .MuiInputBase-input": {

      color: colors.secondary,
    },
    "& .Mui-required": {
      color: colors.primaryLight,
    },

    "& .MuiFormLabel-root": {
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

const StyledFormControlLabel = withStyles({
  root: {
    color: colors.tertiary,
    "& .MuiFormControlLabel-label	": {
      color: colors.secondary,
    },
    "&$checked": {
      color: colors.quinary,
    },
  },
})(FormControlLabel);

const StyledCheckbox = withStyles({
  root: {
    color: colors.tertiary,
    "&$checked": {
      color: colors.quaternary,
    },
  },
  checked: {},
})(Checkbox);


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

    width: "100%", // Fix IE 11 issue.
    marginTop: theme.spacing(3),
  },
  submit: {
    margin: theme.spacing(3, 0, 2),
  },

});

function Alert(props) {
  return <MuiAlert elevation={6} variant="filled" {...props} />;
}

class Register extends Component {
  constructor(props) {
    super(props);
    this.state = {
      firstName: "",
      lastName: "",
      email: "",
      password: "",
      job: "",
      checkbox: false,
      showError: false,
      showSuccess: false,
      fieldEmptyError: false,
    };
  }
  handleCheck = (event) => {
    this.setState({ checkbox: event.target.checked });
  };
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
    if (
      this.state.firstName === "" ||
      this.state.lastName === "" ||
      this.state.email === "" ||
      this.state.password === "" ||
      this.state.job === "" ||
      !this.state.checkbox
    ) {
      this.setState({ fieldEmptyError: "Fields are required" });
      return;
    }
    if (!/\S+@\S+\.\S+/.test(this.state.email) ) {
      this.setState({ fieldEmptyError: "Invalid email" });
      return;
    }

    const url = config.BASE_URL

    let formData = new FormData();

    formData.append("e_mail", this.state.email);
    formData.append("password",this.state.password);
    formData.append("name",this.state.firstName);
    formData.append("surname",this.state.lastName);
    formData.append("job",this.state.job);


    axios.post(url+ "/api/auth_system/user", formData)
      .then((response) => {
        console.log(response)
        if (response.status === 201) {
          this.setState({
            showSuccess: "Successfully registered. Please login to continue.",
          });
          this.setState({
            firstName: "",
            lastName: "",
            email: "",
            password: "",
            job: "",
            checkbox: false,
          });
        }
      })
      .catch((err) => {
        this.setState({ showError: "Error occured. Check your credientials." });
        console.log(err);
      });
  };



  render() {
    const { classes } = this.props;
    return (
      <div className="RegisterPage">
        <AppBar />
        <Container component="main" maxWidth="xs">
          <CssBaseline />
          <div className={classes.paper}>
            <Avatar className={classes.avatar}>
              <CreateIcon />
            </Avatar>
            <Typography
              component="h1"
              variant="h5"
              className={classes.typography}
            >
              Register
            </Typography>

            <Grid container spacing={2}>
              <Grid item xs={12} sm={6}>
                <StyledTextField
                  margin="normal"
                  name="firstName"
                  variant="outlined"
                  required
                  fullWidth
                  id="firstName"
                  label="First Name"
                  autoFocus
                  value={this.state.firstName}
                  onChange={(e) => this.setState({ firstName: e.target.value })}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <StyledTextField
                  margin="normal"
                  variant="outlined"
                  required
                  fullWidth
                  id="lastName"
                  label="Last Name"
                  name="lastName"
                  value={this.state.lastName}
                  onChange={(e) => this.setState({ lastName: e.target.value })}
                />
              </Grid>
            </Grid>

            <Grid item xs={12}>
              <StyledTextField
                variant="outlined"
                required
                fullWidth
                id="email"
                margin="normal"
                label="Email Address"
                name="email"
                value={this.state.email}
                onChange={(e) => this.setState({ email: e.target.value })}
              />
              <StyledTextField
                variant="outlined"
                required
                fullWidth
                margin="normal"
                name="password"
                label="Password"
                type="password"
                id="password"
                value={this.state.password}
                onChange={(e) => this.setState({ password: e.target.value })}
              />
              <StyledTextField
                variant="outlined"
                required
                fullWidth
                margin="normal"
                name="job"
                label="Job"
                type="job"
                id="job"
                value={this.state.job}
                onChange={(e) => this.setState({ job: e.target.value })}
              />
            </Grid>

            <StyledFormControlLabel
              control={
                <StyledCheckbox
                  checked={this.state.checkbox}
                  onChange={this.handleCheck}
                  required
                  value="allowExtraEmails"
                />
              }
              label="I accept the terms and conditions"
            />

            <StyledButton
              type="submit"
              fullWidth
              variant="contained"
              color="primary"
              className={classes.submit}
              onClick={this.handleSubmit}
            >
              Register
            </StyledButton>
            <TermsConditions/>
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
    );
  }
}


export default withStyles(useStyles)(Register);
