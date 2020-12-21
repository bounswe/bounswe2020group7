import React from "react";
import AppBar from "@material-ui/core/AppBar";

import Button from "@material-ui/core/Button";
import Toolbar from "@material-ui/core/Toolbar";
import InputBase from "@material-ui/core/InputBase";
import { fade, makeStyles, withStyles } from "@material-ui/core/styles";
import SearchIcon from "@material-ui/icons/Search";
import Logo from "../Logo/Logo";
import colors from "../../utils/colors";
import { Link, Redirect } from "react-router-dom";
import authService from "../../services/authService";
import jwt_decode from "jwt-decode";

const StyledButton = withStyles({
  root: {
    color: colors.secondary,
    "&:hover": {
      backgroundColor: colors.primary,
    },
  },
})(Button);
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

function handleProfile() {
  const token = localStorage.getItem("jwtToken");
  const decoded = jwt_decode(token);
  document.location.href = "/" + decoded.id;
}

function onKeyUp(event) {
  var edValue = document.getElementById("searchBox").value;
  if (event.charCode === 13) {
    document.location.href = "/search/" + edValue;
  }
}


export default function NavBar() {
  const classes = useStyles();

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
              id = "searchBox"
              placeholder="Search…"
              classes={{
                root: classes.inputRoot,
                input: classes.inputInput,
              }}
              inputProps={{ "aria-label": "search" }}
              onKeyPress = {onKeyUp}
            />
          </div>

          <div>
            <Link to="/" style={{ textDecoration: "none" }}>
              <StyledButton>Home</StyledButton>
            </Link>
            <StyledButton onClick={handleProfile}>Profile</StyledButton>
            <StyledButton onClick={handleLogout}>Log Out</StyledButton>
          </div>
        </Toolbar>
      </AppBar>
    </div>
  );
}
