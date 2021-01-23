import React from 'react';
import ReactDOM from 'react-dom'
import CollaboratorRecommendationDialog from '../CollaboratorRecommendationDialog'
import { BrowserRouter } from 'react-router-dom';

it("renders without crashing", () => {
    const div = document.createElement("div");
    ReactDOM.render(<BrowserRouter><CollaboratorRecommendationDialog/></BrowserRouter>, div)
})