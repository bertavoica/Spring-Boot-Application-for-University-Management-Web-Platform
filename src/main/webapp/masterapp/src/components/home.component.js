import React, { Component } from "react";

import AuthService from "../services/auth.service";
import TitleService from "../services/title.service";
import authUser from "../services/auth-user";
import CourseService from "../services/course.service";
import {Button, Col, Modal, Row, Table, Toast, Form} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";

import {
    faQuestionCircle,
    faTrash,
    faUserMinus,
    faUserPlus
} from "@fortawesome/free-solid-svg-icons";
import UserService from "../services/user.service";

export default class Home extends Component {
    constructor(props) {
        super(props);

        // Teacher enrolling students
        this.enrollStudent = this.enrollStudent.bind(this);
        this.enrollStudentDb = this.enrollStudentDb.bind(this);
        this.removeStudentFromCourse = this.removeStudentFromCourse.bind(this);
        this.removeStudentFromCourseDb = this.removeStudentFromCourseDb.bind(this);
        this.openEnrollUserModal = this.openEnrollUserModal.bind(this);
        this.closeEnrollUserModal = this.closeEnrollUserModal.bind(this);
        this.onChangeStudentUsername = this.onChangeStudentUsername.bind(this);
        this.getEnrolledStudents = this.getEnrolledStudents.bind(this);

        // Titles
        this.deleteTitle = this.deleteTitle.bind(this);
        this.openAddTitleModal = this.openAddTitleModal.bind(this);
        this.closeAddTitleModal = this.closeAddTitleModal.bind(this);
        this.onChangeTitleName = this.onChangeTitleName.bind(this);
        this.addTitle = this.addTitle.bind(this);

        this.state = {
            isStudent: true,
            isTeacher: false,
            isAdmin: false,
            content: "",
            courses: [],
            infoMessage: "",
            showToast: false,
            errorToast: false,
            toastBg: 'danger',
            selectedCourse: null,
            isEnrollUserModalOpen: false,
            isRemoveStudentUserModalOpen: false,
            studentUsername: "",
            titles: [],
            isAddTitleModalOpen: false,
            titleName: "",
            enrolledStudents: [],
            isGetEnrolledModalOpen: false,
            languagePreference: "English",
            messages: {'Header': {'Romanian' : "Pagina principala",
                    'English' : "Home page"},
                'Titles': {'Romanian' : "Rolurile utilizatorilor",
                    'English' : "User roles"},
                'AddTitle': {'Romanian' : "Adaugă rol",
                    'English' : "Add role"},
                'AddTitleToDB': {'Romanian' : "Adăgați un titlu nou în baza de date",
                    'English' : "Add a new title to the DB"},
                'Name': {'Romanian' : "Nume",
                    'English' : "Name"},
                'Close': {'Romanian' : "Închide",
                    'English' : "Close"},
                'Save': {'Romanian' : "Salvează",
                    'English' : "Save"},
                'Actions': {'Romanian' : "Acțiuni",
                    'English' : "Actions"},
                'CompleteName': {'Romanian' : "Numele complet",
                    'English' : "Complete name"},
                'RemoveStudentFromLecture': {'Romanian' : "Șterge studentul de la curs",
                    'English' : "Remove student from course"},
                'Teacher': {'Romanian' : "Profesor",
                    'English' : "Teacher"},
                'Abbreviation': {'Romanian' : "Abreviere",
                    'English' : "Abbreviation"},
                'Description': {'Romanian' : "Descriere",
                    'English' : "Description"},
                'Enroll': {'Romanian' : "Înrolează",
                    'English' : "Enroll"},
                'EnrolledStudents': {'Romanian' : "Studenți înrolați",
                    'English' : "Enrolled students"},
                'MyCourses': {'Romanian' : "Cursurile mele",
                    'English' : "My courses"},
                'Username': {'Romanian' : "Nume utilizator",
                    'English' : "Username"},
                'EnrollStudent': {'Romanian' : "Înroleaza student",
                    'English' : "Enroll student"},
                'Remove': {'Romanian' : "Șterge",
                    'English' : "Remove"},



            },
        };
    }

