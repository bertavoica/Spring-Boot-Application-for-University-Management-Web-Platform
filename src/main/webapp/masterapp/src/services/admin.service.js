import axios from "axios";
import authHeader from './auth-header';

const API_URL = "http://localhost:8080/admin-controller";

class AdminService {

    getAllAdmins(username) {
        return axios.get(API_URL + "?username=" + username,{
            headers: authHeader(),
        });
    }


    updateAdmin(username, role) {
        return axios.put(API_URL, {
            username,
            role,
        },{
            headers: authHeader(),
        });
    }

    deleteAdmin(username) {
        return axios.delete(API_URL + "?username=" + username, {
            headers: authHeader(),
        });
    }
}

export default new AdminService();