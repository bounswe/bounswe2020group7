import React from 'react';
import ReactDOM from 'react-dom'
import ResetPassword from '../ResetPassword'
import { BrowserRouter } from 'react-router-dom';

it("renders without crashing", () => {
    const div = document.createElement("div");
    ReactDOM.render(<BrowserRouter><ResetPassword/></BrowserRouter>, div)
})