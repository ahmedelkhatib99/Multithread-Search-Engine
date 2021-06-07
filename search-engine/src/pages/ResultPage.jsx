import React, { Component, useEffect, useState } from 'react';
import Result from '../components/Result';
import Suggestion from '../components/Suggestion';
import logo from '../assets/img/1280px-Node.js_logo.svg.png';
import axios from 'axios';
import { useHistory } from 'react-router-dom';

import '../assets/css/styles.css';
import '../assets/bootstrap/css/bootstrap.min.css';
import '../assets/fonts/fontawesome-all.min.css';
import '../assets/fonts/font-awesome.min.css';

function ResultPage(props) {
  const [query, setQuery] = useState([]);
  const history = useHistory();
  const [results, setResults] = useState([]);
  const [suggestions, setSuggestions] = useState([]);

  useEffect(() => {
    setQuery(props.match.params.query);
    axios({
      method: 'get',
      url: `http://localhost:8080/api/search?query=${props.match.params.query}`,
    })
      .then((res) => {
        console.log(res.data);
        setResults(res.data);
      })
      .catch((error) => {
        console.log(error);
      });
  }, []);

  const _handleKeyDown = (e) => {
    setQuery(e.target.value);
  };

  return (
    <>
      <div className='row'>
        <div
          className='col d-flex'
          style={{ padding: '0rem 2rem', backgroundColor: '#f8f8f8' }}
        >
          <div style={{ height: '8rem', padding: '2rem 2rem' }}>
            <img alt='logo' src={logo} style={{ height: '100%' }} />
          </div>

          <div className='d-xl-flex justify-content-xl-center align-items-xl-center'>
            <div
              className='d-xl-flex justify-content-xl-center align-items-xl-center'
              style={{ position: 'relative' }}
            >
              <form
                onSubmit={(e) => {
                  e.preventDefault();
                  setResults([]);
                  history.push({
                    pathname: `/search/${query}`,
                  });
                  window.location.reload();
                }}
              >
                <input
                  className='shadow-sm search-bar'
                  type='text'
                  style={{ width: '32rem' }}
                  value={query}
                  onChange={_handleKeyDown}
                />
              </form>
              <i className='fa fa-search search-icn'></i>
              <div className='suggest-list'>
                {suggestions.map((suggest) => {
                  return <Suggestion query={suggest.query} />;
                })}
              </div>
            </div>
          </div>
        </div>
      </div>
      <hr className='m-0' />
      <div className='container'>
        <div className='row'>
          <div className='col'>
            <h5 style={{ marginTop: '0.4rem', marginBottom: 0 }}>
              Found X Marching Entries
            </h5>
            <h6 style={{ color: '#747474' }}>(Showing 1 to 20 of 20)</h6>
          </div>
        </div>
        <div className='row'>
          <div className='col'>
            <hr className='mt-0' />
            {results.map((result) => {
              return (
                <Result result={result} query={[props.match.params.query]} />
              );
            })}
          </div>
        </div>
        <div className='row'>
          <div className='col d-xl-flex justify-content-xl-center'>
            <nav>
              <ul className='pagination'>
                <li className='page-item'>
                  <a className='page-link' href='#' aria-label='Previous'>
                    <span aria-hidden='true'>«</span>
                  </a>
                </li>
                <li className='page-item'>
                  <a className='page-link' href='#'>
                    1
                  </a>
                </li>
                <li className='page-item'>
                  <a className='page-link' href='#'>
                    2
                  </a>
                </li>
                <li className='page-item'>
                  <a className='page-link' href='#'>
                    3
                  </a>
                </li>
                <li className='page-item'>
                  <a className='page-link' href='#'>
                    4
                  </a>
                </li>
                <li className='page-item'>
                  <a className='page-link' href='#'>
                    5
                  </a>
                </li>
                <li className='page-item'>
                  <a className='page-link' href='#' aria-label='Next'>
                    <span aria-hidden='true'>»</span>
                  </a>
                </li>
              </ul>
            </nav>
          </div>
        </div>
      </div>
      <script src='assets/js/jquery.min.js'></script>
      <script src='assets/bootstrap/js/bootstrap.min.js'></script>
    </>
  );
}

export default ResultPage;
