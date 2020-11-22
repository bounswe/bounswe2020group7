import React, { Component } from 'react';
import InfiniteScroll from 'react-infinite-scroller';
import qwest from 'qwest';
import Commentt from './Comment'

const api = {
    baseUrl: 'https://api.soundcloud.com',
    client_id: 'caf73ef1e709f839664ab82bef40fa96'
};

class InfiniteScroller extends Component {
    constructor(props) {
        super(props);

        this.state = {
            tracks: [],
            hasMoreItems: true,
            nextHref: false
        };
    }

    loadItems(page) {
        var self = this;

        var url = api.baseUrl + '/users/8665091/favorites';
        if(this.state.nextHref) {
            url = this.state.nextHref;
        }

        qwest.get(url, {
            client_id: api.client_id,
            linked_partitioning: 1,
            page_size: 10
        }, {
            cache: true
        })
            .then(function(xhr, resp) {
                if(resp) {
                    var tracks = self.state.tracks;
                    resp.collection.map((track) => {
                        if(track.artwork_url == null) {
                            track.artwork_url = track.user.avatar_url;
                        }

                        tracks.push(track);
                    });

                    if(resp.next_href) {
                        self.setState({
                            tracks: tracks,
                            nextHref: resp.next_href
                        });
                    } else {
                        self.setState({
                            hasMoreItems: false
                        });
                    }
                }
            });
    }

    render() {
        const loader = <div className="loader">Loading ...</div>;

        var items = [];
        this.state.tracks.map((track, i) => {
            items.push(
                <Commentt
                    title={track.title}
                    actions={this.actions}
                    author={<a>{track.title}</a>}
                    avatar={track.artwork_url}

                />
            );
        });

        return (
            <InfiniteScroll
                pageStart={0}
                loadMore={this.loadItems.bind(this)}
                hasMore={this.state.hasMoreItems}
                loader={loader}>

                <div className="tracks">
                    {items}
                </div>
            </InfiniteScroll>
        );
    }
};

export default InfiniteScroller;