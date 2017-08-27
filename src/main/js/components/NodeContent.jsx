import React from 'react'
import { ContextMenuTrigger } from "react-contextmenu";
class NodeContent extends React.Component {
    constructor(props) {
        super(props);
    }
    componentDidUpdate() {
        this.refs.input.value = this.props.node.value;
    }
    resetValue() {
        this.refs.input.value = this.props.node.value;
        this.props.requestChangeValue();
    }
    updateValue() {
        this.props.requestChangeValue(this.refs.input.value);
    }
    render() {
        let clazz = "circleNode node_"+this.props.node.id;
        clazz += this.props.changeParentId === this.props.node.id ? " yellow" : this.props.changeParentId ? " red" : "";
        let onClickCallback = this.props.changeParentId ? this.props.changeParentCallback.bind(null, this.props.node.id) : null;
        return (
                <div className={clazz} onClick={onClickCallback}>
                    <ContextMenuTrigger id={"node_"+this.props.node.id}>
                        <input
                            ref="input"
                            type="number"
                            className={this.props.canChangeValue ? "nodeInput": "nodeInput disabled"}
                            readOnly={!this.props.canChangeValue}
                            defaultValue={this.props.node.value}
                            />
                        {this.renderValueButtons()}
                        {this.renderTotal()}
                    </ContextMenuTrigger>
                </div>
        );
    }
    renderValueButtons() {
        return this.props.canChangeValue ? (<div>
            <button onClick={this.updateValue.bind(this)}>save</button>
            <button onClick={this.resetValue.bind(this)}>cancel</button>
        </div>) : null
    }
    renderTotal() {
        return this.props.node.children.length === 0?
            (<text className="totalNumber">Total = {this.props.total}</text>)
            : null;
    }
}

export default NodeContent;