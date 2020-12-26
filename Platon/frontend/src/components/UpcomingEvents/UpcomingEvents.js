import React, { useEffect, useState } from 'react'
import Typography from '@material-ui/core/Typography'
import UpcomingEventsItem from './UpcomingEventsItem/UpcomingEventsItem'
import colors from '../../utils/colors'
import Spinner from '../Spinner/Spinner'
import './UpcomingEvents.css'
import Pagination from '@material-ui/lab/Pagination'
import { withStyles } from '@material-ui/core/styles'

const BASE_URL = 'http://18.185.75.161:5000/api/upcoming_events'

const useStyles = () => ({
  typography: {
    color: colors.secondaryDark,
  },
})

const UpcomingEvents = ({ itemsPerPage = 5, classes, width = '500px' }) => {
  const [data, setData] = useState({})
  const [fetching, setFetching] = useState(false)
  const [page, setPage] = useState(1)

  const StyledPagination = withStyles({
    root: {
      color: colors.secondary,
    },
  }, { name: 'MuiPaginationItem' })(Pagination)

  useEffect(() => {
    const endPoint = `${BASE_URL}?page=${page - 1}&per_page=${itemsPerPage}`
    setFetching(true)
    fetch(endPoint)
      .then((response) => response.json())
      .then((data) => {
        setData(data)
        setFetching(false)
      })
      .catch((error) => {
        console.error('Error:', error)
        setFetching(false)
      })
  }, [page])


  const handlePageChange = (event, page) => {
    setPage(page)
  }

  return (
    <div>
      <Typography
        style={{ color: colors.quaternary, textAlign: 'center' }}
        variant="h5"
        gutterBottom
      >
        Upcoming Events
      </Typography>
        <div className="UpcomingEventsItems" style={{ width: width }}>
          {fetching && (
            <div className="TrendingProjectsSpinner">
              <Spinner />
            </div>
          )}
          {!fetching && data && data.upcoming_events && data.upcoming_events.map((event, index) => (
            <UpcomingEventsItem
              event={event}
              key={index}
            />
          ))}
          <div className='paginationContainerUpcoming'>
            <StyledPagination
              count={data.number_of_pages}
              onChange={handlePageChange}
              page={page}
            />
            <div className="disclaimer" style={{ marginTop: '8px' }}>
              <Typography
                component="body2"
                variant="body2"
                className={classes.typography}
              >
                Disclaimer: Data is provided by <a href='http://www.wikicfp.com/cfp/'>CFP API</a>.
              </Typography>
            </div>
          </div>
        </div>
    </div>
  )
}

export default withStyles(useStyles)(UpcomingEvents)
