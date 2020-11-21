import React from 'react';
import ReactDOM from 'react-dom'
import UpcomingEvents from '../UpcomingEvents'
import { BrowserRouter } from 'react-router-dom';

it("renders without crashing", () => {
    const div = document.createElement("div");
    ReactDOM.render(<BrowserRouter><UpcomingEvents/></BrowserRouter>, div)
})