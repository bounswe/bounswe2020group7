import React from 'react'
import './ProfilePage.css'
import {
  Container,
  Col,
  Row,
  Button,
  Card,
  ResponsiveEmbed,
} from 'react-bootstrap'
import 'bootstrap/dist/css/bootstrap.min.css'
import Rating from '@material-ui/lab/Rating'
import PropTypes from 'prop-types'
import { withStyles } from '@material-ui/core/styles'
import AppBar from '@material-ui/core/AppBar'
import Tabs from '@material-ui/core/Tabs'
import Tab from '@material-ui/core/Tab'
import Typography from '@material-ui/core/Typography'
import Box from '@material-ui/core/Box'
import Grid from '@material-ui/core/Grid'
import NavBar from '../NavBar/NavBar'
import jwt_decode from 'jwt-decode'
import requestService from '../../services/requestService'
import { Link } from 'react-router-dom'
import List from '@material-ui/core/List'
import ListItem from '@material-ui/core/ListItem'
import ListItemAvatar from '@material-ui/core/ListItemAvatar'
import ListItemIcon from '@material-ui/core/ListItemIcon'
import ListItemSecondaryAction from '@material-ui/core/ListItemSecondaryAction'
import ListItemText from '@material-ui/core/ListItemText'
import PersonIcon from '@material-ui/icons/Person'
import Snackbar from '@material-ui/core/Snackbar'
import MuiAlert from '@material-ui/lab/Alert'
import Avatar from '@material-ui/core/Avatar'
import Chip from '@material-ui/core/Chip'
import colors from '../../utils/colors'
import config from '../../utils/config'
import axios from 'axios'
import Spinner from '../Spinner/Spinner'
import EditResearchDialog from './EditResearchDialog/EditResearchDialog'
import InviteDialog from './InviteDialog'

function TabPanel(props) {
  const { children, value, index, ...other } = props

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
  )
}

TabPanel.propTypes = {
  children: PropTypes.node,
  index: PropTypes.any.isRequired,
  value: PropTypes.any.isRequired,
}

function a11yProps(index) {
  return {
    id: `simple-tab-${index}`,
    'aria-controls': `simple-tabpanel-${index}`,
  }
}

function Alert(props) {
  return <MuiAlert elevation={6} variant="filled" {...props} />
}

const useStyles = withStyles((theme) => ({
  root: {
    flexGrow: 1,
    backgroundColor: theme.palette.background.paper,
  },
}))


class ProfilePage extends React.Component {
  constructor(props) {
    super(props)
    this.state = {
      followers: [],
      followings: [],
      researchs: [],
      selectedFollowButton: [],
      followings_id_arr: [],
      follow_requests: [],
      personalSkills: [],
      user: null,
      value: 0,
      job: '',
      renderTrigger: false,
      isLoading: true,
      isMyProfile: true,
      isProfilePrivate: false,
      showSuccess: false,
      inviteDialogOpen: false,
    }
  }

  componentDidMount() {
    const token = localStorage.getItem('jwtToken')
    const decoded = jwt_decode(token)
    const profileId = this.props.match.params.profileId
    if (decoded.id == profileId) {
      this.setState({
        isMyProfile: true,
      })
    } else {
      this.setState({
        isMyProfile: false,
      })
    }
    requestService
      .getUser(profileId)
      .then((response) => {
        if (response.status == 206) {
          this.setState({
            isProfilePrivate: true,
            user: response.data,
          })
        } else {
          this.setState({
            user: response.data,
          })
          Promise.all([
            requestService.followings(profileId).then((response) => {
              this.setState({
                followings: response.data.followings,
              })
            }),
            requestService.followers(profileId).then((response) => {
              this.setState({
                followers: response.data.followers,
              })
            }),
            requestService.getFollowRequests().then((response) => {
              this.setState({
                follow_requests: response.data.follow_requests,
              })
            }),
            requestService.getResearchs(profileId).then((response) => {
              this.setState({
                researchs: response.data.research_info,
              })
            }),
            requestService.getPersonalSkillList(profileId).then((response) => {
              if (response) {
                let skillArray = []
                for (var key in response.data.skills) {
                  skillArray.push(response.data.skills[key].name)
                }
                this.setState({
                  personalSkills: skillArray,
                })
              }
            }),
          ])
        }
      })
      .then(() => this.setState({ isLoading: false }))
  }

