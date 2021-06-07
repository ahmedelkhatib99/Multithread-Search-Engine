import React, { useState, useEffect } from 'react';
import Axios from 'axios';
import logo from '../assets/img/1280px-Node.js_logo.svg.png';
import Suggestion from '../components/Suggestion';
import { useHistory } from 'react-router-dom';

import '../assets/css/styles.css';
import '../assets/bootstrap/css/bootstrap.min.css';
import '../assets/fonts/fontawesome-all.min.css';
import '../assets/fonts/font-awesome.min.css';

function MainPage() {
  const [suggestions, setSuggestions] = useState([]);
  const [query, setQuery] = useState([]);
  const history = useHistory();

  // useEffect(() => {
  //   Axios({
  //     method: 'get',
  //     url: `${process.env.REACT_APP_API_URL}/api/v1/submission/${props.match.params.assId}`,
  //     headers: {
  //       authorization: token,
  //     },
  //   })
  //     .then((res) => {
  //       history.push('/assignments');
  //     })
  //     .catch((err) => {
  //       //console.log(err);
  //     });
  // }, []);

  const _handleKeyDown = (e) => {
    setQuery(e.target.value);
  };

  return (
    <div className='container'>
      <div className='row'>
        <div className='col vh-100'>
          <form
            onSubmit={(e) => {
              e.preventDefault();
              history.push({
                pathname: `/search/${query}`,
              });
            }}
          >
            <div className='d-flex flex-column justify-content-center vh-100'>
              <div
                className='d-flex justify-content-center w-100'
                style={{ padding: '1rem' }}
              >
                <img className='w-50' alt='logo' src={logo} />
              </div>
              <div className='d-flex d-xl-flex justify-content-center align-items-center justify-content-xl-center align-items-xl-center'>
                <div
                  className='d-flex d-xl-flex justify-content-center align-items-center justify-content-xl-center align-items-xl-center w-50'
                  style={{ position: 'relative' }}
                >
                  <input
                    className='shadow-lg w-100 search-bar'
                    type='text'
                    style={{
                      border: 'solid',
                      borderWidth: 'thin',
                      borderColor: '#a7a7a7',
                    }}
                    onKeyDown={_handleKeyDown}
                  />
                  <i className='fa fa-search search-icn'></i>
                  <div className='suggest-list'>
                    {suggestions.map((suggest) => {
                      return <Suggestion query={suggest.query} />;
                    })}
                  </div>
                </div>
              </div>
              <div
                className='d-flex d-xl-flex justify-content-center justify-content-xl-center'
                style={{ padding: '0.5rem' }}
              >
                <button className='btn btn-light btn-lg' type='submit'>
                  Search&nbsp;<i className='fas fa-arrow-circle-right'></i>
                </button>
              </div>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
export default MainPage;
