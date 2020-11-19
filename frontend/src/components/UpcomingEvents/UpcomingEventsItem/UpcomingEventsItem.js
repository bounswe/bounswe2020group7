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
    padding: "8px 16px",
  },
})(ListItem);

const UpcomingEventsItem = (props) => {
  const classes = useStyles();
  const [expanded, setExpanded] = React.useState("");

  const handleChange = (panel) => (event, newExpanded) => {
    setExpanded(newExpanded ? panel : false);
  };

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
