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
import Chip from "@material-ui/core/Chip";

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
      job: "",
      is_private: false,
      profile_photo: undefined,
      google_scholar_name: "",
      researchgate_name: "",
      institution: "",
      skills: [],
      personalSkills: [],
      newSkill: "",
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

    Promise.all([
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
      }),

      requestService.getSkillList().then((response) => {
        if (response) {
          this.setState({
            skills: response.data,
          });
        }
      }),

      requestService.getPersonalSkillList(decoded.id).then((response) => {
        if (response) {
          let skillArray = [];
          for (var key in response.data.skills) {
            skillArray.push(response.data.skills[key].name);
          }
          this.setState({
            personalSkills: skillArray,
          });
        }
      }),

      requestService.getUser(decoded.id).then((response) => {
        this.setState({
          user: response.data,
        });
        this.setState({
          name: this.state.user.name,
          surname: this.state.user.surname,
          job: this.state.user.job,
          google_scholar_name: this.state.user.google_scholar_name,
          researchgate_name: this.state.user.researchgate_name,
          is_private: this.state.user.is_private,
        });
      }),
    ]).then(() => {
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
    requestService
      .putUser(
        this.state.name,
        this.state.surname,
        this.state.job,
        this.state.is_private,
        this.state.profile_photo,
        this.state.google_scholar_name,
        this.state.researchgate_name,
        this.state.institution
      )
      .then((response) => {
        if (response.status === 200) {
          this.setState({
            showSuccess: "You have updated your profile successfully!",
          });
        }
      })
      .catch((err) => {
        this.setState({
          fieldEmptyError: "An error occured!",
        });
      });
  };

  handleJob = (value) => {
    this.setState({
      job: value,
    });
  };

  handleKeyDown = (event) => {
    this.handleJob(event.target.value);
  };

  handleKeyDownSkill = (event) => {
    this.handleSkill(event.target.value);
  };

  handleSkill = (value) => {
    this.setState({
      newSkill: value,
    });
  };

  handlePostSkills = () => {
    const token = localStorage.getItem("jwtToken");
    const decoded = jwt_decode(token);
    requestService.postSkill(this.state.newSkill).then((response) => {
      if (response) {
        this.setState({
          showSuccess: "You have added your skill successfully",
        });
        requestService
          .getPersonalSkillList(decoded.id)
          .then((resp) => {
            if (resp) {
              let skillArray = [];
              for (var key in resp.data.skills) {
                skillArray.push(resp.data.skills[key].name);
              }
              this.setState({
                personalSkills: skillArray,
                newSkill: "",
              });
            }
          })
          .then(() => {
            requestService.getSkillList().then((response) => {
              if (response) {
                this.setState({
                  skills: response.data,
                });
              }
            });
          });
      }
    });
  };

  handleDeleteSkill = (value) => {
    const token = localStorage.getItem("jwtToken");
    const decoded = jwt_decode(token);
    requestService.deleteSkill(value).then((response) => {
      if (response) {
        this.setState({
          showSuccess: "You have deleted your skill successfully",
        });
        requestService
          .getPersonalSkillList(decoded.id)
          .then((resp) => {
            if (resp) {
              let skillArray = [];
              console.log(resp)
              for (var key in resp.data.skills) {
                skillArray.push(resp.data.skills[key].name);
              }
              console.log("s", skillArray)
              this.setState({
                personalSkills: skillArray,
              });
            }
          })
          .then(() => {
            requestService.getSkillList(decoded.id).then((response) => {
              if (response) {
                this.setState({
                  skills: response.data,
                });
              }
            });
          });
      }
    });
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
          <Container className="EditPageContainer">
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
                <InputLabel htmlFor="component-simple">
                  Profile Photo
                </InputLabel>
                <StyledTextField
                  type="file"
                  className="EditProfileTextInput"
                  id="outlined-basic"
                  variant="outlined"
                  fullWidth
                  onChange={(e) =>
                    this.setState({ profile_photo: e.target.files[0] })
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
                <StyledTextField
                  defaultValue={this.state.user.institution}
                  className="EditProfileTextInput"
                  id="outlined-basic"
                  label="Institutiton"
                  variant="outlined"
                  fullWidth
                  onChange={(e) =>
                    this.setState({ institution: e.target.value })
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

            <Row className="mb-3 justify-content-center">
              <Col sm={6}>
                <h2 className="ProfileLargeFont">Edit Skills</h2>
                <hr className="ProfilePageLine" />
              </Col>
            </Row>

            <Row className="mb-3 justify-content-center">
              <Col sm={6}>
                {this.state.personalSkills.map((value, index) => {
                  return (
                    <Chip
                      className="ProfileSkillChip mr-1 mb-1"
                      label={value}
                      onDelete={() => this.handleDeleteSkill(value)}
                      variant="outlined"
                    />
                  );
                })}
              </Col>
            </Row>

            <Row className="mb-3 justify-content-center">
              <Col sm={6}>
                <Autocomplete
                  freeSolo
                  id="tags-outlined"
                  options={this.state.skills}
                  getOptionLabel={(option) => option}
                  value={this.state.newSkill}
                  onChange={(event, newValue) => this.handleSkill(newValue)}
                  filterSelectedOptions
                  renderInput={(params) => {
                    params.inputProps.onChange = this.handleKeyDownSkill;
                    return (
                      <StyledTextField
                        {...params}
                        variant="outlined"
                        required
                        name="affinities"
                        label="Skill"
                        id="Skill"
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
                <Button
                  className="ProfileUpdateButton"
                  variant="primary"
                  size="lg"
                  block
                  onClick={this.handlePostSkills}
                >
                  Add
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
