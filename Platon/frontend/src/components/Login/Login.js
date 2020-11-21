
import { React, Component } from "react";
import Avatar from "@material-ui/core/Avatar";
import Button from "@material-ui/core/Button";
import CssBaseline from "@material-ui/core/CssBaseline";
import TextField from "@material-ui/core/TextField";
import Link from "@material-ui/core/Link";
import Grid from "@material-ui/core/Grid";
import LockOpenOutlinedIcon from "@material-ui/icons/LockOpenOutlined";
import Typography from "@material-ui/core/Typography";
import { withStyles } from "@material-ui/core/styles";
import Container from "@material-ui/core/Container";
import colors from "../../utils/colors";
import MuiAlert from "@material-ui/lab/Alert";
import { Link as RouteLink, Redirect } from "react-router-dom";
import Snackbar from "@material-ui/core/Snackbar";
import AppBar from "../AppBar/AppBar";
import "./Login.css";
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

const StyledLink = withStyles({
  root: {
    color: colors.tertiary,
  },
})(Link);

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

    marginTop: theme.spacing(1),
  },
  submit: {
    margin: theme.spacing(3, 0, 2),
  },
});
function Alert(props) {
  return <MuiAlert elevation={6} variant="filled" {...props} />;
}

class Login extends Component {
  constructor(props) {
    super(props);
    this.state = {

      email: "",
      password: "",
      fieldEmptyError: false,
      showError: false,
      isLoggedIn: false,
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

  handleSubmit = () => {
    if (this.state.email === "" || this.state.password === "") {
      this.setState({ fieldEmptyError: "Fields are required" });
      return;
    }
    if (!/\S+@\S+\.\S+/.test(this.state.email) ) {
      this.setState({ fieldEmptyError: "Invalid email" });
      return;
    }

    const url = "https://react-my-burger-78df4.firebaseio.com";
    const data = { email: this.state.email, password: this.state.password };
    fetch(url+"/users.json", {
    const url = config.BASE_URL
    const data = { email: this.state.email, password: this.state.password };
    fetch(url + "/api/auth_system/login", {
      method: "POST",
      body: JSON.stringify(data),
    })
      .then((response) => {
        if (response.status === 200) {
          this.setState({ isLoggedIn: true });
          this.props.handlerIsAuthenticated();
        }
        return response.json();
      })
      .catch((err) => {
        this.setState({ showError: "Error occured. Check your credientials." });
        console.log(err);

      });
    //const data = { email: this.state.email, password: this.state.password }
    /*axios.post(url, { email: this.state.email, password: this.state.password })
    .then( (response) => {
      //if(response.statusCode === 200){
      debugger;
      if(response){
        this.props.handlerIsAuthenticated();      // Your function call
      }
    })
    .catch( (error) =>{
      debugger;
      console.log(error);
    });*/

  };
  render() {
    if (this.state.isLoggedIn) {
      return <Redirect to="/" />;
    }

    const { classes } = this.props;

    return (
      <div className="LoginPage">
        <AppBar />
        <div>
          <Container component="main" maxWidth="xs">
            <CssBaseline />
            <div className={classes.paper}>
              <Avatar className={classes.avatar}>
                <LockOpenOutlinedIcon />
              </Avatar>
              <Typography
                component="h1"
                variant="h5"
                className={classes.typography}
              >
                Login
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
              <CssTextField
                variant="outlined"
                margin="normal"
                required
                fullWidth
                name="password"
                label="Password"
                type="password"
                id="password"
                value={this.state.password}
                onChange={(e) => this.setState({ password: e.target.value })}
                autoComplete="current-password"
              />
              <StyledButton
                type="submit"
                fullWidth
                variant="contained"
                color="primary"
                className={classes.submit}
                onClick={this.handleSubmit}
              >
                Sign In
              </StyledButton>
              <Grid container>
                <Grid item xs>
                  <RouteLink to="/forgotpassword">
                    <StyledLink href="#" variant="body2">
                      Forgot password?
                    </StyledLink>
                  </RouteLink>
                </Grid>
              </Grid>

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
            </div>
          </Container>
        </div>
      </div>

    );
  }
}


export default withStyles(useStyles)(Login);

