
import React, { createElement } from "react";
import { Comment, Tooltip } from "antd";
import Avatar from "@material-ui/core/Avatar";
import moment from "moment";
import {
  DislikeOutlined,
  LikeOutlined,
  DislikeFilled,
  LikeFilled,
} from "@ant-design/icons";
import colors from "../../utils/colors";
import Box from "@material-ui/core/Box";
import './commentStyle.css'
class Commentt extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      liked: false,
      disliked: false,
      likes: 0,
      dislikes: 0,
      action: "",
      props: props,
    };
  }

  setLikes = (value) => {
    this.setState({ likes: this.state.likes + value });
  };
  setDislikes = (value) => {
    this.setState({ dislikes: this.state.dislikes + value });
  };
  setAction = (value) => {
    this.setState({ action: value });
  };

  like = () => {
    if (!this.state.liked && !this.state.disliked) {
      this.setLikes(1);
      this.setDislikes(0);
      this.setLiked();
      this.setAction("liked");
    } else if (!this.state.liked && this.state.disliked) {
      this.setLikes(1);
      this.setLiked();
      this.setDislikes(-1);
      this.setAction("liked");
    }
  };
  setLiked = () => {
    this.setState({ liked: true, disliked: false });
  };
  setDisliked = () => {
    this.setState({ liked: false, disliked: true });
  };

  dislike = () => {
    if (!this.state.liked && !this.state.disliked) {
      this.setLikes(0);
      this.setDisliked();
      this.setDislikes(1);
      this.setAction("disliked");
    } else if (this.state.liked && !this.state.disliked) {
      this.setLikes(-1);
      this.setDisliked();
      this.setDislikes(1);
      this.setAction("disliked");
    }
  };

  render() {
    const actions = [
      <Box
        justifyContent="flex-start"
        display="flex"
        flexDirection="row"
        css={{ width: 250 }}
      >
        <Box>
          <Tooltip key="comment-basic-like" title="Like">
            <span onClick={this.like}>
              {createElement(
                this.state.action === "liked" ? LikeFilled : LikeOutlined
              )}
              <span className="comment-action">{this.state.likes}</span>
            </span>
          </Tooltip>
        </Box>
        <Box>
          <Tooltip key="comment-basic-dislike" title="Dislike">
            <span onClick={this.dislike}>
              {React.createElement(
                this.state.action === "disliked"
                  ? DislikeFilled
                  : DislikeOutlined
              )}
              <span className="comment-action">{this.state.dislikes}</span>
            </span>
          </Tooltip>
        </Box>
      </Box>,
    ];

    return (
      <div style={{ display: "flex", alignItems: "center"}}>
        <Comment

        avatar={
        <>
              <Avatar
               src={this.props.avatar}
              />
              <p>{this.props.author}</p>
              </>
            }
          content={this.props.title}
          style={this.props.style?this.props.style:{ color: colors.quinary, marginLeft: "10px"}}

          className="comment"
        />
      </div>
    );
  }
}
export default Commentt;
