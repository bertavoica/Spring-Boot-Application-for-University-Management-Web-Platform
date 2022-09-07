import axios from 'axios';
import authHeader from './auth-header';
const API_URL_LANGUAGE = 'http://localhost:8080/user-controller/language';

class UserService {

    updateLanguage(username, language) {
        return axios.post(API_URL_LANGUAGE + "?username=" + username + "&language=" + language, null,{
            headers: authHeader(),
        });
    }

    getLanguage(username) {
        return axios.get(API_URL_LANGUAGE + "?username=" + username,{
            headers: authHeader(),
        });
    }

}

export default new UserService();