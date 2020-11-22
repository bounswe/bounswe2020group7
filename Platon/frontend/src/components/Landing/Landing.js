import AppBar from "../AppBar/AppBar";
import React from "react";
import "./Landing.css";
import TrendingProjects from "../TrendingProjects/TrendingProjects";
import UpcomingEvents from "../UpcomingEvents/UpcomingEvents";
const Landing = (props) => {
  return (
    <div className="Landing">
      <div className="AppBar">
        <AppBar />
      </div>

        <div className="TitleScreen">
          Welcome to Platon
        </div>
        <div className="SubtitleScreen">
          Discover research and connect with your scientific
          community.
        </div>
        <div className="InformationScreen">
          <div><TrendingProjects /></div>
          <div>
            <UpcomingEvents />
          </div>
        </div>

    </div>
  );
};

export default Landing;