import axios from 'axios'
import config from '../../../utils/config'


const { BASE_URL } = config
axios.defaults.headers.common['auth_token'] = localStorage.getItem('jwtToken')

export const getSelfWorkspaces = () => axios.get(BASE_URL + '/api/workspaces/self')

export const inviteUserToWorkspace = ({ workspaceId, inviteeId }) => {
  const INVITATIONS_URL = `${BASE_URL}/api/workspaces/invitations`
  const options = {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  }

  let formData = new FormData()
  formData.append('workspace_id', workspaceId)
  formData.append('invitee_id', inviteeId)

  return axios.post(INVITATIONS_URL, formData, options)
}

export const isInContributors = ({ workspace, inviteeId }) => {
  return workspace?.contributors?.some((contributor) => contributor.id === inviteeId)
}
