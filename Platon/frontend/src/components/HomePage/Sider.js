import { Menu } from 'antd';
import { AppstoreOutlined, MailOutlined } from '@ant-design/icons';
import React from 'react'
import colors from "../../utils/colors";
import {Link} from 'react-router-dom'
import requestService from "../../services/requestService";
import jwt_decode from "jwt-decode";


const { SubMenu } = Menu;

class Sider extends React.Component {
    constructor(props){
        super(props)
        this.state = {
            notifications:null,
            profileId: null,


        }
    }
    handleClick = e => {
        console.log('click ');
    };
    componentDidMount(){

        const token = localStorage.getItem("jwtToken");
        const decoded = jwt_decode(token);
        this.setState({
            profileId: decoded.id
        })


        requestService.getNotifications().then((response) => {
            this.setState({
                notifications: response.data,
            });
            console.log(response.data)
        });
    }

    render() {
        return (
            <Menu
                onClick={this.handleClick}
                style={{ width: 256,color:colors.tertiary,overflowy:'hidden' }}
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
                <Link to ={`/${this.state.profileId}`}>


                <SubMenu key="sub2" icon={<AppstoreOutlined />}  title=" Profile">

                </SubMenu></Link>
                <SubMenu
                    key="sub4"
                    title="Messages">
                </SubMenu>
                <SubMenu
                    key="sub4"
                    title={this.state.notifications?"Notifications("+this.state.notifications.length+")":"Notificatios"}>
                    {this.state.notifications && this.state.notifications.map( (notification) => {
                        return (
                            <p >{notification.text}</p>

                        )


                    })}

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