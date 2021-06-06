import React, { Component } from 'react'
import ResultList from './ResultList'
import axios from 'axios'
import main_image from './main.JPG'

import './ResultPage.css'

export class MainPage extends Component {
    state={
        "value": '',
        "loading": true,
        "results":[{
           "url": "https://www.google.com.eg/",
           "title": "Google",
           "snippet": "search engine"
        },
        {
            "url": "https://www.facebook.com/",
            "title": "Facebook",
            "snippet": "online social media"
        }]
    }

    componentDidMount(){
        this.setState({value: this.props.searchText})
        this.getResults();
    }

    _handleKeyDown = (e) => {
        if (e.key === 'Enter') {
            this.setState({value: e.target.value})
            this.getResults();
        }
    }

    onInputchange = (e) => {
        this.setState({value: e.target.value});
    }

    getResults(){
        //set results array in this function 
        this.setState({loading: true});
        axios.get('http://localhost:3000/')
        .then(res => {
            
        }
        )
        .catch(error => {
            alert(error.response.data.message);
        })
        //this.setState({results:})
        this.setState({value:""})
        this.setState({loading: false});
    }

    render() {
        return (
            <div className="container-fluid">
                <div id="search-row-div" className="row">
                    <input id="search-bar" type="text" value={this.state.value} onChange={this.onInputchange} placeholder="Search" aria-label="Search" onKeyDown={this._handleKeyDown}></input>
                </div>
                <div id="result-list-div" className="row">
                    <hr></hr>
                    <div class="loader" hidden={!this.state.loading}>Loading...</div>
                    <ResultList results={this.state.results}/>
                </div>
            </div>
        )
    }
}

export default MainPage
