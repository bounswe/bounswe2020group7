import React, { useState, useEffect } from "react";
import AppBar from "@material-ui/core/AppBar";
import Toolbar from "@material-ui/core/Toolbar";
import InputBase from "@material-ui/core/InputBase";
import { fade, makeStyles, withStyles } from "@material-ui/core/styles";
import SearchIcon from "@material-ui/icons/Search";
import Logo from "../Logo/Logo";
import colors from "../../utils/colors";
import { Link } from "react-router-dom";
import authService from "../../services/authService";
import jwt_decode from "jwt-decode";
import Popover from "@material-ui/core/Popover";
import IconButton from "@material-ui/core/IconButton";
import HomeIcon from "@material-ui/icons/Home";
import AccountBoxIcon from "@material-ui/icons/AccountBox";
import MeetingRoomIcon from "@material-ui/icons/MeetingRoom";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemText from "@material-ui/core/ListItemText";
import WorkIcon from "@material-ui/icons/Work";
import ListItemSecondaryAction from "@material-ui/core/ListItemSecondaryAction";
import Spinner from "../Spinner/Spinner";
import Pagination from "@material-ui/lab/Pagination";
import NotificationsIcon from "@material-ui/icons/Notifications";
import config from "../../utils/config";
import axios from "axios";
import ClearIcon from "@material-ui/icons/Clear";
import Divider from "@material-ui/core/Divider";

const StyledPagination = withStyles(
  {
    root: {
      "& .Mui-selected": {
        backgroundColor: colors.quinary,
      },
    },
  },
  { name: "MuiPaginationItem" }
)(Pagination);

const useStyles = makeStyles((theme) => ({
  root: {
    flexGrow: 1,
  },
  color: {
    color: colors.quaternary,
  },
  menuButton: {
    marginRight: theme.spacing(2),
  },
  title: {
    flexGrow: 1,
    display: "none",
    [theme.breakpoints.up("sm")]: {
      display: "block",
    },
  },
  search: {
    position: "relative",
    borderRadius: theme.shape.borderRadius,
    backgroundColor: fade(colors.secondary, 0.15),
    "&:hover": {
      backgroundColor: fade(colors.secondary, 0.25),
    },
    marginLeft: 0,
    width: "100%",
    [theme.breakpoints.up("sm")]: {
      marginLeft: theme.spacing(1),
      width: "auto",
    },
  },
  searchIcon: {
    padding: theme.spacing(0, 2),
    height: "100%",
    color: colors.secondary,
    position: "absolute",
    pointerEvents: "none",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
  },
  inputRoot: {
    color: colors.secondary,
  },
  inputInput: {
    padding: theme.spacing(1, 1, 1, 0),
    // vertical padding + font size from searchIcon
    paddingLeft: `calc(1em + ${theme.spacing(4)}px)`,
    transition: theme.transitions.create("width"),
    width: "100%",
    [theme.breakpoints.up("sm")]: {
      width: "12ch",
      "&:focus": {
        width: "20ch",
      },
    },
  },
}));

function handleLogout() {
  authService.logout();
  document.location.href = "/";
}

