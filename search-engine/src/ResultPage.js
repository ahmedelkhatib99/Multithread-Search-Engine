import React, { Component } from 'react';
import ResultList from './ResultList';
import axios from 'axios';
import main_image from './main.JPG';

import './ResultPage.css';

export class MainPage extends Component {
  state = {
    value: this.props.searchText,
    loading: true,
    results: [],
  };

  componentDidMount() {
    this.setState({ results: [] });
    this.getResults();
  }

  _handleKeyDown = (e) => {
    if (e.key === 'Enter') {
      this.setState({ value: e.target.value });
      this.setState({ results: [] });
      this.getResults();
      console.log(this.state.value);
    }
  };

  onInputchange = (e) => {
    this.setState({ value: e.target.value });
  };

  getResults() {
    //set results array in this function
    this.setState({ loading: true });
    axios
      .get('http://localhost:8080/api/search', {
        params: { query: this.state.value },
      })
      .then((res) => {
        console.log(res.data);
        this.setState({ results: res.data });
      })
      .catch((error) => {
        console.log(error);
      });
    this.setState({ value: '' });
    this.setState({ loading: false });
  }

  render() {
    return (
      <>
        {this.state.loading ? (
          <h1>loading</h1>
        ) : (
          <div className='container-fluid'>
            <div id='search-row-div' className='row'>
              <input
                id='search-bar'
                type='text'
                value={this.state.value}
                onChange={this.onInputchange}
                placeholder='Search'
                aria-label='Search'
                onKeyDown={this._handleKeyDown}
              ></input>
            </div>
            <div id='result-list-div' className='row'>
              <hr></hr>
              <div class='loader' hidden={!this.state.loading}>
                Loading...
              </div>
              <ResultList results={this.state.results} />
            </div>
          </div>
        )}
      </>
    );
  }
}

export default MainPage;
