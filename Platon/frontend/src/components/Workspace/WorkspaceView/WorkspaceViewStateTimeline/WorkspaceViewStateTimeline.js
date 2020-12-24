import React from "react";
import Timeline from "@material-ui/lab/Timeline";
import TimelineItem from "@material-ui/lab/TimelineItem";
import TimelineSeparator from "@material-ui/lab/TimelineSeparator";
import TimelineConnector from "@material-ui/lab/TimelineConnector";
import TimelineContent from "@material-ui/lab/TimelineContent";
import TimelineDot from "@material-ui/lab/TimelineDot";
import PeopleIcon from "@material-ui/icons/People";
import PublicIcon from '@material-ui/icons/Public';
import WorkIcon from '@material-ui/icons/Work';
import DoneIcon from '@material-ui/icons/Done';
import colors from '../../../../utils/colors'
export default function WorkspaceViewStateTimeline(props) {

  return (
    <Timeline align="right">
      <TimelineItem>
        <TimelineSeparator>
          <TimelineDot style={ props.state > 0 ? {backgroundColor: colors.quaternary} : {backgroundColor: colors.quinary}} >
          {props.state > 0 ? <DoneIcon style={{color: colors.primary }} />: <PeopleIcon style={{color: colors.primary }}/>}
          </TimelineDot>

          {props.state > 0? <TimelineConnector />:null}
        </TimelineSeparator>
        <TimelineContent>Search For Collaborators</TimelineContent>
      </TimelineItem>
      {props.state > 0?
      <TimelineItem>
        <TimelineSeparator>
        <TimelineDot style={ props.state > 1 ? {backgroundColor: colors.quaternary} : {backgroundColor: colors.quinary}} >
          {props.state > 1 ? <DoneIcon style={{color: colors.primary }} />: <WorkIcon style={{color: colors.primary }}/>}
          </TimelineDot>

          {props.state > 1 ? <TimelineConnector />:null}
        </TimelineSeparator>
        <TimelineContent>Ongoing</TimelineContent>
      </TimelineItem> : null
    }
    {props.state > 1?
      <TimelineItem>
        <TimelineSeparator>
        <TimelineDot style={{backgroundColor: colors.quinary}} >            <PublicIcon style={{color: colors.primary }}/>
          </TimelineDot>
        </TimelineSeparator>
        <TimelineContent>Published</TimelineContent>
      </TimelineItem> : null
    }
    </Timeline>
  );
}
