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

  function GetSortOrder(prop) {
    return function (a, b) {
      if (a[prop] < b[prop]) {
        return 1;
      } else if (a[prop] > b[prop]) {
        return -1;
      }
      return 0;
    };
  }

  useEffect(() => {
    setSuggestions([]);
    if (query && query.length >= 2) {
      Axios({
        method: 'get',
        url: `http://localhost:8080/api/suggest?query=${query}`,
      })
        .then((res) => {
          console.log(query);
          setSuggestions(res.data.sort(GetSortOrder('frequency')).slice(0, 9));
        })
        .catch((err) => {
          console.log(err);
        });
    }
  }, [query]);

  const handleChange = (e) => {
    setQuery(e.target.value);
  };

  return (
    <div className='container'>
      <div className='row'>
        <div className='col vh-100'>
          <form
            onSubmit={(e) => {
              e.preventDefault();
              if (query.length > 0) {
                history.push({
                  pathname: `/search/${query}/1`,
                });
                window.location.reload();
              }
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
                    onChange={handleChange}
                  />
                  <i className='fa fa-search search-icn'></i>
                  <div className='suggest-list'>
                    {suggestions.map((suggest) => {
                      console.log(query);
                      return <Suggestion query={suggest.searchQueryText} />;
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
