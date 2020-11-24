import React from "react";
import "./ProfilePage.css";
import { Container, Col, Row, Button, Card } from "react-bootstrap";
import "bootstrap/dist/css/bootstrap.min.css";
import Rating from "@material-ui/lab/Rating";
import PropTypes from "prop-types";
import { withStyles } from "@material-ui/core/styles";
import AppBar from "@material-ui/core/AppBar";
import Tabs from "@material-ui/core/Tabs";
import Tab from "@material-ui/core/Tab";
import Typography from "@material-ui/core/Typography";
import Box from "@material-ui/core/Box";
import NavBar from "../NavBar/NavBar";
import jwt_decode from "jwt-decode";
import requestService from "../../services/requestService";
import {Link} from 'react-router-dom'
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemAvatar from '@material-ui/core/ListItemAvatar';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemSecondaryAction from '@material-ui/core/ListItemSecondaryAction';
import ListItemText from '@material-ui/core/ListItemText';
import PersonIcon from '@material-ui/icons/Person';
import Avatar from '@material-ui/core/Avatar';
import colors from "../../utils/colors";
import config from "../../utils/config";
import axios from 'axios'

function TabPanel(props) {
  const { children, value, index, ...other } = props;

  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`simple-tabpanel-${index}`}
      aria-labelledby={`simple-tab-${index}`}
      {...other}
    >
      {value === index && (
        <Box p={3}>
          <Typography>{children}</Typography>
        </Box>
      )}
    </div>
  );
}

TabPanel.propTypes = {
  children: PropTypes.node,
  index: PropTypes.any.isRequired,
  value: PropTypes.any.isRequired,
};

function a11yProps(index) {
  return {
    id: `simple-tab-${index}`,
    "aria-controls": `simple-tabpanel-${index}`,
  };
}

const useStyles = withStyles((theme) => ({
  root: {
    flexGrow: 1,
    backgroundColor: theme.palette.background.paper,
  },
}));

