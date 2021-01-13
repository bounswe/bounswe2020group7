import config from "../../../../utils/config"
import axios from 'axios'

const { BASE_URL } = config
const ISSUE_URL = `${BASE_URL}/api/workspaces/issue`
axios.defaults.headers.common['auth_token'] = localStorage.getItem('jwtToken')

export const getIssues = ({ workspaceId }) => {
  const options = {
    params: {
      workspace_id: workspaceId,
    },
  }
  return axios.get(ISSUE_URL, options)
}

export const getIssueAssignees = ({ workspaceId, issueId }) => {
  const options = {
    params: {
      workspace_id: workspaceId,
      issue_id: issueId,
    },
  }
  return axios.get(ISSUE_URL+"/assignee", options)
}

export const getIssueComments = ({ workspaceId, issueId }) => {
  const options = {
    params: {
      workspace_id: workspaceId,
      issue_id: issueId,
    },
  }
  return axios.get(ISSUE_URL+"/comment", options)
}

export const createIssue = ({ workspaceId, title, description, deadline }) => {
  const options = {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  }

  let formData = new FormData()
  formData.append('workspace_id', workspaceId)

  formData.append('title', title)
  formData.append('description', description)
  if(deadline) formData.append('deadline', deadline)
  return axios.post(ISSUE_URL, formData, options)
}

export const updateIssue = ({ workspaceId, issueId, title, description, deadline }) => {
  const options = {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  }

  let formData = new FormData()
  formData.append('workspace_id', workspaceId)
  formData.append('issue_id', issueId)
  if (title) formData.append('title', title)
  if (description) formData.append('description', description)
  if (deadline) formData.append('deadline', deadline)

  return axios.put(ISSUE_URL, formData, options)
}

export const deleteIssue = ({ workspaceId, issueId }) => {
  let formData = new FormData()
  formData.append('workspace_id', workspaceId)
  formData.append('issue_id', issueId)

  const options = {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
    data: formData
  }

  return axios.delete(ISSUE_URL, options)
}
export const deleteComment = ({ workspaceId, issueId, commentId }) => {
  let formData = new FormData()
  formData.append('workspace_id', workspaceId)
  formData.append('issue_id', issueId)
  formData.append('comment_id', commentId)
  console.log("2", workspaceId, issueId, commentId);
  const options = {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
    data: formData
  }

  return axios.delete(ISSUE_URL+"/comment", options)
}