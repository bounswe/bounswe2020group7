import { Comment, Avatar, Form, Button, List, Input } from 'antd';
import moment from 'moment';
import React from 'react'
import ReactDOM from 'react-dom'
import trLocale from 'moment/locale/tr';
import { DatePicker, Space } from 'antd';
import colors from "../../utils/colors";
import TextField from "@material-ui/core/TextField";
import { withStyles } from "@material-ui/core/styles";
import "antd/dist/antd.css";
import jwt_decode from "jwt-decode";
import requestService from "../../services/requestService";
import Commentt from '../HomePage/Comment'
import CreateIssue from './CreateIssue'
import { Tooltip } from 'antd';
import Box from '@material-ui/core/Box';


import { Select } from 'antd';

const { Option } = Select;


const { RangePicker } = DatePicker;

const dateFormat = 'YYYY/MM/DD';
const monthFormat = 'YYYY/MM';

const dateFormatList = ['DD/MM/YYYY', 'DD/MM/YY'];

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

const deleteIssue=(issue_id,workspace_id)=>{
requestService.deleteIssue(issue_id,workspace_id);
}
const addCommentIssue=(issue_id,workspace_id,description)=>{
requestService.addIssueComment(issue_id,workspace_id,description);
}

const CommentList = ({ comments }) => (
 <List
     className="comment-list"
     header={`${comments.length} issues`}
     itemLayout="horizontal"
     dataSource={comments}
     renderItem={item => (
       <li>
         <Commentt
           issue={item.issue}
           author={item.author}
           avatar={item.avatar}
           content={item.description}
           datetime={item.datetime}
           func={deleteIssue}
           funcName={"Delete"}
           func2={addCommentIssue}
           funcName2={"Reply To"}
           childComments={item.childComments}
         />
       </li>
     )}
   />
)

const Editor = ({ onChange, onSubmit, submitting, value }) => (
  <>
    <Form.Item>
      <TextArea rows={4} onChange={onChange} value={value} />
    </Form.Item>
    <Form.Item>
      <Button htmlType="submit" loading={submitting} onClick={onSubmit} type="primary">
        Create Issue
      </Button>
    </Form.Item>
  </>
);

class Issue extends React.Component {
    constructor (props){
    super(props);
    this.state = {
                   comments: [],
                   submitting: false,
                   value: '',
                   user:null,
                   children:[],
                   issues:[],
                   toDisplay:[],
                   page:0,
                 };
    }

  componentDidMount(){

  const token = localStorage.getItem("jwtToken");
  const decoded = jwt_decode(token);
  this.reply=this.reply.bind(this);
    //Get workspace members
    requestService.getIssues(decoded.id,2).then((response)=>{
         const issues=response.data.result;
                      const toDisplay=[];
                      issues.forEach(function (issue,index){
                      const children=[]
                      requestService.getIssueComment(issue.issue_id,issue.workspace_id).then((resp)=>{
                      const childComment=resp.data.result;
                      childComment.forEach(function(child,index){
                      children.push(<Comment
                                         avatar= 'https://zos.alipayobjects.com/rmsportal/ODTLcjxAfvqbxHnVXCYX.png'
                                         author={''+child.owner_name+' '+child.owner_surname}
                                         content={<p>{child.comment}</p>}
                                              />)
                      })
                      })
                      toDisplay.push({
                                         issue:issue,
                                         author: issue.title,
                                         avatar: issue.creator_photo,
                                         content: (
                                           <p>
                                             {issue.description}
                                           </p>
                                         ),
                                         datetime: (
                                             <span>{moment().subtract(1, 'days').fromNow()}</span>

                                         ),
                                         childComments:children
                                         }

                      )
                      })


        this.setState({toDisplay:toDisplay})
    })


    // Get Workspace issues
    requestService.getUser(decoded.id).then((response) => {
      this.setState({
        user: response.data,
      });
    })
      }
    replyToIssue=(issue_id)=>{
    this.setState({comment:'',repliedIssue:issue_id})
    }
    reply= () => {
    this.setState({reply:true})
    }
      componentDidUpdate(){
      const token = localStorage.getItem("jwtToken");
        const decoded = jwt_decode(token);
      requestService.getIssues(decoded.id,2).then((response)=>{
              const issues=response.data.result;
              const toDisplay=[];
              issues.forEach(function (issue,index){
               const children=[]
               requestService.getIssueComment(issue.issue_id,issue.workspace_id).then((resp)=>{
                      const childComment=resp.data.result;
                      childComment.forEach(function(child,index){
                      children.push(<Comment
                                         avatar= 'https://zos.alipayobjects.com/rmsportal/ODTLcjxAfvqbxHnVXCYX.png'
                                         author={''+child.owner_name+' '+child.owner_surname}
                                         content={child.comment}
                                              />)
                      })
                      })
                     toDisplay.push({
                                  author: issue.title,
                                  issue:issue,
                                  avatar: issue.creator_photo,
                                  content: (
                                    <p>
                                      {issue.description}
                                    </p>
                                  ),
                                  datetime: (
                                      <span>{moment().subtract(1, 'days').fromNow()}</span>

                                  ),
                                  childComments:children
                                  }
               )}
              )})
      }


    datePick = (value) =>{
    this.setState({deadline:value})

    }
    deleteIssue = (issue_id) =>{
        requestService.deleteIssue(issue_id,2)
        }
  handleSubmit = () => {
    if(!this.state.description) {
      return;
    }

    this.setState({
      submitting: true,
    });
    requestService.createIssue(this.state.title,this.state.description,this.state.deadline,2).then((response) => {
    this.setState({})
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
  childCommentSubmit = () => {
    if(!this.state.comment) {
      return;
    }

    this.setState({
      submitting: true,
    });
    requestService.createIssue(this.state.title,this.state.description,this.state.deadline,2).then((response) => {
    this.setState({})
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
  handleChange = e => {
    this.setState({
      description: e.target.value,
    });
  };
  childComment = e => {
      this.setState({
        comment: e.target.value,
      });
    };
    sliceArray=(arr,start,end)=>{
    return arr.slice(start, end);
    }

    nextButton=()=>{
    const p=this.state.page;
    if(p*10+10<this.state.toDisplay.length)
    this.setState({page:this.state.page+1})
    }
    prevButton=()=>{
        const p=this.state.page;
        if(p>=1)
        this.setState({page:this.state.page-1})
        }
  render() {
    const { comments, submitting, value } = this.state;
    return (
      <>
        {this.state.toDisplay.length > 0 && <CommentList comments={this.sliceArray(this.state.toDisplay,this.state.page*10,this.state.page*10+10)} />}
        <Button onClick={this.nextButton}>
        NEXT
        </Button>
        <Button onClick={this.prevButton}>
        PREV
                </Button>
            <CreateIssue  user={this.state.user}  />
        </>
    );
  }
}

export default Issue;