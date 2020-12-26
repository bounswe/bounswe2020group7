import React from "react";
import "./Search.css";

import requestService from "../../services/requestService";
import NavBar from "../NavBar/NavBar";
import Spinner from "../Spinner/Spinner";

import { Container, Col, Row, Button, Card } from "react-bootstrap";
import { Link } from "react-router-dom";

class Search extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      searchResult: [],
      isLoading: true,
    };
  }

  componentDidMount() {
    const searchQuery = this.props.match.params.searchQuery;
    Promise.all([
      requestService.getSearchUser(searchQuery).then((response) => {
        this.setState({
          searchResult: response.data.result_list,
        });
      }),
    ]).then(() => {
      this.setState({ isLoading: false });
      console.log(this.state.searchResult);
    });
  }

  render() {
    const items = [];

    for (const [index, value] of this.state.searchResult.entries()) {
      items.push(
        <Link to={`/`+ value.id}>
        <Row className="mb-3 UserCardToSearch">
          <Col sm={2}>
            <img
              className="ProfilePhoto"
              src={
                "http://18.185.75.161:5000/api" + value.profile_photo
              }
              alt="UserImage"
            />
          </Col>
          <Col sm={6}>
            <p className="GeneralMediumFont">
              {value.name} {value.surname}
            </p>
          </Col>
        </Row>
        </Link>
      );
    }

    return (
      <div className="SearchLanding">
        <div className="AppBar">
          <NavBar />
        </div>
        {this.state.isLoading ? (
          <div className="ProfilePageSpinner">
            <Spinner />
          </div>
        ) : (
          <div>
            <Container className="SearchContainer">
                {items}
            </Container>
          </div>
        )}
      </div>
    );
  }
}

export default Search;