class ProfilePage extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      followers: [],
      followings: [],
      researchs: [],
      selectedFollowButton: [],
      user: null,
      value: 0,
      job: "",
    };
  }

  componentDidMount() {
    const token = localStorage.getItem("jwtToken");
    const decoded = jwt_decode(token);
    requestService.followings(decoded.id).then((response) => {
      this.setState({
        followings: response.data.followings,
      });
    });
    requestService.followers(decoded.id).then((response) => {
      this.setState({
        followers: response.data.followers,
        selectedFollowButton: Array(response.data.followers.length).fill("Follow"),
      });
    });
    requestService.getUser(decoded.id).then((response) => {
      this.setState({
        user: response.data,
      });
    });
    requestService.getResearchs(decoded.id).then((response) => {
      this.setState({
        researchs: response.data.research_info,
      });
    });
  }
  handlerFollow = (index, following_id) =>{
    /*
    let prevState = this.state.selectedFollowButton
    prevState[index] = this.state.selectedFollowButton[index] === "Follow" ? "Unfollow" : "Follow"
    this.setState({
      selectedFollowButton:prevState,
    })*/
    //TODO

    let prevState = this.state.selectedFollowButton
    const followButtonState = prevState[index]

    const token = localStorage.getItem("jwtToken");
    const decoded = jwt_decode(token);
    let formData = new FormData();

    formData.append("follower_id", decoded.id);
    formData.append("following_id ", following_id);


    const url = config.BASE_URL

    if(followButtonState==="Follow"){
    axios.post(url + "/api/follow/follow_requests", formData, {
        headers: {
          'auth_token': token, //the token is a variable which holds the token
          'Content-Type': 'multipart/form-data'
        },
      })
      .then((response) => {
        console.log(response)
        if (response.status === 200) {
          prevState[index] = this.state.selectedFollowButton[index] === "Follow" ? "Unfollow" : "Follow"
          this.setState({
            selectedFollowButton:prevState,
          })
        }
        return response;
      })
      .catch((err) => {
        console.log(err);
      });
    }
    else {
      axios.delete(url + "/api/follow/follow_requests", formData, {
        headers: {
          'auth_token': token, //the token is a variable which holds the token
          'Content-Type': 'multipart/form-data'
        },
      })
      .then((response) => {
        console.log(response)
        if (response.status === 200) {
          prevState[index] = this.state.selectedFollowButton[index] === "Follow" ? "Unfollow" : "Follow"
          this.setState({
            selectedFollowButton:prevState,
          })
        }
        return response;
      })
      .catch((err) => {
        console.log(err);
      });
    }



  }

  handleChange = (event, newValue) => {
    this.setState({
      value: newValue,
    });
  };

  render() {

    const { classes } = this.props;
    return (
      <div className="Landing">
        <div className="AppBar">
          <NavBar />
        </div>

        <Container className="ProfilePageContainer">
          <h2 className="GeneralLargeFont">My Profile</h2>
          <hr className="ProfilePageLine" />

          <Row>
            <Col sm={2}>
              <img
                className="ProfilePhoto"
                src={"https://image.flaticon.com/icons/svg/2317/2317981.svg"}
                alt="UserImage"
              />
            </Col>
            <Col sm={6}>
              {this.state.user && (
                <p className="GeneralMediumFont">
                  {this.state.user.name} {this.state.user.surname}
                </p>
              )}
              {this.state.user && (<p className="GeneralSmallFont">{this.state.user.job}</p>)}
            </Col>
            <Col>
              <Row>
                <Col>
                  <p className="FollowInformation">
                    {this.state.followers.length} Followers
                  </p>
                </Col>
                <Col>
                  <p className="FollowInformation">
                    {this.state.followings.length} Following
                  </p>
                </Col>
              </Row>
              <Link to = 'editProfile'>
              <Button
                className="ProfileFollowButton"
                variant="primary"
                size="lg"
                block
              >
                Edit Profile
              </Button></Link>
              <Row className="RatingColumn">
                <Rating
                  name="half-rating-read"
                  defaultValue={0}
                  precision={0.5}
                  readOnly
                  size="large"
                />
              </Row>
            </Col>
          </Row>
        </Container>

        <Container className="ProfilePageContainer">
          <div>
            <AppBar position="static">
              <Tabs
                value={this.state.value}
                onChange={this.handleChange}
                aria-label="simple tabs example"
              >
                <Tab label="My Projects" {...a11yProps(0)} />
                <Tab label="Followers" {...a11yProps(1)} />
                <Tab label="Following" {...a11yProps(2)} />
              </Tabs>
            </AppBar>
            <TabPanel value={this.state.value} index={0}>
              <Row>

                {this.state.researchs.map((value, index) => {
                  return(
                <Col>
                <Card className="ProfileProjectsCard">
                  <Card.Body>
                    <Card.Title>{value.title}</Card.Title>
                    <Card.Text>Year: {value.year}</Card.Text>
                  </Card.Body>
                </Card>
              </Col>)
                })}

              </Row>
            </TabPanel>
            <TabPanel value={this.state.value} index={1}>
            <List>

              {this.state.followers.map((value, index) => {
                return(
                <ListItem>
                  <ListItemAvatar>
                    <Avatar>
                      <PersonIcon />
                    </Avatar>
                  </ListItemAvatar>
                  <ListItemText
                  style={{color: colors.secondary}}
                    primary={`${value.name} ${value.name}`}

                  />
                  <ListItemSecondaryAction>
                <Button id={"FollowButton" + index} key={index} onClick={this.handlerFollow.bind(this,index,value.id)}>{this.state.selectedFollowButton[index]}</Button>
                <Button style={{backgroundColor: "#F44336"}}>Report</Button>

                  </ListItemSecondaryAction>
                </ListItem>
            )})}
            </List>
            </TabPanel>
            <TabPanel value={this.state.value} index={2}>
            <List>
              {this.state.followings.map((value, index) => {
                return(
                <ListItem>
                  <ListItemAvatar>
                    <Avatar>
                      <PersonIcon />
                    </Avatar>
                  </ListItemAvatar>
                  <ListItemText
                  style={{color: colors.secondary}}
                    primary={`${value.name} ${value.name}`}

                  />
                  {
                  <ListItemSecondaryAction>

                      <Button  id={"FollowButtonInFollowings" + index} key={index}>Follow</Button>
                      <Button style={{backgroundColor: "#F44336"}}>Report</Button>

                  </ListItemSecondaryAction>}
                </ListItem>
            )})}
            </List>
            </TabPanel>
          </div>
        </Container>
      </div>
    );
  }
}

export default ProfilePage;
