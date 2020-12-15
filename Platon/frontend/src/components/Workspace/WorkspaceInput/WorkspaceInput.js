import React, { Component } from "react";
import TextField from "@material-ui/core/TextField";
import Grid from "@material-ui/core/Grid";
import Input from "@material-ui/core/Input";
import Radio from "@material-ui/core/Radio";
import RadioGroup from "@material-ui/core/RadioGroup";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import FormControl from "@material-ui/core/FormControl";
import FormLabel from "@material-ui/core/FormLabel";
import WorkspaceInputSkill from './WorkspaceInputSkill/WorkspaceInputSkill';
import { withStyles } from '@material-ui/core/styles';
import colors from '../../../utils/colors';

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


class WorkspaceInput extends Component {
  constructor(props) {
    super(props);
    this.state = {
      is_private: 0,
    };
  }

  handleWorkspacePrivacy = (event) => {
    this.setState({ is_private: event.target.value === "public" ? 0 : 1 });
  };
  render() {
    return (
      <div className="container" style={{ display: "flex", flexDirection:"column",}} >
        <FormControl style={{marginTop:"20px"}} component="fieldset">
          <FormLabel className="MuiFormLabel-root" component="legend">Title</FormLabel>

          <StyledTextField
            fullWidth
            required
            variant="outlined"
          />
        </FormControl>
        <FormControl style={{marginTop:"20px"}}  component="fieldset">
          <FormLabel className="WorkspaceInputFormLabel" component="legend">Description</FormLabel>
          <StyledTextField
            fullWidth
            required
            variant="outlined"
            multiline
            rows={6}
          />
        </FormControl>
        <FormControl style={{marginTop:"20px"}} component="fieldset">
          <FormLabel className="WorkspaceInputFormLabel" component="legend">Workspace Privacy</FormLabel>
          <RadioGroup
            aria-label="workspace-privacy"
            name="workspace-privacy"
            value={!this.state.is_private ? "public" : "private"}
            onChange={this.handleWorkspacePrivacy}
          >
            <FormControlLabel
              value="public"
              control={<Radio />}
              label="Public (Default)"
            />
            <FormControlLabel
              value="private"
              control={<Radio />}
              label="Private"
            />
          </RadioGroup>
        </FormControl>
        <FormControl style={{marginTop:"20px"}} component="fieldset">
          <FormLabel className="WorkspaceInputFormLabel" component="legend">Maximum Number of Collaborators</FormLabel>
          <StyledTextField
          id="outlined-number"
          type="number"
          InputProps={{ inputProps: { min: 1} }}
          variant="outlined"
        />
        </FormControl>
        <FormControl style={{marginTop:"20px"}}  component="fieldset">
          <FormLabel className="WorkspaceInputFormLabel" component="legend">Requirements</FormLabel>
          <StyledTextField
            fullWidth
            required
            variant="outlined"
            multiline
            rows={3}
          />
        </FormControl>
        <FormControl style={{marginTop:"20px"}}  component="fieldset">
          <FormLabel className="WorkspaceInputFormLabel" component="legend">Skills</FormLabel>
          <WorkspaceInputSkill/>
        </FormControl>
      </div>
    );
  }
}

export default WorkspaceInput;
