import './App.css';
import Landing from './components/Landing/Landing';
import React, { Component } from 'react';


import HomePage from './components/HomePage/HomePage'

import {BrowserRouter as Router, Route} from 'react-router-dom'
import ForgotPassword from './components/ForgotPassword/ForgotPassword'
import ResetPassword from './components/ResetPassword/ResetPassword'
import Login from './components/Login/Login'
import Register from './components/Register/Register'

import ProfilePage from './components/ProfilePage/ProfilePage'
import EditProfile from './components/EditProfile/EditProfile';
import Activation from './components/Activation/Activation'
import { setAuthorizationToken } from './helpers/setAuthorizationToken';

class App extends Component {
  constructor(props) {
    super(props);
    this.state = {
      isAuthenticated: false,

    }
  }
  componentDidMount(){
    this.handlerIsAuthenticated();
  }
  handlerIsAuthenticated = () => {
    const jwtToken = localStorage.getItem("jwtToken");
    if (jwtToken) {
      setAuthorizationToken(jwtToken);
      console.log(jwtToken);
      this.setState({
        isAuthenticated: true
      })
    }
  }



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

      <Route path='/activate_account' component={Activation}/>

      <Route path='/' exact
        render ={ () => !this.state.isAuthenticated ?
              <Landing/>
              : <HomePage/>}/>
      <Route path='/profile' exact  component={ProfilePage}/>
      <Route path='/editprofile' exact  component={EditProfile}/>

      <Route path='/login' exact component={() => <Login isAuthenticated={this.state.isAuthenticated} />}/>
      <Route path='/register' exact  component={Register}/>
      <Route path='/forgotpassword' exact component={ForgotPassword}/>
      <Route path='/resetpassword'  component={ResetPassword}/>
      </div>

    </Router>
     );
  }
}

export default App;