import config from '../../../utils/config'
import axios from 'axios'

const { BASE_URL } = config
const COMMENT_URL = `${BASE_URL}/api/follow/comment`
axios.defaults.headers.common['auth_token'] = localStorage.getItem('jwtToken')


export const getComments = ({ profileId }) => {
  const options = {
    params: {
      commented_user_id: profileId,
    },
  }
  return axios.get(COMMENT_URL, options)
}

export const calculateAverageRating = (comments) => {
  const ratingCount = comments.length
  const totalRating = comments.reduce((acc, comment) => acc + comment.rate, 0)
  if (ratingCount === 0) return 0

  return totalRating / ratingCount
}

