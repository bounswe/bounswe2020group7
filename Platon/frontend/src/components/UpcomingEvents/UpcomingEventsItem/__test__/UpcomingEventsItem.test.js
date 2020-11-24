import React from 'react';
import ReactDOM from 'react-dom'
import UpcomingEventsItem from '../UpcomingEventsItem'
import { BrowserRouter } from 'react-router-dom';

it("renders without crashing", () => {
    const div = document.createElement("div");
    ReactDOM.render(<BrowserRouter><UpcomingEventsItem event={{title:"UpcomingEventsItemTestTitle", date: "UpcomingEventsItemTestDate"}}/></BrowserRouter>, div)
})