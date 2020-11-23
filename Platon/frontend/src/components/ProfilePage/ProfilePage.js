import React from "react";
import "./ProfilePage.css";
import { Container, Col, Row, Button, Card } from "react-bootstrap";
import "bootstrap/dist/css/bootstrap.min.css";
import Rating from "@material-ui/lab/Rating";
import PropTypes from "prop-types";
import { makeStyles } from "@material-ui/core/styles";
import AppBar from "@material-ui/core/AppBar";
import Tabs from "@material-ui/core/Tabs";
import Tab from "@material-ui/core/Tab";
import Typography from "@material-ui/core/Typography";
import Box from "@material-ui/core/Box";
import NavBar from "../NavBar/NavBar";

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

const useStyles = makeStyles((theme) => ({
  root: {
    flexGrow: 1,
    backgroundColor: theme.palette.background.paper,
  },
}));

const ProfilePage = (props) => {
  const classes = useStyles();
  const [value, setValue] = React.useState(0);

  const handleChange = (event, newValue) => {
    setValue(newValue);
  };

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
            <p className="GeneralMediumFont">Name Surname</p>
            <p className="GeneralSmallFont">Explanation</p>
          </Col>
          <Col>
            <Row>
              <Col>
                <p className="FollowInformation">200 Followers</p>
              </Col>
              <Col>
                <p className="FollowInformation">10 Following</p>
              </Col>
            </Row>
            <Button
              className="ProfileFollowButton"
              variant="primary"
              size="lg"
              block
            >
              Follow
            </Button>
            <Row className="RatingColumn">
              <Rating
                name="half-rating-read"
                defaultValue={3.5}
                precision={0.5}
                readOnly
                size="large"
              />
            </Row>
          </Col>
        </Row>
      </Container>

      <Container className="ProfilePageContainer">
        <div className={classes.root}>
          <AppBar position="static">
            <Tabs
              value={value}
              onChange={handleChange}
              aria-label="simple tabs example"
            >
              <Tab label="My Projects" {...a11yProps(0)} />
              <Tab label="Item Two" {...a11yProps(1)} />
              <Tab label="Item Three" {...a11yProps(2)} />
            </Tabs>
          </AppBar>
          <TabPanel value={value} index={0}>
          <Row>
          <Col>
            <Card className="ProfileProjectsCard">
              <Card.Img variant="top" src="https://picsum.photos/500/500" />
              <Card.Body>
                <Card.Title>My Project's Name</Card.Title>
                <Card.Text>My project's explanation.</Card.Text>
              </Card.Body>
            </Card>
          </Col>
          <Col>
            <Card className="ProfileProjectsCard">
              <Card.Img variant="top" src="https://picsum.photos/500/500" />
              <Card.Body>
                <Card.Title>My Project's Name</Card.Title>
                <Card.Text>My project's explanation.</Card.Text>
              </Card.Body>
            </Card>
          </Col>
          <Col>
            <Card className="ProfileProjectsCard">
              <Card.Img variant="top" src="https://picsum.photos/500/500" />
              <Card.Body>
                <Card.Title>My Project's Name</Card.Title>
                <Card.Text>My project's explanation.</Card.Text>
              </Card.Body>
            </Card>
          </Col>
          <Col>
            <Card className="ProfileProjectsCard">
              <Card.Img variant="top" src="https://picsum.photos/500/500" />
              <Card.Body>
                <Card.Title>My Project's Name</Card.Title>
                <Card.Text>My project's explanation.</Card.Text>
              </Card.Body>
            </Card>
          </Col>
        </Row>
          </TabPanel>
          <TabPanel value={value} index={1}>
            Item Two
          </TabPanel>
          <TabPanel value={value} index={2}>
            Item Three
          </TabPanel>
        </div>

      </Container>
    </div>
  );
};

export default ProfilePage;
