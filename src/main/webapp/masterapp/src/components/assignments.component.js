import React, { Component } from "react";

import UserService from "../services/user.service";
import TeacherService from "../services/teacher.service";
import {Badge, Button, Col, Container, Form, ListGroup, Modal, Row, Table, Toast} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {
    faDownload,
    faPen, faQuestionCircle,
    faTrash,
    faUpload,
} from "@fortawesome/free-solid-svg-icons";
import ProjectService from "../services/project.service";
import authUser from "../services/auth-user";
import AuthService from "../services/auth.service";

export default class AssignmentsComponent extends Component {
    constructor(props) {
        super(props);

        // Utils
        this.shortenData = this.shortenData.bind(this);
        this.deleteAssignment = this.deleteAssignment.bind(this);
        this.toggleProject = this.toggleProject.bind(this);
        this.onChangeFeedback = this.onChangeFeedback.bind(this);
        this.onChangeGrade = this.onChangeGrade.bind(this);
        this.assignGradeAndFeedback = this.assignGradeAndFeedback.bind(this);
        this.editGradeAndFeedback = this.editGradeAndFeedback.bind(this);

        // Student functions
        this.openUploadAssignmentModal = this.openUploadAssignmentModal.bind(this);
        this.onFileUpload = this.onFileUpload.bind(this);
        this.downloadAssignment = this.downloadAssignment.bind(this);

        this.state = {
            isStudent: true,
            isTeacher: false,
            assignments: [],
            assignmentsReady: false,
            showToast: false,
            errorToast: false,
            infoMessage: "",
            openDetails: [],
            open: false,
            isAssignGradeAndFeedbackModalOpen: false,
            studentGrade: 0,
            studentFeedback: "",
            studentName: "",
            projectId: "",
            selectedFile: null,
            isUploadAssignmentModalOpen: false,
            languagePreference: "English",
            messages: {
                'Header': {
                    'Romanian': "Sarcinile studenților",
                    'English': "Student assignments"
                },
                'UploadAssignment': {
                    'Romanian': "Actualizează sarcină",
                    'English': "Upload assignment"
                },
                'Assignment': {
                    'Romanian': "Sarcină",
                    'English': "Assignment"
                },
                'Assign': {
                    'Romanian': "Asignează",
                    'English': "Assign"
                },
                'AssignGradeFeedback': {
                    'Romanian': "Asignează nota si părere",
                    'English': "Assign grade and feedback"
                },
                'Grade': {
                    'Romanian': "Notă",
                    'English': "Grade"
                },
                'Feedback': {
                    'Romanian': "Părere",
                    'English': "Feedback"
                },
                'ProjectName': {
                    'Romanian': "Nume proiect",
                    'English': "Project name"
                },
                'Assignee': {
                    'Romanian': "Asignat",
                    'English': "Assignee"
                },
                'Teacher': {
                    'Romanian': "Profesor",
                    'English': "Teacher"
                },
                'Description': {
                    'Romanian': "Descriere",
                    'English': "Description"
                },
                'CompeteCourseName': {
                    'Romanian': "Numele complet al cursului",
                    'English': "Compete course name"
                },
                'UploadDate': {
                    'Romanian': "Data încărcării",
                    'English': "Upload date"
                },
                'GradeFeedback': {
                    'Romanian': "Notă și părere",
                    'English': "Grade & feedback"
                },
                'Course': {
                    'Romanian': "Curs",
                    'English': "Course"
                },
                'Deadline': {
                    'Romanian': "Termen limită",
                    'English': "Deadline"
                },
                'Projects': {
                    'Romanian': "Proiecte",
                    'English': "Projects"
                },
                'Name': {
                    'Romanian': "Nume",
                    'English': "Name"
                },
                'Close': {
                    'Romanian': "Închide",
                    'English': "Close"
                },
                'Save': {
                    'Romanian': "Salvează",
                    'English': "Save"
                },
                'Actions': {
                    'Romanian': "Acțiuni",
                    'English': "Actions"
                },
                'DeleteAssignment': {
                    'Romanian': "Ești sigur ca vrei să ștergi sarcina ",
                    'English': "Are you sure you want to delete assignment "
                },
                'From': {
                    'Romanian': " care aparține ",
                    'English': " from "
                },
                //
            }
        };
    }

