import React from 'react';
import ReactDOM from 'react-dom'
import Spinner from '../Spinner'
import { BrowserRouter } from 'react-router-dom';

it("renders without crashing", () => {
    const div = document.createElement("div");
    ReactDOM.render(<BrowserRouter><Spinner/></BrowserRouter>, div)
})