import React, { Component } from 'react';
import PropTypes from 'prop-types';

export class Result extends Component {
  render() {
    return (
      <div className='d-flex justify-content-between align-items-center suggest-item'>
        <h5 style={{ margin: '0.25rem', fontSize: '1.2rem' }}>
          {this.props.query}
        </h5>
        <i
          className='fa fa-arrow-right'
          style={{ margin: '.35rem', color: '#5b5b5b' }}
        ></i>
      </div>
    );
  }
}

Result.propTypes = {
  result: PropTypes.object.isRequired,
};

export default Result;
