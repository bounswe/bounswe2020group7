import React, { Component } from "react";
import TextField from "@material-ui/core/TextField";

import Radio from "@material-ui/core/Radio";
import RadioGroup from "@material-ui/core/RadioGroup";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import FormControl from "@material-ui/core/FormControl";
import FormLabel from "@material-ui/core/FormLabel";
import WorkspaceInputSkill from "../WorkspaceInputSkill/WorkspaceInputSkill";
import { withStyles } from "@material-ui/core/styles";
import colors from "../../../../utils/colors";

const StyledRadio = withStyles({
  root: {

    "&.Mui-checked": {
      color: colors.quinary,
    }
  },
  checked: {},
})(Radio);


const StyledTextField = withStyles({
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
    console.log("is_private", this.props.is_private)
    console.log("requirements", this.props.requirements)
    console.log("max_col", this.props.max_collaborators)
    console.log("skills", this.props.skills)
    return (
      <div
        className="container"
        style={{ display: "flex", flexDirection: "column" }}
      >
        <FormControl style={{ marginTop: "10px" }} component="fieldset">
          <FormLabel className="WorkspaceInputFormLabel" component="legend">
            Workspace Privacy
          </FormLabel>
          <RadioGroup
            aria-label="workspace-privacy"
            name="workspace-privacy"
            value={!this.props.is_private ? "public" : "private"}
            onChange={(e) => this.props.handleIsPrivate(e.target.value === "public" ? 0 : 1 )}
          >
            <FormControlLabel
              value="public"
              control={<StyledRadio />}
              label="Public (Default)"
            />
            <FormControlLabel
              value="private"
              control={<StyledRadio />}
              label="Private"
            />
          </RadioGroup>
        </FormControl>
        <FormControl style={{ marginTop: "10px" }} component="fieldset">
          <FormLabel className="WorkspaceInputFormLabel" component="legend">
            Maximum Number of Collaborators
          </FormLabel>
          <StyledTextField
            id="outlined-number"
            type="number"
            value={this.props.max_collaborators}
            onChange={(e) => this.props.handleMaxCollaborators(e.target.value)}
            InputProps={{ inputProps: { min: 1 } }}
            variant="outlined"
          />
        </FormControl>
        <FormControl style={{ marginTop: "10px" }} component="fieldset">
          <FormLabel className="WorkspaceInputFormLabel" component="legend">
            Requirements
          </FormLabel>
          <StyledTextField
            fullWidth
            required
            value={this.props.requirements}
            onChange={(e) => this.props.handleRequirements(e.target.value)}
            variant="outlined"
            multiline
            rows={3}
          />
        </FormControl>
        <FormControl style={{ marginTop: "10px" }} component="fieldset">
          <FormLabel className="WorkspaceInputFormLabel" component="legend">
            Skills
          </FormLabel>
          <WorkspaceInputSkill skillsList={this.props.skillsList} skills={this.props.skills} handleSkills={this.props.handleSkills}/>
        </FormControl>
      </div>
    );
  }
}

export default WorkspaceInput;