  handleCloseSuccess = (event, reason) => {
    if (reason === 'clickaway') {
      return
    }

    this.setState({ showSuccess: false })
  }

  handleDeleteResearchInformation = (id) => {
    const token = localStorage.getItem('jwtToken')
    const decoded = jwt_decode(token)
    let formData = new FormData()
    const url = config.BASE_URL

    formData.append('research_id', id)
    axios
      .delete(url + '/api/profile/research_information', {
        headers: {
          auth_token: token, //the token is a variable which holds the token
        },
        data: formData,
      })
      .then((response) => {
        if (response.status === 200) {
          document.location.reload()
        }
      })
      .catch((err) => {
        console.log(err)
      })
  }

  handleFollowRequest = () => {
    const token = localStorage.getItem('jwtToken')
    const decoded = jwt_decode(token)
    const follower_id = decoded.id
    const following_id = this.props.match.params.profileId
    requestService.postFollowRequest(follower_id, following_id).then((resp) => {

      requestService
        .getUser(this.props.match.params.profileId)
        .then((response) => {
          if (response.status == 206) {
            this.setState({
              isProfilePrivate: true,
              user: response.data,
            })
          } else {
            this.setState({
              user: response.data,
            })
          }
        })
        .then(() => {
          if (this.state.isProfilePrivate === false) {
            requestService
              .followers(this.props.match.params.profileId)
              .then((response) => {
                this.setState({
                  followers: response.data.followers,
                })
              })
          }
        })
    })
  }

  handleUnFollowRequest = () => {
    const following_id = this.props.match.params.profileId
    requestService.deleteUnfollow(following_id).then((resp) => {

      requestService
        .getUser(this.props.match.params.profileId)
        .then((response) => {
          if (response.status == 206) {
            this.setState({
              isProfilePrivate: true,
              user: response.data,
            })
          } else {
            this.setState({
              user: response.data,
            })
          }
        })
        .then(() => {
          if (this.state.isProfilePrivate === false) {
            requestService
              .followers(this.props.match.params.profileId)
              .then((response) => {
                this.setState({
                  followers: response.data.followers,
                })
              })
          }
        })
    })
  }

  handleChange = (event, newValue) => {
    this.setState({
      value: newValue,
    })
  }

  handleProfile = (id) => {
    const token = localStorage.getItem('jwtToken')
    const decoded = jwt_decode(token)
    document.location.href = '/' + id
  }

  handlePersonalFollowRequests = (id, switchCase) => {
    requestService.deleteFollowRequests(id, switchCase).then((resp) => {
      if (switchCase == 1) {
        this.setState({
          showSuccess: 'Accepted Follow Request',
        })
      } else {
        this.setState({
          showSuccess: 'Rejected Follow Request',
        })
      }
      requestService
        .getUser(this.props.match.params.profileId)
        .then((response) => {
          if (response.status == 206) {
            this.setState({
              isProfilePrivate: true,
              user: response.data,
            })
          } else {
            this.setState({
              user: response.data,
            })
          }
        })
      requestService
        .followers(this.props.match.params.profileId)
        .then((response) => {
          this.setState({
            followers: response.data.followers,
          })
        })

      requestService.getFollowRequests().then((response) => {
        this.setState({
          follow_requests: response.data.follow_requests,
        })
      })
    })
  }

