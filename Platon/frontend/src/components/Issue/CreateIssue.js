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

class CreateIssue extends React.Component {
    constructor(props)
    {
    super(props)}

  state = {
    comments: [],
    submitting: false,
    value: '',
    user:null,
    selectedMembers:[],
    members:[1,2,3],
  };
   pushMembers= ()=>{

      const members=[];
      for (let i = 0; i < this.props.members.length; i++) {
        members.push(<Option key={this.props.members[i].id}>{''+this.props.members[i].name+ ' '+this.props.members[i].surname}</Option>);
      }
      this.setState({members:members});
      }

  componentDidMount(){
  const token = localStorage.getItem("jwtToken");
  const decoded = jwt_decode(token);
  this.pushMembers();
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
    requestService.createIssue(this.state.title,this.state.description,this.state.deadline,this.props.workspaceId/*this.state.workspace_id*/).then((response) => {
        const length=this.state.selectedMembers.length;
        console.log(this.props)
        for(var i=0;i<length;i++)
        requestService.assignIssue(response.data.issue_id,this.props.workspaceId,this.state.selectedMembers[i])
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
  handleChange2=(value)=> {

    this.setState({selectedMembers:value})
  }
  render() {
    const { comments, submitting, value } = this.state;

    return (
      <>

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
            <DatePicker defaultValue={moment('2020/01/01', dateFormat)} format={dateFormat} onChange={this.datePick} />
             <Select
                                      mode="multiple"
                                      allowClear
                                      style={{ width: '100%' }}
                                      placeholder="Please select"
                                      defaultValue={[]}
                                      onChange={this.handleChange2}
                                    >
                                      {this.state.members}
                                    </Select>

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