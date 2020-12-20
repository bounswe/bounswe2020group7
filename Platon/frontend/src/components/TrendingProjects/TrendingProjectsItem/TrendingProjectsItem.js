import React from 'react'
import { withStyles, makeStyles } from '@material-ui/core/styles'
import MuiAccordion from '@material-ui/core/Accordion'
import MuiAccordionSummary from '@material-ui/core/AccordionSummary'
import MuiAccordionDetails from '@material-ui/core/AccordionDetails'
import Typography from '@material-ui/core/Typography'
import List from '@material-ui/core/List'
import ListItem from '@material-ui/core/ListItem'
import ListItemText from '@material-ui/core/ListItemText'
import colors from '../../../utils/colors'

const useStyles = makeStyles((theme) => ({
  root: {
    width: '100%',
    maxWidth: 500,
    margin: 0,
    padding: 0,
  },
  primary: {
    color: colors.secondary,
  },
  secondary: {
    color: colors.primaryDark,
  },
}))

const Accordion = withStyles({
  root: {
    width: '100%',
    backgroundColor: colors.primary,
    borderRadius: '0.5em',
    '&:not(:last-child)': {
      borderBottom: 0,
    },
    '&:before': {
      display: 'none',
    },
    '&$expanded': {
      margin: 'auto',
    },
  },
  expanded: {},
})(MuiAccordion)

const AccordionSummary = withStyles({
  root: {
    backgroundColor: colors.tertiaryDark,

    borderRadius: '0.5em',

    minHeight: 56,
    '&$expanded': {
      minHeight: 56,
    },
  },

  content: {
    '&$expanded': {
      margin: '12px 0',
    },
  },
  expanded: {},
})(MuiAccordionSummary)

const AccordionDetails = withStyles((theme) => ({
  root: {
    color: colors.secondary,
    backgroundColor: colors.primary,
    borderRadius: '0.5em',
  },
}))(MuiAccordionDetails)

const TrendingProjectsItem = ({ id, title, description, contributors }) => {
  const classes = useStyles()
  const [expanded, setExpanded] = React.useState('')

  const handleChange = (panel) => (event, newExpanded) => {
    setExpanded(newExpanded ? panel : false)
  }


  const contributorsText = contributors.reduce((acc, contributor, index) => {
    const nameSurname = `${contributor.name} ${contributor.surname}`
    return index === 0 ? acc + nameSurname : acc + ` - ${nameSurname}`
  }, '')

  return (
    <List className={classes.root}>
      <ListItem>
        <Accordion
          square
          expanded={expanded === 'panel' + id}
          onChange={handleChange('panel' + id)}
        >
          <AccordionSummary
            aria-controls={'panel' + id + ' d-content '}
            id={'panel' + id + ' d-header '}
          >
            <ListItemText
              classes={{ primary: classes.primary, secondary: classes.secondary }}
              primary={title} secondary={contributorsText}
            />
          </AccordionSummary>
          <AccordionDetails>
            <Typography>
              {description}
            </Typography>
          </AccordionDetails>
        </Accordion>
      </ListItem>
    </List>
  )
}

export default TrendingProjectsItem
