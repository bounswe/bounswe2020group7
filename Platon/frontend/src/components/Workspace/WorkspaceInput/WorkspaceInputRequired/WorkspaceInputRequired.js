import React, { Component } from "react";
import TextField from "@material-ui/core/TextField";
import FormControl from "@material-ui/core/FormControl";
import FormLabel from "@material-ui/core/FormLabel";
import { withStyles } from '@material-ui/core/styles';
import colors from '../../../../utils/colors';

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
        <FormControl style={{marginTop:"10px"}} component="fieldset">
          <FormLabel className="MuiFormLabel-root" component="legend">Title</FormLabel>

          <StyledTextField
            fullWidth
            required
            value={this.props.title}
            variant="outlined"
            onChange={(e) => this.props.handleTitle(e.target.value)}
          />
        </FormControl>
        <FormControl style={{marginTop:"10px"}}  component="fieldset">
          <FormLabel className="WorkspaceInputFormLabel" component="legend">Description</FormLabel>
          <StyledTextField
            fullWidth
            required
            value={this.props.description}
            variant="outlined"
            multiline
            onChange={(e) => this.props.handleDescription(e.target.value)}
            rows={6}
          />
        </FormControl>

      </div>
    );
  }
}

export default WorkspaceInput;
