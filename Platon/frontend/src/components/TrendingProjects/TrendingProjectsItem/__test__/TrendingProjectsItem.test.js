import React from 'react';
import ReactDOM from 'react-dom'
import TrendingProjectsItem from '../TrendingProjectsItem'
import { BrowserRouter } from 'react-router-dom';

it("renders without crashing", () => {
    const div = document.createElement("div");
    ReactDOM.render(<BrowserRouter><TrendingProjectsItem project={{title: "TrendingProjectsItemTestTitle", authors: ["TrendingProjectsItemTestAuthors"], description: "TrendingProjectsItemTestDescription"}}/></BrowserRouter>, div)
})