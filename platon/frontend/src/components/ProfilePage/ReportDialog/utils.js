import axios from 'axios'
import config from '../../../utils/config'

const { BASE_URL } = config
axios.defaults.headers.common['auth_token'] = localStorage.getItem('jwtToken')

export const reportUserPost = ({ reported_user_id, text }) => {
    const REPORT_URL = `${BASE_URL}/api/follow/report`
    const options = {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      }
  
    let formData = new FormData()
    formData.append('reported_user_id', reported_user_id)
    formData.append('text', text)
  
    return axios.post(REPORT_URL, formData, options)
  }