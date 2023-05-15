import React, {Component} from "react";

import UserService from "../services/user.service";
import StudentService from "../services/student.service";
import CourseService from "../services/course.service";
import {Badge, Button, Col, Container, Modal, Row, Table, InputGroup, FormControl, Dropdown, Toast, Form} from "react-bootstrap";
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {faEdit, faQuestionCircle, faTrash, faUserMinus, faUserPlus} from '@fortawesome/free-solid-svg-icons'
import SpecializationService from "../services/specialization.service";
import TeacherService from "../services/teacher.service";
import AdminService from "../services/admin.service";
import TitleService from "../services/title.service";
import authUser from "../services/auth-user";
import AuthService from "../services/auth.service";

export default class BoardAdmin extends Component {
    constructor(props) {
        super(props);

        this.onChangeCompleteName = this.onChangeCompleteName.bind(this);
        this.onChangeAbbreviation = this.onChangeAbbreviation.bind(this);
        this.onChangeDescription = this.onChangeDescription.bind(this);
        this.deleteCourse = this.deleteCourse.bind(this);
        this.saveCourse = this.saveCourse.bind(this);

        // Students
        this.saveStudent = this.saveStudent.bind(this);
        this.deleteStudent = this.deleteStudent.bind(this);
        this.updateStudent = this.updateStudent.bind(this);
        this.openUpdateStudentModal = this.openUpdateStudentModal.bind(this);
        this.onChangeStudentUsername = this.onChangeStudentUsername.bind(this);
        this.onChangeStudentEmailAddress = this.onChangeStudentEmailAddress.bind(this);
        this.onChangeStudentSpecialization = this.onChangeStudentSpecialization.bind(this);
        this.onChangeStudentCycle = this.onChangeStudentCycle.bind(this);
        this.onChangeStudentPassword = this.onChangeStudentPassword.bind(this);
        this.onChangeStudentGroup = this.onChangeStudentGroup.bind(this);
        this.selectStudentSpecialization = this.selectStudentSpecialization.bind(this);
        this.selectStudentEducationCycle = this.selectStudentEducationCycle.bind(this);
        this.selectStudentUserRole = this.selectStudentUserRole.bind(this);

        // Teachers
        this.onChangeTeacherUsername = this.onChangeTeacherUsername.bind(this);
        this.onChangeTeacherPassword = this.onChangeTeacherPassword.bind(this);
        this.onChangeTeacherEmailAddress = this.onChangeTeacherEmailAddress.bind(this);
        this.selectTeacherTitle = this.selectTeacherTitle.bind(this);
        this.saveTeacher = this.saveTeacher.bind(this);
        this.deleteTeacher = this.deleteTeacher.bind(this);
        this.openUpdateTeacherModal = this.openUpdateTeacherModal.bind(this);
        this.updateTeacher = this.updateTeacher.bind(this);
        this.selectTeacherUserRole = this.selectTeacherUserRole.bind(this);

        // Admins
        this.deleteAdmin = this.deleteAdmin.bind(this);
        this.openUpdateAdminModal = this.openUpdateAdminModal.bind(this);
        this.closeUpdateAdminModal = this.closeUpdateAdminModal.bind(this);
        this.selectAdminUserRole = this.selectAdminUserRole.bind(this);
        this.updateAdmin = this.updateAdmin.bind(this);

        // Responsible
        this.addResponsible = this.addResponsible.bind(this);
        this.removeResponsible = this.removeResponsible.bind(this);
        this.onChangeResponsibleUsername = this.onChangeResponsibleUsername.bind(this);
        this.addResponsibleDb = this.addResponsibleDb.bind(this);
        this.removeResponsibleDb = this.removeResponsibleDb.bind(this);
        this.getResponsible = this.getResponsible.bind(this);

        // Utils
        this.onChangeSearchUsername = this.onChangeSearchUsername.bind(this);
        this.getAllTitles = this.getAllTitles.bind(this);



        this.state = {
            content: "",
            coursesReady: false,
            courses: [],
            courseCompleteName: "",
            courseAbbreviation: "",
            courseDescription: "",
            isAddCourseModalOpen: false,
            users: [],
            selectedUser: "",
            infoMessage: "",
            showToast: false,
            errorToast: false,
            toastBg: 'danger',
            isAddStudentModalOpen: false,
            studentsReady: false,
            students: [],
            studentUsername: "",
            studentEmailAddress: "",
            studentSpecialization: "",
            studentCycle: "",
            studentGroup: "",
            studentPassword: "",
            selectedCourse: null,
            isAddResponsibleModalOpen: false,
            isRemoveResponsibleModalOpen: false,
            responsibleUsername: "",
            isGetResponsibleModalOpen: false,
            responsibleList: [],
            specializations: [],
            educationalCycles: ['Bachelor', 'Master', 'Doctoral'],
            userRoles: ['Student', 'Teacher', 'Admin'],
            teacherTitles: ['PROFESSOR', 'ASSOCIATE_PROFESSOR', 'HEAD_OF_WORKS', 'ASSISTANT_PROFESSOR'],
            isUpdateStudentMode: false,
            teachers: [],
            isAddTeacherModalOpen: false,
            isUpdateTeacherMode: false,
            teacherUsername: "",
            teacherPassword: "",
            teacherEmailAddress: "",
            teacherTitle: "",
            teacherRole: "",
            studentRole: "",
            admins: [],
            adminUsername: "",
            adminEmailAddress: "",
            adminRole: "",
            isUpdateAdminModalOpen: false,
            searchUsername: "",
            titles: [],
            languagePreference: "English",
            messages: {'Header': {'Romanian' : "Bun venit la meniul adminilor",
                    'English' : "Welcome to the admin board"},
                'Courses': {'Romanian' : "Cursuri",
                    'English' : "Courses"},
                'AddNewCourse': {'Romanian' : "Adaugă un curs nou",
                    'English' : "Add a new course to the database"},
                'Abbreviation': {'Romanian' : "Abrevierea",
                    'English' : "Abbreviation"},
                'Name': {'Romanian' : "Nume",
                    'English' : "Name"},
                'Close': {'Romanian' : "Închide",
                    'English' : "Close"},
                'Save': {'Romanian' : "Salvează",
                    'English' : "Save"},
                'Update': {'Romanian' : "Actualizează",
                    'English' : "Update"},
                'Actions': {'Romanian' : "Acțiuni",
                    'English' : "Actions"},
                'CompleteName': {'Romanian' : "Numele complet",
                    'English' : "Complete name"},
                'Remove': {'Romanian' : "Șterge studentul de la materie",
                    'English' : "Remove student from course"},
                'Teacher': {'Romanian' : "Profesor",
                    'English' : "Teacher"},
                'Description': {'Romanian' : "Descriere",
                    'English' : "Description"},
                'SearchUsername': {'Romanian' : "Caută după nume utilizator",
                    'English' : "Search by username"},
                'Students': {'Romanian' : "Studenți",
                    'English' : "Students"},
                'AddStudent': {'Romanian' : "Adaugă student",
                    'English' : "Add new student"},
                'AddNewStudent': {'Romanian' : "Adaugă un student nou in baza de date",
                    'English' : "Add a new student to the database"},
                'UpdateStudent': {'Romanian' : "Actualizează student din baza de date",
                    'English' : "Update student from the database"},
                'EducationCycle': {'Romanian' : "Ciclu educațional",
                    'English' : "Education cycle"},
                'Username': {'Romanian' : "Nume utilizator",
                    'English' : "Username"},
                'Password': {'Romanian' : "Parolă",
                    'English' : "Password"},
                'Email': {'Romanian' : "Adresă de email",
                    'English' : "Email address"},
                'Specialization': {'Romanian' : "Specializare",
                    'English' : "Specialization"},
                'AddCourse': {'Romanian' : "Adaugă un curs nou",
                    'English' : "Add new course"},
                'UserRole': {'Romanian' : "Rol utilizator",
                    'English' : "User role"},
                'Group': {'Romanian' : "Grup",
                    'English' : "Group"},
                'AddResponsible': {'Romanian' : "Adaugă un nou responsabil pentru curs",
                    'English' : "Add a new responsible for course"},
                'RemoveResponsible': {'Romanian' : "Șterge responsabil de curs",
                    'English' : "Remove responsible for course"},
                'Delete': {'Romanian' : "Șterge",
                    'English' : "Delete"},
                'Teachers': {'Romanian' : "Profesori",
                    'English' : "Teachers"},
                'AddTeacher': {'Romanian' : "Adaugă un nou profesor",
                    'English' : "Add new teacher"},
                'AddNewTeacher': {'Romanian' : "Adaugă un nou profesor în baza de date",
                    'English' : "Add a new teacher to the database"},
                'UpdateTeacher': {'Romanian' : "Actualizează profesor",
                    'English' : "Update teacher"},
                'Title': {'Romanian' : "Rol",
                    'English' : "Title"},
                'Admins': {'Romanian' : "Admini",
                    'English' : "Admins"},
                'UpdateAdmin': {'Romanian' : "Actualizează un utilizator din baza de date",
                    'English' : "Update admin from the database"},
                'DeleteCourse': {'Romanian' : "Ești sigur ca vrei sa ștergi cursul ",
                    'English' : "Are you sure you want to delete course "},
                'DeleteUser': {'Romanian' : "Ești sigur ca vrei sa ștergi utilizatorul ",
                    'English' : "Are you sure you want to delete user "},
                'Responsible': {'Romanian' : "Responsabili de curs",
                    'English' : "Responsible"},

            },

        };
    }

