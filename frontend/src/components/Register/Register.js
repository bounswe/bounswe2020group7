import Avatar from '@material-ui/core/Avatar';
import Button from '@material-ui/core/Button';
import CssBaseline from '@material-ui/core/CssBaseline';
import TextField from '@material-ui/core/TextField';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import Checkbox from '@material-ui/core/Checkbox';
import Grid from '@material-ui/core/Grid';
import CreateIcon from '@material-ui/icons/Create';
import Typography from '@material-ui/core/Typography';
import { makeStyles, withStyles } from '@material-ui/core/styles';
import Container from '@material-ui/core/Container';
import colors from '../../utils/colors';
import Tags from './Tags';
const StyledTextField = withStyles({
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


const StyledFormControlLabel = withStyles({
  root: {
    color: colors.tertiary,
    '& .MuiFormControlLabel-label	': {
      color: colors.secondary,
    },
    '&$checked': {
      color: colors.quinary
    },
  }
})(FormControlLabel);


const StyledCheckbox = withStyles({
  root: {
    color: colors.tertiary,
    '&$checked': {
      color: colors.quaternary,
    },

  },
  checked: {},
})(Checkbox);


const useStyles = makeStyles((theme) => ({

  paper: {
    marginTop: theme.spacing(8),
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',

  },

  typography:{
    color: colors.secondary,
  },
  avatar: {
    margin: theme.spacing(1),
    backgroundColor: colors.tertiary,
    color: colors.secondary,
  },

  form: {
    width: '100%', // Fix IE 11 issue.
    marginTop: theme.spacing(3),
  },
  submit: {
    margin: theme.spacing(3, 0, 2),
  },
}));



export default function Register() {
  const classes = useStyles();

  return (
    <Container component="main" maxWidth="xs">
      <CssBaseline />
      <div className={classes.paper}>
        <Avatar className={classes.avatar}>
          <CreateIcon />
        </Avatar>
        <Typography component="h1" variant="h5" className={classes.typography}>
          Register
        </Typography>
        <form className={classes.form} noValidate>
          <Grid container spacing={2}>
            <Grid item xs={12} sm={6}>
              <StyledTextField
                autoComplete="fname"
                name="firstName"
                variant="outlined"
                required
                fullWidth
                id="firstName"
                label="First Name"
                autoFocus

              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <StyledTextField
                variant="outlined"
                required
                fullWidth
                id="lastName"
                label="Last Name"
                name="lastName"
                autoComplete="lname"
              />
            </Grid>
            <Grid item xs={12}>
              <StyledTextField
                variant="outlined"
                required
                fullWidth
                id="email"
                label="Email Address"
                name="email"
                autoComplete="email"
              />
            </Grid>
            <Grid item xs={12}>
              <StyledTextField
                variant="outlined"
                required
                fullWidth
                name="password"
                label="Password"
                type="password"
                id="password"
                autoComplete="current-password"
              />
            </Grid>
            <Grid item xs={12}>
              <StyledFormControlLabel
                control={<StyledCheckbox value="allowExtraEmails"/>}
                label="I accept the terms and conditions"
              />
            </Grid>

          </Grid>
          <StyledButton
            type="submit"
            fullWidth
            variant="contained"
            color="primary"
            className={classes.submit}
          >
            Register
          </StyledButton>
        </form>
      </div>
    </Container>
  );
}