import React from 'react'
import rest from 'rest'

class Tree extends React.Component {

    constructor(props) {
        super(props);
        this.state = {employees: []};
    }

    componentDidMount() {
        rest('/api/nodes').then(function (response) {
            this.setState({nodes: response.entity})
        });
    }

    render() {
        return (
            <div>
                Tree
            </div>
        );
    }
}

export default Tree