const NavBar = ({ itemsPerPage = 3, width = "500px" }) => {
  const classes = useStyles();
  const token = localStorage.getItem("jwtToken");
  const decoded = jwt_decode(token);
  const [anchorEl, setAnchorEl] = React.useState(null);

  const [data, setData] = useState({});
  const [fetching, setFetching] = useState(false);
  const [page, setPage] = useState(1);
  const [sonBirSarki, setSonBirSarki] = useState(false);
  const [pageNumber, setPageNumber] = useState(0);
  const [notifications, setNotifications] = useState([]);

  useEffect(() => {
    axios.defaults.headers.common["auth_token"] = `${token}`;
    const url = config.BASE_URL;

    axios
      .get(url + "/api/profile/notifications", {
        params: {
          page: page - 1,
          per_page: itemsPerPage,
        },
      })
      .then((response) => {
        if (response.status === 200) {
          setData(response.data);
          setPageNumber(response.data.number_of_pages)
          setNotifications(response.data.notification_list);
          setFetching(false);
        }
      })
      .catch((err) => {
        console.error(err);
        setFetching(false);
      });
  }, [page, sonBirSarki]);

  const handlePageChange = (event, page) => {
    setPage(page);
  };

  const handleClick = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };
  const handleDelete = (id, index) => {
    const token = localStorage.getItem("jwtToken");

    const url = config.BASE_URL;

    let formData = new FormData();
    formData.append("notification_id", id);
    axios
      .delete(url + "/api/profile/notifications", {
        headers: {
          auth_token: token, //the token is a variable which holds the token
        },
        data: formData,
      })
      .then((response) => {
        if (response.status === 200) {
          let prevArray = notifications
          prevArray.splice(index, 1)

          setNotifications([...prevArray])
          if(notifications.length===0){
            setPageNumber(pageNumber-1)
            if(page>1){
              setPage(page-1);
            }
            else{
              setSonBirSarki(!sonBirSarki)
            }
          }

        }
      })
      .catch((err) => {
        console.log(err);
      });
  };

  const open = Boolean(anchorEl);
  const id = open ? "simple-popover" : undefined;


  return (
    <div className={classes.root}>
      <AppBar style={{ background: colors.primaryDark }} position="static">
        <Toolbar style={{ justifyContent: "space-around" }}>
          <Link to="/">
            <Logo height="5vh" width="15vh" fill={colors.secondary} />
          </Link>

          <div className={classes.search}>
            <div className={classes.searchIcon}>
              <SearchIcon />
            </div>
            <InputBase
              placeholder="Search…"
              classes={{
                root: classes.inputRoot,
                input: classes.inputInput,
              }}
              inputProps={{ "aria-label": "search" }}
            />
          </div>

          <div>
            <Link to="/" style={{ textDecoration: "none" }}>
              <IconButton edge="end" aria-label="home">
                <HomeIcon style={{ color: colors.secondary }} />
              </IconButton>
            </Link>
            <Link to={`/${decoded.id}`} style={{ textDecoration: "none" }}>
              <IconButton edge="end" aria-label="account">
                <AccountBoxIcon style={{ color: colors.secondary }} />
              </IconButton>
            </Link>
            <Link
              to={`/${decoded.id}/workspace`}
              style={{ textDecoration: "none" }}
            >
              <IconButton edge="end" aria-label="workspace">
                <WorkIcon style={{ color: colors.secondary }} />
              </IconButton>
            </Link>
            <IconButton
              edge="end"
              aria-label="nofitication"
              onClick={handleClick}
            >
              <NotificationsIcon style={{ color: colors.secondary }} />
            </IconButton>

            <Popover
              disableScrollLock={true}
              id={id}
              open={open}
              anchorEl={anchorEl}
              onClose={handleClose}
              anchorOrigin={{
                vertical: "bottom",
                horizontal: "right",
              }}
              transformOrigin={{
                vertical: "top",
                horizontal: "right",
              }}
            >
              {fetching ? (
                <div>
                  <Spinner />
                </div>
              ): (
                <div style={{ width: width }}>
                  <List style={{ backgroundColor: colors.primaryLight }}>
                    {notifications.map((notification, index) => (
                      <div>
                        <ListItem>
                          <ListItemText
                            style={{ color: colors.quinary }}
                            primary={notification.text}
                            secondary={new Date(notification.timestamp)
                              .toLocaleString()
                              .substring(0, 10)}
                          />
                          <ListItemSecondaryAction>
                            <IconButton
                              edge="end"
                              aria-label="delete"
                              onClick={() =>
                                handleDelete(notification.id, index)
                              }
                            >
                              <ClearIcon />
                            </IconButton>
                          </ListItemSecondaryAction>
                        </ListItem>
                        <Divider />
                      </div>
                    ))}
                  </List>
                  {notifications && (notifications.length === 0) && (pageNumber === 0) && (
                <div
                  style={{
                    padding: "20px 0",
                    textAlign: "center",
                    backgroundColor: colors.primaryLight,
                    color: colors.primary,
                  }}
                >
                  Nothing to show
                </div>
              )}
                  <div
                    className="paginationContainerNavBar"
                    style={{
                      backgroundColor: colors.primaryLight,
                      display: "flex",
                      flexDirection: "column",
                      justifyContent: "center",
                      alignItems: "center",
                    }}
                  >
                    <StyledPagination
                      count={data.number_of_pages}
                      onChange={handlePageChange}
                      page={page}
                    />
                    <br />
                  </div>
                </div>
              )}

            </Popover>
            <IconButton edge="end" aria-label="logout" onClick={handleLogout}>
              <MeetingRoomIcon style={{ color: colors.secondary }} />
            </IconButton>
          </div>
        </Toolbar>
      </AppBar>
    </div>
  );
};
export default NavBar;
