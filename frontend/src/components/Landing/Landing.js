import Register from '../Register/Register';
import Login from '../Login/Login';
import AppBar from '../AppBar/AppBar';
import React from 'react';
import './Landing.css'
const Landing = () => {
    return (

        <div className="Landing">
            <div className="AppBar">
                <AppBar/>
            </div>
            <div className="LoginRegister">
                <Login/>
                <Register/>
            </div>
        </div>
    );
}

export default Landing;