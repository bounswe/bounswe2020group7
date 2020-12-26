import React from "react";
import "./Search.css";

import requestService from "../../services/requestService";
import NavBar from "../NavBar/NavBar";
import Spinner from "../Spinner/Spinner";

import { Container, Col, Row, Button, Card } from "react-bootstrap";
import { Link } from "react-router-dom";
import Avatar from "@material-ui/core/Avatar";

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
    });
  }

  render() {

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
            <Container className="SearchContainer pb-4">
              {this.state.searchResult.map((value, index) => {
                return (
                  <Link to={`/` + value.id}>
                    <Row className="mb-3 UserCardToSearch">
                      <Col sm={2}>
                        <Avatar
                          src={
                            "http://18.185.75.161:5000/api" +
                            value.profile_photo
                          }
                          className="SearchAvatar"
                        />
                      </Col>
                      <Col sm={6} className="SearchInformation">
                        <p className="GeneralMediumFont">
                          {value.name} {value.surname}
                        </p>
                      </Col>
                    </Row>
                  </Link>
                );
              })}
            </Container>
          </div>
        )}
      </div>
    );
  }
}

export default Search;
