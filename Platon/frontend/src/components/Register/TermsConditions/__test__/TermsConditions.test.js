import React from 'react';
import ReactDOM from 'react-dom'
import TermsConditions from '../TermsConditions'
import { BrowserRouter } from 'react-router-dom';

it("renders without crashing", () => {
    const div = document.createElement("div");
    ReactDOM.render(<BrowserRouter><TermsConditions/></BrowserRouter>, div)
})