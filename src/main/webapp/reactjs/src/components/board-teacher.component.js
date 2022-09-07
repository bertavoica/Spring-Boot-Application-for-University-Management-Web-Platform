import React, { Component } from "react";

import ProjectService from "../services/project.service";
import {
    Button,
    Col,
    Container,
    Modal,
    Row,
    Table,
    Toast,
    Form
} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faEdit, faTrash, faUser, faUsers} from "@fortawesome/free-solid-svg-icons";
import CourseService from "../services/course.service";
import authUser from "../services/auth-user";
import Chart from "react-google-charts";
import AuthService from "../services/auth.service";
import UserService from "../services/user.service";

export default class BoardTeacher extends Component {
    constructor(props) {
        super(props);

        // Add project
        this.onChangeProjectDisplay = this.onChangeProjectDisplay.bind(this);
        this.onChangeProjectName = this.onChangeProjectName.bind(this);
        this.onChangeProjectDescription = this.onChangeProjectDescription.bind(this);
        this.saveProjectTemplate = this.saveProjectTemplate.bind(this);
        this.deleteProject = this.deleteProject.bind(this);
        this.selectCourse = this.selectCourse.bind(this);
        this.setProjectDeadlineDate = this.setProjectDeadlineDate.bind(this);
        this.editProject = this.editProject.bind(this);
        this.addProject = this.addProject.bind(this);
        this.updateProjectTemplate = this.updateProjectTemplate.bind(this);

        // Assign project
        this.openModalAssignProjectToUser = this.openModalAssignProjectToUser.bind(this);
        this.openModalAssignProjectToGroup = this.openModalAssignProjectToGroup.bind(this);
        this.onChangeAssignedUser = this.onChangeAssignedUser.bind(this);
        this.onChangeAssignedGroup = this.onChangeAssignedGroup.bind(this);
        this.assignProjectToUser = this.assignProjectToUser.bind(this);
        this.assignProjectToGroup = this.assignProjectToGroup.bind(this);

        // Utils
        this.shortenData = this.shortenData.bind(this);

        this.state = {
            projects: [],
            courses: [],
            coursesReady: false,
            projectsReady: false,
            showToast: false,
            errorToast: false,
            infoMessage: "",
            isAddProjectModalOpen: false,
            projectName: "",
            projectDescription: "",
            projectCourseId: "",
            projectDeadlineDate: "",
            projectDeadlineTime: "",
            projectUniqueId: "",
            addProjectModal: true,
            displayMyProjects: true,
            isAssignProjectToUserModalOpen: false,
            isAssignProjectToGroupModalOpen: false,
            projectToAssign: "",
            projectToAssignUniqueId: "",
            assignedUser: "",
            assignedGroup: "",
            selectedProject: null,
            reviewedStatistics: null,
            deadlineStatistics: null,
            gradesStatistics: null,
            languagePreference: "English",
            messages: {
                'Header': {
                    'Romanian': "Bun venit la meniul profesorilor",
                    'English': "Welcome to the teacher board"
                },
                'DisplayMyProjects': {
                    'Romanian': "Afișează doar proiectele mele",
                    'English': "Display my projects"
                },
                'AddProject': {
                    'Romanian': "Adaugă un nou proiect",
                    'English': "Add new project"
                },
                'AssignProjectToUser': {
                    'Romanian': "Asignează proiectul unui student",
                    'English': "Assign the project to a certain student"
                },
                'ProjectName': {
                    'Romanian': "Nume proiect",
                    'English': "Project name"
                },
                'Assignee': {
                    'Romanian': "Asignat",
                    'English': "Assignee"
                },
                'AssignUser': {
                    'Romanian': "Asignează unui student",
                    'English': "Assign to student"
                },
                'AssignProjectToGroup': {
                    'Romanian': "Asignează proiectul unui grup",
                    'English': "Assign the project to a certain group"
                },
                'AssigneeGroup': {
                    'Romanian': "Grup asignat",
                    'English': "Assignee group"
                },
                'AssigneeToGroup': {
                    'Romanian': "Asignează unui grup",
                    'English': "Assign to group"
                },
                'AddNewProject': {
                    'Romanian': "Adaugă un nou proiect in baza de date",
                    'English': "Add a new project to the database"
                },
                'UpdateProject': {
                    'Romanian': "Actualizează proiect",
                    'English': "Update project"
                },
                'UpdateCourse': {
                    'Romanian': "Actualizează curs",
                    'English': "Update course"
                },
                'Course': {
                    'Romanian': "Curs",
                    'English': "Course"
                },
                'SelectCourse': {
                    'Romanian': "Selectează curs",
                    'English': "Select a course"
                },
                'Deadline': {
                    'Romanian': "Termen limită",
                    'English': "Deadline"
                },
                'UpdateDeadline': {
                    'Romanian': "Actualizează termen limită",
                    'English': "Update deadline"
                },
                'Update': {
                    'Romanian': "Actualizează",
                    'English': "Update"
                },
                'Owner': {
                    'Romanian': "Creator",
                    'English': "Owner"
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
                'CompleteName': {
                    'Romanian': "Numele complet",
                    'English': "Complete name"
                },
                'RemoveStudentFromLecture': {
                    'Romanian': "Șterge studentul de la curs",
                    'English': "Remove student from course"
                },
                'Teacher': {
                    'Romanian': "Profesor",
                    'English': "Teacher"
                },
                'Abbreviation': {
                    'Romanian': "Abreviere",
                    'English': "Abbreviation"
                },
                'Description': {
                    'Romanian': "Descriere",
                    'English': "Description"
                },
                'Enroll': {
                    'Romanian': "Înrolează",
                    'English': "Enroll"
                },
                'EnrolledStudents': {
                    'Romanian': "Studenți înrolați",
                    'English': "Enrolled students"
                },
                'MyCourses': {
                    'Romanian': "Cursurile mele",
                    'English': "My courses"
                },
                'Username': {
                    'Romanian': "Nume utilizator",
                    'English': "Username"
                },
                'EnrollStudent': {
                    'Romanian': "Înroleaza student",
                    'English': "Enroll student"
                },
                'Remove': {
                    'Romanian': "Șterge",
                    'English': "Remove"
                },
                'StatisticsProject': {
                    'Romanian': "Statistici pentru proiectul",
                    'English': "Statistics for project"
                },
                'Back': {
                    'Romanian': "Înapoi",
                    'English': "Back"
                },
                'AssignmentsStatistics': {
                    'Romanian': "Statistici pentru sarcini",
                    'English': "Assignments statistics"
                },
                'DeadlineStatistics': {
                    'Romanian': "Statistici pentru termenele limită",
                    'English': "Deadline statistics"
                },
                'GradesStatistics': {
                    'Romanian': "Statistici pentru note",
                    'English': "Grades statistics"
                },
                'DeleteProject': {
                    'Romanian': "Ești sigur ca dorești să ștergi proiectul ",
                    'English': "Are you sure you want to delete project "
                },
            }
        };
        this.getProjects(!this.state.displayMyProjects);
    }


    assignProjectToUser(e) {
        ProjectService.assignToUser(this.state.projectToAssignUniqueId, this.state.assignedUser).then(
            response => {
                this.displayMessage(response.data, false);
                this.setState({
                    projectToAssign: "",
                    projectToAssignUniqueId: "",
                    assignedUser: "",
                });
                this.closeAssignProjectToUserModal();
            },
            error => {
                this.displayMessage(error.response.data, true);
                this.closeAssignProjectToUserModal();
            });
    }

    assignProjectToGroup(e) {
        ProjectService.assignToGroup(this.state.projectToAssignUniqueId, this.state.assignedGroup).then(
            response => {
                this.displayMessage(response.data, false);
                this.setState({
                    projectToAssign: "",
                    projectToAssignUniqueId: "",
                    assignedGroup: "",
                });
                this.closeAssignProjectToGroupModal();
            },
            error => {
                this.displayMessage(error.response.data, true);
                this.closeAssignProjectToGroupModal();
            });
    }

    onChangeAssignedUser(e) {
        this.setState({
            assignedUser: e.target.value
        });
    }

    onChangeAssignedGroup(e) {
        this.setState({
            assignedGroup: e.target.value
        });
    }

    openAssignProjectToUserModal = () => this.setState({isAssignProjectToUserModalOpen: true});
    closeAssignProjectToUserModal = () => this.setState({isAssignProjectToUserModalOpen: false});

    openAssignProjectToGroupModal = () => this.setState({isAssignProjectToGroupModalOpen: true});
    closeAssignProjectToGroupModal = () => this.setState({isAssignProjectToGroupModalOpen: false});

    openModalAssignProjectToUser(inputProject) {
        this.setState({
            projectToAssign: inputProject.projectName,
            projectToAssignUniqueId: inputProject.uniqueId,
        });
        this.openAssignProjectToUserModal();
    }

    openModalAssignProjectToGroup(inputProject) {
        this.setState({
            projectToAssign: inputProject.projectName,
            projectToAssignUniqueId: inputProject.uniqueId,
        });
        this.openAssignProjectToGroupModal();
    }

    getProjects(displayMyProjects) {
        ProjectService.getProjects(displayMyProjects, authUser()).then(
            response => {
                this.setState({
                    projects: response.data,
                    projectsReady: true,
                });
            },
            error => {
                this.setState({
                    content:
                        (error.response &&
                            error.response.data &&
                            error.response.data.message) ||
                        error.message ||
                        error.toString()
                });
            }
        );
    }

    componentDidMount() {

        this.getLanguagePreference();

        CourseService.getCourses().then(
            response => {
                this.setState({
                    coursesReady: true,
                    courses: response.data,
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


    openAddProjectModal = () => this.setState({isAddProjectModalOpen: true});
    closeAddProjectModal = () => this.setState({isAddProjectModalOpen: false});

    closeToast = () => this.setState({showToast: false});
    displayMessage = (message, error) => {
        this.setState({
            infoMessage: message,
            showToast: true,
            errorToast: error,
        });
        setTimeout(() => this.setState({showToast: false}), 5000);
    }

    onChangeProjectName(e) {
        this.setState({
            projectName: e.target.value
        });
    }

    onChangeProjectDisplay(e) {
        this.setState({ displayMyProjects: e.target.checked });
        this.getProjects(this.state.displayMyProjects);
    }

    onChangeProjectDescription(e) {
        this.setState({
            projectDescription: e.target.value
        });
    }

    saveProjectTemplate(e) {
        ProjectService.addProject(this.state.projectName, this.state.projectDescription, this.state.projectCourseId, this.state.projectDeadlineDate, this.state.projectDeadlineTime, authUser()).then(
            response => {
                this.displayMessage(response.data, false);
                this.setState({
                    projectName: "",
                    projectDescription: "",
                    projectCourseId: "",
                    projectDeadlineDate: "",
                    projectDeadlineTime: "",
                });
                this.getProjects(!this.state.displayMyProjects);
                this.closeAddProjectModal();
            },
            error => {
                this.displayMessage((error.response &&
                    error.response.data &&
                    error.response.data.message) ||
                    error.message ||
                    error.toString(), true);
            });
    }

    updateProjectTemplate(e) {
        ProjectService.updateProject(this.state.projectUniqueId, this.state.projectName, this.state.projectDescription,
            (this.state.projectCourseId === "" ? null : this.state.projectCourseId) ,
            (this.state.projectDeadlineDate === "" ? null : this.state.projectDeadlineDate),
            (this.state.projectDeadlineTime === "" ? null : this.state.projectDeadlineTime)
        ).then(
            response => {
                this.displayMessage(response.data, false);
                this.setState({
                    projectName: "",
                    projectDescription: "",
                    projectCourseId: "",
                    projectDeadlineDate: "",
                    projectDeadlineTime: "",
                    projectUniqueId: "",
                });

                this.getProjects(!this.state.displayMyProjects);
                this.closeAddProjectModal();
            },
            error => {
                this.displayMessage((error.response &&
                    error.response.data &&
                    error.response.data.message) ||
                    error.message ||
                    error.toString(), true);
            });
    }

    deleteProject(e) {
        if (window.confirm(this.state.messages['DeleteProject'][this.state.languagePreference] + e.projectName)) {
            ProjectService.deleteProject(e.uniqueId).then(
                response => {
                    this.displayMessage(response.data, false);
                    this.getProjects(!this.state.displayMyProjects);
                },
                error => {
                });
        }
    }

    editProject(e) {
        this.setState({
            projectName: e.projectName,
            projectUniqueId: e.uniqueId,
            projectDescription: e.description,
            projectCourseId: e.course.uniqueId,
            addProjectModal: false,
        });
        if (e.deadline != null) {
            this.setState({
                projectDeadlineDate: e.deadline.substr(0, 10),
                projectDeadlineTime: "",
            });
        }
        this.openAddProjectModal();

    }

    addProject() {
        this.setState({
            addProjectModal: true,
        });
        this.openAddProjectModal();
    }

    shortenData(inputDate) {
        if (inputDate == null)
            return "";
        return inputDate.substr(0, 16).replace("T", " ");
    }

    selectCourse(e) {
        this.setState({
            projectCourseId: e.target.value,
        });
    }

    setProjectDeadlineDate(e) {
        this.setState({
            projectDeadlineDate: e.substr(0, 10),
            projectDeadlineTime: e.substr(11, 5),
        });
    }

    openProjectStatisticsViewer(e) {
        this.setState({
            selectedProject: e,
        });

        if (e != null) {
            this.refreshDeadlinePieChart(e);
            this.refreshReviewedPieChart(e);
            this.refreshGradesPieChart(e);
        }

    }

    refreshReviewedPieChart(inputProject) {
        ProjectService.getReviewedProjectStatistics(inputProject.uniqueId).then(
            response => {
                this.setState({
                    reviewedStatistics: response.data,
                });
            },
            error => {
                this.displayMessage(error.response.data, true);
            });
    }
    refreshDeadlinePieChart(inputProject) {
        ProjectService.getDeadlineProjectStatistics(inputProject.uniqueId).then(
            response => {
                this.setState({
                    deadlineStatistics: response.data,
                });
            },
            error => {
                this.displayMessage(error.response.data, true);
            });
    }
    refreshGradesPieChart(inputProject) {
        ProjectService.getGradesProjectStatistics(inputProject.uniqueId).then(
            response => {
                this.setState({
                    gradesStatistics: response.data,
                });
            },
            error => {
                this.displayMessage(error.response.data, true);
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
                    {(this.state.projectsReady && this.state.coursesReady) ?
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
                            {(this.state.selectedProject == null) ?
                                <div>
                                    <Row>
                                        <Col lg="6">
                                            <h2>{this.state.messages['Projects'][this.state.languagePreference]}</h2>
                                        </Col>
                                        <Col lg="3">
                                            <Form.Check
                                                label={this.state.messages['DisplayMyProjects'][this.state.languagePreference]}
                                                checked={this.state.displayMyProjects}
                                                onChange={this.onChangeProjectDisplay}
                                            />
                                        </Col>
                                        <Col lg="3">
                                            <button className="btn btn-primary btn-block" onClick={this.addProject}>
                                                <span>{this.state.messages['AddProject'][this.state.languagePreference]}</span>
                                            </button>
                                        </Col>

                                        <Modal show={this.state.isAssignProjectToUserModalOpen} onHide={this.closeAssignProjectToUserModal} size="lg">
                                            <Modal.Header closeButton>
                                                <Modal.Title>{this.state.messages['AssignProjectToUser'][this.state.languagePreference]}</Modal.Title>:
                                            </Modal.Header>
                                            <Modal.Body>
                                                <Form>
                                                    <Form.Group>
                                                        <Form.Label>{this.state.messages['ProjectName'][this.state.languagePreference]}</Form.Label>
                                                        <Form.Control
                                                            disabled={true}
                                                            value={this.state.projectToAssign}
                                                            type='text'/>
                                                    </Form.Group>
                                                    <Form.Group>
                                                        <Form.Label>{this.state.messages['Assignee'][this.state.languagePreference]}</Form.Label>
                                                        <Form.Control
                                                            onChange={this.onChangeAssignedUser}
                                                            value={this.state.assignedUser}
                                                            type='text'/>
                                                    </Form.Group>
                                                </Form>
                                            </Modal.Body>
                                            <Modal.Footer>
                                                <Button variant="secondary" onClick={this.closeAssignProjectToUserModal}>{this.state.messages['Close'][this.state.languagePreference]}</Button>
                                                <Button variant="primary" onClick={this.assignProjectToUser}>{this.state.messages['AssignUser'][this.state.languagePreference]}</Button>
                                            </Modal.Footer>
                                        </Modal>

                                        <Modal show={this.state.isAssignProjectToGroupModalOpen} onHide={this.closeAssignProjectToGroupModal} size="lg">
                                            <Modal.Header closeButton>
                                                <Modal.Title>{this.state.messages['AssignProjectToGroup'][this.state.languagePreference]}</Modal.Title>:
                                            </Modal.Header>
                                            <Modal.Body>
                                                <Form>
                                                    <Form.Group>
                                                        <Form.Label>{this.state.messages['ProjectName'][this.state.languagePreference]}</Form.Label>
                                                        <Form.Control
                                                            disabled={true}
                                                            value={this.state.projectToAssign}
                                                            type='text'/>
                                                    </Form.Group>
                                                    <Form.Group>
                                                        <Form.Label>{this.state.messages['AssigneeGroup'][this.state.languagePreference]}</Form.Label>
                                                        <Form.Control
                                                            onChange={this.onChangeAssignedGroup}
                                                            value={this.state.assignedGroup}
                                                            type='text'/>
                                                    </Form.Group>
                                                </Form>
                                            </Modal.Body>
                                            <Modal.Footer>
                                                <Button variant="secondary" onClick={this.closeAssignProjectToUserModal}>{this.state.messages['Close'][this.state.languagePreference]}</Button>
                                                <Button variant="primary" onClick={this.assignProjectToGroup}>{this.state.messages['AssigneeToGroup'][this.state.languagePreference]}</Button>
                                            </Modal.Footer>
                                        </Modal>

                                        <Modal show={this.state.isAddProjectModalOpen} onHide={this.closeAddProjectModal} size="lg">
                                            <Modal.Header closeButton>
                                                {((this.state.addProjectModal) ?
                                                        <Modal.Title>{this.state.messages['AddNewProject'][this.state.languagePreference]}</Modal.Title>:
                                                        <Modal.Title>{this.state.messages['UpdateProject'][this.state.languagePreference]}</Modal.Title>
                                                )}
                                            </Modal.Header>
                                            <Modal.Body>
                                                <Form>
                                                    <Form.Group>
                                                        <Form.Label>{this.state.messages['ProjectName'][this.state.languagePreference]}</Form.Label>
                                                        <Form.Control
                                                            onChange={this.onChangeProjectName}
                                                            value={this.state.projectName}
                                                            type='text'/>
                                                    </Form.Group>
                                                    <Form.Group>
                                                        <Form.Label>{this.state.messages['Description'][this.state.languagePreference]}</Form.Label>
                                                        <Form.Control
                                                            onChange={this.onChangeProjectDescription}
                                                            value={this.state.projectDescription}
                                                            as='textarea'/>
                                                    </Form.Group>
                                                    <Form.Group>
                                                        {((this.state.addProjectModal) ?
                                                                <Form.Label>{this.state.messages['Course'][this.state.languagePreference]}:</Form.Label>:
                                                                <Form.Label>{this.state.messages['UpdateCourse'][this.state.languagePreference]}:</Form.Label>
                                                        )}
                                                        <Form.Control as='select' onChange={this.selectCourse}>
                                                            <option value=''>{this.state.messages['SelectCourse'][this.state.languagePreference]}:</option>
                                                            {this.state.courses.map((data, i) => (
                                                                <option value={data.uniqueId}>{data.completeName}</option>
                                                            ))}
                                                        </Form.Control>
                                                    </Form.Group>

                                                    <Form.Group>
                                                        {((this.state.addProjectModal) ?
                                                                <Form.Label>{this.state.messages['Deadline'][this.state.languagePreference]}:</Form.Label>:
                                                                <Form.Label>{this.state.messages['UpdateDeadline'][this.state.languagePreference]}:</Form.Label>
                                                        )}
                                                        <Form.Control type="datetime-local" placeholder="Date" onChange={(e) => this.setProjectDeadlineDate(e.target.value)}/>
                                                    </Form.Group>


                                                </Form>
                                            </Modal.Body>
                                            <Modal.Footer>
                                                <Button variant="secondary" onClick={this.closeAddProjectModal}>{this.state.messages['Close'][this.state.languagePreference]}</Button>
                                                {((this.state.addProjectModal) ?
                                                        <Button variant="primary" onClick={this.saveProjectTemplate}>{this.state.messages['Save'][this.state.languagePreference]}</Button>:
                                                        <Button variant="warning" onClick={this.updateProjectTemplate}>{this.state.messages['Update'][this.state.languagePreference]}</Button>
                                                )}

                                            </Modal.Footer>
                                        </Modal>
                                    </Row>
                                    {(this.state.projects.length > 0) ?
                                        <Row>
                                            <Col lg="12">
                                                <Table striped bordered hover>
                                                    <thead>
                                                    <tr>
                                                        <th>#</th>
                                                        <th>{this.state.messages['ProjectName'][this.state.languagePreference]}</th>
                                                        <th>{this.state.messages['Owner'][this.state.languagePreference]}</th>
                                                        <th>{this.state.messages['Deadline'][this.state.languagePreference]}</th>
                                                        <th>{this.state.messages['Course'][this.state.languagePreference]}</th>
                                                        <th>{this.state.messages['Actions'][this.state.languagePreference]}</th>
                                                    </tr>
                                                    </thead>
                                                    <tbody>
                                                    {this.state.projects.map((data, i) => (

                                                        <tr data-toggle="collapse" data-target=".multi-collapse1">
                                                            <td onClick={() => this.openProjectStatisticsViewer(data)}>{i + 1}</td>
                                                            <td onClick={() => this.openProjectStatisticsViewer(data)}>{data.projectName}</td>
                                                            <td onClick={() => this.openProjectStatisticsViewer(data)}>{data.owner}</td>
                                                            <td onClick={() => this.openProjectStatisticsViewer(data)}>{this.shortenData(data.deadline)}</td>
                                                            <td onClick={() => this.openProjectStatisticsViewer(data)}>{data.course.completeName}</td>
                                                            <td>
                                                                <div className="btn-toolbar">
                                                                    <Button type="button" className="btn btn-dark mx-2" onClick={() => this.deleteProject(data)}><FontAwesomeIcon icon={faTrash} /></Button>
                                                                    <Button type="button" className="btn btn-warning mx-2" onClick={() => this.editProject(data)}><FontAwesomeIcon icon={faEdit} /></Button>
                                                                    <Button type="button" className="btn btn-primary mx-2" onClick={() => this.openModalAssignProjectToUser(data)}><FontAwesomeIcon icon={faUser} /></Button>
                                                                    <Button type="button" className="btn btn-primary mx-2" onClick={() => this.openModalAssignProjectToGroup(data)}><FontAwesomeIcon icon={faUsers} /></Button>
                                                                </div>
                                                            </td>
                                                        </tr>
                                                    ))}
                                                    </tbody>
                                                </Table>
                                            </Col>
                                        </Row> : null }
                                </div> :
                                <div>
                                    <Row>
                                        <Col lg="10">
                                            <h4>{this.state.messages['StatisticsProject'][this.state.languagePreference]} {this.state.selectedProject.projectName}</h4>
                                        </Col>
                                        <Col lg="2">
                                            <button className="btn btn-warning btn-block" onClick={() => this.openProjectStatisticsViewer(null)}>
                                                <span>{this.state.messages['Back'][this.state.languagePreference]}</span>
                                            </button>
                                        </Col>
                                    </Row>
                                    <Row>
                                        {(this.state.reviewedStatistics != null) ?
                                            <Chart
                                                chartType="PieChart"
                                                width="100%"
                                                height="400px"
                                                data={this.state.reviewedStatistics}
                                                options={{
                                                    title: this.state.messages['AssignmentsStatistics'][this.state.languagePreference],
                                                    pieHole: 0.4,
                                                    is3D: false
                                                }}
                                            />
                                            : null }
                                    </Row>
                                    <Row>
                                        {(this.state.deadlineStatistics != null) ?
                                            <Chart
                                                chartType="PieChart"
                                                width="100%"
                                                height="400px"
                                                data={this.state.deadlineStatistics}
                                                options={{
                                                    title: this.state.messages['DeadlineStatistics'][this.state.languagePreference],
                                                    pieHole: 0.4,
                                                    is3D: false
                                                }}
                                            />
                                            : null }
                                    </Row>
                                    <Row>
                                        {(this.state.gradesStatistics != null) ?
                                            <Chart
                                                chartType="LineChart"
                                                width="100%"
                                                height="400px"
                                                data={this.state.gradesStatistics}
                                                options={{
                                                    title: this.state.messages['GradesStatistics'][this.state.languagePreference],
                                                    curveType: "function",
                                                    legend: { position: "bottom" }
                                                }}
                                            />
                                            : null }
                                    </Row>

                                </div>}

                        </div> : null }
                </Container>
            </div>
        );
    }


}