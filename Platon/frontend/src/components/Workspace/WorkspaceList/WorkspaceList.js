import React, { Component } from "react";
import Navbar from "../../NavBar/NavBar";
import colors from "../../../utils/colors";
import config from "../../../utils/config";
import WorkspaceStepper from "../WorkspaceStepper/WorkspaceStepper";
import axios from "axios";
import Spinner from "../../Spinner/Spinner";
import MuiAlert from "@material-ui/lab/Alert";
import Snackbar from "@material-ui/core/Snackbar";
import jwt_decode from "jwt-decode";

function Alert(props) {
  return <MuiAlert elevation={6} variant="filled" {...props} />;
}

class WorkspaceList extends Component {
    constructor(props) {
        super(props);
        this.state = {  }
    }
    componentDidMount(){
        const token = localStorage.getItem("jwtToken");
        const decoded = jwt_decode(token);
        //const profileId = this.props.match.params.profileId

    }
    render() {
        return ( <div>Workspace List</div> );
    }
}

export default WorkspaceList;