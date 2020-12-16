import React, { Component } from 'react';
import { makeStyles, withStyles} from '@material-ui/core/styles';
import Stepper from '@material-ui/core/Stepper';
import Step from '@material-ui/core/Step';
import StepLabel from '@material-ui/core/StepLabel';
import Button from '@material-ui/core/Button';
import Typography from '@material-ui/core/Typography';
import WorkspaceInputOptional from '../WorkspaceInput/WorkspaceInputOptional/WorkspaceInputOptional';
import WorkspaceInputRequired from '../WorkspaceInput/WorkspaceInputRequired/WorkspaceInputRequired';
import WorkspaceInputDate from '../WorkspaceInput/WorkspaceInputDate/WorkspaceInputDate';
import CssBaseline from '@material-ui/core/CssBaseline';
import Paper from '@material-ui/core/Paper';
import Spinner from '../../Spinner/Spinner'
import colors from '../../../utils/colors'



const StyledStepLabel = withStyles({
    root: {
      "& .MuiStepIcon-root": {
        color: colors.quaternaryDark,
      },
      "& .MuiStepIcon-completed": {
        color: colors.quaternaryLight,
      },
      "& .MuiStepLabel-label": {
        color: colors.secondary,
      },
      "& .MuiStepLabel-completed": {
        color: colors.secondaryDark,
      }
    },
  })(StepLabel);



const useStyles = (theme) => ({

  layout: {
    width: 'auto',
    marginLeft: theme.spacing(1),
    marginRight: theme.spacing(1),
    [theme.breakpoints.up(600 + theme.spacing(2) * 2)]: {
      width: 600,
      marginLeft: 'auto',
      marginRight: 'auto',
    },
  },
  paper: {
    marginTop: theme.spacing(3),
    marginBottom: theme.spacing(3),
    padding: theme.spacing(2),
    [theme.breakpoints.up(600 + theme.spacing(3) * 2)]: {
      marginTop: theme.spacing(3),
      marginBottom: theme.spacing(3),
      padding: theme.spacing(3),
    },
  },
  stepper: {
    padding: theme.spacing(3, 0, 5),
  },
  buttons: {
    display: 'flex',
    justifyContent: 'flex-end',
  },
  button: {
    marginTop: theme.spacing(3),
    marginLeft: theme.spacing(1),
  },
});
const StyledButton = withStyles({
  root: {
    background: colors.tertiaryDark ,
    color: colors.secondary,

    "&:hover": {
      backgroundColor: colors.tertiary,
    },
  },
})(Button);
const steps = ['Workspace outline', 'Specify workspace', 'Deadline'];

function getStepContent(step,props) {
  switch (step) {
    case 0:
      return <WorkspaceInputRequired {...props}/>;
    case 1:
      return <WorkspaceInputOptional {...props}/>;
    case 2:
      return <WorkspaceInputDate {...props} />;
    default:
      throw new Error('Unknown step');
  }
}


class WorkspaceStepper extends Component {
  constructor(props) {
    super(props);
    this.state = { activeStep: 0 }
  }
  handleNext = () => {
    this.setState({activeStep: this.state.activeStep + 1});
  };

  handleBack = () => {
    this.setState({activeStep: this.state.activeStep - 1});
  };
  handleCreateWorkspace = () => {
    this.handleNext();
    this.props.handleSubmit();
  }


  render() {
    const { classes } = this.props;
    return (
      <React.Fragment>
        <CssBaseline />
        <main className={classes.layout}>
          <Paper style={{backgroundColor: colors.primaryLight}} className={classes.paper}>
            <Typography  style={{color: colors.secondary}} component="h1" variant="h5" align="center">
              Create a new workspace
            </Typography>
            <Stepper style={{backgroundColor: colors.primaryLight}} activeStep={this.state.activeStep} className={classes.stepper}>
              {steps.map((label) => (
                <Step key={label}>
                  <StyledStepLabel style={{color: colors.secondaryDark}} optional={<Typography variant="caption">{label === steps[0] ? "Required" : "Optional" }</Typography>}>{label}</StyledStepLabel>
                </Step>
              ))}
            </Stepper>
            <React.Fragment>
              {this.state.activeStep === steps.length ?
                this.props.isSending ? (
                  <div
                    style={{
                      margin: "5vh 0px",
                      height: "5vh",
                      display: "flex",
                      justifyContent: "center",
                      alignItems: "center",
                    }}
                  >
                    <Spinner />
                  </div>
                ):(
                <React.Fragment>
                  <Typography style={(this.props.created) ? {color: colors.quaternary} : {color: colors.quinary}} variant="h6" gutterBottom align="center">
                  {(this.props.created) ? ("Your workspace is created.") : ("Oops!...")}
                  </Typography>
                  <Typography style={{color: colors.secondary}} variant="subtitle1" align="center">
                  {(this.props.created) ?  ("You can check it in your workspace page.") : ("Error occured. Your workspace can not be created.")}
                  </Typography>
                </React.Fragment>
              ) : (
                <React.Fragment>
                  {getStepContent(this.state.activeStep, {...this.props})}
                  <div className={classes.buttons}>
                    {this.state.activeStep !== 0 && (
                      <StyledButton onClick={this.handleBack} className={classes.button}>
                        Back
                      </StyledButton>
                    )}
                    <StyledButton
                      variant="contained"
                      color="primary"
                      onClick={this.state.activeStep === steps.length - 1 ? this.handleCreateWorkspace : this.handleNext}
                      className={classes.button}
                      disabled={this.props.title === "" || this.props.description === ""}
                    >
                      {this.state.activeStep === steps.length - 1 ? 'Create Workspace' : 'Next'}
                    </StyledButton>
                  </div>
                </React.Fragment>
              )}
            </React.Fragment>
          </Paper>
        </main>
      </React.Fragment>
    );
  }
}

export default withStyles(useStyles)(WorkspaceStepper);