import React from 'react';
import { withStyles } from '@material-ui/core/styles';
import TextField from '@material-ui/core/TextField';
import colors from '../../../../utils/colors';
import FormControl from "@material-ui/core/FormControl";
import FormLabel from "@material-ui/core/FormLabel";
import Select from "@material-ui/core/Select";
import MenuItem from '@material-ui/core/MenuItem';
import { makeStyles } from '@material-ui/core/styles';
import { Menu } from '@material-ui/core';

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



  const StyledMenuItem = withStyles({
    root: {
      "& .Mui-selected":{
        color: "green",
        backgroundColor: "red",
      },
    }


  })(MenuItem)


export default function WorkspaceInputDate(props) {

  return (
    <div className="container" style={{ display: "flex", flexDirection:"column"}} >
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
      <StyledFormControl style={{marginTop:"10px"}}  variant="outlined">
      <FormLabel  className="MuiFormLabel-root" component="legend">Attach Upcoming Events</FormLabel>
        <Select
          multiple
          value={props.skills}
          onChange={(e)=>props.handleUpcomingEvents(e.target.value)}
          value={props.upcomingEvents}
        >
          {props.upcomingEventsList.map((event, index)=>
            <MenuItem style={{color: colors.quaternaryDark}} value={index}>{event.acronym}</MenuItem>
        )}

        </Select>
      </StyledFormControl>
      </div>
  );
}