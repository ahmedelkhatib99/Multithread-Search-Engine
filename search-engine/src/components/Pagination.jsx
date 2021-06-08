import React from 'react';

function Pagination(props) {
  return (
    <li className='page-item'>
      <a
        className='page-link'
        style={{
          backgroundColor: '#be0000',
          fontWeight: 800,
          color: '#ffffff',
        }}
        href={`/search/${props.query}/${props.number}`}
        aria-label='Previous'
      >
        <span aria-hidden='true'>{props.number}</span>
      </a>
    </li>
  );
}

export default Pagination;
