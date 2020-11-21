import './App.css';
import Landing from './components/Landing/Landing';
import React, { Component } from 'react';

import {BrowserRouter as Router, Switch, Route} from 'react-router-dom'
import HomePage from './components/HomePage/HomePage'
import {BrowserRouter as Router, Route} from 'react-router-dom'
import ForgotPassword from './components/ForgotPassword/ForgotPassword'
import ResetPassword from './components/ResetPassword/ResetPassword'
import Login from './components/Login/Login'
import Register from './components/Register/Register'
import ProfilePage from './components/ProfilePage/ProfilePage'
import EditProfile from './components/EditProfile/EditProfile';


class App extends Component {
  constructor(props) {
    super(props);
    this.state = {
      isAuthenticated: false,

    }
  }
  handlerIsAuthenticated = () => {
    this.setState({
      isAuthenticated: true
    })
  }
  render() {

    return (
      <Router>
      <div className="App">
      <Route path='/' exact
        render ={ () => !this.state.isAuthenticated ?
              <Landing/>
              : <h1>Home</h1>}/>
      
      <Route path='/profile' exact  component={ProfilePage}/>
      <Route path='/editprofile' exact  component={EditProfile}/>

      <Route path='/login' exact component={() => <Login  handlerIsAuthenticated={this.handlerIsAuthenticated}/>}/>
      <Route path='/register' exact  component={Register}/>
      <Route path='/forgotpassword' exact component={ForgotPassword}/>
      <Route path='/resetpassword'  component={ResetPassword}/>
      </div>

    </Router>
     );
  }
}

export default App;