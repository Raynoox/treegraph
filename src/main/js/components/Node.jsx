import React from 'react';
import NodeContent from './NodeContent.jsx';
class Node extends React.Component {
    constructor(props) {
        super(props);
        this.state = {tree: [], root: {}};
    }

    render() {
        return (
            <div className="node">
                <NodeContent
                    node={this.props.node}
                    changeParentCallback={this.props.changeParentCallback}
                    changeParentId={this.props.changeParentId}
                    canChangeValue={this.props.changeValueId === this.props.node.id}
                    total={this.props.total}
                    requestChangeValue={this.props.requestChangeValue}/>
                {this.renderChildren()}
            </div>
        );
    }
    renderChildren() {
        let me = this;
        return (
            <div className="childrenEvenly">
                {this.props.node.children !== undefined ? (this.props.node.children.map((node, index) => {
                        return <Node
                            key={index}
                            node={node}
                            tree={me.props.tree}
                            changeParentCallback = {me.props.changeParentCallback}
                            changeParentId = {me.props.changeParentId}
                            changeValueId={me.props.changeValueId}
                            requestChangeValue={me.props.requestChangeValue}
                            total={me.props.total+node.value}/>;
                    })) : null}
            </div>
        );
    }
}

export default Node