    getAllTitles() {
        TitleService.getTitles(authUser()).then(
            response => {
                this.state.titles = [];
                for (let i = 0; i < response.data.length; i++) {
                    this.state.titles.push(response.data[i].name);
                }
            },
            error => {
                this.displayMessage((error.response &&
                    error.response.data &&
                    error.response.data.message) ||
                    error.message ||
                    error.toString(), true);
            });

    }

    openGetResponsibleModal = () => this.setState({isGetResponsibleModalOpen: true});
    closeGetResponsibleModal = () => this.setState({isGetResponsibleModalOpen: false});

    openAddResponsibleModal = () => this.setState({isAddResponsibleModalOpen: true});
    closeAddResponsibleModal = () => this.setState({isAddResponsibleModalOpen: false});

    openRemoveResponsibleModal = () => this.setState({isRemoveResponsibleModalOpen: true});
    closeRemoveResponsibleModal = () => this.setState({isRemoveResponsibleModalOpen: false});

    openAddStudentModal = () => this.setState({
        isAddStudentModalOpen: true,
        isUpdateStudentMode: false,
        studentUsername: "",
        studentEmailAddress: "",
        studentCycle: "",
        studentSpecialization: "",
    });
    closeAddStudentModal = () => this.setState({isAddStudentModalOpen: false});

    openAddTeacherModal = () => this.setState({
        isAddTeacherModalOpen: true,
        isUpdateTeacherMode: false,
        teacherUsername: "",
        teacherEmailAddress: "",
        teacherTitle: "",
    });
    closeAddTeacherModal = () => this.setState({isAddTeacherModalOpen: false});

    closeUpdateAdminModal = () => this.setState({isUpdateAdminModalOpen: false});

    openModal = () => this.setState({isAddCourseModalOpen: true});
    closeModal = () => this.setState({isAddCourseModalOpen: false});
    closeToast = () => this.setState({showToast: false});
    displayMessage = (message, error) => {
        this.setState({
            infoMessage: message,
            showToast: true,
            errorToast: error,
        });
        setTimeout(() => this.setState({showToast: false}), 5000);
    }

    selectTeacherTitle(e) {
        this.setState({
            teacherTitle: e,
        });
    }

    selectAdminUserRole(e) {
        this.setState({
            adminRole: e,
        });
    }

    selectStudentUserRole(e) {
        this.setState({
            studentRole: e,
        });
    }


    selectTeacherUserRole(e) {
        this.setState({
            teacherRole: e,
        });
    }

