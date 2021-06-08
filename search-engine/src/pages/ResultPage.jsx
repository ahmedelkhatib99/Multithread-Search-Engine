import React, { Component, useEffect, useState } from 'react';
import Result from '../components/Result';
import Suggestion from '../components/Suggestion';
import Pagination from '../components/Pagination';
import logo from '../assets/img/Asset 1.svg';
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
  const [count, setCount] = useState(0);
  const [page, setPage] = useState(1);
  const [pagesArray, setPageArray] = useState([]);
  const [suggestions, setSuggestions] = useState([]);
  const [loading, setLoading] = useState(true);
  const pageCount = 20;

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
    setQuery(props.match.params.query);
    setPage(props.match.params.page);
    setSuggestions([]);
    axios({
      method: 'get',
      url: `http://localhost:8080/api/search?query=${props.match.params.query}&page=${props.match.params.page}`,
    })
      .then((res) => {
        console.log(res.data);
        setResults(res.data.Results);
        setCount(res.data.Count);
        setLoading(false);
      })
      .catch((error) => {
        console.log(error);
      });
  }, []);

  useEffect(() => {
    getPagination();
  }, [count]);

  const getPagination = () => {
    var pageArray = [
      <Pagination number={1} query={[props.match.params.query]} />,
    ];
    for (var i = 2; i <= (parseInt(count) + pageCount - 1) / pageCount; i++) {
      console.log(parseInt(count) + pageCount - 1);
      pageArray.push(
        <Pagination number={i} query={[props.match.params.query]} />
      );
    }
    setPageArray(pageArray);
  };

  useEffect(() => {
    setSuggestions([]);
    if (query && query.length >= 2) {
      axios({
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
    <>
      <div>
        <div className='row'>
          <div
            className='col d-flex'
            style={{ padding: '0rem 2rem', backgroundColor: '#f8f8f8' }}
          >
            <div
              style={{
                height: '7rem',
                padding: '2rem 2rem',
                cursor: 'pointer',
              }}
              onClick={(e) => {
                e.preventDefault();
                history.push({
                  pathname: `/`,
                });
              }}
            >
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
                    if (query.length > 0) {
                      history.push({
                        pathname: `/search/${query}/1`,
                      });
                      window.location.reload();
                    }
                  }}
                >
                  <input
                    className='shadow-sm search-bar'
                    type='text'
                    style={{ width: '32rem' }}
                    value={query}
                    onChange={handleChange}
                  />
                </form>
                <i className='fa fa-search search-icn'></i>
                <div className='suggest-list'>
                  {suggestions.map((suggest) => {
                    return <Suggestion query={suggest.searchQueryText} />;
                  })}
                </div>
              </div>
            </div>
          </div>
        </div>
        <hr className='m-0' />
        {loading ? (
          <h1>Loading</h1>
        ) : (
          <div className='container'>
            <div className='row'>
              <div className='col'>
                <h5 style={{ marginTop: '0.4rem', marginBottom: 0 }}>
                  {`Found ${count} Matching Entries`}
                </h5>
                <h6 style={{ color: '#747474' }}>{`(Showing ${
                  count > 0 && (page - 1) * pageCount + 1 <= count
                    ? (page - 1) * pageCount + 1
                    : 0
                } to ${
                  count - (page - 1) * pageCount > pageCount
                    ? page * pageCount
                    : count
                } of ${count})`}</h6>
              </div>
            </div>
            <div className='row'>
              <div className='col'>
                <hr className='mt-0' />
                {!loading &&
                  results.map((result) => {
                    return (
                      <Result
                        result={result}
                        query={props.match.params.query.split(' ')}
                      />
                    );
                  })}
              </div>
            </div>
            <div className='row'>
              <div className='col d-xl-flex justify-content-xl-center'>
                <nav>
                  <ul className='pagination'>{pagesArray}</ul>
                </nav>
              </div>
            </div>
          </div>
        )}
      </div>
    </>
  );
}

export default ResultPage;