    onFileChange = event => {
        // Update the state
        this.setState({ selectedFile: event.target.files[0] });
    };

    onFileUpload() {
        ProjectService.uploadAssignment(this.state.selectedFile, this.state.projectId, authUser()).then(
            response => {
                this.displayMessage(response.data, false);
                this.setState({
                    selectedFile: null,
                });
                this.getAssignedProjectsForOwner();
                this.closeUploadAssignmentModal();
            },
            error => {
                this.displayMessage((error.response &&
                    error.response.data &&
                    error.response.data.message) ||
                    error.message ||
                    error.toString(), true);
            });
    };

    editGradeAndFeedback(e) {
        if (e.grade !== null) {
            this.setState({
                studentGrade: e.grade,
            });
        }
        if (e.feedback !== null) {
            this.setState({
                studentFeedback: e.feedback,
            });
        }
        this.setState({
            studentName: e.assignee,
            projectId: e.uniqueId,
        });

        this.openAssignGradeAndFeedbackModal();
    }

    openAssignGradeAndFeedbackModal = () => this.setState({isAssignGradeAndFeedbackModalOpen: true});
    closeAssignGradeAndFeedbackModal = () => this.setState({isAssignGradeAndFeedbackModalOpen: false});

    getAssignedProjectsForOwner() {
        TeacherService.getAssignedProjects().then(
            response => {
                this.setState({
                    assignments: response.data,
                    assignmentsReady: true,
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

    componentDidMount() {

        this.getLanguagePreference();
        const user = AuthService.getCurrentUser();

        if (user) {
            this.setState({
                isTeacher: user.roles.includes("ROLE_TEACHER"),
                isStudent: user.roles.includes("ROLE_STUDENT"),
            });
        }

        this.getAssignedProjectsForOwner();
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

    displayMessage = (message, error) => {
        this.setState({
            infoMessage: message,
            showToast: true,
            errorToast: error,
        });
        setTimeout(() => this.setState({showToast: false}), 5000);
    }

    shortenData(inputDate) {
        if (inputDate == null)
            return "";
        return inputDate.substr(0, 16).replace("T", " ");
    }

    deleteAssignment(e) {
        if (window.confirm(this.state.messages['DeleteAssignment'][this.state.languagePreference] + e.projectName + this.state.messages['From'][this.state.languagePreference] + e.assignee + " ?")) {
            TeacherService.deleteAssignment(e).then(
                response => {
                    this.displayMessage(response.data, false);
                    this.getAssignedProjectsForOwner();
                },
                error => {
                });
        }
    }

    downloadAssignment(e) {
        let finalFileName = e.outputLocation.split('\\');
        finalFileName = finalFileName[finalFileName.length - 1];
        finalFileName = finalFileName.replace(finalFileName.split('_')[0] + '_', '');
        finalFileName = e.assignee + '_' + this.shortenData(e.projectName) + '_' + this.shortenData(e.uploadDate).replace(' ', '_').replace(':', '-') + '_' + finalFileName;

        ProjectService.downloadAssignment(e.uniqueId, e.assignee).then((response) => {
            const url = window.URL.createObjectURL(new Blob([response.data]));
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', finalFileName); //or any other extension
            document.body.appendChild(link);
            link.click();
        });
    }

    openUploadAssignmentModal(e) {
        this.setState({projectId: e.uniqueId});
        this.setState({isUploadAssignmentModalOpen: true});
    }

    closeUploadAssignmentModal = () => this.setState({isUploadAssignmentModalOpen: false});

    toggleProject(e) {
        const index = this.state.openDetails.indexOf(e.uniqueId);
        if (index > -1)
            this.state.openDetails.splice(index, 1);
        else
            this.state.openDetails.push(e.uniqueId);
        this.setState({ openDetails: this.state.openDetails })
    }

    onChangeGrade(e) {
        this.setState({
            studentGrade: e.target.value
        });
    }

    onChangeFeedback(e) {
        this.setState({
            studentFeedback: e.target.value
        });
    }

    assignGradeAndFeedback(e) {
        ProjectService.assignGradeAndFeedback(this.state.studentGrade, this.state.studentFeedback, this.state.studentName, this.state.projectId).then(
            response => {
                this.displayMessage(response.data, false);
                this.setState({
                    studentGrade: 0,
                    studentFeedback: "",
                    studentName: "",
                    projectId: "",
                });
                this.getAssignedProjectsForOwner();
                this.closeAssignGradeAndFeedbackModal();
            },
            error => {
                this.displayMessage((error.response &&
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

                <Modal show={this.state.isUploadAssignmentModalOpen} onHide={this.closeUploadAssignmentModal} size="lg">
                    <Modal.Header closeButton>
                        <Modal.Title>{this.state.messages['UploadAssignment'][this.state.languagePreference]}</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        <Form>
                            <Form.Group>
                                <Form.Label>{this.state.messages['Assignment'][this.state.languagePreference]}</Form.Label>
                                <Form.Control
                                    onChange={this.onFileChange}
                                    type="file"/>
                            </Form.Group>
                        </Form>
                    </Modal.Body>
                    <Modal.Footer>
                        <Button variant="secondary" onClick={this.closeUploadAssignmentModal}>{this.state.messages['Close'][this.state.languagePreference]}</Button>
                        <Button variant="primary" onClick={this.onFileUpload}>{this.state.messages['Assign'][this.state.languagePreference]}</Button>
                    </Modal.Footer>
                </Modal>

                <Modal show={this.state.isAssignGradeAndFeedbackModalOpen} onHide={this.closeAssignGradeAndFeedbackModal} size="lg">
                    <Modal.Header closeButton>
                        <Modal.Title>{this.state.messages['AssignGradeFeedback'][this.state.languagePreference]}</Modal.Title>:
                    </Modal.Header>
                    <Modal.Body>
                        <Form>
                            <Form.Group>
                                <Form.Label>{this.state.messages['Grade'][this.state.languagePreference]}</Form.Label>
                                <Form.Control
                                    onChange={this.onChangeGrade}
                                    value={this.state.studentGrade}
                                    type='number'/>
                            </Form.Group>
                            <Form.Group>
                                <Form.Label>{this.state.messages['Feedback'][this.state.languagePreference]}</Form.Label>
                                <Form.Control
                                    onChange={this.onChangeFeedback}
                                    value={this.state.studentFeedback}
                                    as='textarea'/>
                            </Form.Group>
                        </Form>
                    </Modal.Body>
                    <Modal.Footer>
                        <Button variant="secondary" onClick={this.closeAssignGradeAndFeedbackModal}>{this.state.messages['Close'][this.state.languagePreference]}</Button>
                        <Button variant="primary" onClick={this.assignGradeAndFeedback}>{this.state.messages['Save'][this.state.languagePreference]}</Button>
                    </Modal.Footer>
                </Modal>

                <Container>
                    {(this.state.assignmentsReady) ?
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
                            {(this.state.assignments.length > 0) ?
                                <Row>
                                    <Col lg="12">
                                        <Table striped bordered hover>
                                            <thead>
                                            <tr>
                                                <th>#</th>
                                                <th>{this.state.messages['ProjectName'][this.state.languagePreference]}</th>
                                                <th>{this.state.messages['Deadline'][this.state.languagePreference]}</th>
                                                <th>{this.state.messages['Course'][this.state.languagePreference]}</th>
                                                <th>{this.state.messages['Assignee'][this.state.languagePreference]}</th>
                                                <th>{this.state.messages['Grade'][this.state.languagePreference]}</th>
                                                <th>{this.state.messages['Actions'][this.state.languagePreference]}</th>
                                            </tr>
                                            </thead>
                                            {this.state.assignments.map((data, i) => (
                                                <tbody>
                                                    <tr>
                                                        <td onClick={() => this.toggleProject(data)}>{i + 1}</td>
                                                        <td onClick={() => this.toggleProject(data)}>{data.projectName}</td>
                                                        <td onClick={() => this.toggleProject(data)}>{this.shortenData(data.deadline)}</td>
                                                        <td onClick={() => this.toggleProject(data)}>{data.course.abbreviation}</td>
                                                        <td onClick={() => this.toggleProject(data)}>{data.assignee}</td>
                                                        <td onClick={() => this.toggleProject(data)}>
                                                            {(data.grade === 0) ?
                                                                <FontAwesomeIcon style={{color: "grey"}} icon={faQuestionCircle} />
                                                                : (data.grade >= 5) ?
                                                                    <Badge pill variant="success">{data.grade}</Badge> :
                                                                    <Badge pill variant="warning">{data.grade}</Badge>
                                                            }
                                                        </td>

                                                        <td>
                                                            <div className="btn-toolbar">
                                                            </div>
                                                            {this.state.isTeacher ?
                                                                <Button type="button" className="btn btn-dark mx-2" onClick={() => this.deleteAssignment(data)}><FontAwesomeIcon icon={faTrash} /></Button>
                                                                : null }
                                                            {this.state.isStudent ?
                                                                    <Button type="button" className="btn btn-primary mx-2" onClick={() => this.openUploadAssignmentModal(data)}><FontAwesomeIcon icon={faUpload} /></Button>
                                                                : null }
                                                            {data.outputLocation !== null ?
                                                                <Button type="button" className="btn btn-success mx-2" onClick={() => this.downloadAssignment(data)}><FontAwesomeIcon icon={faDownload} /></Button>
                                                                : null }
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        {(this.state.openDetails.indexOf(data.uniqueId) > -1) ?
                                                            <td colSpan={7}>
                                                                <Row>
                                                                    <Col lg="12">
                                                                        <ListGroup variant="flush">
                                                                            <ListGroup.Item>{this.state.messages['Description'][this.state.languagePreference]}: {data.description}</ListGroup.Item>
                                                                            <ListGroup.Item>{this.state.messages['Teacher'][this.state.languagePreference]}: {data.owner}</ListGroup.Item>
                                                                            <ListGroup.Item>{this.state.messages['CompeteCourseName'][this.state.languagePreference]}: {data.course.completeName}</ListGroup.Item>
                                                                        </ListGroup>
                                                                    </Col>
                                                                </Row>
                                                                <p></p>
                                                                <hr style={{
                                                                    color: '#000000',
                                                                    backgroundColor: '#000000',
                                                                    height: .5,
                                                                    borderColor : '#000000'
                                                                }}/>
                                                                <Row>

                                                                    <Col lg="8">
                                                                        <ListGroup variant="flush">
                                                                            {(data.uploadDate !== null) ?
                                                                                <ListGroup.Item>{this.state.messages['UploadDate'][this.state.languagePreference]}: {this.shortenData(data.uploadDate)}</ListGroup.Item>
                                                                                : null }
                                                                            <ListGroup.Item>{this.state.messages['Feedback'][this.state.languagePreference]}: {data.feedback}</ListGroup.Item>
                                                                        </ListGroup>
                                                                    </Col>
                                                                    <Col lg="4">
                                                                        {this.state.isTeacher ?
                                                                            <Button type="button" className="btn btn-success mx-2" onClick={() => this.editGradeAndFeedback(data)}><FontAwesomeIcon icon={faPen} />{' '}{this.state.messages['GradeFeedback'][this.state.languagePreference]}</Button>
                                                                            : null
                                                                        }
                                                                    </Col>
                                                                </Row>
                                                            </td>
                                                            : null}
                                                    </tr>
                                                </tbody>
                                            ))}
                                        </Table>
                                    </Col>
                                </Row> : null }
                        </div> : null }
                </Container>
            </div>
        );
    }
}