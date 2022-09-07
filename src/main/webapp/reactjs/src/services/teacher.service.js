import axios from "axios";
import authHeader from './auth-header';
import authUser from './auth-user';

const API_URL_PROJECTS = "http://localhost:8080/project-controller/assignments";
const API_URL = "http://localhost:8080/teacher-controller";

class TeacherService {

    getAssignedProjects() {
        return axios.get(API_URL_PROJECTS + "?owner=" + authUser(), { headers: authHeader() });
    }

    deleteAssignment(assignment) {
        return axios.delete(API_URL_PROJECTS + "?assignmentId=" + assignment.uniqueId + "&username=" + assignment.assignee, { headers: authHeader() });
    }

    getAllTeachers(username, availability, specialization="") {
        return axios.get(API_URL + "?username=" + username + "&available=" + availability + "&specialization=" + specialization,{
            headers: authHeader(),
        });
    }

    addTeacher(username, emailAddress, password, title) {
        return axios.post(API_URL, {
            username,
            emailAddress,
            title,
            password,
        },{
            headers: authHeader(),
        });
    }

    updateTeacher(username, title, role) {
        if (title === '')
            title = 'UNKNOWN';
        return axios.put(API_URL, {
            username,
            title,
            role,
        },{
            headers: authHeader(),
        });
    }

    deleteTeacher(username) {
        return axios.delete(API_URL + "?username=" + username, {
            headers: authHeader(),
        });
    }
}

export default new TeacherService();