import React, { Component } from 'react';
import axios from 'axios';
import main_image from './main.JPG';
import ResultPage from './ResultPage';

import './MainPage.css';

export class MainPage extends Component {
  state = {
    value: '',
  };

  componentDidMount() {}

  _handleKeyDown = (e) => {
    if (e.key === 'Enter') {
      this.setState({
        redirect: true,
        value: e.target.value,
      });
    }
  };

  render() {
    if (this.state.redirect) {
      return <ResultPage searchText={this.state.value} />;
    }
    return (
      <div id='main-page-div' className='container-fluid'>
        <div id='main-row-div' className='row'></div>
        <div id='main-row-div' className='row'>
          <div className='row'>
            <img src={main_image} alt='main pic' />
          </div>
          <div className='row'>
            <input
              id='search-bar'
              type='text'
              placeholder='Search'
              aria-label='Search'
              onKeyDown={this._handleKeyDown}
            ></input>
          </div>
        </div>
        <div id='main-row-div' className='row'></div>
      </div>
    );
  }
}

export default MainPage;
