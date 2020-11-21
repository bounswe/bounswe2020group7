import './App.css';
import Landing from './components/Landing/Landing';
import React, { Component } from 'react';
import {BrowserRouter as Router, Switch, Route} from 'react-router-dom'
import HomePage from './components/HomePage/HomePage'
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
      <div >
      <Route path='/' exact
        render ={ () => !this.state.isAuthenticated ?
            <HomePage>
            </HomePage>
              : <h1>Home</h1>}/>

             </div>

    </Router>
     );
  }
}

export default App;