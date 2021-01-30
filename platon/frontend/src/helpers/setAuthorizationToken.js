import axios from 'axios'


export const setAuthorizationToken = token => {
    if (token) {
        axios.defaults.headers.common["auth_token"] = `${token}`;
    }
    else
        delete axios.defaults.headers.common["auth_token"];
}