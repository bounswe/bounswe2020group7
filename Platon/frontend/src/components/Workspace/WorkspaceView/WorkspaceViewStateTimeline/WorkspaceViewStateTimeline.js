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
export default function WorkspaceViewStateTimeline(props) {

  return (
    <Timeline align="right">
      <TimelineItem>
        <TimelineSeparator>
          <TimelineDot>
            <PeopleIcon />
          </TimelineDot>
          {props.state > 0? <TimelineConnector />:null}
        </TimelineSeparator>
        <TimelineContent>Search For Collaborators</TimelineContent>
      </TimelineItem>
      {props.state > 0?
      <TimelineItem>
        <TimelineSeparator>
          <TimelineDot>
            <WorkIcon />
          </TimelineDot>
          {props.state > 1 ? <TimelineConnector />:null}
        </TimelineSeparator>
        <TimelineContent>Ongoing</TimelineContent>
      </TimelineItem> : null
    }
    {props.state > 1?
      <TimelineItem>
        <TimelineSeparator>
          <TimelineDot>
            <PublicIcon />
          </TimelineDot>
        </TimelineSeparator>
        <TimelineContent>Published</TimelineContent>
      </TimelineItem> : null
    }
    </Timeline>
  );
}
