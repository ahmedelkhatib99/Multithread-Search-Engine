import React from 'react';
import PropTypes from 'prop-types';
import { useHistory } from 'react-router-dom';

function Suggestion(props) {
  const history = useHistory();
  return (
    <div
      className='d-flex justify-content-between align-items-center suggest-item'
      style={{ cursor: 'pointer' }}
      onClick={(e) => {
        e.preventDefault();
        history.push({
          pathname: `/search/${props.query}`,
        });
        window.location.reload();
      }}
    >
      <h5 style={{ margin: '0.25rem', fontSize: '1.2rem' }}>{props.query}</h5>
      <i
        className='fa fa-arrow-right'
        style={{ margin: '.35rem', color: '#5b5b5b' }}
      ></i>
    </div>
  );
}

Suggestion.propTypes = {
  result: PropTypes.object.isRequired,
};

export default Suggestion;
