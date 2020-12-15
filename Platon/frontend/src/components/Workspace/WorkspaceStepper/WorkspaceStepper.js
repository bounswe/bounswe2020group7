import React from 'react';
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



const useStyles = makeStyles((theme) => ({

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
}));
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
      return <WorkspaceInputDate/>;
    default:
      throw new Error('Unknown step');
  }
}

export default function WorkspaceStepper(props) {
  const classes = useStyles();
  const [activeStep, setActiveStep] = React.useState(0);

  const handleNext = () => {
    setActiveStep(activeStep + 1);
  };

  const handleBack = () => {
    setActiveStep(activeStep - 1);
  };

  return (
    <React.Fragment>
      <CssBaseline />
      <main className={classes.layout}>
        <Paper style={{backgroundColor: colors.primaryLight}} className={classes.paper}>
          <Typography  style={{color: colors.secondary}} component="h1" variant="h5" align="center">
            Create a new workspace
          </Typography>
          <Stepper style={{backgroundColor: colors.primaryLight}} activeStep={activeStep} className={classes.stepper}>
            {steps.map((label) => (
              <Step key={label}>
                <StyledStepLabel style={{color: colors.secondaryDark}} optional={<Typography variant="caption">{label === steps[0] ? "Required" : "Optional" }</Typography>}>{label}</StyledStepLabel>
              </Step>
            ))}
          </Stepper>
          <React.Fragment>
            {activeStep === steps.length ? (
              <React.Fragment>
                <Typography variant="h5" gutterBottom align="center">
                  Your workspace is created.
                </Typography>
                <Typography variant="subtitle1" align="center">
                  You can check it in your workspace page.
                </Typography>
              </React.Fragment>
            ) : (
              <React.Fragment>
                {getStepContent(activeStep, {...props})}
                <div className={classes.buttons}>
                  {activeStep !== 0 && (
                    <StyledButton onClick={handleBack} className={classes.button}>
                      Back
                    </StyledButton>
                  )}
                  <StyledButton
                    variant="contained"
                    color="primary"
                    onClick={handleNext}
                    className={classes.button}
                    disabled={props.title === "" || props.description === ""}
                  >
                    {activeStep === steps.length - 1 ? 'Create Workspace' : 'Next'}
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