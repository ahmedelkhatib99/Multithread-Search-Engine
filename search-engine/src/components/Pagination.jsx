import React from 'react';

function Pagination(props) {
  return (
    <li className='page-item'>
      <a
        className='page-link'
        href={`/search/${props.query}/${props.number}`}
        aria-label='Previous'
      >
        <span aria-hidden='true'>{props.number}</span>
      </a>
    </li>
  );
}

export default Pagination;
