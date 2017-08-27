import React from 'react';
import rest from 'rest';
import Node from './Node.jsx';
import LineTo from 'react-lineto';
import { ContextMenu, MenuItem} from "react-contextmenu";
import {request} from './restClient';
import Alert from 'react-s-alert';

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
        window.addEventListener("resize", this.updateComponent.bind(this));
    }
    updateComponent() {
        this.setState({});
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
        request('DELETE','/api/nodes/'+node.id,this.fetchNodes.bind(this),this.showError.bind(this));
    }
    addNode(e, node) {
        console.log("adding node to "+node.id+ " NOT IMPLEMENTED");
        request('POST','/api/nodes',this.fetchNodes.bind(this),this.showError.bind(this), {parentId: node.id, value: 0});
     }
    fetchNodes(){
        const me = this;
        request('GET','/api/nodes',function(data){me.createTreeTopology(JSON.parse(data.entity))})
    }
    showError(response) {
        console.log(response);
        let message = JSON.parse(response.entity).message;
        return Alert.error("Error "+response.status.code.toString()+" "+message,{
            position: 'top-right',
            effect:'slide',
            timeout: 5000
        });
    }
    changeNodeParent(e, node) {
        this.setState({
            toPickId: node.id,
            changeValueId: null
        });
    }
    requestChangeParent(newParentId) {
        request('PATCH','/api/nodes/'+this.state.toPickId,this.fetchNodes.bind(this),this.showError.bind(this), {parentId: newParentId});
        this.setState({
            toPickId: null
        });
    }
    changeNodeValue(e, node) {
        this.setState({
            changeValueId: node.id,
            toPickId: null
        });
    }
    submitValueChange(newValue) {
        if(!!newValue) {
            request('PATCH', '/api/nodes/' + this.state.changeValueId, this.fetchNodes.bind(this), this.showError.bind(this), {value: newValue})
        }
        this.setState({
            changeValueId: null
        });
    }
    render() {
        return (
            <div>
                {this.state.root.id !== undefined ?
                    (<div>
                        <Node
                            node = {this.state.root}
                            tree = {this.state.tree}
                            changeParentCallback = {this.requestChangeParent.bind(this)}
                            changeParentId = {this.state.toPickId}
                            requestChangeValue = {this.submitValueChange.bind(this)}
                            changeValueId = {this.state.changeValueId}
                            total = {this.state.root.value}/>
                            {this.renderLines()}
                            {this.renderMenus()}
                    </div>): null}


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
                        to={"node_"+node.parentId}
                        delay={1}/>
                ): null})}
        </div>
    }
    renderMenus() {
        const me = this;
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
