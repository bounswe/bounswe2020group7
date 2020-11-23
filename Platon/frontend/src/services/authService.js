import axios from 'axios';
import { setAuthorizationToken } from '../helpers/setAuthorizationToken';
import config from '../utils/config';

const login = (email, password) => {
    const url = config.BASE_URL;
    let formData = new FormData();

    formData.append("e_mail", email);
    formData.append("password", password);

    return axios.post(url + "/api/auth_system/login", formData)
        .then(user => {
            //eğer kullanıcı bulunursa (user.data.status = true) 
            if (user.status) {
                const { token } = user.data;
                localStorage.setItem("jwtToken", token);
                setAuthorizationToken(token);
            }
            return user;
        })
        .catch(err => console.log(err));

}


const logout = () => {
    localStorage.removeItem("jwtToken");
    setAuthorizationToken(false);
}

export default { login, logout };