    selectStudentSpecialization(e) {
        this.setState({
            studentSpecialization: e,
        });
    }

    selectStudentEducationCycle(e) {
        this.setState({
            studentCycle: e,
        });
    }

    saveTeacher(e) {
        TeacherService.addTeacher(this.state.teacherUsername, this.state.teacherEmailAddress, this.state.teacherPassword, this.state.teacherTitle).then(
            response => {
                this.displayMessage('Successfully added teacher with username ' + this.state.teacherUsername, false);
                this.setState({
                    teachers: response.data,
                    teacherUsername: "",
                    teacherEmailAddress: "",
                    teacherPassword: "",
                    teacherTitle: "",
                });
                this.closeAddTeacherModal();
            },
            error => {
                this.displayMessage( (error.response &&
                    error.response.data &&
                    error.response.data.message) ||
                    error.message ||
                    error.toString(), true);
            });
    }

    saveStudent(e) {
        StudentService.addStudent(this.state.studentUsername, this.state.studentEmailAddress, this.state.studentSpecialization,
            this.state.studentCycle, this.state.studentPassword, this.state.studentGroup).then(
            response => {
                this.displayMessage('Successfully added student with username ' + this.state.studentUsername, false);
                this.setState({
                    students: response.data,
                    studentUsername: "",
                    studentEmailAddress: "",
                    studentSpecialization: "",
                    studentCycle: "",
                    studentGroup: "",
                    studentPassword: "",
                });
                this.closeAddStudentModal();
            },
            error => {
                this.displayMessage(error.response.data, true);
            });
    }

    addResponsibleDb(e) {
        CourseService.addResponsible(this.state.selectedCourse, this.state.responsibleUsername).then(
            response => {
                this.displayMessage(response.data, false);
                this.setState({
                    selectedCourse: null,
                    responsibleUsername: "",
                });
                this.closeAddResponsibleModal();
            },
            error => {
                this.displayMessage(error.response.data, true);
            });
    }

    removeResponsibleDb(e) {
        CourseService.removeResponsible(this.state.selectedCourse, this.state.responsibleUsername).then(
            response => {
                this.displayMessage(response.data, false);
                this.setState({
                    selectedCourse: null,
                    responsibleUsername: "",
                });
                this.closeRemoveResponsibleModal();
            },
            error => {
                this.displayMessage(error.response.data, true);
            });
    }


    saveCourse(e) {
        CourseService.addCourse(this.state.courseCompleteName, this.state.courseAbbreviation, this.state.courseDescription).then(
            response => {
                this.setState({
                    courses: response.data,
                    courseCompleteName: "",
                    courseAbbreviation: "",
                    courseDescription: "",
                });
                this.closeModal();
            },
            error => {

            });
    }

    componentDidMount() {

        this.getLanguagePreference();
        this.getAllStudents();
        this.getAllTeachers();
        this.getAllAdmins();
        this.getAllTitles();

        SpecializationService.getSpecializations().then(
            response => {
                this.setState({
                    specializations: response.data,
                });
            },
            error => {
                this.displayMessage(error.response.data, true);
            });

        CourseService.getCourses().then(
            response => {
                this.setState({
                    coursesReady: true,
                    courses: response.data,
                });
            },
            error => {
                this.displayMessage(
                    (error.response &&
                        error.response.data &&
                        error.response.data.message) ||
                    error.message ||
                    error.toString(), true);
            }
        );
    }

    getLanguagePreference() {
        let currentUser = AuthService.getCurrentUser();

        if (!currentUser)
            return;

        UserService.getLanguage(currentUser.username).then(
            response => {
                this.setState({
                    languagePreference: response.data,
                });
            },
            error => {
                this.displayMessage((error.response &&
                    error.response.data &&
                    error.response.data.message) ||
                    error.message ||
                    error.toString(), true);
            });
    }

    getAllTeachers(inputUsername = "") {
        TeacherService.getAllTeachers(inputUsername, "").then(
            response => {
                this.setState({
                    teachers: response.data,
                });
            },
            error => {
                this.displayMessage(
                    (error.response &&
                        error.response.data &&
                        error.response.data.message) ||
                    error.message ||
                    error.toString(), true);
            }
        );
    }

    getAllAdmins(inputUsername = "") {
        AdminService.getAllAdmins(inputUsername).then(
            response => {
                this.setState({
                    admins: response.data,
                });
            },
            error => {
                this.displayMessage(
                    (error.response &&
                        error.response.data &&
                        error.response.data.message) ||
                    error.message ||
                    error.toString(), true);
            }
        );
    }

    getAllStudents(inputUsername = "") {
        StudentService.getAllStudents(inputUsername).then(
            response => {
                this.setState({
                    students: response.data,
                    studentsReady: true,
                });
            },
            error => {
                this.setState(
                    (error.response &&
                        error.response.data &&
                        error.response.data.message) ||
                    error.message ||
                    error.toString());
            }
        );
    }

    onChangeSearchUsername(e) {
        this.setState({
            searchUsername: e.target.value
        });

        this.getAllStudents(e.target.value);
        this.getAllTeachers(e.target.value);
        this.getAllAdmins(e.target.value);
    }

    onChangeCompleteName(e) {
        this.setState({
            courseCompleteName: e.target.value
        });
    }

    onChangeAbbreviation(e) {
        this.setState({
            courseAbbreviation: e.target.value
        });
    }

    onChangeDescription(e) {
        this.setState({
            courseDescription: e.target.value
        });
    }

    onChangeTeacherUsername(e) {
        this.setState({
            teacherUsername: e.target.value
        });
    }

    onChangeTeacherPassword(e) {
        this.setState({
            teacherPassword: e.target.value
        });
    }


    onChangeTeacherEmailAddress(e) {
        this.setState({
            teacherEmailAddress: e.target.value
        });
    }

    onChangeStudentUsername(e) {
        this.setState({
            studentUsername: e.target.value
        });
    }

    onChangeStudentPassword(e) {
        this.setState({
            studentPassword: e.target.value
        });
    }

