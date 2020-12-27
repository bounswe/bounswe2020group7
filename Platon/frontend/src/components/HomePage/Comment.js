import React, { createElement } from 'react';
import { Tooltip } from 'antd';
import Avatar from '@material-ui/core/Avatar';
import moment from 'moment';
import { DislikeOutlined, LikeOutlined, DislikeFilled, LikeFilled } from '@ant-design/icons';
import colors from "../../utils/colors";
import Box from '@material-ui/core/Box';
import { Comment,Form, Button, List, Input } from 'antd';
import TextField from "@material-ui/core/TextField";
import requestService from "../../services/requestService";
import { withStyles } from "@material-ui/core/styles";
const { TextArea } = Input;
const StyledTextField = withStyles({
  root: {
    "& .MuiInputBase-input": {

      color: colors.secondary,
    },
    "& .Mui-required": {
      color: colors.primaryLight,
    },

    "& .MuiFormLabel-root": {
      color: colors.primaryLight,
    },


    "& label.Mui-focused": {
      color: colors.tertiary,
    },
    "& .MuiInput-underline:after": {
      borderBottomColor: colors.tertiary,
    },
    "& .MuiOutlinedInput-root": {
      "& fieldset": {
        borderColor: colors.secondaryLight,
      },
      "&:hover fieldset": {
        borderColor: colors.secondaryDark,
      },
      "&.Mui-focused fieldset": {
        borderColor: colors.tertiary,
      },
    },
  },
})(TextField);


const Editor = ({ onChange, onSubmit, submitting, value }) => (
  <>
    <Form.Item>
      <TextArea rows={4} onChange={onChange} value={value} />
    </Form.Item>
    <Form.Item>
      <Button htmlType="submit" onClick={onSubmit} type="primary">
        Add Comment
      </Button>
    </Form.Item>
  </>
);

class Commentt extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            liked:false,
            disliked:false,
            likes: 0,
            dislikes: 0,
            action:'',
            props: props,
            reply:false,
            description:'',
            showChildren:false,
        };
    }

handleSubmit = () => {
    if (!this.state.description) {
      return;
    }


    requestService.createIssue(this.state.title,this.state.description,this.state.deadline,this.state.workspace_id).then((response) => {
       })

  };

  handleChange = e => {
    this.setState({
      description: e.target.value,
    });
  };

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
     if(!this.state.liked && !this.state.disliked )
        {
        this.setLikes(1);
        this.setDislikes(0);
        this.setLiked();
        this.setAction('liked');
        }
       else if(!this.state.liked && this.state.disliked)
                {        this.setLikes(1);
                         this.setLiked();
                         this.setDislikes(-1);
                         this.setAction('liked');
                }
    };
     setLiked = () => {
            this.setState({liked:true,disliked:false})
        }
     setDisliked = () => {
                    this.setState({liked:false,disliked:true})
                }

    dislike = () => {
        if(!this.state.liked && !this.state.disliked)
        {this.setLikes(0);
        this.setDisliked();
        this.setDislikes(1);
        this.setAction('disliked');}
        else if(this.state.liked && !this.state.disliked)
        {        this.setLikes(-1);
                 this.setDisliked();
                 this.setDislikes(1);
                 this.setAction('disliked');
        }

    };
    deleteIssue = (issue_id,workspace_id) => {
        requestService.deleteIssue(issue_id,workspace_id)

        };

reply= () => {
this.setState({reply:!this.state.reply,showChildren:!this.state.showChildren})
}
handleSubmit = () => {
    if (!this.state.description) {
      return;
    }

    this.setState({
      submitting: true,
    });
    requestService.addIssueComment(this.state.title,this.state.description,this.state.deadline,2/*this.state.workspace_id*/).then((response) => {
        const length=this.state.selectedMembers.length;
        console.log(this.props)
        for(var i=0;i<length;i++)
        requestService.assignIssue(78,2,this.state.selectedMembers[i])
       })
        setTimeout(() => {
             this.setState({
               submitting: false,
               value: '',
               comments: [
                 {
                   title: 'Han Solo',
                   avatar: 'https://zos.alipayobjects.com/rmsportal/ODTLcjxAfvqbxHnVXCYX.png',
                   description: <p>{this.state.description}</p>,
                   datetime: moment().fromNow(),
                 },
                 ...this.state.comments,
               ],
             });
           }, 1000);
  };

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
               {this.props.func && <Box>
                     <Tooltip key="issueFunc" title="func">
                      <Button onClick={()=>{this.props.func(this.props.issue.issue_id,this.props.issue.workspace_id,this.setState({}))}}>
                          <span className="comment-action">{this.props.funcName}</span>
                      </Button>
                                    </Tooltip>,
                                </Box>}

                 <Box>
                                    <Tooltip key="comment-basic-reply" title="Reply">

                      <Button onClick={this.reply}>
                                                <span className="comment-action">{"Reply To"}</span>
                                            </Button>

                        {this.state.reply && <Comment
                                              avatar={
                                                <Avatar
                                                  src={this.state.user?this.state.user.profile_photo:"https://zos.alipayobjects.com/rmsportal/ODTLcjxAfvqbxHnVXCYX.png"}
                                                  alt="Han Solo"
                                                />
                                              }
                                              content={
                                              <div>
                                               <Editor
                                              onChange={this.handleChange}
                                              onSubmit={()=>{this.props.func2(this.props.issue.issue_id,this.props.issue.workspace_id,this.state.description)}}
                                              value={this.state.description}
                                            />
                                            </div>
                                          }
                                        /> }


                                    </Tooltip>
                                </Box>
            </Box>
        ];
        //
        return (

        <div
        style={{display:'flex'}}
        >
            <Avatar
                                 src={this.props.avatar}
                                 alt="Han Solo"
                             />

            <Comment
                actions={this.props.actions?this.props.actions:actions}
                author={this.props.author}
                content={
                    <p>
                    {this.props.message}
                    </p>
                }
                style={{color:colors.tertiary} }
            >
            {this.state.showChildren && this.props.childComments}
            </Comment>
            </div>
        );
    };
}
export default Commentt