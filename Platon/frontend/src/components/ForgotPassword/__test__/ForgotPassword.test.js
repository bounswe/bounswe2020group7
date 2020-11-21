import React from 'react';
import ReactDOM from 'react-dom'
import ForgotPassword from '../ForgotPassword'
import { BrowserRouter } from 'react-router-dom';

it("renders without crashing", () => {
    const div = document.createElement("div");
    ReactDOM.render(<BrowserRouter><ForgotPassword/></BrowserRouter>, div)
})