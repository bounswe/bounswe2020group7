import React from "react";
import "./EditProfile.css";
import AppBar from "../AppBar/AppBar";
import colors from "../../utils/colors";
import { Container, Col, Row, Button } from "react-bootstrap";
import 'bootstrap/dist/css/bootstrap.min.css';
import { TextField, withStyles } from '@material-ui/core';

const StyledTextField = withStyles({
    root: {
      "& .MuiInputBase-input": {
        color: colors.secondary,
      },
      "& .MuiOutlinedInput-input": {
        color: colors.secondary,
      },
      "& .Mui-required": {
        color: colors.primaryLight,
      },
      "& label.Mui-focused": {
        color: colors.tertiary,
      },
      "& .MuiInput-underline:after": {
        borderBottomColor: colors.tertiary,
      },
      "& .MuiOutlinedInput-root": {
        "& fieldset": {
          borderColor: colors.secondaryLight,
        },
        "&:hover fieldset": {
          borderColor: colors.secondaryDark,
        },
        "&.Mui-focused fieldset": {
          borderColor: colors.tertiary,
        },
      },
    },
  })(TextField);

const EditProfile = (props) => {
    return (
      <div className="Landing">
        <div className="AppBar">
          <AppBar />
        </div>

        <Container className = "ProfilePageContainer">
            <Row className="mb-3 justify-content-center">
                <Col sm={6}>
                    <h2 className="ProfileLargeFont">Edit Profile</h2>
                    <hr className="ProfilePageLine" />
                </Col>
            </Row>


            <Row className="mb-3 justify-content-center">
                <Col sm={6}>
                    <Row>
                        <Col sm={6}>
                            <StyledTextField className="EditProfileTextInput" id="outlined-basic" label="First Name" variant="outlined" fullWidth />
                        </Col>
                        <Col sm={6}>
                            <StyledTextField className="EditProfileTextInput" id="outlined-basic" label="Second Name" variant="outlined" fullWidth required/>
                        </Col>
                    </Row>
                </Col>
            </Row>

            <Row className="mb-3 justify-content-center">
                <Col sm={6}>
                    <StyledTextField className="EditProfileTextInput" id="outlined-basic" label="E-mail" variant="outlined" fullWidth required/>
                </Col>
            </Row>

            <Row className="mb-3 justify-content-center">
                <Col sm={6}>
                    <StyledTextField className="EditProfileTextInput" id="outlined-basic" label="Password" variant="outlined" type="password" fullWidth required/>
                </Col>
            </Row>

            <Row className="mb-3 justify-content-center">
                <Col sm={6}>
                    <StyledTextField className="EditProfileTextInput" id="outlined-basic" label="Job" variant="outlined" fullWidth required/>
                </Col>
            </Row>

            <Row className="mb-3 justify-content-center">
                <Col sm={6}>
                    <StyledTextField className="EditProfileTextInput" id="outlined-basic" label="Profile Photo Url" variant="outlined" fullWidth required/>
                </Col>
            </Row>

            <Row className="mb-3 justify-content-center">
                <Col sm={6}>
                    <Button className="ProfileUpdateButton" variant="primary" size="lg" block>
                        Update
                    </Button>
                </Col>
            </Row>

        </Container>

      </div>
    );
  };

  export default EditProfile;