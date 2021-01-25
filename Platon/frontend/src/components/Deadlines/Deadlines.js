import React, { useState, useEffect } from 'react'
import Typography from '@material-ui/core/Typography'
import DeadlinesItem from './DeadlinesItem/DeadlinesItem'
import Spinner from '../Spinner/Spinner'
import colors from '../../utils/colors'
import './Deadlines.css'
import { withStyles } from '@material-ui/core/styles'
import Pagination from '@material-ui/lab/Pagination'
import axios from 'axios'

const DEADLINES_URL = 'http://18.185.75.161:5000/api/upcoming_events/personal_calendar'
axios.defaults.headers.common['auth_token'] = localStorage.getItem('jwtToken')

const StyledPagination = withStyles({
  root: {
    color: colors.secondary,
  },
}, { name: 'MuiPaginationItem' })(Pagination)

const Deadlines = ({
  itemsPerPage = 5,
  width = '500px',
  marginLeft = '0px',
  marginRight = '0px',
}) => {
  const [rawData, setRawData] = useState([])
  const [fetching, setFetching] = useState(false)
  const [page, setPage] = useState(1)

  useEffect(() => {
    setFetching(true)
    axios.get(DEADLINES_URL)
      .then((res) => {
        setRawData(res.data)
        setFetching(false)
      })
      .catch((error) => {
        console.error('Error:', error)
        setFetching(false)
      })
  }, [])

  const getPageData = () => {
    const dataStartIndex = (page - 1) * itemsPerPage
    const dataEndIndex = (page * itemsPerPage)
    return rawData.slice(dataStartIndex, dataEndIndex)
  }

  const handlePageChange = (event, newPage) => {
    setPage(newPage)
  }

  const pageData = getPageData()

  return (
    <div>
      <Typography
        style={{ color: 'rgb(255, 139, 51)', textAlign: 'center' }}
        variant="h5"
        gutterBottom
      >
        Upcoming Deadlines
      </Typography>
      {fetching && (
        <div className="DeadlinesSpinner">
          <Spinner />
        </div>
      )}
      {pageData && (
        <div
          className="DeadlinesItems"
          style={{ width: width, marginLeft: marginLeft, marginRight: marginRight }}
        >
          {pageData.map((item, index) => (
            <DeadlinesItem
              id={index}
              key={index}
              deadline={item}
            />
          ))}
          <div className='paginationContainerTrending'>
            <StyledPagination
              count={Math.floor(rawData.length / itemsPerPage)}
              onChange={handlePageChange}
              page={page}
              siblingCount={0}
            />
          </div>
        </div>
      )}
    </div>
  )
}

export default Deadlines
