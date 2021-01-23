import config from "../../../utils/config"
import axios from 'axios'

const { BASE_URL } = config
const COMMENT_URL = `${BASE_URL}/api/follow/comment`
axios.defaults.headers.common['auth_token'] = localStorage.getItem('jwtToken')


export const commentUser = ({userId, commentText, rating}) => {
  const options = {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  }

  let formData = new FormData()
  formData.append('commented_user_id', userId)
  formData.append('rate', rating)
  formData.append('text', commentText)

  return axios.post(COMMENT_URL, formData, options)
}
