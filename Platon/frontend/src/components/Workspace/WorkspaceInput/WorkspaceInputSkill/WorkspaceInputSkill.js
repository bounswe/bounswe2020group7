import React from "react";
import TextField from "@material-ui/core/TextField";
import Autocomplete from "@material-ui/lab/Autocomplete";
import {makeStyles, withStyles } from '@material-ui/core/styles';
import colors from '../../../../utils/colors';

const useStyles = makeStyles((theme) => ({
  paper: {
    marginTop: theme.spacing(8),
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
  },
  inputColor:{
    color: colors.secondary,
  },
  typography:{
    color: colors.secondary,
  },
  avatar: {
    margin: theme.spacing(1),
    backgroundColor: colors.tertiary,
    color: colors.secondary,
  },

  form: {
    width: '100%', // Fix IE 11 issue.
    marginTop: theme.spacing(3),
  },
  submit: {
    margin: theme.spacing(3, 0, 2),
  },
}));
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


export default function Affinities(props) {
  const handleKeyDown = event => {
    switch (event.key) {
      case ",":
      case " ": {
        event.preventDefault();
        event.stopPropagation();
        if (event.target.value.length > 0) {
          props.handleSkills([...props.skills, event.target.value]);
        }
        break;
      }
      default:
    }
  };
  return (
    <div>
      <Autocomplete
        multiple
        freeSolo
        id="tags-outlined"
        options={props.skillsList}
        getOptionLabel={option => option}
        value={props.skills}
        onChange={(event, newValue) => props.handleSkills(newValue)}
        filterSelectedOptions
        renderInput={params => {
          params.inputProps.onKeyDown = handleKeyDown;
          return (
            <StyledTextField
              {...params}
              variant="outlined"
              required
              name="affinities"
              label={props.label}
              placeholder={props.label}
              id="affinities"
              fullWidth
            />
          );
        }}
      />
    </div>
  );
}