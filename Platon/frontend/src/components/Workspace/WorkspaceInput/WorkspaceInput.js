import React, { Component } from "react";
import TextField from "@material-ui/core/TextField";
import Grid from "@material-ui/core/Grid";
import Radio from "@material-ui/core/Radio";
import RadioGroup from "@material-ui/core/RadioGroup";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import FormControl from "@material-ui/core/FormControl";
import FormLabel from "@material-ui/core/FormLabel";

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
        <FormControl component="fieldset">
          <FormLabel component="legend">Title</FormLabel>

          <TextField
            fullWidth

            required
            label="Title"
            variant="outlined"
          />
        </FormControl>{" "}
        <FormControl component="fieldset">
          <FormLabel component="legend">Description</FormLabel>
          <TextField
            fullWidth
            required
            label="Description"
            variant="outlined"
          />
        </FormControl>
        <FormControl component="fieldset">
          <FormLabel component="legend">Workspace Privacy</FormLabel>
          <RadioGroup
            aria-label="workspace-privacy"
            name="workspace-privacy"
            value={!this.state.is_private ? "public" : "private"}
            onChange={this.handleWorkspacePrivacy}
          >
            <FormControlLabel
              value="public"
              control={<Radio />}
              label="Public"
            />
            <FormControlLabel
              value="private"
              control={<Radio />}
              label="Private"
            />
          </RadioGroup>
        </FormControl>
      </div>
    );
  }
}

export default WorkspaceInput;
