<<<<<<< HEAD
import { Menu } from "antd";
import { AppstoreOutlined, MailOutlined } from "@ant-design/icons";
import React from "react";
import colors from "../../utils/colors";
import { Link } from "react-router-dom";
import requestService from "../../services/requestService";
import jwt_decode from "jwt-decode";

const { SubMenu } = Menu;

class Sider extends React.Component {
  constructor(props) {
    super(props);
=======
import { Menu } from 'antd'
import { AppstoreOutlined, MailOutlined } from '@ant-design/icons'
import React from 'react'
import colors from '../../utils/colors'
import { Link } from 'react-router-dom'
import requestService from '../../services/requestService'
import jwt_decode from 'jwt-decode'


const { SubMenu } = Menu


class Sider extends React.Component {
  constructor(props) {
    super(props)
>>>>>>> d1ca6fbf45774b1e32ffa32931d9451325048e24
    this.state = {
      notifications: null,
      profileId: null,
      notificationNum: 0,
<<<<<<< HEAD
    };
  }
  handleClick = (e) => {
    console.log("click ");
  };
  componentDidMount() {
    const token = localStorage.getItem("jwtToken");
    const decoded = jwt_decode(token);
    this.setState({
      profileId: decoded.id,
    });

    requestService.getNotifications(1).then((response) => {
      console.log(response);
      this.setState({
        notifications: response.data.notification_list,
        notificationNum: response.data.notificationNum,
      });
      console.log(response.data);
    });
=======

    }
  }

  componentDidMount() {

    const token = localStorage.getItem('jwtToken')
    const decoded = jwt_decode(token)
    this.setState({
      profileId: decoded.id,
    })


    requestService.getNotifications(1).then((response) => {
      console.log(response)
      this.setState({
        notifications: response.data.notification_list,
        notificationNum: response.data.notificationNum,
      })
      console.log(response.data)
    })
>>>>>>> d1ca6fbf45774b1e32ffa32931d9451325048e24
  }

  render() {
    return (
      <Menu
<<<<<<< HEAD
        onClick={this.handleClick}
        style={{ width: 256, color: colors.tertiary, overflowy: "hidden" }}
        defaultSelectedKeys={["1"]}
        defaultOpenKeys={["sub1"]}
        mode="inline"
        theme={"dark"}
      >
        <Link to={"/"}>
          <SubMenu
            onClick={this.handleClick()}
            key="sub1"
            title={
              <span>
                <MailOutlined />
                <span>Home</span>
              </span>
            }
          ></SubMenu>
        </Link>
        <Link to={`/${this.state.profileId}`}>
          <SubMenu
            key="sub2"
            icon={<AppstoreOutlined />}
            title=" Profile"
          ></SubMenu>
        </Link>
        <Link to={`/${this.state.profileId}`}>
          <SubMenu key="sub4" title="Messages"></SubMenu>
        </Link>
        <Link to={`/${this.state.profileId}`}>
          <SubMenu
            key="sub4"
            title={
              this.state.notifications
                ? "Notifications(" + this.state.notificationNum + ")"
                : "Notifications"
            }
          >
            {this.state.notifications &&
              this.state.notifications.map((notification) => {
                return <p>{notification.text}</p>;
              })}
          </SubMenu>
        </Link>
        <Link to={`/${this.state.profileId}/workspace`}>
          <SubMenu key="sub4" title="Workspaces"></SubMenu>
        </Link>
        <Link to={`/${this.state.profileId}`}>
          <SubMenu key="sub4" title="Settings"></SubMenu>
        </Link>
      </Menu>
    );
  }
}
export default Sider;
=======
        style={{ width: 256, color: colors.tertiary, overflowy: 'hidden' }}
        defaultSelectedKeys={['1']}
        mode="inline"
        theme={'dark'}
      >
        <Menu.Item
          key={'1'}
        >
          <Link to={'/'}>
            <span>
              <MailOutlined />
              <span>Home</span>
            </span>
          </Link>
        </Menu.Item>
        <Menu.Item key="2">
          <Link to={`/${this.state.profileId}`}>
            <AppstoreOutlined />
            <span>Profile</span>
          </Link>
        </Menu.Item>
        <Menu.Item
          key="3"
        >
          <Link to={`/${this.state.profileId}`}>
            <span> Messages </span>
          </Link>
        </Menu.Item>
        <SubMenu
          key="4"
          title={this.state.notifications ? 'Notifications(' + this.state.notificationNum + ')' :
            'Notifications'}
        >
          {this.state.notifications && this.state.notifications.map((notification, index) => {
            return (
              <Menu.Item key={'notification-' + index.toString()}>{notification.text}</Menu.Item>
            )
          })}
        </SubMenu>

        <Menu.Item key="5">
          <Link to={`/${this.state.profileId}`}>
            <span>My Projects</span>
          </Link>
        </Menu.Item>

        <Menu.Item key="6">
          <Link to={`/${this.state.profileId}`}>
            <span>Settings</span>
          </Link>
        </Menu.Item>

      </Menu>
    )
  }
}


                </SubMenu>
                </Link>
               <Link to ={`/${this.state.profileId}/workspace`}>
                <SubMenu
                    key="sub4"
                    title="Workspaces">
                </SubMenu>
                </Link>
               <Link to ={`/${this.state.profileId}`}>
                <SubMenu
                    key="sub4"
                    title="Settings">
                </SubMenu>
</Link>


export default Sider
>>>>>>> d1ca6fbf45774b1e32ffa32931d9451325048e24
