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
import Box from '@material-ui/core/Box';
import { Container } from '@material-ui/core'

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
          background: colors.primary,
        }}
      >            <NavBar />
        <Container style={{maxWidth: "1400px", marginTop: "20px" }}>
        <Box display="flex" p={1} justifyContent="space-evenly" >
         <TrendingProjects marginLeft="auto" marginRight="auto" itemsPerPage={3} width='320px'/>
         <InfiniteScroller/>
         <UpcomingEvents marginLeft="auto" marginRight="auto" itemsPerPage={3} width='320px' />
        </Box>
        </Container>
      </Layout>


    )
  }

}


export default HomePage
