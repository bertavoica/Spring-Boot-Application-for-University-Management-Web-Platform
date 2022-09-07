import axios from "axios";
import authHeader from './auth-header';

const API_URL = "http://localhost:8080/project-controller";
const API_URL_ASSIGN_USER = "http://localhost:8080/project-controller/assign-user";
const API_URL_ASSIGN_GROUP = "http://localhost:8080/project-controller/assign-group";
const API_URL_REVIEW = "http://localhost:8080/project-controller/review";
const API_URL_UPLOAD = "http://localhost:8080/project-controller/upload";
const API_URL_DOWNLOAD = "http://localhost:8080/project-controller/download";
const API_URL_STATISTICS = "http://localhost:8080/project-controller/statistics";

class ProjectService {

    uploadAssignment(inputFile, projectId, studentName) {
        const formData = new FormData();
        formData.append('purpose', "student-assignment");
        formData.append('assignment', inputFile);
        formData.append('fileName', inputFile.name);
        formData.append('projectId', projectId);
        formData.append('username', studentName);

        return axios.put(API_URL_UPLOAD, formData,{
            headers: authHeader(),
        });
    }

    addProject(projectName, description, courseUniqueId, inputDate, inputTime, owner) {
        return axios.post(API_URL, {
            projectName,
            description,
            courseUniqueId,
            inputDate,
            inputTime,
            owner,
        },{
            headers: authHeader(),
        });
    }

    updateProject(uniqueId, projectName, description, courseUniqueId, inputDate, inputTime) {
        return axios.put(API_URL, {
            uniqueId,
            projectName,
            description,
            courseUniqueId,
            inputDate,
            inputTime,
        },{
            headers: authHeader(),
        });
    }

    getProjects(displayMyProjects, username) {
        let url = API_URL;

        if (!displayMyProjects)
            url += "?owner=" + username

        return axios.get(url,{
            headers: authHeader(),
        });
    }

    deleteProject(uniqueId) {
        return axios.delete(API_URL + "?uniqueId=" + uniqueId, {
            headers: authHeader(),
        });
    }

    assignToUser(projectToAssignUniqueId, assignedUser) {
        return axios.post(API_URL_ASSIGN_USER + "?uniqueId=" + projectToAssignUniqueId + "&username=" + assignedUser, null, {
            headers: authHeader(),
        });
    }

    assignToGroup(projectToAssignUniqueId, assignedGroup) {
        return axios.post(API_URL_ASSIGN_GROUP + "?uniqueId=" + projectToAssignUniqueId + "&groupName=" + assignedGroup, null, {
            headers: authHeader(),
        });
    }

    assignGradeAndFeedback(studentGrade, studentFeedback, studentName, projectId) {
        return axios.put(API_URL_REVIEW, {
            studentGrade,
            studentFeedback,
            studentName,
            projectId,
        },{
            headers: authHeader(),
        });
    }

    downloadAssignment(projectId, studentName) {
        return axios.get(API_URL_DOWNLOAD + "?uniqueId=" + projectId + "&studentName=" + studentName,{
            headers: authHeader(),
        })
    }

    getReviewedProjectStatistics(uniqueId) {
        return axios.get(API_URL_STATISTICS + "/reviewed" + "?uniqueId=" + uniqueId,{
            headers: authHeader(),
        })

    }

    getDeadlineProjectStatistics(uniqueId) {
        return axios.get(API_URL_STATISTICS + "/deadline" + "?uniqueId=" + uniqueId,{
            headers: authHeader(),
        })

    }

    getGradesProjectStatistics(uniqueId) {
        return axios.get(API_URL_STATISTICS + "/grades" + "?uniqueId=" + uniqueId,{
            headers: authHeader(),
        })

    }
}

export default new ProjectService();