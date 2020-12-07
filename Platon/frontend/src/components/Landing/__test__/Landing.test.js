import React from 'react';
import ReactDOM from 'react-dom'
import Landing from '../Landing'
import { BrowserRouter } from 'react-router-dom';

it("renders without crashing", () => {
    const div = document.createElement("div");
    ReactDOM.render(<BrowserRouter><Landing/></BrowserRouter>, div)
})