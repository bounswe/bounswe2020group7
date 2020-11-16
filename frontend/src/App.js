import './App.css';
import Landing from './components/Landing/Landing';
import React, { Component } from 'react';
import {BrowserRouter as Router, Switch, Route} from 'react-router-dom'
import ForgotPassword from './components/ForgotPassword/ForgotPassword'

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
              <Landing handlerIsAuthenticated={this.handlerIsAuthenticated}/>
              : <h1>Home</h1>}/>
      <Route path='/forgotpassword' exact component={ForgotPassword}/>
      </div>
    </Router>
     );
  }
}

export default App;