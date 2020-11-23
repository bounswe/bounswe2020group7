import AppBar from '../AppBar/AppBar';
import React from "react";
import './style.css'
import moment from 'moment'
import InfiniteScroller from './InfiniteScroll.js'
import 'moment/locale/zh-cn';
import { Layout } from 'antd';
import Sider from './Sider'
import colors from "../../utils/colors";

const { Header,Footer } = Layout;

moment.locale('zh-cn');

class HomePage extends React.Component {
    state = {
        data: [],
        loading: false,
        hasMore: true,
    };
    onPanelChange(value, mode) {
        console.log(value.format('YYYY-MM-DD'), mode);
    }
/*
*  <div>
                    <Calendarr/>
                </div>
                   <div>
                       <Sider></Sider>
                   </div>
                  <div>
                      <InfiniteScroller/>
                  </div>
        <Layout>
            <Layout>
                <AppBar/>
            </Layout>
            <Layout>
                <Layout
                    style={{
                        overflow: 'auto',
                        height: '100vh',
                        position: 'fixed',
                        left: 0,
                    }}
                >
                   <Sider></Sider>
                </Layout>
                <Layout className="site-layout" style={{ marginLeft: 400,height:400 }}>
                    <Header className="site-layout-background" style={{ padding: 0 }} />
                   <Layout><InfiniteScroller></InfiniteScroller>
                   </Layout>
                </Layout>

                </Layout>
            <Footer style={{ textAlign: 'center',position:'fixed',marginLeft:200 }}>Ant Design ©2018 Created by Ant UED</Footer>
        </Layout>*/

    render(){
    return (
        <Layout  style={{
            background : colors.primaryDark
        }}>
            <Layout>
                <AppBar/>
            </Layout>
            <Layout>
                <Layout
                    style={{
                        overflow: 'auto',
                        position: 'fixed',
                        left: 0,
                        top:80,
                    }}
                >
                    <Sider></Sider>
                </Layout>
                <Layout className="site-layout" style={{marginLeft:400 }}>
                    <Header className="site-layout-background" style={{ padding: 0 }} />
                    <Layout><InfiniteScroller></InfiniteScroller>:
                    </Layout>
                </Layout>
            </Layout>
            <Footer style={{ textAlign: 'center',position:'fixed',marginLeft:200 }}>Ant Design ©2018 Created by Ant UED</Footer>
        </Layout>


            );
    }
}

export default HomePage;