import React, { Component } from 'react'
import Result from './Result.js'
import PropTypes from 'prop-types';

export class ResultList extends Component {
    render() {
        return this.props.results.map((result) => (
            <Result result={result}/>
        ));
    }
}

ResultList.propTypes = {
    results: PropTypes.array.isRequired
}

export default ResultList
