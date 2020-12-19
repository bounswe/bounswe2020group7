import React, { Component } from "react";
import { makeStyles } from "@material-ui/core/styles";
import {withStyles} from '@material-ui/core/styles';

import TreeView from "@material-ui/lab/TreeView";
import ExpandMoreIcon from "@material-ui/icons/ExpandMore";
import ChevronRightIcon from "@material-ui/icons/ChevronRight";
import TreeItem from "@material-ui/lab/TreeItem";
import colors from "../../../../utils/colors";


const StyledTreeItem = withStyles({
  root: {
     "&.Mui-selected:focus > .MuiTreeItem-content .MuiTreeItem-label": {
      backgroundColor: colors.tertiaryDark
    }

  },
})(TreeItem);

const data = {
  1: {
    id: "1",
    name: "Files",
    children: [
    ],
  },
  2: {
    id: "2",
    name: "Images",
    children: [
    ],
  },
};
const renderTree = (nodes) => {
  return (
    <StyledTreeItem   key={nodes.id} nodeId={nodes.id} label={nodes.name}>
      {Array.isArray(nodes.children)
        ? nodes.children.map((node) => renderTree(node))
        : null}
    </StyledTreeItem>
  );
};
class WorkspaceViewFileSection extends Component {
  constructor(props) {
    super(props);
    this.state = {
      expanded: [],
      selected: [],
      fileStructure: data,
      folderName: "",
      fileName: "",
    };
  }

  handleToggle = (event, nodeIds) => {
    this.setState({ expanded: [...this.state.expanded, nodeIds] });
  };
  handleSelect = (event, nodeIds) => {
    this.setState({ selected: [nodeIds] });
  };
  createFolder = () => {
    //TODO dont create a directory with same name exists already
    if (this.state.folderName !== "") {
      for(var key in this.state.fileStructure){
        if(this.state.folderName === this.state.fileStructure[key].name){
          console.log("File name exists!");
          return;
        }
      }
      let size = Object.keys(this.state.fileStructure).length + 1;
      this.setState({
        fileStructure: {...this.state.fileStructure,
            [`${size}`]: { id: `${size}`, name: this.state.folderName, children: []},
           },
      });
    }
  };

  createFile = (selected) => {
    //TODO dont create a directory with same name exists already
    if (this.state.fileName !== "") {
      for(var key in this.state.fileStructure.children){
        if(this.state.fileName === this.state.fileStructure[key].name){
          console.log("File name exists!");
          return;
        }
      }
    }
    if(selected.length===0){
      console.log("klasör seçmedin dostum");
      return;
    }
    if(!(selected[0] in this.state.fileStructure)){
      console.log("Filea file eklenmez");
      return;
    }
    let size = this.state.fileStructure[selected[0]].children.length + 1
    let fileObjId = selected[0] + "-" + size
    let fileObj = {id: fileObjId, name: this.state.fileName, children:[]}
    let prevState = this.state.fileStructure;
    prevState[selected[0]].children.push(fileObj)
    this.setState({
      fileStructure: prevState
    })

  };
  render() {
    return (
      <div>
        <TreeView
          defaultCollapseIcon={<ExpandMoreIcon />}
          defaultExpandIcon={<ChevronRightIcon />}

          onNodeToggle={this.handleToggle}
          onNodeSelect={this.handleSelect}
          style={{ color: colors.secondary }}
        >
          {Object.keys(this.state.fileStructure).map((index)=> renderTree(this.state.fileStructure[index]))}
        </TreeView>

        <input
          onChange={(e) => this.setState({ folderName: e.target.value })}
          name="folderName"
          label="FolderName"
        />
        <button onClick={this.createFolder}>Create folder</button>
        <input
        type="file"
          onChange={(e) => this.setState({ fileName: e.target.value })}
          name="fileName"
          label="fileName"
        />
        <button onClick={() => this.createFile(this.state.selected)}>Create File</button>
      </div>
    );
  }
}

export default WorkspaceViewFileSection;