    onChangeStudentEmailAddress(e) {
        this.setState({
            studentEmailAddress: e.target.value
        });
    }

    onChangeStudentSpecialization(e) {
        this.setState({
            studentSpecialization: e.target.value
        });
    }

    onChangeStudentCycle(e) {
        this.setState({
            studentCycle: e.target.value
        });
    }

    onChangeStudentGroup(e) {
        this.setState({
            studentGroup: e.target.value
        });
    }

    onChangeResponsibleUsername(e) {
        this.setState({
            responsibleUsername: e.target.value
        });
    }

    deleteCourse(e) {
        if (window.confirm(this.state.messages['DeleteCourse'][this.state.languagePreference] + e.completeName)) {
            CourseService.deleteCourse(e.uniqueId).then(
                response => {
                    this.setState({
                        courses: response.data,
                    });
                },
                error => {
                });
        }
    }

    addResponsible(e) {
        this.setState({
            selectedCourse: e,
        });
        this.openAddResponsibleModal();
    }

    getResponsible(e) {
        this.setState({
            selectedCourse: e,
        });

        CourseService.getResponsible(e).then(
            response => {
                this.setState({
                    responsibleList: response.data,
                });
                this.openGetResponsibleModal();
            },
            error => {
                this.displayMessage(error.response.data, true);
            });


    }

    removeResponsible(e) {
        this.setState({
            selectedCourse: e,
        });

        this.openRemoveResponsibleModal();
    }

    deleteStudent(e) {
        if (window.confirm(this.state.messages['DeleteUser'][this.state.languagePreference] + e.username)) {
            StudentService.deleteStudent(e.username).then(
                response => {
                    this.setState({
                        students: response.data,
                    });
                },
                error => {
                });
        }

    }

    deleteAdmin(e) {
        if (window.confirm(this.state.messages['DeleteUser'][this.state.languagePreference] + e.username)) {
            AdminService.deleteAdmin(e.username).then(
                response => {
                    this.setState({
                        admins: response.data,
                    });
                },
                error => {
                });
        }
    }

    deleteTeacher(e) {
        if (window.confirm(this.state.messages['DeleteUser'][this.state.languagePreference] + e.username)) {
            TeacherService.deleteTeacher(e.username).then(
                response => {
                    this.setState({
                        teachers: response.data,
                    });
                },
                error => {
                });
        }
    }

    openUpdateStudentModal(e) {
        let specializationCorrect = false;
        this.setState({
            isAddStudentModalOpen: true,
            isUpdateStudentMode: true,
            studentUsername: e.username,
            studentEmailAddress: e.emailAddress,
            studentGroup: e.group,
            studentRole: "Student",
        });

        if (this.state.educationalCycles.includes(e.educationCycle))
            this.setState({
                studentCycle: e.educationCycle,
            });
        else {
            this.setState({
                studentCycle: "",
            });
        }

        for (let i = 0; i < this.state.specializations.length; i++) {
            if (this.state.specializations[i].name === e.specialization) {
                specializationCorrect = true;
                this.setState({
                    studentSpecialization: e.specialization,
                });
            }
        }
        if (!specializationCorrect) {
            this.setState({
                studentSpecialization: "",
            });
        }
    }
    openUpdateAdminModal(e) {
        this.setState({
            isUpdateAdminModalOpen: true,
            adminUsername: e.username,
            adminEmailAddress: e.emailAddress,
            adminRole: "Admin",
        });
    }

    openUpdateTeacherModal(e) {
        this.setState({
            isAddTeacherModalOpen: true,
            isUpdateTeacherMode: true,
            teacherUsername: e.username,
            teacherEmailAddress: e.emailAddress,
            teacherRole: "Teacher",
        });

        if (this.state.titles.includes(e.title))
            this.setState({
                teacherTitle: e.title,
            });
        else {
            this.setState({
                teacherTitle: "",
            });
        }
    }

    updateStudent() {
        StudentService.updateStudent(this.state.studentUsername, this.state.studentSpecialization,
            this.state.studentCycle, this.state.studentGroup, this.state.studentRole).then(
            response => {
                this.displayMessage('Successfully updated student with username ' + this.state.studentUsername, false);
                this.setState({
                    students: response.data,
                    studentUsername: "",
                    studentEmailAddress: "",
                    studentSpecialization: "",
                    studentCycle: "",
                    studentGroup: "",
                    studentRole: "",
                });
                this.closeAddStudentModal();
                this.getAllTeachers();
                this.getAllAdmins();
            },
            error => {
                this.displayMessage( (error.response &&
                    error.response.data &&
                    error.response.data.message) ||
                    error.message ||
                    error.toString(), true);
            });
    }

    updateTeacher() {
        TeacherService.updateTeacher(this.state.teacherUsername, this.state.teacherTitle, this.state.teacherRole).then(
            response => {
                this.displayMessage('Successfully updated teacher with username ' + this.state.teacherUsername, false);
                this.setState({
                    teachers: response.data,
                    teacherUsername: "",
                    teacherTitle: "",
                });
                this.closeAddTeacherModal();
                this.getAllStudents();
                this.getAllAdmins();
            },
            error => {
                this.displayMessage( (error.response &&
                    error.response.data &&
                    error.response.data.message) ||
                    error.message ||
                    error.toString(), true);
            });

    }

    updateAdmin() {
        AdminService.updateAdmin(this.state.adminUsername, this.state.adminRole).then(
            response => {
                this.displayMessage('Successfully updated admin with username ' + this.state.adminUsername, false);
                this.setState({
                    teachers: response.data,
                    adminUsername: "",
                    adminRole: "",
                });
                this.closeUpdateAdminModal();
                this.getAllStudents();
                this.getAllTeachers();
            },
            error => {
                this.displayMessage( (error.response &&
                    error.response.data &&
                    error.response.data.message) ||
                    error.message ||
                    error.toString(), true);
            });
    }


