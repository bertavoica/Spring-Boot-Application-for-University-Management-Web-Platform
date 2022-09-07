import axios from "axios";
import authHeader from './auth-header';

const API_URL = "http://localhost:8080/student-controller";

class StudentService {

    getDetails(username) {
        return axios.get(API_URL + "?username=" + username, { headers: authHeader() });
    }

    getAllStudents(username) {
        return axios.get(API_URL + "?username=" + username,{
            headers: authHeader(),
        });
    }

    deleteStudent(username) {
        return axios.delete(API_URL + "?username=" + username, {
            headers: authHeader(),
        });
    }

    addStudent(username, emailAddress, specialization, cycle, password, group) {
        return axios.post(API_URL, {
            username,
            emailAddress,
            specialization,
            cycle,
            password,
            group,
        },{
            headers: authHeader(),
        });

    }

    updateStudent(username, specialization, cycle, group, role) {
        return axios.put(API_URL, {
            username,
            specialization,
            cycle,
            group,
            role,
        },{
            headers: authHeader(),
        });

    }

}

export default new StudentService();