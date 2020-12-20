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

const CommentList = ({ comments }) => (
 <List
     itemLayout="horizontal"
     dataSource={comments}
     renderItem={item => (
       <List.Item>
         <List.Item.Meta

         /> <Commentt
                      avatar={item.profile_URL}
                      author={item.title}
                      message={<p>{item.description}</p>}
                    />
       </List.Item>

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
                 };
    }

  componentDidMount(){

  const token = localStorage.getItem("jwtToken");
  const decoded = jwt_decode(token);
    //Get workspace members
    requestService.getIssues()
    // Get Workspace issues


    this.setState({children:children})


    requestService.getUser(decoded.id).then((response) => {
      this.setState({
        user: response.data,
      });
    })
      }


    datePick = (value) =>{
    this.setState({deadline:value})

    }
  handleSubmit = () => {
    if (!this.state.description) {
      return;
    }

    this.setState({
      submitting: true,
    });
    requestService.createIssue(this.state.title,this.state.description,this.state.deadline,this.state.workspace_id).then((response) => {
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


  render() {
    const { comments, submitting, value } = this.state;

    return (
      <>
        {comments.length > 0 && <CommentList comments={comments} />}
        <Comment
          avatar={
            <Avatar
              src={this.state.user?this.state.user.profile_photo:"https://zos.alipayobjects.com/rmsportal/ODTLcjxAfvqbxHnVXCYX.png"}
              alt="Han Solo"
            />
          }
          content={
          <div>
            <StyledTextField
                            margin="normal"
                            name="title"
                            variant="outlined"
                            required
                            fullWidth
                            id="title"
                            label="Title"
                            autoFocus
                            value={this.state.title}
                            onChange={(e) => this.setState({ title: e.target.value })}
                          />
            <DatePicker defaultValue={moment('2015/01/01', dateFormat)} format={dateFormat} onChange={this.datePick} />
            <Editor
              onChange={this.handleChange}
              onSubmit={this.handleSubmit}
              submitting={submitting}
              value={this.state.description}
            />
            </div>
          }
        />
        </>

    );
  }
}

export default CreateIssue;