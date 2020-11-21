import { Menu } from 'antd';
import { AppstoreOutlined, MailOutlined, SettingOutlined } from '@ant-design/icons';
import React from 'react'
import ReactDOM from 'react-dom'

import colors from "../../utils/colors";

const { SubMenu } = Menu;

class Sider extends React.Component {
    handleClick = e => {
        console.log('click ');
    };

    render() {
        return (
            <Menu
                onClick={this.handleClick}
                style={{ width: 256,color:colors.tertiary }}
                defaultSelectedKeys={['1']}
                defaultOpenKeys={['sub1']}
                mode="inline"
                theme={'dark'}
            >
                <SubMenu
                    onClick={this.handleClick()}
                    key="sub1"
                    title={
                        <span>
              <MailOutlined />
              <span>Home</span>
            </span>
                    }
                >
                </SubMenu>
                <SubMenu key="sub2" icon={<AppstoreOutlined />}  title=" Profile">

                </SubMenu>
                <SubMenu
                    key="sub4"
                    title="Messages">
                </SubMenu>
                <SubMenu
                    key="sub4"
                    title="Notifications">
                </SubMenu>
                <SubMenu
                    key="sub4"
                    title="My Projects">
                </SubMenu>
                <SubMenu
                    key="sub4"
                    title="Settings">
                </SubMenu>


            </Menu>
        );
    }
}
export default Sider;