    addTitle() {
        TitleService.addTitle(this.state.titleName).then(
            response => {
                this.displayMessage(response.data, false);
                this.setState({
                    titleName: "",
                });
                this.closeAddTitleModal();
                this.getAllTitles(this.state.isAdmin);
            },
            error => {
                this.displayMessage(error.response.data, true);
            });
    }

    deleteTitle(inputTitle) {
        if (window.confirm(this.state.messages['TitleDeletionConfirmation'][this.state.languagePreference] + inputTitle.name)) {
            TitleService.deleteTitle(inputTitle.name).then(
                response => {
                    this.setState({
                        titles: response.data,
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
    }


    openGetEnrolledStudentsModal = () => this.setState({isGetEnrolledModalOpen: true});
    closeGetEnrolledStudentsModal = () => this.setState({isGetEnrolledModalOpen: false});


    openAddTitleModal = () => this.setState({isAddTitleModalOpen: true});
    closeAddTitleModal = () => this.setState({isAddTitleModalOpen: false});

    openEnrollUserModal = () => this.setState({isEnrollUserModalOpen: true});
    closeEnrollUserModal = () => this.setState({isEnrollUserModalOpen: false});

    openRemoveStudentFromCourseModal = () => this.setState({isRemoveStudentUserModalOpen: true});
    closeRemoveStudentFromCourseModal = () => this.setState({isRemoveStudentUserModalOpen: false});


    closeToast = () => this.setState({showToast: false});
    displayMessage = (message, error) => {
        this.setState({
            infoMessage: message,
            showToast: true,
            errorToast: error,
        });
        setTimeout(() => this.setState({showToast: false}), 5000);
    }


    componentDidMount() {

        this.getLanguagePreference();

        const user = AuthService.getCurrentUser();

        if (user) {
            this.setState({
                isTeacher: user.roles.includes("ROLE_TEACHER"),
                isStudent: user.roles.includes("ROLE_STUDENT"),
                isAdmin: user.roles.includes("ROLE_ADMIN"),
            });
        }

        this.getCoursesForUser();
        this.getAllTitles(user.roles.includes("ROLE_ADMIN"));
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

    getAllTitles(isAdmin=false) {
        if (!isAdmin)
            return;

        TitleService.getTitles(authUser()).then(
            response => {
                this.setState({
                    titles: response.data,
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

    getCoursesForUser() {
        CourseService.getCoursesUser(authUser()).then(
            response => {
                this.setState({
                    courses: response.data,
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

    enrollStudent(e) {
        this.setState({
            selectedCourse: e,
        });
        this.openEnrollUserModal();
    }

    enrollStudentDb() {
        CourseService.enrollStudent(this.state.selectedCourse, this.state.studentUsername).then(
            response => {
                this.displayMessage(response.data, false);
                this.setState({
                    selectedCourse: null,
                    studentUsername: "",
                });
                this.getCoursesForUser();
                this.closeEnrollUserModal();
            },
            error => {
                this.displayMessage(error.response.data, true);
            });
    }

    getEnrolledStudents(e) {

        CourseService.getEnrolled(e).then(
            response => {
                this.setState({
                    enrolledStudents: response.data,
                });
                this.openGetEnrolledStudentsModal();
            },
            error => {
                this.displayMessage(error.response.data, true);
            });

    }

    removeStudentFromCourse(e) {
        this.setState({
            selectedCourse: e,
        });
        this.openRemoveStudentFromCourseModal();
    }

    removeStudentFromCourseDb() {
        CourseService.removeStudent(this.state.selectedCourse, this.state.studentUsername).then(
            response => {
                this.displayMessage(response.data, false);
                this.setState({
                    selectedCourse: null,
                    studentUsername: "",
                });
                this.getCoursesForUser();
                this.closeRemoveStudentFromCourseModal();
            },
            error => {
                this.displayMessage(error.response.data, true);
            });
    }

    onChangeStudentUsername(e) {
        this.setState({
            studentUsername: e.target.value
        });
    }

    onChangeTitleName(e) {
        this.setState({
            titleName: e.target.value
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

                <Modal show={this.state.isEnrollUserModalOpen} onHide={this.closeEnrollUserModal} size="lg">
                    <Modal.Header closeButton>
                        <Modal.Title>{this.state.messages['Enroll'][this.state.languagePreference]}</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        <Form>
                            <Form.Group>
                                <Form.Label>{this.state.messages['Username'][this.state.languagePreference]}</Form.Label>
                                <Form.Control
                                    onChange={this.onChangeStudentUsername}
                                    value={this.state.studentUsername}
                                    type='text'/>
                            </Form.Group>
                        </Form>
                    </Modal.Body>
                    <Modal.Footer>
                        <Button variant="secondary" onClick={this.closeEnrollUserModal}>{this.state.messages['Close'][this.state.languagePreference]}</Button>
                        <Button variant="primary" onClick={this.enrollStudentDb}>{this.state.messages['EnrollStudent'][this.state.languagePreference]}</Button>
                    </Modal.Footer>
                </Modal>

                <Modal show={this.state.isRemoveStudentUserModalOpen} onHide={this.closeRemoveStudentFromCourseModal} size="lg">
                    <Modal.Header closeButton>
                        <Modal.Title>{this.state.messages['RemoveStudentFromLecture'][this.state.languagePreference]}</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        <Form>
                            <Form.Group>
                                <Form.Label>{this.state.messages['Username'][this.state.languagePreference]}</Form.Label>
                                <Form.Control
                                    onChange={this.onChangeStudentUsername}
                                    value={this.state.studentUsername}
                                    type='text'/>
                            </Form.Group>
                        </Form>
                    </Modal.Body>
                    <Modal.Footer>
                        <Button variant="secondary" onClick={this.closeRemoveStudentFromCourseModal}>{this.state.messages['Close'][this.state.languagePreference]}</Button>
                        <Button variant="danger" onClick={this.removeStudentFromCourseDb}>{this.state.messages['Remove'][this.state.languagePreference]}</Button>
                    </Modal.Footer>
                </Modal>

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

                {(this.state.courses.length > 0) ?
                    <div>
                        <Row>
                            <Col lg="12">
                                {this.state.languagePreference === 'English' ?
                                    <div>
                                        <p>The platform was created in order to manage university projects in an easier and more efficient way, both for a student and for a professor.</p>
                                        <p>It provides statistical data related to students' grades, provides access to the faculty hierarchy and offers the possibility to manage your daily tasks in an organized way.</p>
                                        <p>Users will have different functionalities depending on the status, which can be seen from the platform menu.</p>
                                    </div>
                                :
                                    <div>
                                        <p>Platforma a fost creată pentru a gestiona proiectele universitare într-un mod mai ușor și mai eficient, atât pentru un student, cât și pentru un profesor.</p>
                                        <p>Oferă date statistice legate de notele studenților, oferă acces la ierarhia facultății și oferă posibilitatea de a vă gestiona sarcinile zilnice într-un mod organizat.</p>
                                        <p>Utilizatorii vor avea funcționalități diferite în funcție de stare, care pot fi văzute din meniul platformei.</p>
                                    </div>
                                }
                            </Col>
                        </Row>
                        <h3>{this.state.messages['MyCourses'][this.state.languagePreference]}</h3>
                        <Row>
                            <Col lg="12">
                                <Table striped bordered hover>
                                    <thead>
                                    <tr>
                                        <th>#</th>
                                        <th>{this.state.messages['CompleteName'][this.state.languagePreference]}</th>
                                        <th>{this.state.messages['Abbreviation'][this.state.languagePreference]}</th>
                                        <th>{this.state.messages['Description'][this.state.languagePreference]}</th>
                                        {this.state.isTeacher ?
                                         <th>{this.state.messages['EnrolledStudents'][this.state.languagePreference]}</th>
                                         : null }
                                        <th>{this.state.messages['Actions'][this.state.languagePreference]}</th>
                                    </tr>
                                    </thead>
                                    {this.state.courses.map((data, i) => (
                                        <tbody>
                                        <tr>
                                            <td>{i + 1}</td>
                                            <td>{data.completeName}</td>
                                            <td>{data.abbreviation}</td>
                                            <td>{data.description}</td>
                                            {this.state.isTeacher ?
                                            <td>{data.assignedUsers}</td>
                                            : null }

                                            <td>
                                                {this.state.isTeacher ?
                                                    <div className="btn-toolbar">
                                                        <Button className="btn btn-success mx-2" onClick={() => this.enrollStudent(data)}><FontAwesomeIcon icon={faUserPlus} /></Button>
                                                        <Button className="btn btn-secondary mx-2" onClick={() => this.removeStudentFromCourse(data)}><FontAwesomeIcon icon={faUserMinus} /></Button>
                                                        <Button className="btn btn-info mx-2" onClick={() => this.getEnrolledStudents(data)}><FontAwesomeIcon icon={faQuestionCircle} /></Button>
                                                    </div>
                                                    : null }
                                            </td>
                                        </tr>
                                        </tbody>
                                    ))}
                                </Table>
                            </Col>
                        </Row>

                        <Modal show={this.state.isGetEnrolledModalOpen} onHide={this.closeGetEnrolledStudentsModal} size="lg">
                            <Modal.Header closeButton>
                                <Modal.Title>{this.state.messages['EnrolledStudents'][this.state.languagePreference]}</Modal.Title>
                            </Modal.Header>
                            <Modal.Body>
                                {(this.state.enrolledStudents != null) ?
                                    <ol>
                                        {this.state.enrolledStudents.map((data, i) => (
                                            <li>{data}</li>
                                        ))}
                                    </ol> : null
                                }
                            </Modal.Body>
                            <Modal.Footer>
                                <Button variant="secondary" onClick={this.closeGetEnrolledStudentsModal}>{this.state.messages['Close'][this.state.languagePreference]}</Button>
                            </Modal.Footer>
                        </Modal>

                    </div>
                    : null }

                {this.state.isAdmin ?
                    <div>
                        <Row>
                            <Col lg="9">
                                <h2>{this.state.messages['Titles'][this.state.languagePreference]}</h2>
                            </Col>
                            <Col lg="3">
                                <button className="btn btn-primary btn-block" onClick={this.openAddTitleModal}>
                                    <span>{this.state.messages['AddTitle'][this.state.languagePreference]}</span>
                                </button>
                            </Col>
                        </Row>

                        <Modal show={this.state.isAddTitleModalOpen} onHide={this.closeAddTitleModal} size="lg">
                            <Modal.Header closeButton>
                                <Modal.Title>{this.state.messages['AddTitleToDB'][this.state.languagePreference]}</Modal.Title>
                            </Modal.Header>
                            <Modal.Body>
                                <Form>
                                    <Form.Group>
                                        <Form.Label>{this.state.messages['Name'][this.state.languagePreference]}</Form.Label>
                                        <Form.Control
                                            onChange={this.onChangeTitleName}
                                            value={this.state.titleName}
                                            type='text'/>
                                    </Form.Group>
                                </Form>
                            </Modal.Body>
                            <Modal.Footer>
                                <Button variant="secondary" onClick={this.closeAddTitleModal}>{this.state.messages['Close'][this.state.languagePreference]}</Button>
                                <Button variant="primary" onClick={this.addTitle}>{this.state.messages['Save'][this.state.languagePreference]}</Button>
                            </Modal.Footer>
                        </Modal>
                        {(this.state.titles.length > 0) ?
                            <Row>
                                <Col lg="12">
                                    <Table striped bordered hover>
                                        <thead>
                                        <tr>
                                            <th>#</th>
                                            <th>{this.state.messages['Name'][this.state.languagePreference]}</th>
                                            <th>{this.state.messages['Actions'][this.state.languagePreference]}</th>
                                        </tr>
                                        </thead>
                                        {this.state.titles.map((data, i) => (
                                            <tbody>
                                            <tr>
                                                <td>{i + 1}</td>
                                                <td>{data.name}</td>
                                                <td>
                                                    <div className="btn-toolbar">
                                                        <Button className="btn btn-dark mx-2" onClick={() => this.deleteTitle(data)}><FontAwesomeIcon icon={faTrash} /></Button>
                                                    </div>
                                                </td>
                                            </tr>
                                            </tbody>
                                        ))}
                                    </Table>
                                </Col>
                            </Row>
                        : null}
                    </div>
                    : null }
            </div>

        );
    }


}