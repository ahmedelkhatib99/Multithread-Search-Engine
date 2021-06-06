import React, { Component } from 'react'
import PropTypes from 'prop-types';
import './Result.css'

export class Result extends Component {
    state={
        "title":"",
        "url":"",
        "snippet":""
    }

    componentDidMount(){
        this.setState({title:this.props.result.title});
        this.setState({url:this.props.result.url});
        this.setState({snippet:this.props.result.snippet});
    }

    render() {
        return (
            <div id="result-row-div" className="container-fluid">
                <div className="row">
                    <p>{this.state.url}</p>
                    <p><a href={this.state.url}>{this.state.title}</a></p>
                    <p>{this.state.snippet}</p>
                </div>
            </div>
        )
    }
}

Result.propTypes = {
    result: PropTypes.object.isRequired
}

export default Result
