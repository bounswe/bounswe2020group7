import './App.css';
import Landing from './components/Landing/Landing';
import React, { Component } from 'react';
import HomePage from './components/HomePage/HomePage'
import {BrowserRouter as Router, Switch, Route} from 'react-router-dom'
import ForgotPassword from './components/ForgotPassword/ForgotPassword'
import ResetPassword from './components/ResetPassword/ResetPassword'
import Login from './components/Login/Login'
import Register from './components/Register/Register'
import ProfilePage from './components/ProfilePage/ProfilePage'
import EditProfile from './components/EditProfile/EditProfile';
import Activation from './components/Activation/Activation';
import WorkspaceCreate from './components/Workspace/WorkspaceCreate/WorkspaceCreate';
import WorkspaceList from './components/Workspace/WorkspaceList/WorkspaceList';
import WorkspaceView from './components/Workspace/WorkspaceView/WorkspaceView';
import WorkspaceEdit from './components/Workspace/WorkspaceEdit/WorkspaceEdit';
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

      this.setState({
        isAuthenticated: true
      })
    }
  }
  handlerSuccessfulLogin = () => {
      this.setState({
        isAuthenticated: true
      })
  }

  render() {

    return (
      <Router>


<Switch>


      <div className="App">
      <Route path='/activate_account' component={Activation}/>

      <Route path='/' exact
        render ={ () => !this.state.isAuthenticated ?
              <Landing/>
              : <HomePage/>}/>

      <Route path='/:profileId(\d+)' exact component={!this.state.isAuthenticated ? Landing : ProfilePage}/>
      <Route path='/:profileId(\d+)/edit' exact component={!this.state.isAuthenticated ? Landing : EditProfile}/>
      <Route path='/:profileId(\d+)/workspace' exact component={WorkspaceList}/>
      <Route path='/:profileId(\d+)/workspace/new' exact component={WorkspaceCreate}/>
      <Route path='/:profileId(\d+)/workspace/workspaceid' exact component={WorkspaceView}/>
      <Route path='/:profileId(\d+)/workspace/workspaceid/edit' exact component={WorkspaceEdit}/>
      <Route path='/login' exact component={() => <Login isAuthenticated={this.state.isAuthenticated} handlerSuccessfulLogin={this.handlerSuccessfulLogin} />}/>
      <Route path='/register' exact  component={Register}/>
      <Route path='/forgotpassword' exact component={ForgotPassword}/>
      <Route path='/resetpassword'  component={ResetPassword}/>



      </div>
      </Switch>


    </Router>
     );
  }
}

export default App;
