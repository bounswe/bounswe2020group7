import React from 'react';
import { withStyles } from '@material-ui/core/styles';
import MenuItem from '@material-ui/core/MenuItem';
import FormLabel from '@material-ui/core/FormLabel';
import FormControl from '@material-ui/core/FormControl';
import Select from '@material-ui/core/Select';
import colors from '../../../../utils/colors';

const StyledFormControl = withStyles({
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
  })(FormControl);
export default function SimpleSelect(props) {


    return (
        <div
        className="container"
        style={{ display: "flex", flexDirection: "column" }}
      >
        <StyledFormControl variant="outlined" style={{marginTop:"10px"}} component="fieldset">
        <FormLabel  component="legend">State</FormLabel>
        <Select
          labelId="demo-simple-select-outlined-label"
          id="demo-simple-select-outlined"
          value={props.state}
          onChange={(e) => props.handleState(e.target.value)}
          fullWidth
        >

          <MenuItem value={0}>Search For Collaborators</MenuItem>
          <MenuItem value={1}>Ongoing</MenuItem>
          <MenuItem value={2}>Published</MenuItem>
        </Select>
      </StyledFormControl>
      </div>
    );
}