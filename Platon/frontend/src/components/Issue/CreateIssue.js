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
  state = {
    comments: [],
    submitting: false,
    value: '',
    user:null,
    selectedMembers:[]
  };
  componentDidMount(){

  const token = localStorage.getItem("jwtToken");
  const decoded = jwt_decode(token);
  // get members
    const children = [];
    for (let i = 0; i < 6; i++) {
      children.push(<Option key={i}>{i}</Option>);
    }

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

        for(int i=0;i<this.state.selectedMembers.length;i++)
        requestService.assignIssue(null,null,this.state.selectedMembers(i))
       })
  };

  handleChange = e => {
    this.setState({
      description: e.target.value,
    });
  };
  handleChange2=(value)=> {
    var mems=this.state.selectedMembers
    mems.append(value)
    var unique = mems.filter((v, i, a) => a.indexOf(v) === i);
    this.setState({selectedMembers:unique})
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
            <Editor
              onChange={this.handleChange}
              onSubmit={this.handleSubmit}
              submitting={submitting}
              value={this.state.description}
            />
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
                  </>
            </div>
          }
        />
      </>
    );
  }
}

export default CreateIssue;