import React, { Component } from 'react';
import InfiniteScroll from 'react-infinite-scroller';
import qwest from 'qwest';
import Commentt from './Comment'
import config from '../../utils/config';
import requestService from "../../services/requestService";

class InfiniteScroller extends Component {
    constructor(props) {
        super(props);

        this.state = {
            tracks: [],
        };
    }

    loadItems(page) {
        var self = this;

        var url = config.BASE_URL + '/api/profile/front_page';
        requestService.getFeed().then((resp) =>{
                if(resp) {
                    console.log(resp)
                    var tracks = self.state.tracks;
                    resp.data.map( (track) => {
                        if(track.artwork_url == null) {
                            track.artwork_url = track.image;
                        }
                        tracks.push(track);
                    });

                        self.setState({
                            tracks: tracks,
                        })
                    }
            });
    }

    render() {
        const loader = <div className="loader">Loading ...</div>;

        var items = [];
        this.state.tracks.map((track, i) => {
            items.push(
                <Commentt
                    message={track.message}
                    author={<a>{track.author}</a>}
                    avatar={track.avatar}
                />
            );
        });

        return (
            <InfiniteScroll
                pageStart={0}
                loadMore={this.loadItems.bind(this)}
                hasMore={this.state.tracks.length<20}
                loader={loader}>

                <div className="tracks">
                    {items}
                </div>
            </InfiniteScroll>
        );
    }
};

export default InfiniteScroller;