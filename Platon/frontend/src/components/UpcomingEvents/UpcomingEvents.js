import React, { useState, useEffect } from "react";
import Typography from "@material-ui/core/Typography";
import UpcomingEventsItem from "./UpcomingEventsItem/UpcomingEventsItem";
import colors from '../../utils/colors'
import './UpcomingEvents.css'
const UpcomingEvents = () => {
  const [upcomingEventsList, setUpcomingEventsList] = useState([]);
  useEffect(
    () => {
      const url = "https://react-my-burger-78df4.firebaseio.com";
      fetch(url + "/upcomingevents.json")
        .then((response) => response.json())
        .then((data) => {
          setUpcomingEventsList(data);
        })
        .catch((error) => {
          console.error("Error:", error);
        });
    },
    [ upcomingEventsList ]
  );
  return (
    <div>
      <Typography style={{color: colors.quaternary, textAlign: "center"}} variant="h5" gutterBottom>
        Upcoming Events
      </Typography>
      <div className="UpcomingEventsItems">

      {Object.keys(upcomingEventsList).map((events, index) => (


          <UpcomingEventsItem event={upcomingEventsList[events]} key={index} id = {index}/>


      ))}
    </div>
    </div>
  );
};

export default UpcomingEvents;
