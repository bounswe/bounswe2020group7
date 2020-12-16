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
import Grid from "@material-ui/core/Grid";
import NavBar from "../NavBar/NavBar";
import jwt_decode from "jwt-decode";
import requestService from "../../services/requestService";
import {Link} from 'react-router-dom'
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemAvatar from '@material-ui/core/ListItemAvatar';
import ListItemSecondaryAction from '@material-ui/core/ListItemSecondaryAction';
import ListItemText from '@material-ui/core/ListItemText';
import PersonIcon from '@material-ui/icons/Person';
import Avatar from '@material-ui/core/Avatar';
import colors from "../../utils/colors";
import config from "../../utils/config";
import axios from 'axios'
import Spinner from '../Spinner/Spinner'
import EditResearchDialog from './EditResearchDialog/EditResearchDialog'


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
      followings_id_arr: [],
      user: null,
      value: 0,
      job: "",
      renderTrigger: false,
      isLoading: true,


    };
  }
  componentDidMount() {
    const token = localStorage.getItem("jwtToken");
    const decoded = jwt_decode(token);
    const profileId = this.props.match.params.profileId
    Promise.all([
    requestService.followings(profileId).then((response) => {


      this.setState({
        followings: response.data.followings,
      });
      let temp_followings_id_arr = []
      for(var obj in this.state.followings){
        temp_followings_id_arr.push(this.state.followings[obj].id)
      }
      this.setState({
        followings_id_arr: temp_followings_id_arr
      })
    }),
    requestService.followers(profileId).then((response) => {


      this.setState({
        followers: response.data.followers,
        selectedFollowButton: Array(response.data.followers.length).fill("Follow"),
      });

    }),
    requestService.getUser(profileId).then((response) => {
      this.setState({
        user: response.data,
      });
    }),
    requestService.getResearchs(profileId).then((response) => {
      this.setState({
        researchs: response.data.research_info,
      });
    }),
  ]).then(() => this.setState({isLoading: false}));


  }



  handleDeleteResearchInformation = (id) => {
    console.log("id", id)
    const token = localStorage.getItem("jwtToken");
    const decoded = jwt_decode(token);
    let formData = new FormData();
    const url = config.BASE_URL

    formData.append("research_id", id);
    axios.delete(url + "/api/profile/research_information", {
      headers: {
        'auth_token': token, //the token is a variable which holds the token
      },
      data: formData,
    })
    .then((response) => {
      if (response.status === 200) {
        document.location.reload()
      }

    })
    .catch((err) => {
      console.log(err);
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
    formData.append("following_id", following_id);


    const url = config.BASE_URL

    if(followButtonState==="Follow"){
    axios.post(url + "/api/follow/follow_requests", {
        headers: {
          'auth_token': token, //the token is a variable which holds the token
        },
        formData,
      })
      .then((response) => {
        if (response.status === 200) {

          let temp_followings_id_arr = [following_id, ...this.state.followings_id_arr]

          this.setState({
            followings_id_arr: temp_followings_id_arr
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
      <div className="ProfilePageLanding">
        <div className="AppBar">
          <NavBar />
        </div>
        { this.state.isLoading ? <div className="ProfilePageSpinner"><Spinner/></div> :
        <div>


        <Container className="ProfilePageContainer">
          <h2 className="GeneralLargeFont">Profile</h2>
          <hr className="ProfilePageLine" />

          <Row>
            <Col sm={2}>
              <img
                className="ProfilePhoto"
                src={this.state.user.profile_photo ? this.state.user.profile_photo : "https://picsum.photos/500/500"}

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
              <Link to = {`/${this.props.match.params.profileId}/edit`}>


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
                <Tab label="Workspaces" {...a11yProps(0)} />
                <Tab label="Researchs" {...a11yProps(1)} />
                <Tab label="Followers" {...a11yProps(2)} />
                <Tab label="Following" {...a11yProps(3)} />
              </Tabs>
            </AppBar>
            <TabPanel value={this.state.value} index={0}>
              <Row>
              <Col>
              </Col>
              </Row>
            </TabPanel>
            <TabPanel value={this.state.value} index={1}>
              <Row>

                {this.state.researchs.map((value, index) => {
                  return(
                <Col>
                <Card style={{backgroundColor: colors.tertiaryLight}}>
                  <Card.Body>
                  <Card.Title  style={{color: colors.primaryDark}}>Title</Card.Title>
                    <Card.Text style={{color: colors.primaryLight}}>{value.title}</Card.Text>
                    <Card.Title style={{color: colors.primaryDark}}>Description</Card.Title>

                    <Card.Text style={{color: colors.primaryLight}}>{value.description === "" ? "No description is provided." : value.description}</Card.Text>
                    <Card.Title style={{color: colors.primaryDark}}>Year</Card.Title>

                    <Card.Text style={{color: colors.primary}}>{value.year}</Card.Text>
                  </Card.Body>
                  <Grid   container
              spacing={0}
                  direction="column"
                  alignItems="center"
                  justify="center"
                  >
                  <EditResearchDialog  type="EDIT" dialogTitle="Edit Research"  id={value.id} title={value.title} description={value.description} year={value.year}/>
                  <Button  onClick = {() => this.handleDeleteResearchInformation(value.id)} style={{backgroundColor: colors.quinary}}>Delete</Button>
                  </Grid>
                </Card>
              </Col>)
                })}

              </Row>
              <Row style={{marginTop: "20px"}}>
              <Grid container
                  direction="column"
                  alignItems="center"
                  justify="center" >

              <EditResearchDialog type="NEW" dialogTitle="Add New Research" id={""} title={""} description={""} year={""}/>
              </Grid>
              </Row>
            </TabPanel>
            <TabPanel value={this.state.value} index={2}>
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
                    primary={`${value.name} ${value.surname}`}

                  />
                  <ListItemSecondaryAction>
                <Button id={"FollowButton" + index} key={index} onClick={this.handlerFollow.bind(this,index,value.id)}>{this.state.followings_id_arr.includes(value.id)  ? "Unfollow" : "Follow"}</Button>
                <Button style={{backgroundColor: "#F44336"}}>Report</Button>

                  </ListItemSecondaryAction>
                </ListItem>
            )})}
            </List>
            </TabPanel>
            <TabPanel value={this.state.value} index={3}>
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
                    primary={`${value.name} ${value.surname}`}

                  />
                  {
                  <ListItemSecondaryAction>

                      <Button  id={"FollowButtonInFollowings" + index} key={index}>Unfollow</Button>


                      <Button style={{backgroundColor: "#F44336"}}>Report</Button>

                  </ListItemSecondaryAction>}
                </ListItem>
            )})}
            </List>
            </TabPanel>
          </div>
        </Container>
        </div>
  }

      </div>
    );
  }
}

export default ProfilePage;
