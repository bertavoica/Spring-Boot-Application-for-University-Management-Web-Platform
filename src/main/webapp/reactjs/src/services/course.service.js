import axios from "axios";
import authHeader from './auth-header';

const API_URL = "http://localhost:8080/course-controller";
const API_URL_ENROLLED = "http://localhost:8080/course-controller/enrolled";
const API_URL_USER = "http://localhost:8080/course-controller/user";
const API_URL_RESPONSIBLE = "http://localhost:8080/course-controller/responsible";

class CourseService {

    addCourse(completeName, abbreviation, description) {
        return axios.post(API_URL, {
            completeName,
            abbreviation,
            description,
        },{
            headers: authHeader(),
        });
    }

    getCourses() {
        return axios.get(API_URL,{
            headers: authHeader(),
        });
    }

    deleteCourse(uniqueId) {
        return axios.delete(API_URL + "?uniqueId=" + uniqueId, {
            headers: authHeader(),
        });
    }

    addResponsible(course, username) {
        return axios.put(API_URL_RESPONSIBLE + "?username=" + username + "&uniqueId=" + course.uniqueId, null, {
            headers: authHeader(),
        });
    }

    removeResponsible(course, username) {
        return axios.delete(API_URL_RESPONSIBLE + "?username=" + username + "&uniqueId=" + course.uniqueId, {
            headers: authHeader(),
        });
    }

    getResponsible(course) {
        return axios.get(API_URL_RESPONSIBLE + "?uniqueId=" + course.uniqueId, {
            headers: authHeader(),
        });
    }

    getCoursesUser(username) {
        return axios.get(API_URL_USER + "?username=" + username, {
            headers: authHeader(),
        });
    }

    enrollStudent(course, username) {
        return axios.put(API_URL_USER + "?username=" + username + "&uniqueId=" + course.uniqueId, null, {
            headers: authHeader(),
        });
    }

    removeStudent(course, username) {
        return axios.delete(API_URL_USER + "?username=" + username + "&uniqueId=" + course.uniqueId, {
            headers: authHeader(),
        });
    }

    getEnrolled(course) {
        return axios.get(API_URL_ENROLLED + "?uniqueId=" + course.uniqueId, {
            headers: authHeader(),
        });
    }
}

export default new CourseService();