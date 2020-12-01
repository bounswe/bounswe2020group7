import React, { Component } from "react";
import "./EditProfile.css";
import NavBar from "../NavBar/NavBar";
import colors from "../../utils/colors";
import { Container, Col, Row, Button } from "react-bootstrap";
import 'bootstrap/dist/css/bootstrap.min.css';
import { TextField, withStyles } from '@material-ui/core';
import axios from 'axios';
import MuiAlert from "@material-ui/lab/Alert";
import jwt_decode from "jwt-decode";
import config from "../../utils/config";
import Snackbar from "@material-ui/core/Snackbar";
import { Link } from "react-router-dom";
import requestService from "../../services/requestService";
import Spinner from '../Spinner/Spinner'
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

function Alert(props) {
  return <MuiAlert elevation={6} variant="filled" {...props} />;
}
class EditProfile extends Component {
  constructor(props) {
    super(props);
    this.state = {
      name:"",
      surname:"",
      job: "",
      skills: "",
      is_private: false,
      profile_photo: "",
      google_scholar_name: "",
      researchgate_name: "",
      showSuccess: false,
      fieldEmptyError: false,
      user: null,
      profileId: null,
      isLoading: true,
     }
  }

  componentDidMount(){
    const token = localStorage.getItem("jwtToken");
    const decoded = jwt_decode(token);
    this.setState({
      profileId: decoded.id
    })
    requestService.getUser(decoded.id).then((response) => {
      this.setState({
        user: response.data,
      });
      this.setState({
        name: this.state.user.name,
        surname: this.state.user.surname,
        job: this.state.user.job,
        profile_photo: this.state.user.profile_photo,
        google_scholar_name: this.state.user.google_scholar_name,
        researchgate_name: this.state.user.researchgate_name,
      })
      this.setState({
        isLoading: false
      })
    });


  }

  handleCloseFieldEmpty = (event, reason) => {
    if (reason === "clickaway") {
      return;
    }

    this.setState({ fieldEmptyError: false });
  };



