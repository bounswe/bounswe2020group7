import React from "react";
import { withStyles, makeStyles } from "@material-ui/core/styles";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemText from "@material-ui/core/ListItemText";
import colors from "../../../utils/colors";
const useStyles = makeStyles((theme) => ({
  root: {
    width: "100%",
    maxWidth: 500,
    paddingTop: "8px",
    paddingBottom: "8px",

  },
  primary: {
    color: colors.secondary,
  },
  secondary: {
    color: colors.primaryDark,
  },
}));
const StyledListItem = withStyles({
  root: {
    backgroundColor: colors.quaternaryDark,
    borderRadius: "0.5em",

  },
})(ListItem);

const UpcomingEventsItem = (props) => {
  const classes = useStyles();

  return (
    <List className={classes.root}>
      <StyledListItem>
        <ListItemText
          classes={{ primary: classes.primary, secondary: classes.secondary }}
          primary={props.event.title}
          secondary={"Start Date: " + props.event.date}
        />
      </StyledListItem>
    </List>
  );
};

export default UpcomingEventsItem;