  render() {
    const { classes } = this.props
    return (
      <div className="ProfilePageLanding">
        <div className="AppBar">
          <NavBar />
        </div>
        {this.state.isLoading ? (
          <div className="ProfilePageSpinner">
            <Spinner />
          </div>
        ) : (
          <div>
            <Container className="ProfilePageContainer">
              <h2 className="GeneralLargeFont">Profile</h2>
              <hr className="ProfilePageLine" />

              <Row>
                <Col sm={2}>
                  <img
                    className="ProfilePhoto"
                    src={
                      'http://18.185.75.161:5000/api' +
                      this.state.user.profile_photo
                    }
                    alt="UserImage"
                  />
                </Col>
                <Col sm={6}>
                  {this.state.user && (
                    <p className="GeneralMediumFont">
                      {this.state.user.name} {this.state.user.surname}
                    </p>
                  )}
                  {this.state.user && (
                    <p className="GeneralSmallFont">
                      {this.state.user.job}{' '}
                      {this.state.user.institution && ' - '}{' '}
                      {this.state.user.institution}
                    </p>
                  )}
                  {this.state.isProfilePrivate
                    ? null
                    : this.state.personalSkills.map((value, index) => {
                      return (
                        <Chip
                          className="mr-1 mb-1 ProfileSkillChip"
                          label={value}
                          variant="outlined"
                        />
                      )
                    })}
                </Col>
                <Col>
                  {this.state.isProfilePrivate ? null : (
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
                  )}
                  {this.state.isMyProfile ? (
                    <Link to={`/${this.props.match.params.profileId}/edit`}>
                      <Button
                        className="ProfileFollowButton mb-3"
                        variant="primary"
                        size="lg"
                        block
                      >
                        Edit Profile
                      </Button>
                    </Link>
                  ) : this.state.user.following_status == -1 ? (
                    <Button
                      onClick={() => this.handleFollowRequest()}
                      className="ProfileFollowButton"
                      variant="primary"
                      size="lg"
                      block
                    >
                      Follow
                    </Button>
                  ) : this.state.user.following_status == 0 ? (
                    <Button
                      disabled
                      className="ProfileFollowButton"
                      variant="primary"
                      size="lg"
                      block
                    >
                      Requested
                    </Button>
                  ) : (
                    <Button
                      onClick={() => this.handleUnFollowRequest()}
                      className="ProfileFollowButton"
                      variant="primary"
                      size="lg"
                      block
                    >
                      Unfollow
                    </Button>
                  )}
                  {this.state.isMyProfile ? null : (
                    <Button
                      className="ProfileFollowButton"
                      variant="primary"
                      size="lg"
                      onClick={() => this.setState({
                        inviteDialogOpen: true,
                      })}
                      block
                    >
                      Invite
                    </Button>
                  )}
                  {this.state.isMyProfile ? null : (
                    <Button
                      className="ProfileFollowButton"
                      variant="primary"
                      size="lg"
                      block
                    >
                      Rate
                    </Button>
                  )}
                  {this.state.isProfilePrivate ? null : (
                    <Row className="RatingColumn">
                      <Rating
                        name="half-rating-read"
                        defaultValue={0}
                        precision={0.5}
                        readOnly
                        size="large"
                      />
                    </Row>
                  )}
                </Col>
              </Row>
            </Container>

            {this.state.isProfilePrivate ? null : (
              <Container className="ProfilePageContainer">
                <div>
                  <AppBar position="static">
                    <Tabs
                      value={this.state.value}
                      onChange={this.handleChange}
                      aria-label="simple tabs example"
                    >
                      <Tab label="Researchs" {...a11yProps(0)} />
                      <Tab label="Followers" {...a11yProps(1)} />
                      <Tab label="Following" {...a11yProps(2)} />
                      {!this.state.isMyProfile ? null : (
                        <Tab label="Requests" {...a11yProps(3)} />
                      )}
                    </Tabs>
                  </AppBar>
                  <TabPanel value={this.state.value} index={0}>
                    {!this.state.isMyProfile ? null : (
                      <Row
                        className="mb-3"
                        style={{
                          display: 'flex',
                          flexDirection: 'column',
                          alignItems: 'center',
                        }}
                      >
                        <EditResearchDialog
                          type="NEW"
                          dialogTitle="Add New Research"
                          id={''}
                          title={''}
                          description={''}
                          year={''}
                        />
                      </Row>
                    )}
                    {this.state.researchs.map((value, index) => {
                      return (
                        <Row className="mb-3">
                          <Card className="ProfileProjectsCard" style={{ width: '100%' }}>
                            <Card.Body
                              style={{
                                display: 'flex',
                                flexDirection: 'column',
                                alignItems: 'center',
                              }}
                            >
                              <Card.Title style={{ color: colors.primaryDark }}>
                                Title
                              </Card.Title>
                              <Card.Text style={{ color: colors.primaryLight }}>
                                {value.title}
                              </Card.Text>
                              <Card.Title style={{ color: colors.primaryDark }}>
                                Description
                              </Card.Title>

                              <Card.Text style={{ color: colors.primaryLight }}>
                                {value.description === ''
                                  ? 'No description is provided.'
                                  : value.description}
                              </Card.Text>
                              <Card.Title style={{ color: colors.primaryDark }}>
                                Year
                              </Card.Title>

                              <Card.Text style={{ color: colors.primary }}>
                                {value.year}
                              </Card.Text>

                              {!this.state.isMyProfile ? null : (
                                <EditResearchDialog
                                  className="mb-3 allign-items-center"
                                  type="EDIT"
                                  dialogTitle="Edit Research"
                                  id={value.id}
                                  title={value.title}
                                  description={value.description}
                                  year={value.year}
                                />
                              )}
                              {!this.state.isMyProfile ? null : (
                                <Button
                                  className="mt-3"
                                  onClick={() =>
                                    this.handleDeleteResearchInformation(value.id)
                                  }
                                  style={{ backgroundColor: colors.quinary, width: '176px' }}
                                >
                                  Delete
                                </Button>
                              )}
                            </Card.Body>
                          </Card>
                        </Row>
                      )
                    })}
                  </TabPanel>
                  <TabPanel value={this.state.value} index={1}>
                    <List>
                      {this.state.followers.map((value, index) => {
                        return (
                          <Link onClick={() => this.handleProfile(value.id)}>
                            <ListItem>
                              <ListItemAvatar>
                                <Avatar
                                  src={
                                    'http://18.185.75.161:5000/api' +
                                    value.profile_photo
                                  }
                                ></Avatar>
                              </ListItemAvatar>
                              <ListItemText
                                style={{ color: colors.secondary }}
                                primary={`${value.name} ${value.surname}`}
                              />
                            </ListItem>
                          </Link>
                        )
                      })}
                    </List>
                  </TabPanel>
                  <TabPanel value={this.state.value} index={2}>
                    <List>
                      {this.state.followings.map((value, index) => {
                        return (
                          <Link onClick={() => this.handleProfile(value.id)}>
                            <ListItem>
                              <ListItemAvatar>
                                <Avatar
                                  src={
                                    'http://18.185.75.161:5000/api' +
                                    value.profile_photo
                                  }
                                ></Avatar>
                              </ListItemAvatar>
                              <ListItemText
                                style={{ color: colors.secondary }}
                                primary={`${value.name} ${value.surname}`}
                              />
                            </ListItem>
                          </Link>
                        )
                      })}
                    </List>
                  </TabPanel>
                  {!this.state.isMyProfile ? null : (
                    <TabPanel value={this.state.value} index={3}>
                      <List>
                        {this.state.follow_requests.map((value, index) => {
                          return (
                            <ListItem>
                              <ListItemAvatar>
                                <Avatar>
                                  <PersonIcon />
                                </Avatar>
                              </ListItemAvatar>
                              <Link
                                onClick={() => this.handleProfile(value.id)}
                              >
                                <ListItemText
                                  style={{ color: colors.secondary }}
                                  primary={`${value.name} ${value.surname}`}
                                />
                              </Link>
                              <div className="RightSideButtons">
                                <Button
                                  className="mr-3"
                                  onClick={() =>
                                    this.handlePersonalFollowRequests(
                                      value.id,
                                      1,
                                    )
                                  }
                                >
                                  Accept
                                </Button>
                                <Button
                                  className=""
                                  onClick={() =>
                                    this.handlePersonalFollowRequests(
                                      value.id,
                                      2,
                                    )
                                  }
                                >
                                  Reject
                                </Button>
                              </div>
                            </ListItem>
                          )
                        })}
                      </List>
                    </TabPanel>
                  )}
                </div>
              </Container>
            )}
          </div>
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
        <InviteDialog
          open={this.state.inviteDialogOpen}
          user={this.state.user}
          closeDialog={() => this.setState({
            inviteDialogOpen: false,
          })}
        />
      </div>
    )
  }
}


export default ProfilePage
