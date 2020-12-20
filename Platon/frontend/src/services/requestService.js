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
const createIssue = (title,description,deadline,workspace_id) => {
    const url = config.BASE_URL;
    const params = {
        title: title,
        description:description,
        deadline:deadline,
        workspace_id:workspace_id
    };
    let formData = new FormData();

        formData.append("title", title);
        formData.append("description",description);
        formData.append("deadline",deadline);
        formData.append("workspace_id",workspace_id);

    const token = localStorage.getItem("jwtToken");
    axios.defaults.headers.common["auth_token"] = `${token}`;
    return axios.post('https://jsonplaceholder.typicode.com/posts', formData)
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
const addIssueComment = (issue_id,description) => {
    const url = config.BASE_URL;
    const params = {
        title: title,
        description:description,
        deadline:deadline,
        workspace_id:workspace_id
    };
    let formData = new FormData();

        formData.append("issue_id", title);
        formData.append("comment",description);

    const token = localStorage.getItem("jwtToken");
    axios.defaults.headers.common["auth_token"] = `${token}`;
    return axios.post(url + "/api/issue/comment", formData)
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

const assignIssue = (workspace_id,issue_id,assignee_id) => {
    const url = config.BASE_URL;

    let formData = new FormData();

        formData.append("issue_id", issue_id);
        formData.append("assignee_id",assignee_id);
        formData.append("workspace_id",workspace_id);
    const token = localStorage.getItem("jwtToken");
    axios.defaults.headers.common["auth_token"] = `${token}`;
    return axios.post(url + "/api/issue/assignee", formData)
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

const getIssueComment = (workspace_id,issue_id,assignee_id) => {
    const url = config.BASE_URL;

    const params = {
        issue_id: issue_id,
        workspace_id:workspace_id
    };

    let formData = new FormData();
    const token = localStorage.getItem("jwtToken");
    axios.defaults.headers.common["auth_token"] = `${token}`;
    return axios.get(url + "/api/issue/comment", {params})
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


const getIssues = (id,workspace_id) => {
    const url = config.BASE_URL;
    const params = {
        user_id: id,
        workspace_id:workspace_id,
    };
    const token = localStorage.getItem("jwtToken");
    axios.defaults.headers.common["auth_token"] = `${token}`;
    return axios.get(url + "/api/issue", {params})
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
const getNotifications = (page) => {
    const url = config.BASE_URL;
    const token = localStorage.getItem("jwtToken");
    axios.defaults.headers.common["auth_token"] = `${token}`;
    return axios.get(url + "/api/profile/notifications",{ params: { per_page:1, page:page} } )
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
export default { followings, followers, getUser, getResearchs,getNotifications,getFeed,createIssue,getIssues };