    render() {

        return (
            <div>
                <div className="container">
                    <header className="jumbotron">
                        <h3>{this.state.messages['Header'][this.state.languagePreference]}</h3>
                    </header>
                </div>

                <style type="text/css">
                    {`
                        .toast {
                            position:fixed;
                            top:70px;
                            right:40px;
                        }
                    `}
                </style>

                <Container>
                    {(this.state.coursesReady) ?
                        <div>

                            {(this.state.showToast) ?
                                <Row>
                                    <Toast onClose={this.closeToast} bg="danger" className={this.state.errorToast ? "bg-danger" : ""}>
                                        <Toast.Header>
                                            <img className="rounded me-2" alt=""/>
                                            <strong className="me-auto">Info message</strong>
                                        </Toast.Header>
                                        <Toast.Body>{this.state.infoMessage}</Toast.Body>
                                    </Toast>
                                </Row>
                                : null}

                            <Row style={{margin: 35}}>
                                <Col lg="9">
                                    <h2>{this.state.messages['Courses'][this.state.languagePreference]}</h2>
                                </Col>
                                <Col lg="3">
                                    <button className="btn btn-primary btn-block" onClick={this.openModal}>
                                        <span>{this.state.messages['AddCourse'][this.state.languagePreference]}</span>
                                    </button>
                                </Col>

                                <Modal show={this.state.isAddCourseModalOpen} onHide={this.closeModal} size="lg">
                                    <Modal.Header closeButton>
                                        <Modal.Title>{this.state.messages['AddNewCourse'][this.state.languagePreference]}</Modal.Title>
                                    </Modal.Header>
                                    <Modal.Body>
                                        <Form>
                                            <Form.Group>
                                                <Form.Label>{this.state.messages['CompleteName'][this.state.languagePreference]}</Form.Label>
                                                <Form.Control
                                                    onChange={this.onChangeCompleteName}
                                                    value={this.state.courseCompleteName}
                                                    type='text'/>
                                            </Form.Group>
                                            <Form.Group>
                                                <Form.Label>{this.state.messages['Abbreviation'][this.state.languagePreference]}</Form.Label>
                                                <Form.Control
                                                    onChange={this.onChangeAbbreviation}
                                                    value={this.state.courseAbbreviation}
                                                    type='text'/>
                                            </Form.Group>
                                            <Form.Group>
                                                <Form.Label>{this.state.messages['Description'][this.state.languagePreference]}</Form.Label>
                                                <Form.Control
                                                    onChange={this.onChangeDescription}
                                                    value={this.state.courseDescription}
                                                    type='text'/>
                                            </Form.Group>
                                        </Form>
                                    </Modal.Body>
                                    <Modal.Footer>
                                        <Button variant="secondary" onClick={this.closeModal}>{this.state.messages['Close'][this.state.languagePreference]}</Button>
                                        <Button variant="primary" onClick={this.saveCourse}>{this.state.messages['Save'][this.state.languagePreference]}</Button>
                                    </Modal.Footer>
                                </Modal>
                            </Row>
                            <Row>
                                <Col lg="12">
                                    <Table striped bordered hover>
                                        <thead>
                                        <tr>
                                            <th>#</th>
                                            <th>{this.state.messages['CompleteName'][this.state.languagePreference]}</th>
                                            <th>{this.state.messages['Abbreviation'][this.state.languagePreference]}</th>
                                            <th>{this.state.messages['Description'][this.state.languagePreference]}</th>
                                            <th>{this.state.messages['Actions'][this.state.languagePreference]}</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        {this.state.courses.map((data, i) => (
                                            <tr>
                                                <td>{i + 1}</td>
                                                <td>{data.completeName}</td>
                                                <td>{data.abbreviation}</td>
                                                <td>{data.description}</td>
                                                <td>
                                                    <div className="btn-toolbar">
                                                        <Button className="btn btn-dark mx-2" onClick={() => this.deleteCourse(data)}><FontAwesomeIcon icon={faTrash} /></Button>
                                                        <Button className="btn btn-success mx-2" onClick={() => this.addResponsible(data)}><FontAwesomeIcon icon={faUserPlus} /></Button>
                                                        <Button className="btn btn-secondary mx-2" onClick={() => this.removeResponsible(data)}><FontAwesomeIcon icon={faUserMinus} /></Button>
                                                        <Button className="btn btn-info mx-2" onClick={() => this.getResponsible(data)}><FontAwesomeIcon icon={faQuestionCircle} /></Button>
                                                    </div>
                                                </td>
                                            </tr>
                                        ))}
                                        </tbody>
                                    </Table>
                                </Col>
                            </Row>
                        </div> : null}
                    <Row style={{margin: 35}}>
                        <Col lg="12">
                            <InputGroup className="mb-3">
                                <FormControl
                                    placeholder={this.state.messages['SearchUsername'][this.state.languagePreference]}
                                    aria-label="Username"
                                    autoComplete="off"
                                    onChange={this.onChangeSearchUsername}
                                    value={this.state.searchUsername}
                                />
                            </InputGroup>
                        </Col>
                    </Row>

                    <Row style={{margin: 35}}>
                        <Col lg="9">
                            <h2>{this.state.messages['Students'][this.state.languagePreference]}</h2>
                        </Col>
                        <Col lg="3">
                            <button className="btn btn-primary btn-block" onClick={this.openAddStudentModal}>
                                <span>{this.state.messages['AddStudent'][this.state.languagePreference]}</span>
                            </button>
                        </Col>

                        <Modal show={this.state.isAddStudentModalOpen} onHide={this.closeAddStudentModal} size="lg">
                                    <Modal.Header closeButton>
                                        {(this.state.isUpdateStudentMode) ?
                                            <Modal.Title>{this.state.messages['UpdateStudent'][this.state.languagePreference]}</Modal.Title>
                                            :
                                            <Modal.Title>{this.state.messages['AddNewStudent'][this.state.languagePreference]}</Modal.Title>
                                        }
                                    </Modal.Header>
                                    <Modal.Body>
                                        <Form>
                                            <Form.Group>
                                                <Form.Label>{this.state.messages['Username'][this.state.languagePreference]}</Form.Label>
                                                <Form.Control
                                                    disabled={this.state.isUpdateStudentMode}
                                                    onChange={this.onChangeStudentUsername}
                                                    value={this.state.studentUsername}
                                                    type='text'/>
                                            </Form.Group>
                                            {(!this.state.isUpdateStudentMode) ?
                                                <Form.Group>
                                                    <Form.Label>{this.state.messages['Password'][this.state.languagePreference]}</Form.Label>
                                                    <Form.Control
                                                        autocomplete="new-password"
                                                        onChange={this.onChangeStudentPassword}
                                                        value={this.state.studentPassword}
                                                        type='password'/>
                                                </Form.Group>
                                                :
                                                null
                                            }
                                            {(!this.state.isUpdateStudentMode) ?
                                                <Form.Group>
                                                    <Form.Label>{this.state.messages['Email'][this.state.languagePreference]}</Form.Label>
                                                    <Form.Control
                                                        onChange={this.onChangeStudentEmailAddress}
                                                        value={this.state.studentEmailAddress}
                                                        type='email'/>
                                                </Form.Group>
                                                :
                                                null
                                            }


                                            <Form.Group>
                                                <Form.Label>{this.state.messages['EducationCycle'][this.state.languagePreference]}</Form.Label>
                                                <Dropdown>
                                                    <Dropdown.Toggle variant="light" id="dropdown-basic" style={{width: "100%"}}>
                                                        {this.state.studentCycle}
                                                    </Dropdown.Toggle>

                                                    <Dropdown.Menu>
                                                        {this.state.educationalCycles.map((data, i) => (
                                                            <Dropdown.Item onClick={() => this.selectStudentEducationCycle(data)}>{data}</Dropdown.Item>
                                                        ))}
                                                    </Dropdown.Menu>
                                                </Dropdown>
                                            </Form.Group>

                                            <Form.Group>
                                                <Form.Label>{this.state.messages['Specialization'][this.state.languagePreference]}</Form.Label>
                                                <Dropdown>
                                                    <Dropdown.Toggle variant="light" id="dropdown-basic" style={{width: "100%"}}>
                                                        {this.state.studentSpecialization}
                                                    </Dropdown.Toggle>

                                                    <Dropdown.Menu>
                                                        {this.state.specializations.map((data, i) => (
                                                            <Dropdown.Item onClick={() => this.selectStudentSpecialization(data.name)}>{data.name}</Dropdown.Item>
                                                        ))}
                                                    </Dropdown.Menu>
                                                </Dropdown>
                                            </Form.Group>
                                            <Form.Group>
                                                <Form.Label>{this.state.messages['Group'][this.state.languagePreference]}</Form.Label>
                                                <Form.Control
                                                    onChange={this.onChangeStudentGroup}
                                                    value={this.state.studentGroup}
                                                    type='text'/>
                                            </Form.Group>

                                            {(this.state.isUpdateStudentMode) ?
                                                <Form.Group>
                                                    <Form.Label>{this.state.messages['UserRole'][this.state.languagePreference]}</Form.Label>
                                                    <Dropdown>
                                                        <Dropdown.Toggle variant="light" id="dropdown-basic" style={{width: "100%"}}>
                                                            {this.state.studentRole}
                                                        </Dropdown.Toggle>

                                                        <Dropdown.Menu>
                                                            {this.state.userRoles.map((data, i) => (
                                                                <Dropdown.Item onClick={() => this.selectStudentUserRole(data)}>{data}</Dropdown.Item>
                                                            ))}
                                                        </Dropdown.Menu>
                                                    </Dropdown>
                                                </Form.Group>
                                                :
                                                null
                                            }
                                        </Form>
                                    </Modal.Body>
                                    <Modal.Footer>
                                        <Button variant="secondary" onClick={this.closeAddStudentModal}>{this.state.messages['Close'][this.state.languagePreference]}</Button>
                                        {(this.state.isUpdateStudentMode) ?
                                            <Button variant="warning" onClick={this.updateStudent}>{this.state.messages['Update'][this.state.languagePreference]}</Button>
                                            :
                                            <Button variant="primary" onClick={this.saveStudent}>{this.state.messages['Save'][this.state.languagePreference]}</Button>
                                        }

                                    </Modal.Footer>
                                </Modal>
                        <Modal show={this.state.isAddResponsibleModalOpen} onHide={this.closeAddResponsibleModal} size="lg">
                                    <Modal.Header closeButton>
                                        <Modal.Title>{this.state.messages['AddResponsible'][this.state.languagePreference]}</Modal.Title>
                                    </Modal.Header>
                                    <Modal.Body>
                                        <Form>
                                            <Form.Group>
                                                <Form.Label>{this.state.messages['Username'][this.state.languagePreference]}</Form.Label>
                                                <Form.Control
                                                    onChange={this.onChangeResponsibleUsername}
                                                    value={this.state.responsibleUsername}
                                                    type='text'/>
                                            </Form.Group>
                                        </Form>
                                    </Modal.Body>
                                    <Modal.Footer>
                                        <Button variant="secondary" onClick={this.closeAddResponsibleModal}>{this.state.messages['Close'][this.state.languagePreference]}</Button>
                                        <Button variant="primary" onClick={this.addResponsibleDb}>{this.state.messages['Save'][this.state.languagePreference]}</Button>
                                    </Modal.Footer>
                                </Modal>
                        <Modal show={this.state.isRemoveResponsibleModalOpen} onHide={this.closeRemoveResponsibleModal} size="lg">
                                    <Modal.Header closeButton>
                                        <Modal.Title>{this.state.messages['RemoveResponsible'][this.state.languagePreference]}</Modal.Title>
                                    </Modal.Header>
                                    <Modal.Body>
                                        <Form>
                                            <Form.Group>
                                                <Form.Label>{this.state.messages['Username'][this.state.languagePreference]}</Form.Label>
                                                <Form.Control
                                                    onChange={this.onChangeResponsibleUsername}
                                                    value={this.state.responsibleUsername}
                                                    type='text'/>
                                            </Form.Group>
                                        </Form>
                                    </Modal.Body>
                                    <Modal.Footer>
                                        <Button variant="secondary" onClick={this.closeRemoveResponsibleModal}>{this.state.messages['Close'][this.state.languagePreference]}</Button>
                                        <Button variant="danger" onClick={this.removeResponsibleDb}>{this.state.messages['Delete'][this.state.languagePreference]}</Button>
                                    </Modal.Footer>
                                </Modal>
                        <Modal show={this.state.isGetResponsibleModalOpen} onHide={this.closeGetResponsibleModal} size="lg">
                            <Modal.Header closeButton>
                                <Modal.Title>{this.state.messages['Responsible'][this.state.languagePreference]}</Modal.Title>
                            </Modal.Header>
                            <Modal.Body>
                                {(this.state.responsibleList != null) ?
                                    <ol>
                                        {this.state.responsibleList.map((data, i) => (
                                            <li>{data}</li>
                                        ))}
                                    </ol> : null
                                }
                            </Modal.Body>
                            <Modal.Footer>
                                <Button variant="secondary" onClick={this.closeGetResponsibleModal}>{this.state.messages['Close'][this.state.languagePreference]}</Button>
                            </Modal.Footer>
                        </Modal>
                    </Row>
                    {(this.state.students.length > 0) ?
                        <Row>
                            <Col lg="12">
                                <Table striped bordered hover>
                                    <thead>
                                    <tr>
                                        <th>#</th>
                                        <th>{this.state.messages['Username'][this.state.languagePreference]}</th>
                                        <th>{this.state.messages['Email'][this.state.languagePreference]}</th>
                                        <th>{this.state.messages['EducationCycle'][this.state.languagePreference]}</th>
                                        <th>{this.state.messages['Specialization'][this.state.languagePreference]}</th>
                                        <th>{this.state.messages['Group'][this.state.languagePreference]}</th>
                                        <th>{this.state.messages['Actions'][this.state.languagePreference]}</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    {this.state.students.map((data, i) => (
                                        <tr>
                                            <td>{i + 1}</td>
                                            <td>{data.username}</td>
                                            <td>{data.emailAddress}</td>
                                            <td>{data.educationCycle}</td>
                                            <td>{data.specialization}</td>
                                            <td>{data.group}</td>
                                            <td>
                                                <div className="btn-toolbar">
                                                    <Button className="btn btn-dark mx-2" onClick={() => this.deleteStudent(data)}><FontAwesomeIcon icon={faTrash} /></Button>
                                                    <Button className="btn btn-warning mx-2" onClick={() => this.openUpdateStudentModal(data)}><FontAwesomeIcon icon={faEdit} /></Button>
                                                </div>
                                            </td>
                                        </tr>
                                    ))}
                                    </tbody>
                                </Table>
                            </Col>
                        </Row> : null }
                        <Row style={{margin: 35}}>
                            <Col lg="9">
                                <h2>{this.state.messages['Teachers'][this.state.languagePreference]}</h2>
                            </Col>
                            <Col lg="3">
                                <button className="btn btn-primary btn-block" onClick={this.openAddTeacherModal}>
                                    <span>{this.state.messages['AddTeacher'][this.state.languagePreference]}</span>
                                </button>
                            </Col>
                        </Row>
                    <Row>
                        <Modal show={this.state.isAddTeacherModalOpen} onHide={this.closeAddTeacherModal} size="lg">
                                    <Modal.Header closeButton>
                                        {(this.state.isUpdateTeacherMode) ?
                                            <Modal.Title>{this.state.messages['UpdateTeacher'][this.state.languagePreference]}</Modal.Title>
                                            :
                                            <Modal.Title>{this.state.messages['AddNewTeacher'][this.state.languagePreference]}</Modal.Title>
                                        }
                                    </Modal.Header>
                                    <Modal.Body>
                                        <Form>
                                            <Form.Group>
                                                <Form.Label>{this.state.messages['Username'][this.state.languagePreference]}</Form.Label>
                                                <Form.Control
                                                    disabled={this.state.isUpdateTeacherMode}
                                                    onChange={this.onChangeTeacherUsername}
                                                    value={this.state.teacherUsername}
                                                    type='text'/>
                                            </Form.Group>
                                            {(!this.state.isUpdateTeacherMode) ?
                                                <Form.Group>
                                                    <Form.Label>{this.state.messages['Password'][this.state.languagePreference]}</Form.Label>
                                                    <Form.Control
                                                        autocomplete="new-password"
                                                        onChange={this.onChangeTeacherPassword}
                                                        value={this.state.teacherPassword}
                                                        type='password'/>
                                                </Form.Group>
                                                :
                                                null
                                            }

                                            {(!this.state.isUpdateTeacherMode) ?
                                                <Form.Group>
                                                    <Form.Label>{this.state.messages['Email'][this.state.languagePreference]}</Form.Label>
                                                    <Form.Control
                                                        onChange={this.onChangeTeacherEmailAddress}
                                                        value={this.state.teacherEmailAddress}
                                                        type='email'/>
                                                </Form.Group>
                                                :
                                                null
                                            }
                                            <Form.Group>
                                                <Form.Label>{this.state.messages['Title'][this.state.languagePreference]}</Form.Label>
                                                <Dropdown>
                                                    <Dropdown.Toggle variant="light" id="dropdown-basic" style={{width: "100%"}}>
                                                        {this.state.teacherTitle}
                                                    </Dropdown.Toggle>

                                                    <Dropdown.Menu>
                                                        {this.state.titles.map((data, i) => (
                                                            <Dropdown.Item onClick={() => this.selectTeacherTitle(data)}>{data}</Dropdown.Item>
                                                        ))}
                                                    </Dropdown.Menu>
                                                </Dropdown>
                                            </Form.Group>

                                            {(this.state.isUpdateTeacherMode) ?
                                                <Form.Group>
                                                    <Form.Label>{this.state.messages['UserRole'][this.state.languagePreference]}</Form.Label>
                                                    <Dropdown>
                                                        <Dropdown.Toggle variant="light" id="dropdown-basic" style={{width: "100%"}}>
                                                            {this.state.teacherRole}
                                                        </Dropdown.Toggle>

                                                        <Dropdown.Menu>
                                                            {this.state.userRoles.map((data, i) => (
                                                                <Dropdown.Item onClick={() => this.selectTeacherUserRole(data)}>{data}</Dropdown.Item>
                                                            ))}
                                                        </Dropdown.Menu>
                                                    </Dropdown>
                                                </Form.Group>
                                                :
                                                null
                                            }

                                        </Form>
                                    </Modal.Body>
                                    <Modal.Footer>
                                        <Button variant="secondary" onClick={this.closeAddTeacherModal}>{this.state.messages['Close'][this.state.languagePreference]}</Button>
                                        {(this.state.isUpdateTeacherMode) ?
                                            <Button variant="warning" onClick={this.updateTeacher}>{this.state.messages['Update'][this.state.languagePreference]}</Button>
                                            :
                                            <Button variant="primary" onClick={this.saveTeacher}>{this.state.messages['Save'][this.state.languagePreference]}</Button>
                                        }
                                    </Modal.Footer>
                                </Modal>
                    </Row>
                    {(this.state.teachers.length > 0) ?
                        <Row>
                            <Col lg="12">
                                <Table striped bordered hover>
                                    <thead>
                                    <tr>
                                        <th>#</th>
                                        <th>{this.state.messages['Username'][this.state.languagePreference]}</th>
                                        <th>{this.state.messages['Email'][this.state.languagePreference]}</th>
                                        <th>{this.state.messages['Title'][this.state.languagePreference]}</th>
                                        <th>{this.state.messages['Actions'][this.state.languagePreference]}</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    {this.state.teachers.map((data, i) => (
                                        <tr>
                                            <td>{i + 1}</td>
                                            <td>{data.username}</td>
                                            <td>{data.emailAddress}</td>
                                            <td>{data.title}</td>
                                            <td>
                                                <div className="btn-toolbar">
                                                    <Button className="btn btn-dark mx-2"
                                                            onClick={() => this.deleteTeacher(data)}><FontAwesomeIcon
                                                        icon={faTrash}/></Button>
                                                    <Button className="btn btn-warning mx-2"
                                                            onClick={() => this.openUpdateTeacherModal(data)}><FontAwesomeIcon
                                                        icon={faEdit}/></Button>
                                                </div>
                                            </td>
                                        </tr>
                                    ))}
                                    </tbody>
                                </Table>
                            </Col>
                        </Row> : null }
                    {(this.state.admins.length > 0) ?
                        <div>
                            <Row style={{margin: 35}}>
                                <Col lg="9">
                                    <h2>{this.state.messages['Admins'][this.state.languagePreference]}</h2>
                                </Col>
                            </Row>
                            <Row>
                                <Modal show={this.state.isUpdateAdminModalOpen} onHide={this.closeUpdateAdminModal} size="lg">
                                    <Modal.Header closeButton>
                                        <Modal.Title>{this.state.messages['UpdateAdmin'][this.state.languagePreference]}</Modal.Title>
                                    </Modal.Header>
                                    <Modal.Body>
                                        <Form>
                                            <Form.Group>
                                                <Form.Label>{this.state.messages['Username'][this.state.languagePreference]}</Form.Label>
                                                <Form.Control
                                                    disabled={true}
                                                    value={this.state.adminUsername}
                                                    type='text'/>
                                            </Form.Group>
                                            <Form.Group>
                                                <Form.Label>{this.state.messages['Email'][this.state.languagePreference]}</Form.Label>
                                                <Form.Control
                                                    disabled={true}
                                                    value={this.state.adminEmailAddress}
                                                    type='email'/>
                                            </Form.Group>



                                            <Form.Group>
                                                <Form.Label>{this.state.messages['UserRole'][this.state.languagePreference]}</Form.Label>
                                                <Dropdown>
                                                    <Dropdown.Toggle variant="light" id="dropdown-basic" style={{width: "100%"}}>
                                                        {this.state.adminRole}
                                                    </Dropdown.Toggle>

                                                    <Dropdown.Menu>
                                                        {this.state.userRoles.map((data, i) => (
                                                            <Dropdown.Item onClick={() => this.selectAdminUserRole(data)}>{data}</Dropdown.Item>
                                                        ))}
                                                    </Dropdown.Menu>
                                                </Dropdown>
                                            </Form.Group>

                                        </Form>
                                    </Modal.Body>
                                    <Modal.Footer>
                                        <Button variant="secondary" onClick={this.closeUpdateAdminModal}>{this.state.messages['Close'][this.state.languagePreference]}</Button>
                                        <Button variant="warning" onClick={this.updateAdmin}>{this.state.messages['Update'][this.state.languagePreference]}</Button>
                                    </Modal.Footer>
                                </Modal>
                            </Row>
                            <Row>
                                <Col lg="12">
                                    <Table striped bordered hover>
                                        <thead>
                                        <tr>
                                            <th>#</th>
                                            <th>{this.state.messages['Username'][this.state.languagePreference]}</th>
                                            <th>{this.state.messages['Email'][this.state.languagePreference]}</th>
                                            <th>{this.state.messages['Actions'][this.state.languagePreference]}</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        {this.state.admins.map((data, i) => (
                                            <tr>
                                                <td>{i + 1}</td>
                                                <td>{data.username}</td>
                                                <td>{data.emailAddress}</td>
                                                <td>
                                                    <div className="btn-toolbar">
                                                        <Button className="btn btn-dark mx-2" onClick={() => this.deleteAdmin(data)}><FontAwesomeIcon icon={faTrash} /></Button>
                                                        <Button className="btn btn-warning mx-2" onClick={() => this.openUpdateAdminModal(data)}><FontAwesomeIcon icon={faEdit} /></Button>
                                                    </div>
                                                </td>
                                            </tr>
                                        ))}
                                        </tbody>
                                    </Table>
                                </Col>
                            </Row>
                        </div> : null}
                </Container>
            </div>
        );
    }
}