import React, { createElement } from 'react';
import { Comment, Tooltip, Avatar } from 'antd';
import moment from 'moment';
import { DislikeOutlined, LikeOutlined, DislikeFilled, LikeFilled } from '@ant-design/icons';
import colors from "../../utils/colors";
import Box from '@material-ui/core/Box';

class Commentt extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            likes: 0,
            dislikes: 0,
            action:'',
            props: props,
        };
    }

    setLikes = (value) => {
        this.setState({likes: this.state.likes + value})
    }
    setDislikes = (value) => {
        this.setState({dislikes: this.state.dislikes + value})
    }
    setAction=(value)=>{
        this.setState({action:value})
    }

    like = () => {
        this.setLikes(1);
        this.setDislikes(0);
        this.setAction('liked');
    };

    dislike = () => {
        this.setLikes(0);
        this.setDislikes(1);
        this.setAction('disliked');
    };

/*
*
* */

    render() {
        const actions = [
            <Box  justifyContent="flex-start" display="flex" flexDirection="row" css={{width:250}}>
                <Box>
                    <Tooltip key="comment-basic-like" title="Like">
      <span onClick={this.like}>
        {createElement(this.state.action === 'liked' ? LikeFilled : LikeOutlined)}
          <span className="comment-action">{this.state.likes}</span>
      </span>
                    </Tooltip>,
                </Box>
                <Box>
                    <Tooltip key="comment-basic-dislike" title="Dislike">
      <span onClick={this.dislike}>
        {React.createElement(this.state.action === 'disliked' ? DislikeFilled : DislikeOutlined)}
          <span className="comment-action">{this.state.dislikes}</span>
      </span>
                    </Tooltip>,
                </Box>
                <Box>
                    <span key="comment-basic-reply-to">Reply to</span>
                </Box>
            </Box>
        ];
        //
        return (
            <Comment
                actions={actions}
                author={<a>{this.props.title}</a>}
                avatar={
                    <Avatar
                        src={this.props.avatar}
                        alt="Han Solo"
                    />
                }
                content={
                    <p>
                        We supply a series of design principles, practical patterns and high quality design
                        resources (Sketch and Axure), to help people create their product prototypes beautifully
                        and efficiently.
                    </p>
                }
                datetime={
                    <Tooltip title={moment().format('YYYY-MM-DD HH:mm:ss')}>
                        <span>{moment().fromNow()}</span>
                    </Tooltip>
                }
                style={{color:colors.tertiary} }
            />
        );
    };
}
export default Commentt