import React from 'react';
import ReactDOM from 'react-dom'
import TrendingProjects from '../TrendingProjects'
import { BrowserRouter } from 'react-router-dom';

it("renders without crashing", () => {
    const div = document.createElement("div");
    ReactDOM.render(<BrowserRouter><TrendingProjects/></BrowserRouter>, div)
})