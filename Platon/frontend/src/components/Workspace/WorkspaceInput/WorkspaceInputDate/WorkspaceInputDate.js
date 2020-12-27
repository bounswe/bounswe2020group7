import React from 'react';
import { withStyles } from '@material-ui/core/styles';
import TextField from '@material-ui/core/TextField';
import colors from '../../../../utils/colors';
import FormControl from "@material-ui/core/FormControl";
import FormLabel from "@material-ui/core/FormLabel";

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


export default function WorkspaceInputDate(props) {

  return (
    <div className="container" style={{ display: "flex", flexDirection:"column",}} >
    <FormControl style={{marginTop:"10px"}} component="fieldset">
    <FormLabel className="MuiFormLabel-root" component="legend">Deadline</FormLabel>
      <StyledTextField
        id="date"
        type="date"
        variant="outlined"
        name="date"
        InputLabelProps={{ shrink: true }}
        value={props.deadline}
        onChange={(e) => props.handleDeadline(e.target.value)}
        fullWidth
      />
      </FormControl>
      </div>
  );
}