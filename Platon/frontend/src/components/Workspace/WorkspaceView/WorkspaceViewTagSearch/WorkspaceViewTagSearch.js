import React, { useEffect } from "react";
import PropTypes from "prop-types";
import { makeStyles } from "@material-ui/core/styles";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemText from "@material-ui/core/ListItemText";
import DialogTitle from "@material-ui/core/DialogTitle";
import Dialog from "@material-ui/core/Dialog";
import { blue } from "@material-ui/core/colors";
import TrendingFlatIcon from '@material-ui/icons/TrendingFlat';
import colors from "../../../../utils/colors";
import ListItemSecondaryAction from "@material-ui/core/ListItemSecondaryAction";
import Spinner from "../../../Spinner/Spinner";
import config from "../../../../utils/config";
import axios from "axios";
const { BASE_URL } = config;
const SEARCH_URL = `${BASE_URL}/api/search_engine/tag_search`;
axios.defaults.headers.common["auth_token"] = localStorage.getItem("jwtToken");

const useStyles = makeStyles({
  avatar: {
    backgroundColor: blue[100],
    color: blue[600],
  },
});

function SimpleDialog(props) {
  const classes = useStyles();
  const { onClose, tag, open, tagColor } = props;


  return (
    <Dialog

    onClose={onClose}
      aria-labelledby="simple-dialog-title"
      open={open}
    >{props.loaded ? (
      <div>
      <DialogTitle
        style={{ backgroundColor: colors.secondary }}
        id="simple-dialog-title"
      >
        {`Workspaces with `}<span style={{ color: tagColor }}>{tag}</span>
      </DialogTitle>
      <List style={{ backgroundColor: colors.secondaryLight }}>
        {props.recommendations.map((workspace) => (
          <ListItem key={workspace}>
            <ListItemText primary={workspace.title} secondary={`Owner: ${workspace.creator_name} ${workspace.creator_surname}`}/>
            <ListItemSecondaryAction>
            <a
                href={`/${workspace.creator_id}/workspace/${workspace.id}`}
                style={{ textDecoration: "none" }}
              >
                  <TrendingFlatIcon style={{color: colors.septenaryDark }}/>
              </a>

            </ListItemSecondaryAction>

          </ListItem>
        ))}

      </List>
      </div>):<div style={{display: "flex", alignItems: "center"}}><div style={{margin:"30px 100px"}}><Spinner/></div></div>}
    </Dialog>
  );
}

SimpleDialog.propTypes = {
  onClose: PropTypes.func.isRequired,
  open: PropTypes.bool.isRequired,
};

export default function SimpleDialogDemo(props) {
  const [recommendations, setRecommendations] = React.useState([]);
  const [loaded, setLoaded] = React.useState(false);

  useEffect(() => {
    const tagArray=[]
    tagArray.push(props.tag)
    const options = {
      params: {
        search_type: 1,
        skills: JSON.stringify(tagArray),
        page: 0,
        per_page: 5
      },
    };
    if(props.tag === "") return
    axios
      .get(SEARCH_URL, options)
      .then((response) => {
        setRecommendations(response.data.result_list)
        setLoaded(true);
      })
      .catch((error) => {
        console.log(error);
      });
  }, [props.tag]);

  return (
    <div>
      <SimpleDialog
      style={{width: "600px"}}
        loaded={loaded}
        recommendations={recommendations}
        workspaceId={props.workspaceId}
        open={props.open}
        onClose={props.onClose}
        tag={props.tag}
        tagColor={props.tagColor}
      />
    </div>
  );
}
