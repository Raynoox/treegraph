import React from 'react';
import rest from 'rest';
import Node from './Node.jsx';
import LineTo from 'react-lineto';
// import ContextMenu from 'react-context-menu';
import { ContextMenu, MenuItem, ContextMenuTrigger } from "react-contextmenu";
import {request} from './restClient';
class Tree extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            tree: [],
            root: {},
            linesToUpdate: false
        };
    }

    componentDidMount() {
        let me = this;
        rest('/api/nodes').then(function (response) {
            me.createTreeTopology(JSON.parse(response.entity));
        });
        window.addEventListener("resize", this.setState.bind(this,{}));
    }
    createTreeTopology(nodes) {
        let nodesById = [];
        nodes.forEach(item => nodesById[item.id] = {
            id: item.id,
            value: item.value,
            children: [],
            parentId: item.parentId
        });

        nodesById.forEach(item => {
            if (item.parentId !== null) {
                nodesById[item.parentId].children.push(item);
            }
        });
        this.setState({tree: nodesById, root: nodesById.find(node => node !== undefined && node.parentId === null)});
    }
    removeNode(e, node) {
        request('DELETE','/api/nodes/'+node.id,this.fetchNodes.bind(this),console.log.bind(null, 'alertify'));
    }
    addNode(e, node) {
        console.log("adding node to "+node.id+ " NOT IMPLEMENTED");
        request('POST','/api/nodes',this.fetchNodes.bind(this),console.log.bind(null, 'alertify'), {parentId: node.id, value: 0});
     }
    fetchNodes(){
        var me = this;
        request('GET','/api/nodes',function(data){me.createTreeTopology(JSON.parse(data.entity))})
    }
    changeNodeParent(e, node) {
        console.log("changing parent of node "+node.id+ " NOT IMPLEMENTED");
    }
    changeNodeValue(e, node) {
        console.log("changing  node value "+node.id+ " NOT IMPLEMENTED");
    }
    render() {
        return (
            <div>
                <Node
                    node = {this.state.root}
                    tree = {this.state.tree}
                />
                {this.renderLines()}
                {this.renderMenus()}
            </div>
        );
    }
    renderLines() {
        return <div>
            {this.state.tree.map((node, index)=>{
                return node !== undefined ? (
                    <LineTo
                        key={"line_"+index}
                        className="line"
                        from={"node_"+node.id}
                        to={"node_"+node.parentId}/>
                ): null})}
        </div>
    }
    renderMenus() {
        var me = this;
        return <div>
            {this.state.tree.map((node, index)=>{
                return node !== undefined ? (
                    <ContextMenu key={"menu"+index} id={"node_"+node.id}>
                        <MenuItem data={node} onClick={me.removeNode.bind(me)}>
                            Remove
                        </MenuItem>
                        <MenuItem data={node} onClick={me.addNode.bind(me)}>
                            Add node
                        </MenuItem>
                        <MenuItem data={node} onClick={me.changeNodeParent.bind(me)}>
                            Change parent
                        </MenuItem>
                        <MenuItem data={node} onClick={me.changeNodeValue.bind(me)}>
                            Change value
                        </MenuItem>

                    </ContextMenu>): null})}
        </div>
    }
}

export default Tree
