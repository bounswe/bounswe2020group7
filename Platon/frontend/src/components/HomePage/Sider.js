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
    this.state = {
      notifications: null,
      profileId: null,
      notificationNum: 0,

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
  }

  render() {
    return (
      <Menu
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
