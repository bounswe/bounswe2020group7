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
//        formData.append("deadline",deadline);
        formData.append("workspace_id",workspace_id);
    const token = localStorage.getItem("jwtToken");
    axios.defaults.headers.common["auth_token"] = `${token}`;
    return axios.post(url + "/api/workspaces/issue", formData)
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

const deleteIssue = (issue_id,workspace_id) => {
    const url = config.BASE_URL;
const token = localStorage.getItem("jwtToken");


    let formData = new FormData();
    formData.append("issue_id",issue_id);
   //        formData.append("description",description);
   //        formData.append("deadline",deadline);
           formData.append("workspace_id",workspace_id);
    axios
      .delete(url + "/api/workspaces/issue", {
        headers: {
          auth_token: token, //the token is a variable which holds the token
        },
        data: formData,
      })
      .then((response) => {
        if (response.status === 200) {
          this.fetchFileStructure();
        }
      })
      .catch((err) => {
        console.log(err);
      });


}
const addIssueComment = (issue_id,workspace_id,description) => {
    const url = config.BASE_URL;

    let formData = new FormData();

        formData.append("issue_id", issue_id);
        formData.append("comment",description);
        formData.append("workspace_id",workspace_id);

    const token = localStorage.getItem("jwtToken");
    axios.defaults.headers.common["auth_token"] = `${token}`;
    return axios.post(url + "/api/workspaces/issue/comment", formData)
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

const assignIssue = (issue_id,workspace_id,assignee_id) => {
    const url = config.BASE_URL;

    let formData = new FormData();

        formData.append("issue_id", issue_id);
        formData.append("assignee_id",assignee_id);
        formData.append("workspace_id",workspace_id);
    const token = localStorage.getItem("jwtToken");
    axios.defaults.headers.common["auth_token"] = `${token}`;
    return axios.post(url + "/api/workspaces/issue/assignee", formData)
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

const getIssueComment = (issue_id,workspace_id,assignee_id) => {
    const url = config.BASE_URL;
    const params = {
        issue_id: issue_id,
        workspace_id:workspace_id
    };

    let formData = new FormData();
    const token = localStorage.getItem("jwtToken");
    axios.defaults.headers.common["auth_token"] = `${token}`;
    return axios.get(url + "/api/workspaces/issue/comment", {params})
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
    return axios.get(url + "/api/workspaces/issue", {params})
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
    return axios.get(url + "/api/profile/notifications")
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

const getFeed = (page,per_page) => {
  const url = config.BASE_URL;
  const token = localStorage.getItem("jwtToken");
  const params = {
          page: page,
          per_page:per_page,
      };
  axios.defaults.headers.common["auth_token"] = `${token}`;
  return axios
    .get(url + "/api/activity_stream",{params})
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

const getSearchUser = (search_query, job_filter, sorting_criteria) => {
  const url = config.BASE_URL;

  const token = localStorage.getItem("jwtToken");
  axios.defaults.headers.common["auth_token"] = `${token}`;

  const params = {
    search_query: search_query,
    sorting_criteria: sorting_criteria,
  };
  
  if (job_filter !== "") {
    params.job_filter = job_filter;
  }

  return axios
    .get(url + "/api/search_engine/user", { params })
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
  formData.append("job", job);
  if (is_private === true) {
    formData.append("is_private", 1);
  } else {
    formData.append("is_private", 0);
  }
  if (profile_photo !== undefined) {
    formData.append("profile_photo", profile_photo);
  }
  formData.append("google_scholar_name", google_scholar_name);
  formData.append("researchgate_name", researchgate_name);
  formData.append("institution", institution);

  return axios
    .put(url + "/api/auth_system/user", formData)
    .then((response) => {
      return response;
    })
    .catch((err) => {
      return err.response;
    });
};

const getSkillList = () => {
  const url = config.BASE_URL;
  return axios
    .get(url + "/api/profile/skills")
    .then((response) => {
      return response;
    })
    .catch((err) => {
      return err.response;
    });
};

const getPersonalSkillList = (user_id) => {
  const url = config.BASE_URL;

  const token = localStorage.getItem("jwtToken");
  const decoded = jwt_decode(token);
  axios.defaults.headers.common["auth_token"] = `${token}`;

  const params = {
    user_id: user_id,
  };

  return axios
    .get(url + "/api/auth_system/skills", { params })
    .then((response) => {
      return response;
    })
    .catch((err) => {
      return err.response;
    });
};

const postSkill = (skillName) => {
  const url = config.BASE_URL;

  const token = localStorage.getItem("jwtToken");
  axios.defaults.headers.common["auth_token"] = `${token}`;

  let formData = new FormData();
  formData.append("skill", skillName);

  return axios
    .post(url + "/api/auth_system/skills", formData)
    .then((response) => {
      return response;
    })
    .catch((err) => {
      return err.response;
    });
};

const deleteSkill = (skillName) => {
  const url = config.BASE_URL;
  const token = localStorage.getItem("jwtToken");

  let formData = new FormData();
  formData.append("skill", skillName);

  axios.defaults.headers.common["auth_token"] = `${token}`;

  return axios
    .delete(url + "/api/auth_system/skills", {
      data: formData,
    })
    .then((response) => {
      return response;
    })
    .catch((err) => {
      return err.response;
    });
};

const getSeachWorkspace = (
  search_query,
  skill_filter,
  creator_name,
  creator_surname,
  starting_date_start,
  starting_date_end,
  deadline_start,
  deadline_end,
  sorting_criteria
) => {
  const url = config.BASE_URL;

  const token = localStorage.getItem("jwtToken");
  axios.defaults.headers.common["auth_token"] = `${token}`;

  const params = {
    search_query: search_query,
    skill_filter: skill_filter,
    creator_name: creator_name,
    creator_surname: creator_surname,
    sorting_criteria: sorting_criteria,
  };

  if (starting_date_start !== "") {
    params.starting_date_start = (starting_date_start + ":00").replaceAt(
      10,
      " "
    );
  }
  if (starting_date_end !== "") {
    params.starting_date_end = (starting_date_end + ":00").replaceAt(10, " ");
  }
  if (deadline_start !== "") {
    params.deadline_start = (deadline_start + ":00").replaceAt(10, " ");
  }
  if (deadline_end !== "") {
    params.deadline_end = (deadline_end + ":00").replaceAt(10, " ");
  }

  return axios
    .get(url + "/api/search_engine/workspace", { params })
    .then((response) => {
      console.log(response);
      return response;
    })
    .catch((err) => {
      return err.response;
    });
};

const getSearchUpcomingEvents = (
  search_query,
  date_filter_start,
  date_filter_end,
  deadline_filter_start,
  deadline_filter_end,
  sorting_criteria
) => {
  const url = config.BASE_URL;

  const token = localStorage.getItem("jwtToken");
  axios.defaults.headers.common["auth_token"] = `${token}`;

  const params = {
    search_query: search_query,
    sorting_criteria: sorting_criteria,
  };

  if (date_filter_start !== "") {
    params.date_filter_start = (date_filter_start + ":00").replaceAt(
      10,
      " "
    );
  }
  if (date_filter_end !== "") {
    params.date_filter_end = (date_filter_end + ":00").replaceAt(10, " ");
  }
  if (deadline_filter_start !== "") {
    params.deadline_filter_start = (deadline_filter_start + ":00").replaceAt(10, " ");
  }
  if (deadline_filter_end !== "") {
    params.deadline_filter_end = (deadline_filter_end + ":00").replaceAt(10, " ");
  } 

  return axios
    .get(url + "/api/search_engine/upcoming_events", { params })
    .then((response) => {
      console.log(response);
      return response;
    })
    .catch((err) => {
      return err.response;
    });
};

const getSearchHistory = (searchQuery) => {
  const url = config.BASE_URL;

  const token = localStorage.getItem("jwtToken");
  axios.defaults.headers.common["auth_token"] = `${token}`;

  return axios
    .get(url + "/api/search_engine/upcoming_events?search_query=" + searchQuery)
    .then((response) => {
      return response;
    })
    .catch((err) => {
      return err.response;
    });
};

String.prototype.replaceAt = function (index, replacement) {
  return (
    this.substr(0, index) +
    replacement +
    this.substr(index + replacement.length)
  );
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
  putUser,
  getSkillList,
  getPersonalSkillList,
  postSkill,
  deleteSkill,
  getSeachWorkspace,
  getSearchUpcomingEvents,
  addIssueComment,
  getIssueComment,
  createIssue,
  assignIssue,
  getIssues,
  deleteIssue 
};
