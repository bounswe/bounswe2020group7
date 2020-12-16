import React, {useState} from 'react';
import { withStyles } from '@material-ui/core/styles';
import TextField from '@material-ui/core/TextField';
import colors from '../../../../utils/colors';
import FormControl from "@material-ui/core/FormControl";
import FormLabel from "@material-ui/core/FormLabel";
import moment from 'moment';

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
    let today = new Date();
    let dd = today.getDate();
    let mm = today.getMonth()+1;
    const yyyy = today.getFullYear();
    if(dd<10)
    {
        dd=`0${dd}`;
    }
    if(mm<10)
    {
        mm=`0${mm}`;
    }
    today = `${yyyy}-${mm}-${dd}`;

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