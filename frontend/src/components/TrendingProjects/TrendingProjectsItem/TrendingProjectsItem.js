import React from "react";
import { withStyles, makeStyles } from "@material-ui/core/styles";
import MuiAccordion from "@material-ui/core/Accordion";
import MuiAccordionSummary from "@material-ui/core/AccordionSummary";
import MuiAccordionDetails from "@material-ui/core/AccordionDetails";
import Typography from "@material-ui/core/Typography";

import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemText from "@material-ui/core/ListItemText";
import colors from '../../../utils/colors'
const useStyles = makeStyles((theme) => ({
  root: {
    width: "100%",
    maxWidth: 500,
    margin:0,
    padding:0
  },
}));

const Accordion = withStyles({
  root: {
    borderRadius: "0.5em",
    "&:not(:last-child)": {
      borderBottom: 0,
    },
    "&:before": {
      display: "none",
    },
    "&$expanded": {
      margin: "auto",
    },
  },
  expanded: {},
})(MuiAccordion);

const AccordionSummary = withStyles({
  root: {
    backgroundColor: colors.primaryDark,

    borderRadius: "0.5em",

    minHeight: 56,
    "&$expanded": {
      minHeight: 56,
    },
  },
  content: {
    "&$expanded": {
      margin: "12px 0",
    },
  },
  expanded: {},
})(MuiAccordionSummary);

const AccordionDetails = withStyles((theme) => ({
  root: {
    padding: theme.spacing(2),
  },
}))(MuiAccordionDetails);
const TrendingProjectsItem = (props) => {
  const classes = useStyles();
  const [expanded, setExpanded] = React.useState("");

  const handleChange = (panel) => (event, newExpanded) => {
    setExpanded(newExpanded ? panel : false);
  };

  return (
      <List className={classes.root}>
        <ListItem>
          <Accordion
            square
            expanded={expanded === "panel"+props.id }
            onChange={handleChange("panel"+props.id)}
          >
            <AccordionSummary
              aria-controls={"panel" + props.id +  " d-content "}
              id={"panel" + props.id +  " d-header "}
            >
            <ListItemText style={{color: colors.tertiary}} primary={props.project.title} secondary={props.project.authors.join(" - ")} />
            </AccordionSummary>
            <AccordionDetails>
              <Typography>
                {props.project.description}
              </Typography>
            </AccordionDetails>
          </Accordion>
        </ListItem>

      </List>

  );
};

export default TrendingProjectsItem;
