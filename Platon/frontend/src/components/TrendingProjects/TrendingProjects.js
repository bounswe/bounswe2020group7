import React, { useState, useEffect } from "react";
import Typography from "@material-ui/core/Typography";
import TrendingProjectsItem from "./TrendingProjectsItem/TrendingProjectsItem";
import colors from '../../utils/colors'
import './TrendingProjects.css'
const TrendingProjects = () => {
  const [trendingProjectsList, setTrendingProjectsList] = useState([]);
  useEffect(
    () => {
      const url = "https://react-my-burger-78df4.firebaseio.com";
      fetch(url + "/trendingprojects.json")
        .then((response) => response.json())
        .then((data) => {
          setTrendingProjectsList(data);
        })
        .catch((error) => {
          console.error("Error:", error);
        });
    },
  [trendingProjectsList ]
  );
  return (
    <div>
      <Typography style={{color: colors.tertiary, textAlign: "center"}} variant="h5" gutterBottom>
        Trending Projects
      </Typography>
      <div className="TrendingProjectsItems">
      {Object.keys(trendingProjectsList).map((projects, index) => (
          <TrendingProjectsItem project={trendingProjectsList[projects]} key={index} id = {index}/>
      ))}
    </div>
    </div>
  );
};

export default TrendingProjects;
