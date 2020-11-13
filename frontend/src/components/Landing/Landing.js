import Register from '../Register/Register';
import Login from '../Login/Login';
import React from 'react';
import './Landing.css'
const Landing = () => {
    return (
        <div className="Landing">
            <Login/>
            <Register/>
        </div>
    );
}

export default Landing;