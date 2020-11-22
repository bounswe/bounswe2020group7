import React from "react";
import "./ProfilePage.css";
import AppBar from "../AppBar/AppBar";
import { Container, Col, Row, Button, Card } from "react-bootstrap";
import 'bootstrap/dist/css/bootstrap.min.css';
import Rating from '@material-ui/lab/Rating';

const ProfilePage = (props) => {
    return (
      <div className="Landing">
        <div className="AppBar">
          <AppBar />
        </div>
        
        <Container className = "ProfilePageContainer">
            <h2 className="GeneralLargeFont">My Profile</h2>
            <hr className="ProfilePageLine" />

            <Row>
                <Col sm={2}>
                    <img className = "ProfilePhoto" src={"https://image.flaticon.com/icons/svg/2317/2317981.svg"} alt="UserImage"/>
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
                    <Button className="ProfileFollowButton" variant="primary" size="lg" block>
                        Follow
                    </Button>
                    <Row className="RatingColumn">
                        <Rating name="half-rating-read" defaultValue={3.5} precision={0.5} readOnly size="large"/>
                    </Row> 
                </Col>
            </Row>

        </Container>

        <Container className = "ProfilePageContainer">
            <h2 className="ProfileLargeFont">My Projects</h2>
            <hr className="ProfilePageLine" />

            <Row>
                <Col>
                    <Card className="ProfileProjectsCard">
                        <Card.Img variant="top" src="https://picsum.photos/500/500" />
                        <Card.Body>
                            <Card.Title>My Project's Name</Card.Title>
                            <Card.Text>
                            My project's explanation.
                            </Card.Text>
                        </Card.Body>
                    </Card>
                </Col>
                <Col>
                    <Card className="ProfileProjectsCard">
                        <Card.Img variant="top" src="https://picsum.photos/500/500" />
                        <Card.Body>
                            <Card.Title>My Project's Name</Card.Title>
                            <Card.Text>
                            My project's explanation.
                            </Card.Text>
                        </Card.Body>
                    </Card>    
                </Col>
                <Col>
                    <Card className="ProfileProjectsCard">
                        <Card.Img variant="top" src="https://picsum.photos/500/500" />
                        <Card.Body>
                            <Card.Title>My Project's Name</Card.Title>
                            <Card.Text>
                            My project's explanation.
                            </Card.Text>
                        </Card.Body>
                    </Card>
                </Col>
                <Col>
                    <Card className="ProfileProjectsCard">
                        <Card.Img variant="top" src="https://picsum.photos/500/500" />
                        <Card.Body>
                            <Card.Title>My Project's Name</Card.Title>
                            <Card.Text>
                            My project's explanation.
                            </Card.Text>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>

        </Container>

      </div>
    );
  };
  
  export default ProfilePage;