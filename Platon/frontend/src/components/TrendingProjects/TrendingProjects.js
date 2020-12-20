import React, { useState, useEffect } from 'react'
import Typography from '@material-ui/core/Typography'
import TrendingProjectsItem from './TrendingProjectsItem/TrendingProjectsItem'
import Spinner from '../Spinner/Spinner'
import colors from '../../utils/colors'
import './TrendingProjects.css'
import { withStyles } from '@material-ui/core/styles'
import Pagination from '@material-ui/lab/Pagination'

const TRENDING_PROJECTS_URL = 'http://18.185.75.161:5000/api/workspaces/trending_projects?number_of_workspaces=100000'
const ITEMS_PER_PAGE = 5

const StyledPagination = withStyles({
  root: {
    color: colors.secondary,
  },
}, { name: 'MuiPaginationItem' })(Pagination)

const TrendingProjects = () => {
  const [rawData, setRawData] = useState([])
  const [fetching, setFetching] = useState(false)
  const [page, setPage] = useState(1)

  useEffect(() => {
    setFetching(true)
    fetch(TRENDING_PROJECTS_URL)
      .then((response) => response.json())
      .then((data) => {
        setRawData(data.trending_projects)
        setFetching(false)
      })
      .catch((error) => {
        console.error('Error:', error)
        setFetching(false)
      })
  }, [])

  const getPageData = () => {
    const dataStartIndex = (page - 1) * ITEMS_PER_PAGE
    const dataEndIndex = (page * ITEMS_PER_PAGE)
    return rawData.slice(dataStartIndex, dataEndIndex)
  }

  const handlePageChange = (event, newPage) => {
    setPage(newPage)
  }

  const pageData = getPageData()

  return (
    <div>
      <Typography
        style={{ color: colors.tertiary, textAlign: 'center' }}
        variant="h5"
        gutterBottom
      >
        Trending Projects
      </Typography>
      {fetching && (
        <div className="TrendingProjectsSpinner">
          <Spinner />
        </div>
      )}
      {pageData && (
        <div className="TrendingProjectsItems">
          {pageData.map((item, index) => (
            <TrendingProjectsItem
              id={index}
              key={index}
              title={item.title}
              description={item.description}
              contributors={item.contributor_list}
            />
          ))}
          <div className='paginationContainerTrending'>
            <StyledPagination
              count={Math.floor(rawData.length / ITEMS_PER_PAGE)}
              onChange={handlePageChange}
              page={page}
            />
          </div>
        </div>
      )}
    </div>
  )
}

export default TrendingProjects
