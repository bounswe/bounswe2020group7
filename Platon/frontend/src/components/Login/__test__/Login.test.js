import React from 'react';
import ReactDOM from 'react-dom'
import Login from '../Login'
import { BrowserRouter } from 'react-router-dom';

it("renders without crashing", () => {
    const div = document.createElement("div");
    ReactDOM.render(<BrowserRouter><Login/></BrowserRouter>, div)
})