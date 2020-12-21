import axios from 'axios';
import config from '../utils/config';
import jwt_decode from "jwt-decode";

const followings = (id) => {
    const url = config.BASE_URL;
    const params = {
        follower_id: id,
    };
    const token = localStorage.getItem("jwtToken");
    axios.defaults.headers.common["auth_token"] = `${token}`;
    return axios.get(url + "/api/follow/followings", {params})
        .then(response => {
            return response;
        })
        .catch(err => {
            return err.response;
        });
}

const followers = (id) => {
    const url = config.BASE_URL;
    const params = {
        following_id: id,
    };
    const token = localStorage.getItem("jwtToken");
    axios.defaults.headers.common["auth_token"] = `${token}`;
    return axios.get(url + "/api/follow/followers", {params})
        .then(response => {
            return response;
        })
        .catch(err => {
            return err.response;
        });

}

const getUser = (id) => {
    const url = config.BASE_URL;
    const params = {
        user_id: id,
    };
    const token = localStorage.getItem("jwtToken");
    axios.defaults.headers.common["auth_token"] = `${token}`;
    return axios.get(url + "/api/auth_system/user", {params})
        .then(response => {
            return response;
        })
        .catch(err => {
            return err.response;
        });

}
const getResearchs = (id) => {
    const url = config.BASE_URL;
    const params = {
        user_id: id,
    };
    const token = localStorage.getItem("jwtToken");
    axios.defaults.headers.common["auth_token"] = `${token}`;
    return axios.get(url + "/api/profile/research_information", {params})
        .then(response => {
            return response;
        })
        .catch(err => {
            return err.response;
        });

}
const getNotifications = (page) => {
    const url = config.BASE_URL;
    const token = localStorage.getItem("jwtToken");
    axios.defaults.headers.common["auth_token"] = `${token}`;
    return axios.get(url + "/api/profile/notifications",{ params: { per_page:1, page:page} } )
        .then(response => {
            return response;
        })
        .catch(err => {
            return err.response;
        });

}

const getFeed = () => {
    const url = config.BASE_URL;
    const token = localStorage.getItem("jwtToken");
    axios.defaults.headers.common["auth_token"] = `${token}`;
    return axios.get(url + "/api/profile/front_page" )
        .then(response => {
            return response;
        })
        .catch(err => {
            return err.response;
        });

}

const postFollowRequest = (follower_id, following_id) => {
    const url = config.BASE_URL;

    const token = localStorage.getItem("jwtToken");
    axios.defaults.headers.common["auth_token"] = `${token}`;

    let formData = new FormData();
    formData.append("follower_id", follower_id);
    formData.append("following_id", following_id);

    return axios.post(url + "/api/follow/follow_requests", formData)
        .then(response => {
            return response;
        })
        .catch(err => {
            return err.response;
        });

}

const getSearchUser = (searchQuery) => {
    const url = config.BASE_URL;

    const token = localStorage.getItem("jwtToken");
    axios.defaults.headers.common["auth_token"] = `${token}`;

    return axios.get(url + "/api/search_engine/user?search_query="+searchQuery)
        .then(response => {
            return response;
        })
        .catch(err => {
            return err.response;
        });

}

const getFollowRequests = () => {
    const url = config.BASE_URL;
    const token = localStorage.getItem("jwtToken");
    const decoded = jwt_decode(token);
    const params = {
        following_id: decoded.id,
    };
    
    axios.defaults.headers.common["auth_token"] = `${token}`;

    return axios.get(url + "/api/follow/follow_requests",{params})
        .then(response => {
            console.log(response);
            return response;
        })
        .catch(err => {
            return err.response;
        });

}

const deleteFollowRequests = (follower_id, state) => {
    const url = config.BASE_URL;
    const token = localStorage.getItem("jwtToken");
    const decoded = jwt_decode(token);
    const params = {
        follower_id: follower_id,
        following_id: decoded.id,
        state: state,
    };
    
    axios.defaults.headers.common["auth_token"] = `${token}`;

    return axios.delete(url + "/api/follow/follow_requests",{params})
        .then(response => {
            console.log(response);
            return response;
        })
        .catch(err => {
            return err.response;
        });

}

export default { followings, followers, getUser, getResearchs, getNotifications, getFeed, postFollowRequest, getSearchUser, getFollowRequests, deleteFollowRequests };

