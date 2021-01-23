import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import ListItem from '@material-ui/core/ListItem'
import ListItemAvatar from '@material-ui/core/ListItemAvatar'
import Avatar from '@material-ui/core/Avatar'
import ListItemText from '@material-ui/core/ListItemText'
import colors from '../../../utils/colors'
import List from '@material-ui/core/List'
import { getComments } from '../RatingComponent/utils'
import Rating from '@material-ui/lab/Rating'

const CommentsTabComponent = ({ profileId, goToProfile }) => {
  const [comments, setComments] = useState([])

  useEffect(() => {
    getComments({ profileId }).then((res) => {
      if (res.status === 200) {
        setComments(res.data.result)
      }
    })
  }, [profileId])

  return (<List>
    {comments?.map((comment) => {
      return (
        <Link onClick={() => goToProfile(comment.owner_id)}>
          <ListItem>
            <ListItemAvatar>
              <Avatar src={'http://18.185.75.161:5000/api' + comment.owner_profile_photo} />
            </ListItemAvatar>
            <ListItemText
              style={{ color: colors.secondary, flexShrink: 0 }}
              primary={`${comment.owner_name} ${comment.owner_surname}`}
            />
            <ListItemText
              style={{ color: colors.secondary, marginLeft: '48px', marginRight: '32px' }}
              primary={`sfadsfafdasdfasdfaksmfdasdfmalmd flk mfsadkmfa ldsmf aksdmfl askdmfalskmdflaskmdfl kmasldfm kalsdfm lasdkmflaksdmf`}
            />
            <Rating
              readOnly
              value={comment.rate}
              precision={1}
            />
          </ListItem>
        </Link>
      )
    })}
  </List>)

}

export default CommentsTabComponent
