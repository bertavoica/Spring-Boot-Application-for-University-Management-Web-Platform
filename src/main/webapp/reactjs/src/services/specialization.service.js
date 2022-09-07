import axios from "axios";
import authHeader from './auth-header';

const API_URL = "http://localhost:8080/specialization-controller";
const API_URL_MEMBER = "http://localhost:8080/specialization-controller/member";

class SpecializationService {

    addSpecialization(name) {
        return axios.post(API_URL, {
            name,
        },{
            headers: authHeader(),
        });
    }

    updateSpecialization(name, uniqueId) {
        return axios.put(API_URL, {
            name,
            uniqueId,
        },{
            headers: authHeader(),
        });
    }

    getSpecializations() {
        return axios.get(API_URL,{
            headers: authHeader(),
        });
    }

    deleteSpecialization(uniqueId) {
        return axios.delete(API_URL + "?uniqueId=" + uniqueId, {
            headers: authHeader(),
        });
    }

    addMemberToSpecialization(specializationName, name, superior) {
        return axios.post(API_URL_MEMBER, {
            name,
            specializationName,
            superior
        },{
            headers: authHeader(),
        });
    }

    deleteMemberToSpecialization(specializationName, name) {
        return axios.delete(API_URL_MEMBER + "?stringName=" + specializationName + "&username=" + name, {
            headers: authHeader(),
        });
    }

    getGraphData(specializationName) {
        return axios.get(API_URL_MEMBER + "?stringName=" + specializationName,
            {
            headers: authHeader(),
        });

    }

    modifyMemberFromSpecialization(specializationName, name, superiorName) {
        return axios.put(API_URL_MEMBER, {
            name,
            specializationName,
            superiorName,
        },{
            headers: authHeader(),
        });

    }
}

export default new SpecializationService();