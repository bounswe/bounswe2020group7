import React, { useState, useEffect } from 'react'
import { calculateAverageRating, getComments } from './utils'
import Rating from '@material-ui/lab/Rating'

const RatingComponent = ({ profileId }) => {
  const [averageRating, setAverageRating] = useState(null)

  useEffect(() => {
    getComments({ profileId }).then((res) => {
      if(res.status === 200) {
        const averageRating = calculateAverageRating(res.data.result)
        setAverageRating(averageRating)
      }
    })
  }, [])

  return <Rating
    readOnly
    value={averageRating}
    precision={0.1}
  />

}

export default RatingComponent
