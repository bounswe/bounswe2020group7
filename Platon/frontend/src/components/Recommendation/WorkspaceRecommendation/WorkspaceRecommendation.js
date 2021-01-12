import React, { useState, useEffect } from "react";
import { makeStyles, useTheme } from "@material-ui/core/styles";
import MobileStepper from "@material-ui/core/MobileStepper";
import Paper from "@material-ui/core/Paper";
import Typography from "@material-ui/core/Typography";
import Button from "@material-ui/core/Button";
import KeyboardArrowLeft from "@material-ui/icons/KeyboardArrowLeft";
import KeyboardArrowRight from "@material-ui/icons/KeyboardArrowRight";
import Spinner from '../../Spinner/Spinner'
import config from "../../../utils/config";
import colors from "../../../utils/colors";
import axios from "axios";
import { Link } from "react-router-dom";

const { BASE_URL } = config;
const REC_URL = `${BASE_URL}/api/recommendation_system/workspace`;
axios.defaults.headers.common["auth_token"] = localStorage.getItem("jwtToken");


const useStyles = makeStyles((theme) => ({
  root: {
    maxWidth: 400,
    flexGrow: 1,
    border: "none",
    borderRadius: "0.5em",
    backgroundColor: colors.secondary,

  },

}));
const NUMBER_OF_RECOMMENDATIONS = 3;
const WorkspaceRecommendation = () => {
  const classes = useStyles();
  const theme = useTheme();
  const [activeStep, setActiveStep] = React.useState(0);
  const [recommendations, setRecommendations] = React.useState([]);
  const [maxSteps, setMaxSteps] = React.useState(0);
  const [loaded, setLoaded] = React.useState(false);
  const options = {
    params: {
      number_of_recommendations: NUMBER_OF_RECOMMENDATIONS,
    },
  };
  useEffect(() => {
    axios
      .get(REC_URL, options)
      .then((response) => {
        setRecommendations(response.data.recommendation_list);
        setMaxSteps(response.data.recommendation_list.length);
        setLoaded(true);
      })
      .catch((error) => {
        console.log(error);
      });
  }, []);

  const handleNext = () => {
    setActiveStep((prevActiveStep) => prevActiveStep + 1);
  };

  const handleBack = () => {
    setActiveStep((prevActiveStep) => prevActiveStep - 1);
  };

  return (
    <div>
        {loaded ?
      <div className={classes.root}>
           <Link
                    to={`/${recommendations[activeStep].creator_id}/workspace/${recommendations[activeStep].id}`}
                    style={{ textDecoration: "none" }}
                  >
          <Typography variant="h6" align="center" style={{color: colors.tertiaryDark, padding: "8px 16px"}}>{recommendations[activeStep].title}</Typography>
          </Link>
          <Typography style={{color: colors.tertiary, padding: "8px 16px"}}><span style={{color: colors.primary}} >Description: </span>{recommendations[activeStep].description.substring(0,120)}{recommendations[activeStep].description.length>120 ? "..."  : null}</Typography>
          <Typography style={{color: colors.quaternaryDark, padding: "8px 16px"}}><span style={{color: colors.primary}} >Contributors: </span>{recommendations[activeStep].contributor_list.map((contributor, index) => <span>{`${contributor.name} ${contributor.surname}`}{recommendations[activeStep].contributor_list && index !== (recommendations[activeStep].contributor_list.length-1) ? " - ": null}</span>)}</Typography>
          <Typography style={{color: colors.quinaryDark, padding: "8px 16px"}}><span style={{color: colors.primary}} >State: </span>{recommendations[activeStep].state===0 ? "Search For Collaborators" : (recommendations[activeStep].state===1 ? "Ongoing": "Published") }</Typography>

        <MobileStepper
          steps={maxSteps}
          style={{backgroundColor: colors.secondaryLight, borderRadius: "0.5em"}}
          position="static"
          variant="text"
          activeStep={activeStep}
          nextButton={
            <Button
              size="small"
              onClick={handleNext}
              disabled={activeStep === maxSteps - 1}
            >
              Next
              {theme.direction === "rtl" ? (
                <KeyboardArrowLeft />
              ) : (
                <KeyboardArrowRight />
              )}
            </Button>
          }
          backButton={
            <Button
              size="small"
              onClick={handleBack}
              disabled={activeStep === 0}
            >
              {theme.direction === "rtl" ? (
                <KeyboardArrowRight />
              ) : (
                <KeyboardArrowLeft />
              )}
              Back
            </Button>
          }
        />
      </div>:<div style={{marginTop: "40px", display: "flex", flexDirection: "column", alignItems:"center"}}><Spinner/></div>}
    </div>
  );
};

export default WorkspaceRecommendation;
