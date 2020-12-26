import React, { Component } from "react";
import "./EditProfile.css";
import NavBar from "../NavBar/NavBar";
import colors from "../../utils/colors";
import { Container, Col, Row, Button } from "react-bootstrap";
import "bootstrap/dist/css/bootstrap.min.css";
import {
  TextField,
  withStyles,
  Switch,
  FormControlLabel,
} from "@material-ui/core";

import axios from "axios";
import MuiAlert from "@material-ui/lab/Alert";
import jwt_decode from "jwt-decode";
import config from "../../utils/config";
import Snackbar from "@material-ui/core/Snackbar";
import { Link } from "react-router-dom";
import requestService from "../../services/requestService";
import Spinner from "../Spinner/Spinner";
import Autocomplete, {
  createFilterOptions,
} from "@material-ui/lab/Autocomplete";
import InputLabel from "@material-ui/core/InputLabel";

const filter = createFilterOptions();

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
      name: "",
      surname: "",
      email: "",
      job: "",
      is_private: false,
      profile_photo: undefined,
      google_scholar_name: "",
      researchgate_name: "",
      institution: "",
      skills: "",
      showSuccess: false,
      fieldEmptyError: false,
      user: null,
      profileId: null,
      isLoading: true,
      jobList: [],
    };
  }

  componentDidMount() {
    const token = localStorage.getItem("jwtToken");
    const decoded = jwt_decode(token);
    this.setState({
      profileId: decoded.id,
    });

    requestService.getJobList().then((response) => {
      if (response) {
        let jobArray = [];
        for (var key in response.data) {
          jobArray.push(response.data[key].name);
        }
        this.setState({
          jobList: jobArray,
        });
      }
    });

    requestService
      .getUser(decoded.id)
      .then((response) => {
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
          is_private: this.state.user.is_private,
        });
      })
      .then(() => {
        this.setState({
          isLoading: false,
        });
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

  handleSwitch = (event) => {
    this.setState({
      is_private: !this.state.is_private,
    });
  };

  handleSubmit = () => {
    const token = localStorage.getItem("jwtToken");
    const decoded = jwt_decode(token);

    let formData = new FormData();
    let dbcheck = false;

    if (this.state.name !== this.state.user.name) {
      formData.append("name", this.state.name);
      dbcheck = true;
    }
    if (this.state.surname !== this.state.user.surname) {
      formData.append("surname", this.state.surname);
      dbcheck = true;
    }
    if (this.state.job !== this.state.user.job) {
      formData.append("job", this.state.job);
      dbcheck = true;
    }
    
    formData.append("profile_photo", this.state.profile_photo_file);

    if (
      this.state.google_scholar_name !== this.state.user.google_scholar_name
    ) {
      formData.append("google_scholar_name", this.state.google_scholar_name);
      dbcheck = true;
    }
    if (this.state.researchgate_name !== this.state.user.researchgate_name) {
      formData.append("researchgate_name", this.state.researchgate_name);
      dbcheck = true;
    }
    if (this.state.is_private !== this.state.user.is_private) {
      formData.append("is_private", this.state.is_private);
      dbcheck = true;
    }

    if (!dbcheck) {
      return;
    }

    for (var pair of formData.entries()) {
      console.log(pair[0]+ ', ' + pair[1]); 
  }

    const url = config.BASE_URL;
    axios
      .put(url + "/api/auth_system/user", formData, {
        headers: {
          auth_token: token, //the token is a variable which holds the token
        },
      })
      .then((response) => {
        console.log(response);
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
  };

  handleJob = (value) => {
    this.setState({
      job: value,
    });
  };

  handleKeyDown = (event) => {
    this.handleJob(event.target.value);
    console.log(this.state.job);
  };

  render() {
    return (
      <div className="EditProfileLanding">
        <div className="AppBar">
          <NavBar />
        </div>
        {this.state.isLoading ? (
          <div className="EditProfileSpinner">
            <Spinner />
          </div>
        ) : (
          <Container className="ProfilePageContainer">
            <Row className="mb-3 justify-content-center">
              <Col sm={6}>
                <a href={`/${this.state.profileId}`}>Back to profile</a>
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
                    <StyledTextField
                      defaultValue={this.state.user.name}
                      className="EditProfileTextInput"
                      id="outlined-basic"
                      label="First Name"
                      variant="outlined"
                      fullWidth
                      onChange={(e) => this.setState({ name: e.target.value })}
                    />
                  </Col>
                  <Col sm={6}>
                    <StyledTextField
                      defaultValue={this.state.user.surname}
                      className="EditProfileTextInput"
                      id="outlined-basic"
                      label="Second Name"
                      variant="outlined"
                      fullWidth
                      onChange={(e) =>
                        this.setState({ surname: e.target.value })
                      }
                    />
                  </Col>
                </Row>
              </Col>
            </Row>

            <Row className="mb-3 justify-content-center">
              <Col sm={6}>
                <Autocomplete
                  freeSolo
                  id="tags-outlined"
                  options={this.state.jobList}
                  getOptionLabel={(option) => option}
                  value={this.state.job}
                  onChange={(event, newValue) => this.handleJob(newValue)}
                  filterSelectedOptions
                  renderInput={(params) => {
                    params.inputProps.onChange = this.handleKeyDown;
                    return (
                      <StyledTextField
                        {...params}
                        variant="outlined"
                        required
                        name="affinities"
                        label="Job"
                        id="job"
                        ffullWidth
                        margin="normal"
                      />
                    );
                  }}
                />
              </Col>
            </Row>

            <Row className="mb-3 justify-content-center">
              <Col sm={6}>
                <StyledTextField
                  defaultValue={this.state.user.skills}
                  className="EditProfileTextInput"
                  id="outlined-basic"
                  label="Email"
                  variant="outlined"
                  fullWidth
                  onChange={(e) => this.setState({ email: e.target.value })}
                />
              </Col>
            </Row>

            <Row className="mb-3 justify-content-center">
              <Col sm={6}>
                <StyledTextField
                  defaultValue={this.state.user.skills}
                  className="EditProfileTextInput"
                  id="outlined-basic"
                  label="Skills"
                  variant="outlined"
                  fullWidth
                  onChange={(e) => this.setState({ skills: e.target.value })}
                />
              </Col>
            </Row>
            <Row className="mb-3 justify-content-center">
              <Col sm={6}>
                <InputLabel htmlFor="component-simple">
                  Profile Photo Url
                </InputLabel>
                <StyledTextField
                  type="file"
                  className="EditProfileTextInput"
                  id="outlined-basic"
                  variant="outlined"
                  fullWidth
                  onChange={(e) =>
                    this.setState({ profile_photo_file: e.target.files })
                  }
                />
              </Col>
            </Row>
            <Row className="mb-3 justify-content-center">
              <Col sm={6}>
                <StyledTextField
                  defaultValue={this.state.user.google_scholar_name}
                  className="EditProfileTextInput"
                  id="outlined-basic"
                  label="Google Scholar Url"
                  variant="outlined"
                  fullWidth
                  onChange={(e) =>
                    this.setState({ google_scholar_name: e.target.value })
                  }
                />
              </Col>
            </Row>
            <Row className="mb-3 justify-content-center">
              <Col sm={6}>
                <StyledTextField
                  defaultValue={this.state.user.researchgate_name}
                  className="EditProfileTextInput"
                  id="outlined-basic"
                  label="Research Gate Url"
                  variant="outlined"
                  fullWidth
                  onChange={(e) =>
                    this.setState({ researchgate_name: e.target.value })
                  }
                />
              </Col>
            </Row>

            <Row className="mb-3 justify-content-center">
              <Col sm={6}>
                <FormControlLabel
                  control={
                    <Switch
                      checked={this.state.is_private}
                      onChange={this.handleSwitch}
                      name="checkedA"
                      inputProps={{ "aria-label": "secondary checkbox" }}
                    />
                  }
                  label={this.state.is_private ? "Private" : "Public"}
                />
              </Col>
            </Row>

            <Row className="mb-3 justify-content-center">
              <Col sm={6}>
                <Button
                  className="ProfileUpdateButton"
                  variant="primary"
                  size="lg"
                  block
                  onClick={this.handleSubmit}
                >
                  Update
                </Button>
              </Col>
            </Row>
          </Container>
        )}

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
