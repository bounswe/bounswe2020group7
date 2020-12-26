import config from "../../../../utils/config"
import axios from 'axios'

const { BASE_URL } = config
const MILESTONE_URL = `${BASE_URL}/api/workspaces/milestone`
axios.defaults.headers.common['auth_token'] = localStorage.getItem('jwtToken')

export const getMilestones = ({ workspaceId }) => {
  const options = {
    params: {
      workspace_id: workspaceId,
    },
  }
  return axios.get(MILESTONE_URL, options)
}

export const createMilestone = ({ workspaceId, title, description, deadline }) => {
  const options = {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  }

  let formData = new FormData()
  formData.append('workspace_id', workspaceId)
  formData.append('title', title)
  formData.append('description', description)
  formData.append('deadline', deadline)

  return axios.post(MILESTONE_URL, formData, options)
}

export const updateMilestone = ({ workspaceId, milestoneId, title, description, deadline }) => {
  const options = {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  }

  let formData = new FormData()
  formData.append('workspace_id', workspaceId)
  formData.append('milestone_id', milestoneId)
  if (title) formData.append('title', title)
  if (description) formData.append('description', description)
  if (deadline) formData.append('deadline', deadline)

  return axios.put(MILESTONE_URL, formData, options)
}

export const deleteMilestone = ({ workspaceId, milestoneId }) => {
  let formData = new FormData()
  formData.append('workspace_id', workspaceId)
  formData.append('milestone_id', milestoneId)

  const options = {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
    data: formData
  }

  return axios.delete(MILESTONE_URL, options)
}
