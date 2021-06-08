import React, { Component } from 'react';
import PropTypes from 'prop-types';

export class Result extends Component {
  makeBold(snippet, query) {
    return {
      __html: snippet.replace(
        new RegExp('(\\b)(' + query.join('|') + ')(\\b)', 'ig'),
        '$1<strong>$2</strong>$3'
      ),
    };
  }

  render() {
    return (
      <div className='card shadow' style={{ marginBottom: '0.7rem' }}>
        <div className='card-header' style={{ padding: '0.4rem 1rem' }}>
          <h4 style={{ margin: 0, color: '#545454' }}>
            <i
              className='fa fa-globe'
              style={{
                margin: '0.2rem',
                color: '#969696',
                paddingRight: '0.3rem',
              }}
            ></i>
            {this.props.result.title}
          </h4>
        </div>
        <div className='card-body' style={{ padding: '0.4rem 1rem' }}>
          <p className='card-text'>
            <div
              dangerouslySetInnerHTML={this.makeBold(
                this.props.result.snippet,
                this.props.query
              )}
            />
          </p>
        </div>
        <div
          className='card-footer'
          style={{ padding: '0.4rem 1rem', backgroundColor: '#fff' }}
        >
          <a href={this.props.result.url}>
            <i className='fa fa-link' style={{ paddingRight: '0.3rem' }}></i>
            {this.props.result.url}
          </a>
        </div>
      </div>
    );
  }
}

Result.propTypes = {
  result: PropTypes.object.isRequired,
};

export default Result;
