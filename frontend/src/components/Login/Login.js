import React from 'react';
import Avatar from '@material-ui/core/Avatar';
import Button from '@material-ui/core/Button';
import CssBaseline from '@material-ui/core/CssBaseline';
import TextField from '@material-ui/core/TextField';
import Link from '@material-ui/core/Link';
import Grid from '@material-ui/core/Grid';
import Box from '@material-ui/core/Box';
import LockOpenOutlinedIcon from '@material-ui/icons/LockOpenOutlined';
import Typography from '@material-ui/core/Typography';
import { makeStyles, withStyles } from '@material-ui/core/styles';
import Container from '@material-ui/core/Container';

const colorPrimary = "#313638";
const colorPrimaryDark = "#222527";
const colorPrimaryLight = "#5A5E5F";
const colorSecondary = "#E0DFD5";
const colorSecondaryDark = "#9C9C95";
const colorSecondaryLight = "#E6E5DD";
const colorTertiary = "#8F9DD3";
const colorTertiaryDark = "#646D93";
const colorTertiaryLight = "#A5B0DB";
const colorQuaternary = "#B0DB06";
const colorQuaternaryDark = "#7B9904";
const colorQuaternaryLight = "#BFE237";
const colorQuinary = "#FF8B33";
const colorQuinaryDark = "#B26123";
const colorQuinaryLight = "#FFA25B";

const CssTextField = withStyles({
  root: {
    '& .MuiInputBase-input': {
      color: colorSecondary,
    },
    "& .Mui-required": {
      color: colorPrimaryLight,
    },
    '& label.Mui-focused': {
      color: colorTertiary,
    },
    '& .MuiInput-underline:after': {
      borderBottomColor: colorTertiary,
    },
    '& .MuiOutlinedInput-root': {
      '& fieldset': {
        borderColor: colorSecondaryLight,
      },
      '&:hover fieldset': {
        borderColor: colorSecondaryDark,
      },
      '&.Mui-focused fieldset': {
        borderColor: colorTertiary,
      },
    },
  },
})(TextField);

const StyledButton = withStyles({
  root: {
    background: colorTertiary,
    color: colorSecondary,
    '&:hover': {
      backgroundColor: colorTertiaryDark,
    }
  }
})(Button);

const StyledLink = withStyles({
  root: {
    color: colorTertiary,
  }
})(Link);



const useStyles = makeStyles((theme) => ({
  paper: {
    marginTop: theme.spacing(8),
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
  },


  typography:{
    color: colorSecondary,
  },
  avatar: {
    margin: theme.spacing(1),
    backgroundColor: colorTertiary,
    color: colorSecondary,
  },
  form: {
    width: '100%', // Fix IE 11 issue.
    marginTop: theme.spacing(1),
  },
  submit: {
    margin: theme.spacing(3, 0, 2),
  },
}));

export default function Login() {
  const classes = useStyles();

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
        <form className={classes.form} noValidate>
          <CssTextField
            variant="outlined"
            margin="normal"
            required
            fullWidth
            id="email"
            label="Email Address"
            name="email"
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
            autoComplete="current-password"
          />
          <StyledButton
            type="submit"
            fullWidth
            variant="contained"
            color="primary"
            className={classes.submit}
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
        </form>
      </div>
    </Container>
  );
}