import Register from '../Register/Register';
import Login from '../Login/Login';
import AppBar from '../AppBar/AppBar';
import React from 'react';
import './Landing.css'

const Landing = (props) => {
    /*
    <div className="LoginRegister">
                <Login {...props}/>
                <Register/>
            </div>*/
    return (
        <div className="Landing">
            <div className="AppBar">
                <AppBar/>
            </div>
            <div>
                Welcome to Platon
            </div>
            <div>
                Upcoming Events
            </div>
            <div>
                Trending Projects
            </div>

        </div>
    );
}

export default Landing;