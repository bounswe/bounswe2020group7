import { React, Component } from 'react';
import Avatar from '@material-ui/core/Avatar';
import Button from '@material-ui/core/Button';
import CssBaseline from '@material-ui/core/CssBaseline';
import TextField from '@material-ui/core/TextField';
import Link from '@material-ui/core/Link';
import Grid from '@material-ui/core/Grid';
import LockOpenOutlinedIcon from '@material-ui/icons/LockOpenOutlined';
import Typography from '@material-ui/core/Typography';
import {  withStyles } from '@material-ui/core/styles';
import Container from '@material-ui/core/Container';
import colors from '../../utils/colors';
import MuiAlert from "@material-ui/lab/Alert";
import axios from "axios";

const CssTextField = withStyles({
  root: {
    '& .MuiInputBase-input': {
      color: colors.secondary,
    },
    "& .Mui-required": {
      color: colors.primaryLight,
    },
    '& label.Mui-focused': {
      color: colors.tertiary,
    },
    '& .MuiInput-underline:after': {
      borderBottomColor: colors.tertiary,
    },
    '& .MuiOutlinedInput-root': {
      '& fieldset': {
        borderColor: colors.secondaryLight,
      },
      '&:hover fieldset': {
        borderColor: colors.secondaryDark,
      },
      '&.Mui-focused fieldset': {
        borderColor: colors.tertiary,
      },
    },
  },
})(TextField);

const StyledButton = withStyles({
  root: {
    background: colors.tertiary,
    color: colors.secondary,
    '&:hover': {
      backgroundColor: colors.tertiaryDark,
    }
  }
})(Button);

const StyledLink = withStyles({
  root: {
    color: colors.tertiary,
  }
})(Link);



const useStyles = (theme) => ({
  paper: {
    marginTop: theme.spacing(8),
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
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
    width: '100%', // Fix IE 11 issue.
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
      email: '',
      password: '',
      error:'',

    }
  }



  handleSubmit  = () => {


    if (this.state.email === "" || this.state.password === "") {
      this.setState({error: "Fields are required"});
      return;
    }
    const url = "https://react-my-burger-78df4.firebaseio.com/users.json";
    const data = { email: this.state.email, password: this.state.password }
   fetch(url, {
      method: 'POST',
      body: JSON.stringify(data)
    }).then(response => {
        if (response.status === 200) {
          console.log("asiye")
          this.props.handlerIsAuthenticated();
        }
        return response.json();
      }).catch(err=> {
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

  }
  render() {
    const { classes } = this.props;
    return (
      <Container component="main" maxWidth="xs">
        <CssBaseline />
        <div className={classes.paper}>
          <Avatar className={classes.avatar}>
            <LockOpenOutlinedIcon />
          </Avatar>
          <Typography component="h1" variant="h5" className={classes.typography}>
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
                <StyledLink href="#" variant="body2">
                  Forgot password?
              </StyledLink>
              </Grid>

            </Grid>

            {this.state.error && (
        <Alert severity="error" onClick={() => this.setState({error:null})}>
          {this.props.error || this.state.error}
        </Alert>
      )}
        </div>
      </Container>
    );
  }
}

export default withStyles(useStyles, CssTextField)(Login);