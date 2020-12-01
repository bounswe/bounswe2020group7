import React from 'react';
import AppBar from '../AppBar/AppBar';
import NavBar from '../NavBar/NavBar';
import './NotFound.css'
const NotFound = (props) => {
    let bar = <AppBar/>
    if(props.isAuthenticated){
        bar = <NavBar/>
    }
    return (
        <div className="NotFoundPage">
            {bar}
            <h1>Page Not Found</h1>
                <section class="NotFoundPage-error-container">
                <span class="NotFoundPage-four"><span class="NotFoundPage-screen-reader-text">4</span></span>
                <span class="NotFoundPage-zero"><span class="NotFoundPage-screen-reader-text">0</span></span>
                <span class="NotFoundPage-four"><span class="NotFoundPage-screen-reader-text">4</span></span>
                </section>
        </div>
    );
}

export default NotFound;