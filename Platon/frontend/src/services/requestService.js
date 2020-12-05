import axios from 'axios';
import config from '../utils/config';

const followings = (id) => {
    const url = config.BASE_URL;
    const params = {
        follower_id: id,
    };
    const token = localStorage.getItem("jwtToken");
    axios.defaults.headers.common["auth_token"] = `${token}`;
    return axios.get(url + "/api/follow/followings", {params})
        .then(response => {
            //eğer kullanıcı bulunursa (user.data.status = true)
            if (response) {
                console.log(response);

            }
            return response;
        })
        .catch(err => {
            console.log(err)
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
            //eğer kullanıcı bulunursa (user.data.status = true)
            if (response) {
            }
            return response;
        })
        .catch(err => {
            console.log(err)
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
            //eğer kullanıcı bulunursa (user.data.status = true)
            if (response) {
            }
            return response;
        })
        .catch(err => {
            console.log(err)
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
            //eğer kullanıcı bulunursa (user.data.status = true)
            if (response) {
            }
            return response;
        })
        .catch(err => {
            console.log(err)
            return err.response;
        });

}
const getNotifications = () => {
    const url = config.BASE_URL;
    const token = localStorage.getItem("jwtToken");
    axios.defaults.headers.common["auth_token"] = `${token}`;
    return axios.get(url + "/api/profile/notifications" )
        .then(response => {
            if (response) {
            }
            return response;
        })
        .catch(err => {
            console.log(err)
            return err.response;
        });

}

const getFeed = () => {
    const url = config.BASE_URL;
    const token = localStorage.getItem("jwtToken");
    axios.defaults.headers.common["auth_token"] = `${token}`;
    return axios.get(url + "/api/profile/front_page" )
        .then(response => {
            if (response) {
            }
            return response;
        })
        .catch(err => {
            console.log(err)
            return err.response;
        });

}
export default { followings, followers, getUser, getResearchs,getNotifications,getFeed };

