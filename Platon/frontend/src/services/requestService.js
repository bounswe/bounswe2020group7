import axios from "axios";
import config from "../utils/config";
import jwt_decode from "jwt-decode";

const followings = (id) => {
  const url = config.BASE_URL;
  const params = {
    follower_id: id,
  };
  const token = localStorage.getItem("jwtToken");
  axios.defaults.headers.common["auth_token"] = `${token}`;
  return axios
    .get(url + "/api/follow/followings", { params })
    .then((response) => {
      return response;
    })
    .catch((err) => {
      return err.response;
    });
};

const followers = (id) => {
  const url = config.BASE_URL;
  const params = {
    following_id: id,
  };
  const token = localStorage.getItem("jwtToken");
  axios.defaults.headers.common["auth_token"] = `${token}`;
  return axios
    .get(url + "/api/follow/followers", { params })
    .then((response) => {
      return response;
    })
    .catch((err) => {
      return err.response;
    });
};

const getUser = (id) => {
  const url = config.BASE_URL;
  const params = {
    user_id: id,
  };
  const token = localStorage.getItem("jwtToken");
  axios.defaults.headers.common["auth_token"] = `${token}`;
  return axios
    .get(url + "/api/auth_system/user", { params })
    .then((response) => {
      return response;
    })
    .catch((err) => {
      return err.response;
    });
};
const getResearchs = (id) => {
  const url = config.BASE_URL;
  const params = {
    user_id: id,
  };
  const token = localStorage.getItem("jwtToken");
  axios.defaults.headers.common["auth_token"] = `${token}`;
  return axios
    .get(url + "/api/profile/research_information", { params })
    .then((response) => {
      return response;
    })
    .catch((err) => {
      return err.response;
    });
};
const getNotifications = (page) => {
  const url = config.BASE_URL;
  const token = localStorage.getItem("jwtToken");
  axios.defaults.headers.common["auth_token"] = `${token}`;
  return axios
    .get(url + "/api/profile/notifications", {
      params: { per_page: 1, page: page },
    })
    .then((response) => {
      return response;
    })
    .catch((err) => {
      return err.response;
    });
};

const getFeed = () => {
  const url = config.BASE_URL;
  const token = localStorage.getItem("jwtToken");
  axios.defaults.headers.common["auth_token"] = `${token}`;
  return axios
    .get(url + "/api/profile/front_page")
    .then((response) => {
      return response;
    })
    .catch((err) => {
      return err.response;
    });
};

const postFollowRequest = (follower_id, following_id) => {
  const url = config.BASE_URL;

  const token = localStorage.getItem("jwtToken");
  axios.defaults.headers.common["auth_token"] = `${token}`;

  let formData = new FormData();
  formData.append("follower_id", follower_id);
  formData.append("following_id", following_id);

  return axios
    .post(url + "/api/follow/follow_requests", formData)
    .then((response) => {
      return response;
    })
    .catch((err) => {
      return err.response;
    });
};

const getSearchUser = (searchQuery) => {
  const url = config.BASE_URL;

  const token = localStorage.getItem("jwtToken");
  axios.defaults.headers.common["auth_token"] = `${token}`;

  return axios
    .get(url + "/api/search_engine/user?search_query=" + searchQuery)
    .then((response) => {
      return response;
    })
    .catch((err) => {
      return err.response;
    });
};

const getFollowRequests = () => {
  const url = config.BASE_URL;
  const token = localStorage.getItem("jwtToken");
  const decoded = jwt_decode(token);
  const params = {
    following_id: decoded.id,
  };

  axios.defaults.headers.common["auth_token"] = `${token}`;

  return axios
    .get(url + "/api/follow/follow_requests", { params })
    .then((response) => {
      return response;
    })
    .catch((err) => {
      return err.response;
    });
};

const deleteFollowRequests = (follower_id, switchCase) => {
  const url = config.BASE_URL;
  const token = localStorage.getItem("jwtToken");
  const decoded = jwt_decode(token);

  let formData = new FormData();
  formData.append("follower_id", follower_id);
  formData.append("following_id", decoded.id);
  formData.append("state", switchCase);

  axios.defaults.headers.common["auth_token"] = `${token}`;

  return axios
    .delete(url + "/api/follow/follow_requests", {
      data: formData,
    })
    .then((response) => {
      return response;
    })
    .catch((err) => {
      return err.response;
    });
};

const deleteUnfollow = (following_id) => {
  const url = config.BASE_URL;
  const token = localStorage.getItem("jwtToken");

  let formData = new FormData();
  formData.append("following_id", following_id);

  axios.defaults.headers.common["auth_token"] = `${token}`;

  return axios
    .delete(url + "/api/follow/followings", {
      data: formData,
    })
    .then((response) => {
      return response;
    })
    .catch((err) => {
      return err.response;
    });
};

const getJobList = () => {
  const url = config.BASE_URL;
  return axios
    .get(url + "/api/profile/jobs")
    .then((response) => {
      return response;
    })
    .catch((err) => {
      return err.response;
    });
};

const getProfilePhoto = (endpoint) => {
  const url = config.BASE_URL;

  return axios
    .get(url + "/api" + endpoint)
    .then((response) => {
      return response;
    })
    .catch((err) => {
      return err.response;
    });
};

const putUser = (
  name,
  surname,
  e_mail,
  job,
  is_private,
  profile_photo,
  google_scholar_name,
  researchgate_name,
  institution
) => {
  const url = config.BASE_URL;

  const token = localStorage.getItem("jwtToken");
  axios.defaults.headers.common["auth_token"] = `${token}`;

  let formData = new FormData();
  formData.append("name", name);
  formData.append("surname", surname);
  formData.append("e_mail", e_mail);
  formData.append("job", job);
  formData.append("is_private", is_private);
  formData.append("profile_photo", profile_photo);
  formData.append("google_scholar_name", google_scholar_name);
  formData.append("researchgate_name", researchgate_name);
  formData.append("institution", institution);

  return axios
    .post(url + "/api/auth_system/user", formData)
    .then((response) => {
      return response;
    })
    .catch((err) => {
      return err.response;
    });
};

export default {
  followings,
  followers,
  getUser,
  getResearchs,
  getNotifications,
  getFeed,
  postFollowRequest,
  getSearchUser,
  getFollowRequests,
  deleteFollowRequests,
  deleteUnfollow,
  getJobList,
  getProfilePhoto,
};
