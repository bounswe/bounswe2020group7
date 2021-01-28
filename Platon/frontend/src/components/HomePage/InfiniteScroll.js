import React, { Component } from 'react';
import InfiniteScroll from 'react-infinite-scroller';
import qwest from 'qwest';
import Commentt from './Comment'
import config from '../../utils/config';
import requestService from "../../services/requestService";
import colors from "../../utils/colors";
import Typography from '@material-ui/core/Typography'
const BASE_URL = config.BASE_URL

class InfiniteScroller extends Component {
    constructor(props) {
        super(props);

        this.state = {
            tracks: [],
            page:0,
            per_page:10,
            totalItems:-1,
        };
    }

    loadItems(page) {
        var self = this;
        if(this.state.totalItems==-1 || this.state.page<this.state.totalItems)
        requestService.getFeed(this.state.page,this.state.per_page).then((resp) =>{
                if(resp) {
                    console.log(resp)
                    console.log(this.state.page)
                    var tracks = self.state.tracks;

                    resp.data.orderedItems.map( (track) => {
                    console.log(track)
                        tracks.push(track);
                    });
                        self.setState({
                            tracks: tracks,
                            page:this.state.page+1,
                            totalItems:resp.data.totalItems,
                        })
                    }
            });
    }

    render() {
        const loader = <div className="loader">Loading ...</div>;

        var items = [];
        this.state.tracks.map((track, i) => {
            items.push(
                <div style={{marginTop: "16px"}}>
                <Commentt
                    title={track.summary}
                    author={track.actor.name}
                    avatar={BASE_URL + '/api' +track.actor.image.url}

                    style={{ color: colors.tertiary, textAlign: 'center'
                     }}
                    style={{ color: colors.tertiary, textAlign: 'center' }}
                    userId={track.actor.id}

                />
                <hr style={{backgroundColor: colors.primaryLight,}} />
                </div>
            );
        });

        return (
            <div>
            <Typography
            style={{ color: colors.septenary, textAlign: 'center' }}
            variant="h5"
            gutterBottom
          >
            What's Happening?
          </Typography>
          <div  style={{backgroundColor: colors.primaryLight, width: "480px", padding:"5px", borderRadius: "0.5em"}} >
            <InfiniteScroll
                pageStart={0}
                style={{ color: colors.primaryLight, textAlign: 'center' }}
                loadMore={this.loadItems.bind(this)}
                hasMore={true}
                threshold={1500}
                loader={loader}>

                <div className="tracks">
                    {items}
                </div>
            </InfiniteScroll>
            </div>
            </div>
        );
    }
};

export default InfiniteScroller;