  handleCloseSuccess = (event, reason) => {
    if (reason === "clickaway") {
      return;
    }

    this.setState({ showSuccess: false });
  };
  handleSubmit = () => {
    const token = localStorage.getItem("jwtToken");
    const decoded = jwt_decode(token);

    let formData = new FormData();
    let dbcheck = false

    if(this.state.name !== this.state.user.name){
      formData.append("name", this.state.name)
      dbcheck=true
    }
    if(this.state.surname !== this.state.user.surname){
      formData.append("surname", this.state.surname)
      dbcheck=true
    }
    if(this.state.job !== this.state.user.job){
      formData.append("job", this.state.job)
      dbcheck=true
    }
    if(this.state.profile_photo !== this.state.user.profile_photo){
      formData.append("profile_photo", this.state.profile_photo)
      dbcheck=true
    }
    if(this.state.google_scholar_name !== this.state.user.google_scholar_name){
      formData.append("google_scholar_name", this.state.google_scholar_name)
      dbcheck=true
    }
    if(this.state.researchgate_name !== this.state.user.researchgate_name){
      formData.append("researchgate_name", this.state.researchgate_name)
      dbcheck=true
    }

    if(!dbcheck){
      return
    }
    const url = config.BASE_URL
    axios.put(url + "/api/auth_system/user", formData, {
        headers: {
          'auth_token': token, //the token is a variable which holds the token
        },
      })
      .then((response) => {
        console.log(response)
        if (response.status === 200) {
          this.setState({
            showSuccess: "Your profile is successfully edited.",
          });
        }
        return response;
      })
      .catch((err) => {
        this.setState({
            fieldEmptyError: "A problem is occurred",
        });
        console.log(err);
      });
  }
  render() {
    console.log(this.state.isLoading)
    return (
      <div className="Landing">

        <div className="AppBar">
          <NavBar />
        </div>
        { this.state.isLoading ? <Spinner/> :
        <Container className = "ProfilePageContainer">
        <Row className="mb-3 justify-content-center">
                <Col sm={6}>
                    <Link to={`/${this.state.profileId}`}>
                        Back to profile
                    </Link>
                </Col>
            </Row>
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
                            <StyledTextField defaultValue={this.state.user.name} className="EditProfileTextInput" id="outlined-basic" label="First Name" variant="outlined" fullWidth
                            onChange={(e) => this.setState({ name: e.target.value })}/>
                        </Col>
                        <Col sm={6}>
                            <StyledTextField defaultValue={this.state.user.surname}  className="EditProfileTextInput" id="outlined-basic" label="Second Name" variant="outlined" fullWidth
                            onChange={(e) => this.setState({ surname: e.target.value })}/>
                        </Col>
                    </Row>
                </Col>
            </Row>

            <Row className="mb-3 justify-content-center">
                <Col sm={6}>
                    <StyledTextField defaultValue={this.state.user.job} className="EditProfileTextInput" id="outlined-basic" label="Job" variant="outlined" fullWidth
                    onChange={(e) => this.setState({ job: e.target.value })}/>
                </Col>
            </Row>
            <Row className="mb-3 justify-content-center">
                <Col sm={6}>
                    <StyledTextField defaultValue={this.state.user.skills} className="EditProfileTextInput" id="outlined-basic" label="Skills" variant="outlined" fullWidth
                    onChange={(e) => this.setState({ skills: e.target.value })}/>
                </Col>
            </Row>
            <Row className="mb-3 justify-content-center">
                <Col sm={6}>
                    <StyledTextField defaultValue={this.state.user.profile_photo} className="EditProfileTextInput" id="outlined-basic" label="Profile Photo Url" variant="outlined" fullWidth
                    onChange={(e) =>  this.setState({ profile_photo: e.target.value })}/>
                </Col>
            </Row>
            <Row className="mb-3 justify-content-center">
                <Col sm={6}>
                    <StyledTextField defaultValue={this.state.user.google_scholar_name} className="EditProfileTextInput" id="outlined-basic" label="Google Scholar Url" variant="outlined" fullWidth
                     onChange={(e) => this.setState({ google_scholar_name: e.target.value })}/>
                </Col>
            </Row>
            <Row className="mb-3 justify-content-center">
                <Col sm={6}>
                    <StyledTextField defaultValue={this.state.user.researchgate_name}  className="EditProfileTextInput" id="outlined-basic" label="Research Gate Url" variant="outlined" fullWidth
                    onChange={(e) => this.setState({ researchgate_name: e.target.value })}/>
                </Col>
            </Row>

            <Row className="mb-3 justify-content-center">
                <Col sm={6}>
                    <Button className="ProfileUpdateButton" variant="primary" size="lg" block onClick={this.handleSubmit}>
                        Update
                    </Button>
                </Col>
            </Row>

        </Container> }
        {this.state.fieldEmptyError && (
              <Snackbar
                open={this.state.fieldEmptyError}
                autoHideDuration={3000}
                onClose={this.handleCloseFieldEmpty}
              >
                <Alert
                  style={{ backgroundColor: colors.quinary }}
                  severity="error"
                  onClose={this.handleCloseFieldEmpty}
                >
                  {this.props.fieldEmptyError || this.state.fieldEmptyError}
                </Alert>
              </Snackbar>
            )}
            {this.state.showSuccess && (
              <Snackbar
                open={this.state.showSuccess}
                autoHideDuration={3000}
                onClose={this.handleCloseSuccess}
              >
                <Alert
                  style={{ backgroundColor: colors.quaternary }}
                  severity="success"
                  onClose={this.handleCloseSuccess}
                >
                  {this.props.showSuccess || this.state.showSuccess}
                </Alert>
              </Snackbar>
            )}

      </div>

    );
  }
}


export default EditProfile;