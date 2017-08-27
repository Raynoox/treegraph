import React from 'react'
import { ContextMenuTrigger } from "react-contextmenu";
class NodeContent extends React.Component {
    constructor(props) {
        super(props);
        // this.state = {tree: [], root: {}};
    }
    render() {
        let me = this;
        let clazz = "circleNode node_"+this.props.node.id;
        return (
                <div className={clazz}>
                    <ContextMenuTrigger id={"node_"+this.props.node.id}>
                            id={this.props.node.id}
                            <br/>
                            value={this.props.node.value}
                    </ContextMenuTrigger>
                </div>

        );
    }
}

export default NodeContent;