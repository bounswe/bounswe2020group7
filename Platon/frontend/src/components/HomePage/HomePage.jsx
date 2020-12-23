import React from 'react'
import './style.css'
import moment from 'moment'
import InfiniteScroller from './InfiniteScroll.js'
import 'moment/locale/zh-cn'
import { Layout } from 'antd'
import Sider from './Sider'
import colors from '../../utils/colors'
import NavBar from '../NavBar/NavBar'
import UpcomingEvents from '../UpcomingEvents/UpcomingEvents'
import TrendingProjects from '../TrendingProjects/TrendingProjects'

const { Footer } = Layout

moment.locale('zh-cn')


class HomePage extends React.Component {
  state = {
    data: [],
    loading: false,
    hasMore: true,
  }

  onPanelChange(value, mode) {
    console.log(value.format('YYYY-MM-DD'), mode)
  }

  render() {
    return (
      <Layout
        style={{
          background: colors.primaryDark,
        }}
      >
        <Layout>
          <Layout
            style={{
              overflow: 'auto',
              position: 'fixed',
              left: 0,
              top: 80,
            }}
          >
          </Layout>
          <Layout
            style={{
              position: 'fixed',
              right: 16,
              top: 80,
            }}
          >
            <div style={{ overflow: 'scroll', height: 'calc(100vh - 100px)' }}>
              <div><TrendingProjects itemsPerPage={3} width='400px' /></div>
              <div style={{ marginTop: '16px' }}><UpcomingEvents itemsPerPage={3} width='400px' />
              </div>
            </div>

          </Layout>
          <Layout
            style={{
              position: 'fixed',
              top: 0,
              width: '100%',
              zIndex: 100,
            }}
          >
            <NavBar />
          </Layout>
          <Layout
            className="site-layout"
            style={{ marginLeft: '400px', marginTop: '64px', marginRight: '516px' }}
          >
            <Layout>
              <div>
                <div>
                  <InfiniteScroller />
                </div>
              </div>
            </Layout>
          </Layout>
        </Layout>
        <Footer style={{ textAlign: 'center', position: 'fixed', marginLeft: 200 }}>Platon</Footer>
      </Layout>


    )
  }
}


export default HomePage
