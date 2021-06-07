import MainPage from './pages/MainPage';
import ResultPage from './pages/ResultPage';
import { BrowserRouter as Router, Switch, Route } from 'react-router-dom';

function App() {
  return (
    <Router>
      <Switch>
        <Route path='/search/:query' component={ResultPage}></Route>
        <Route path='/' component={MainPage}></Route>
      </Switch>
    </Router>
  );
}

export default App;
