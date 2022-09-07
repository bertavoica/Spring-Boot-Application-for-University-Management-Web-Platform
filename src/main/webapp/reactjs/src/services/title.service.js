import axios from "axios";
import authHeader from './auth-header';

const API_URL = "http://localhost:8080/title-controller";

class TitleService {

    getTitles() {
        return axios.get(API_URL, { headers: authHeader() });
    }

    addTitle(name) {
        return axios.post(API_URL, {
            name,
        },{
            headers: authHeader(),
        });
    }

    deleteTitle(name) {
        return axios.delete(API_URL + "?name=" + name, {
            headers: authHeader(),
        });
    }
}

export default new TitleService();