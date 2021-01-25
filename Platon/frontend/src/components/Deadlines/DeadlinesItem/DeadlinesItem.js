import React, { useCallback } from 'react'
import { makeStyles, withStyles } from '@material-ui/core/styles'
import MuiAccordion from '@material-ui/core/Accordion'
import MuiAccordionSummary from '@material-ui/core/AccordionSummary'
import MuiAccordionDetails from '@material-ui/core/AccordionDetails'
import List from '@material-ui/core/List'
import ListItem from '@material-ui/core/ListItem'
import colors from '../../../utils/colors'
import { useParams } from 'react-router-dom'
import { format } from 'date-fns'

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
    backgroundColor: colors.secondary,
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
    backgroundColor: colors.secondary,

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
    backgroundColor: colors.secondary,
    borderRadius: '0.5em',
  },
}))(MuiAccordionDetails)


const DEADLINE_TYPE_MAPPING = {
  2: 'Milestone Deadline',
  3: 'Issue Deadline',
}

const DATE_FORMAT = 'd MMM yyyy HH:mm'

const getSecondaryText = (type, title) => {
  if (type === 1) return 'Workspace Deadline'
  return `${DEADLINE_TYPE_MAPPING[type]}: ${title}`
}

const DeadlinesItem = ({
  id,
  deadline,
}) => {
  const classes = useStyles()
  const [expanded, setExpanded] = React.useState('')

  const { profileId } = useParams()
  const { deadline: deadlineStr, title, type, workspace_id, workspace_title } = deadline

  const secondaryText = getSecondaryText(type, title)

  const deadlineText = format(Date.parse(deadlineStr), DATE_FORMAT)


  const handleChange = (panel) => (event, newExpanded) => {
    setExpanded(newExpanded ? panel : false)
  }


  return (
    <List className={classes.root}>
      <ListItem disableGutters>
        <Accordion
          square
          expanded={expanded === 'panel' + id}
          onChange={handleChange('panel' + id)}
          onClick={useCallback(() => {
            window.location.href =
              `/${profileId}/workspace/${workspace_id}`
          }, [profileId, workspace_id])}
        >
          <AccordionSummary
            aria-controls={'panel' + id + ' d-content '}
            id={'panel' + id + ' d-header '}
          >
            <div style={{ display: 'flex', flexDirection: 'column' }}>
              <div style={{ color: colors.quaternaryDark }}>
                {workspace_title}
              </div>
              <div style={{ color: colors.primaryDark, fontSize: '14px' }}>{secondaryText}</div>
              <div style={{ color: colors.quinaryDark, fontSize: '14px' }}>{deadlineText}</div>
            </div>
          </AccordionSummary>
        </Accordion>
      </ListItem>
    </List>
  )
}

export default DeadlinesItem
