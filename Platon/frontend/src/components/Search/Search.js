import React from "react";
import "./Search.css";

import requestService from "../../services/requestService";
import NavBar from "../NavBar/NavBar";
import AppBar from "../AppBar/AppBar";
import Spinner from "../Spinner/Spinner";
import colors from "../../utils/colors";

import { Container, Col, Row, Button, Card } from "react-bootstrap";
import { Link, Redirect } from "react-router-dom";
import { TextField, withStyles } from "@material-ui/core";
import Avatar from "@material-ui/core/Avatar";
import Checkbox from "@material-ui/core/Checkbox";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import Select from "@material-ui/core/Select";
import FormControl from "@material-ui/core/FormControl";
import InputLabel from "@material-ui/core/InputLabel";
import Autocomplete, {
  createFilterOptions,
} from "@material-ui/lab/Autocomplete";

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

class Search extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      searchResult: [],
      isLoading: true,
      checkedUser: true,
      checkedWorkspace: false,
      checkedUpcomingEvents: false,
      searchQuery: "",
      checkedUserAfter: true,
      checkedWorkspaceAfter: false,
      checkedUpcomingEventsAfter: false,
      checkedTagSearchAfter: false,
      checkedTagSearch: false,
      job_id: null,
      job: "",
      job_list: [],
      sorting_criteria: 0,
      skills: [],
      searchSkill: [],
      skill_filter: "",
      creator_name: "",
      creator_surname: "",
      starting_date_start: "",
      starting_date_end: "",
      deadline_start: "",
      deadline_end: "",
      search_type: 0,
      search_type_after: 0,
    };
  }

  componentDidMount() {
    const searchQuery = this.props.match.params.searchQuery;
    this.setState({ searchQuery: searchQuery });
    if (this.state.checkedUserAfter === true) {
      requestService.getSearchUser(searchQuery).then((response) => {
        this.setState({
          searchResult: response.data.result_list,
        });
      });
    }
    if (this.state.checkedWorkspaceAfter === true) {
      requestService.getSearchWorkspace(searchQuery).then((response) => {
        this.setState({
          searchResult: response.data.result_list,
        });
      });
    }
    if (this.state.checkedUpcomingEventsAfter === true) {
      requestService.getSearchUpcomingEvents(searchQuery).then((response) => {
        this.setState({
          searchResult: response.data.result_list,
        });
      });
    }

    Promise.all([
      requestService.getJobList().then((response) => {
        if (response) {
          this.setState({
            job_list: response.data,
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
    ]).then(() => {
      this.setState({ isLoading: false });
    });
  }

  handleSearch = () => {

    if(this.state.searchQuery==="") return

    if (this.state.checkedUser === true) {
      requestService
        .getSearchUser(
          this.state.searchQuery,
          this.state.job_id,
          this.state.sorting_criteria
        )
        .then((response) => {
          this.setState({
            searchResult: response.data.result_list,
            checkedUserAfter: true,
            checkedWorkspaceAfter: false,
            checkedUpcomingEventsAfter: false,
            checkedTagSearchAfter: false,
          });
        });
    }
    if (this.state.checkedWorkspace === true) {
      requestService
        .getSearchWorkspace(
          this.state.searchQuery,
          this.state.skill_filter,
          this.state.creator_name,
          this.state.creator_surname,
          this.state.starting_date_start,
          this.state.starting_date_end,
          this.state.deadline_start,
          this.state.deadline_end,
          this.state.sorting_criteria
        )
        .then((response) => {
          this.setState({
            searchResult: response.data.result_list,
            checkedUserAfter: false,
            checkedWorkspaceAfter: true,
            checkedUpcomingEventsAfter: false,
            checkedTagSearchAfter: false,
          });
        });
    }
    if (this.state.checkedUpcomingEvents === true) {
      requestService
        .getSearchUpcomingEvents(
          this.state.searchQuery,
          this.state.starting_date_start,
          this.state.starting_date_end,
          this.state.deadline_start,
          this.state.deadline_end,
          this.state.sorting_criteria
        )
        .then((response) => {
          this.setState({
            searchResult: response.data.result_list,
            checkedUserAfter: false,
            checkedWorkspaceAfter: false,
            checkedUpcomingEventsAfter: true,
            checkedTagSearchAfter: false,
          });
        });
    }
    if (this.state.checkedTagSearch === true) {
      console.log("1");
      requestService
        .getTagSearch(this.state.search_type, this.state.searchSkill)
        .then((response) => {
          console.log(response);
          if (response.status === 200 || response.status === 201) {
            this.setState({
              searchResult: response.data.result_list,
              checkedUserAfter: false,
              checkedWorkspaceAfter: false,
              checkedUpcomingEventsAfter: false,
              checkedTagSearchAfter: true,
            });
          }
        });
    }
  };

  handleChangeUser = () => {
    if (this.state.checkedUser === true) {
      this.setState({
        checkedUser: false,
      });
    } else {
      this.setState({
        checkedUser: true,
        checkedWorkspace: false,
        checkedUpcomingEvents: false,
        checkedTagSearch: false,
      });
    }
  };

  handleChangeWorkspace = () => {
    if (this.state.checkedWorkspace === true) {
      this.setState({
        checkedWorkspace: false,
      });
    } else {
      this.setState({
        checkedUser: false,
        checkedWorkspace: true,
        checkedUpcomingEvents: false,
        checkedTagSearch: false,
      });
    }
  };

  handleChangeUpcomingEvents = () => {
    if (this.state.checkedUpcomingEvents === true) {
      this.setState({
        checkedUpcomingEvents: false,
      });
    } else {
      this.setState({
        checkedUser: false,
        checkedWorkspace: false,
        checkedUpcomingEvents: true,
        checkedTagSearch: false,
      });
    }
  };

  handleChangeTagSearch = () => {
    if (this.state.checkedTagSearch === true) {
      this.setState({
        checkedTagSearch: false,
      });
    } else {
      this.setState({
        checkedUser: false,
        checkedWorkspace: false,
        checkedUpcomingEvents: false,
        checkedTagSearch: true,
      });
    }
  };

  handleSkill = (value) => {
    this.setState((prevState) => ({
      searchSkill: value,
    }));
    console.log(this.state.searchSkill);
  };

  render() {
    return (
      <div className="SearchLanding">
        <div className="AppBar">
          {localStorage.getItem("jwtToken") ? <NavBar /> : <AppBar />}
        </div>
        {this.state.isLoading ? (
          <div className="ProfilePageSpinner">
            <Spinner />
          </div>
        ) : (
          <div>
            <Container className="SearchContainer pb-4">
              <Row className="mb-3 mt-3">
                <Col sm={10}>
                  <StyledTextField
                    defaultValue={this.props.match.params.searchQuery}
                    id="outlined-basic"
                    label="Search"
                    variant="outlined"
                    fullWidth
                    onChange={(e) =>
                      this.setState({ searchQuery: e.target.value })
                    }
                  />
                </Col>
                <Col sm={2}>
                  <Button
                    className="ProfileUpdateButton"
                    variant="primary"
                    size="lg"
                    block
                    onClick={this.handleSearch}
                  >
                    Search
                  </Button>
                </Col>
              </Row>
              <Row>
                <Col sm={3}>
                  <FormControlLabel
                    className="SearchCheckBoxes"
                    control={
                      <Checkbox
                        checked={this.state.checkedUser}
                        onChange={this.handleChangeUser}
                        name="checkedUser"
                        color="primary"
                      />
                    }
                    label="User"
                  />
                  <FormControlLabel
                    className="SearchCheckBoxes"
                    control={
                      <Checkbox
                        checked={this.state.checkedWorkspace}
                        onChange={this.handleChangeWorkspace}
                        name="checkedWorkspace"
                        color="primary"
                      />
                    }
                    label="Workspace"
                  />
                  <FormControlLabel
                    className="SearchCheckBoxes"
                    control={
                      <Checkbox
                        checked={this.state.checkedUpcomingEvents}
                        onChange={this.handleChangeUpcomingEvents}
                        name="checkedUpcomingEvents"
                        color="primary"
                      />
                    }
                    label="Upcoming Events"
                  />
                  <FormControlLabel
                    className="SearchCheckBoxes"
                    control={
                      <Checkbox
                        checked={this.state.checkedTagSearch}
                        onChange={this.handleChangeTagSearch}
                        name="checkedTagSearch"
                        color="primary"
                      />
                    }
                    label="Tag Search"
                  />
                  {this.state.checkedUser ? (
                    <div>
                      <FormControl className="mb-2">
                        <InputLabel htmlFor="job-native-simple">Job</InputLabel>
                        <Select
                          native
                          onChange={(event) => {
                            this.setState({ job_id: event.target.value });
                          }}
                          inputProps={{
                            name: "job",
                            id: "job-native-simple",
                          }}
                        >
                          <option aria-label="None" value="" />
                          {this.state.job_list.map((value, index) => {
                            return (
                              <option value={value.id}>{value.name}</option>
                            );
                          })}
                        </Select>
                      </FormControl>
                      <FormControl className="mb-2">
                        <InputLabel htmlFor="sort-native-simple">
                          Sorting Createria
                        </InputLabel>
                        <Select
                          native
                          onChange={(e) => {
                            this.setState({ sorting_criteria: e.target.value });
                          }}
                          inputProps={{
                            name: "sort",
                            id: "sort-native-simple",
                          }}
                        >
                          <option aria-label="None" value="" />
                          <option value={0}>Alphabetical Order A to Z</option>
                          <option value={1}>Alphabetical Order Z to A</option>
                        </Select>
                      </FormControl>
                    </div>
                  ) : null}
                  {this.state.checkedWorkspace ? (
                    <div>
                      <StyledTextField
                        className="mb-2"
                        id="outlined-basic"
                        label="Skill"
                        variant="outlined"
                        fullWidth
                        onChange={(e) =>
                          this.setState({ skill_filter: e.target.value })
                        }
                      />
                      <StyledTextField
                        className="mb-2"
                        id="outlined-basic"
                        label="Creator Name"
                        variant="outlined"
                        fullWidth
                        onChange={(e) =>
                          this.setState({ creator_name: e.target.value })
                        }
                      />
                      <StyledTextField
                        className="mb-2"
                        id="outlined-basic"
                        label="Creator Surname"
                        variant="outlined"
                        fullWidth
                        onChange={(e) =>
                          this.setState({ creator_surname: e.target.value })
                        }
                      />
                      <InputLabel htmlFor="date-native-simple">
                        Start Date From
                      </InputLabel>
                      <StyledTextField
                        className="mb-2"
                        id="date-native-simple"
                        variant="outlined"
                        type="datetime-local"
                        fullWidth
                        onChange={(e) =>
                          this.setState({ starting_date_start: e.target.value })
                        }
                      />
                      <InputLabel htmlFor="date2-native-simple">
                        Start Date To
                      </InputLabel>
                      <StyledTextField
                        className="mb-2"
                        id="date2-native-simple"
                        variant="outlined"
                        type="datetime-local"
                        fullWidth
                        onChange={(e) =>
                          this.setState({ starting_date_end: e.target.value })
                        }
                      />
                      <InputLabel htmlFor="date3-native-simple">
                        End Date From
                      </InputLabel>
                      <StyledTextField
                        className="mb-2"
                        id="date3-native-simple"
                        variant="outlined"
                        type="datetime-local"
                        fullWidth
                        onChange={(e) =>
                          this.setState({ deadline_start: e.target.value })
                        }
                      />
                      <InputLabel htmlFor="date4-native-simple">
                        End Date To
                      </InputLabel>
                      <StyledTextField
                        className="mb-2"
                        id="date4-native-simple"
                        variant="outlined"
                        type="datetime-local"
                        fullWidth
                        onChange={(e) =>
                          this.setState({ deadline_end: e.target.value })
                        }
                      />
                      <FormControl className="mb-2">
                        <InputLabel htmlFor="sort2-native-simple">
                          Sorting Createria
                        </InputLabel>
                        <Select
                          native
                          onChange={(e) => {
                            this.setState({ sorting_criteria: e.target.value });
                          }}
                          inputProps={{
                            name: "sort2",
                            id: "sort2-native-simple",
                          }}
                        >
                          <option aria-label="None" value="" />
                          <option value={0}>Ascending Date</option>
                          <option value={1}>Descending Date</option>
                          <option value={2}>
                            Ascending Number of Collaborators Needed
                          </option>
                          <option value={3}>
                            Descending Number of Collaborators Needed
                          </option>
                          <option value={4}>
                            Ascending Alphabetical Order
                          </option>
                          <option value={5}>
                            Descending Alphabetical Orde
                          </option>
                        </Select>
                      </FormControl>
                    </div>
                  ) : null}

                  {this.state.checkedUpcomingEvents ? (
                    <div>
                      <InputLabel htmlFor="date1-native-simple">
                        Start Date From
                      </InputLabel>
                      <StyledTextField
                        className="mb-2"
                        id="date1-native-simple"
                        variant="outlined"
                        type="datetime-local"
                        fullWidth
                        onChange={(e) =>
                          this.setState({ starting_date_start: e.target.value })
                        }
                      />
                      <InputLabel htmlFor="date12-native-simple">
                        Start Date To
                      </InputLabel>
                      <StyledTextField
                        className="mb-2"
                        id="date12-native-simple"
                        variant="outlined"
                        type="datetime-local"
                        fullWidth
                        onChange={(e) =>
                          this.setState({ starting_date_end: e.target.value })
                        }
                      />
                      <InputLabel htmlFor="date13-native-simple">
                        End Date From
                      </InputLabel>
                      <StyledTextField
                        className="mb-2"
                        id="date13-native-simple"
                        variant="outlined"
                        type="datetime-local"
                        fullWidth
                        onChange={(e) =>
                          this.setState({ deadline_start: e.target.value })
                        }
                      />
                      <InputLabel htmlFor="date14-native-simple">
                        End Date To
                      </InputLabel>
                      <StyledTextField
                        className="mb-2"
                        id="date14-native-simple"
                        variant="outlined"
                        type="datetime-local"
                        fullWidth
                        onChange={(e) =>
                          this.setState({ deadline_end: e.target.value })
                        }
                      />
                      <FormControl className="mb-2">
                        <InputLabel htmlFor="sort3-native-simple">
                          Sorting Createria
                        </InputLabel>
                        <Select
                          native
                          onChange={(e) => {
                            this.setState({ sorting_criteria: e.target.value });
                          }}
                          inputProps={{
                            name: "sort3",
                            id: "sort2-native-simple",
                          }}
                        >
                          <option aria-label="None" value="" />
                          <option value={0}>
                            Ascending Alphabetical Order
                          </option>
                          <option value={1}>Ascending Date</option>
                        </Select>
                      </FormControl>
                    </div>
                  ) : null}
                  {this.state.checkedTagSearch ? (
                    <div>
                      <FormControl className="mb-2">
                        <InputLabel style={{width:"150px"}} htmlFor="sort-native-simple">
                          Tag Search Type
                        </InputLabel>
                        <Select
                          native
                          onChange={(e) => {
                            this.setState({
                              search_type: e.target.value,
                              searchResult: [],
                            });
                          }}
                          inputProps={{
                            name: "sort",
                            id: "sort-native-simple",
                          }}
                        >
                          <option value={0}>User</option>
                          <option value={1}>Workspace</option>
                        </Select>
                      </FormControl>
                      <Autocomplete
                        multiple
                        id="tags-outlined"
                        options={this.state.skills}
                        getOptionLabel={(option) => option}
                        value={this.state.newSkill}
                        onChange={(event, newValue) =>
                          this.handleSkill(newValue)
                        }
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
                    </div>
                  ) : null}
                </Col>
                <Col sm={9}>
                  {this.state.checkedUserAfter
                    ? this.state.searchResult.map((value, index) => {
                        return (
                          <Link to={`/` + value.id}>
                            <Row className="mb-3 UserCardToSearch">
                              <Col sm={2}>
                                <Avatar
                                  src={
                                    "http://18.185.75.161:5000/api" +
                                    value.profile_photo
                                  }
                                  className="SearchAvatar"
                                />
                              </Col>
                              <Col sm={6} className="SearchInformation">
                                <p className="GeneralMediumFont">
                                  {value.name} {value.surname}
                                </p>
                              </Col>
                            </Row>
                          </Link>
                        );
                      })
                    : null}
                  {this.state.checkedTagSearchAfter &&
                  this.state.search_type == 0
                    ? this.state.searchResult.map((value, index) => {
                        return (
                          <Link to={`/` + value.id}>
                            <Row className="mb-3 UserCardToSearch">
                              <Col sm={2}>
                                <Avatar
                                  src={
                                    "http://18.185.75.161:5000/api" +
                                    value.profile_photo
                                  }
                                  className="SearchAvatar"
                                />
                              </Col>
                              <Col sm={6} className="SearchInformation">
                                <p className="GeneralMediumFont">
                                  {value.name} {value.surname}
                                </p>
                              </Col>
                            </Row>
                          </Link>
                        );
                      })
                    : null}
                  {this.state.checkedTagSearchAfter &&
                  this.state.search_type == 1
                    ? this.state.searchResult.map((value, index) => {
                        return (
                          <Link
                            to={`/${value.creator_id}/workspace/${value.id}`}
                          >
                            <Row className="mb-3 UserCardToSearch">
                              <Col sm={2}>
                                <Avatar
                                  src={
                                    "http://18.185.75.161:5000/api/auth_system/logo"
                                  }
                                  className="SearchAvatar"
                                />
                              </Col>
                              <Col sm={6} className="mt-1">
                                <p className="GeneralMediumFont mb-0">
                                  {value.title}
                                </p>
                                <p className="GeneralSmallFont mb-0">
                                  {value.description}
                                </p>
                                <p className="GeneralSmallFont mb-0">
                                  by {value.creator_name}{" "}
                                  {value.creator_surname}
                                </p>
                              </Col>
                            </Row>
                          </Link>
                        );
                      })
                    : null}
                  {this.state.checkedWorkspaceAfter
                    ? this.state.searchResult.map((value, index) => {
                        return (
                          <Link
                            to={`/${value.creator_id}/workspace/${value.id}`}
                          >
                            <Row className="mb-3 UserCardToSearch">
                              <Col sm={2}>
                                <Avatar
                                  src={
                                    "http://18.185.75.161:5000/api/auth_system/logo"
                                  }
                                  className="SearchAvatar"
                                />
                              </Col>
                              <Col sm={6} className="mt-1">
                                <p className="GeneralMediumFont mb-0">
                                  {value.title}
                                </p>
                                <p className="GeneralSmallFont mb-0">
                                  {value.description}
                                </p>
                                <p className="GeneralSmallFont mb-0">
                                  by {value.creator_name}{" "}
                                  {value.creator_surname}
                                </p>
                              </Col>
                            </Row>
                          </Link>
                        );
                      })
                    : null}
                  {this.state.checkedUpcomingEventsAfter
                    ? this.state.searchResult.map((value, index) => {
                        return (
                          <a href={value.link}>
                            <Row className="mb-3 UserCardToSearch">
                              <Col sm={2}>
                                <Avatar
                                  src={
                                    "http://18.185.75.161:5000/api" +
                                    value.profile_photo
                                  }
                                  className="SearchAvatar"
                                />
                              </Col>
                              <Col sm={6} className="mt-1">
                                <p className="GeneralMediumFont mb-0">
                                  {value.title}
                                </p>
                                <p className="GeneralSmallFont mb-0">
                                  {value.date}
                                </p>
                              </Col>
                            </Row>
                          </a>
                        );
                      })
                    : null}
                </Col>
              </Row>
            </Container>
          </div>
        )}
      </div>
    );
  }
}

export default Search;
