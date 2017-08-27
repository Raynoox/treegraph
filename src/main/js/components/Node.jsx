import React from 'react';
import NodeContent from './NodeContent.jsx';
class Node extends React.Component {
    constructor(props) {
        super(props);
        this.state = {tree: [], root: {}};
    }
    render() {
        let me = this;
        return (
                <div className="node">
                    <NodeContent node={this.props.node}/>
                    <div className="childrenEvenly">
                        {this.props.node.children !== undefined ? (this.props.node.children.map((node, index) => {
                            return <Node key={index} node={node} tree={me.props.tree}></Node>;
                        })) : null}
                    </div>

                </div>
        );
    }
}

export default Node