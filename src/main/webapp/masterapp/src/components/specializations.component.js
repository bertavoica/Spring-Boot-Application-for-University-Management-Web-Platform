import React, {Component} from "react";

import UserService from "../services/user.service";
import TeacherService from "../services/teacher.service";
import SpecializationService from "../services/specialization.service";
import {
    Button,
    Col,
    Container,
    Modal,
    Row,
    Table,
    Toast,
    Form, Dropdown
} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {
    faEdit, faExchangeAlt,
    faMinus,
    faPlus, faProjectDiagram, faStar,
    faTrash,
    faUser,
    faUserMinus,
    faUserPlus,
    faUsers
} from "@fortawesome/free-solid-svg-icons";
import authUser from "../services/auth-user";
import Chart from "react-google-charts";
import AuthService from "../services/auth.service";
import ComboBox from "react-responsive-combo-box";
import 'react-responsive-combo-box/dist/index.css'

export default class Specializations extends Component {
    constructor(props) {
        super(props);

        // Add specialization
        this.onChangeSpecializationName = this.onChangeSpecializationName.bind(this);
        this.openAddSpecializationModal = this.openAddSpecializationModal.bind(this);
        this.closeAddSpecializationModal = this.closeAddSpecializationModal.bind(this);
        this.saveSpecialization = this.saveSpecialization.bind(this);

        // Edit specialization
        this.openUpdateSpecializationModal = this.openUpdateSpecializationModal.bind(this);
        this.closeUpdateSpecializationModal = this.closeUpdateSpecializationModal.bind(this);
        this.updateSpecialization = this.updateSpecialization.bind(this);

        // Add members to specialization
        this.openAddMemberModal = this.openAddMemberModal.bind(this);
        this.closeAddMemberModal = this.closeAddMemberModal.bind(this);
        this.onChangeTeacherName = this.onChangeTeacherName.bind(this);
        this.onChangeSuperiorName = this.onChangeSuperiorName.bind(this);
        this.onChangeTeacherTitle = this.onChangeTeacherTitle.bind(this);
        this.saveMember = this.saveMember.bind(this);
        this.selectTeacherTitle = this.selectTeacherTitle.bind(this);
        this.getAvailableTeachers = this.getAvailableTeachers.bind(this);

        // Delete members from specializations
        this.deleteMember = this.deleteMember.bind(this);
        this.openDeleteMemberModal = this.openDeleteMemberModal.bind(this);
        this.closeDeleteMemberModal = this.closeDeleteMemberModal.bind(this);

        // Modify - rank
        this.modifySuperiorMember = this.modifySuperiorMember.bind(this);
        this.openModifyMemberModal = this.openModifyMemberModal.bind(this);
        this.closeModifyMemberModal = this.closeModifyMemberModal.bind(this);

        // Utils
        this.shortenData = this.shortenData.bind(this);

        this.state = {
            specializations: [],
            specializationsReady: false,
            showToast: false,
            errorToast: false,
            infoMessage: "",
            isAddSpecializationModalOpen: false,
            specializationName: "",
            selectedSpecialization: null,
            isAddMemberModalOpen: false,
            isDeleteMemberModalOpen: false,
            isModifyMemberModalOpen: false,
            teacherName: "",
            teacherTitle: "",
            superiorName: "",
            specializationGraph: null,
            isAdmin: false,
            isTeacher: false,
            isUpdateSpecializationModalOpen: false,
            specializationUniqueId: "",
            availableTeachers: [],
            specializationTeachers: [],
            teacherHName: "",
            languagePreference: "English",
            messages: {
                'Header': {
                    'Romanian': "Specializările universității",
                    'English': "University specializations"
                },
                'Specializations': {
                    'Romanian': "Specializări",
                    'English': "Specializations"
                },
                'Specialization': {
                    'Romanian': "Specializarea ",
                    'English': "Specialization "
                },
                'Name': {
                    'Romanian': "Nume",
                    'English': "Name"
                },
                'TotalTeachers': {
                    'Romanian': "Numărul total de profesori",
                    'English': "Total teachers"
                },
                'Actions': {
                    'Romanian': "Acțiuni",
                    'English': "Actions"
                },
                'Close': {
                    'Romanian': "Închide",
                    'English': "Close"
                },
                'Save': {
                    'Romanian': "Salvează",
                    'English': "Save"
                },
                'Update': {
                    'Romanian': "Actualizează",
                    'English': "Update"
                },
                'Delete': {
                    'Romanian': "Șterge",
                    'English': "Delete"
                },

                'AddNewSpecialization': {
                    'Romanian': "Adaugă o specializare nouă",
                    'English': "Add new specialization"
                },
                'SpecializationDelete': {
                    'Romanian': "Ești sigur că vrei să ștergi specializarea ",
                    'English': "Are you sure you want to delete specialization "
                },
                'UpdateSpecialization': {
                    'Romanian': "Actualizează specializarea",
                    'English': "Update specialization"
                },
                'SpecializationName': {
                    'Romanian': "Numele specializării",
                    'English': "Specialzation name"
                },
                'TeacherName': {
                    'Romanian': "Numele profesorului",
                    'English': "Teacher name"
                },
                'SuperiorName': {
                    'Romanian': "Numele superiorului (opțional)",
                    'English': "Superior name (optional)"
                },
                'SuperiorNameModify': {
                    'Romanian': "Numele superiorului (lăsați liber pentru primul nivel)",
                    'English': "Superior name (leave empty for lead)"
                },
                'Back': {
                    'Romanian': "Înapoi",
                    'English': "Back"
                },
                'AddMember': {
                    'Romanian': "Adaugă un nou membru specializării ",
                    'English': "Add a new member to specialization "
                },
                'ModifyMember': {
                    'Romanian': "Modifică membru din specializarea ",
                    'English': "Modify member from specialization "
                },
                'RemoveMember': {
                    'Romanian': "Șterge membru din specializare ",
                    'English': "Remove member from specialization "
                },
                'ChooseTeacherSpecialization': {
                    'Romanian': "Alege membru din specializare ",
                    'English': "Choose teacher from specialization "
                },
                'ChooseTeacher': {
                    'Romanian': "Alege profesor",
                    'English': "Choose teacher"
                },
                'ChooseSuperiorTeacher': {
                    'Romanian': "Alege profesor superior",
                    'English': "Choose superior teacher"
                },
                'CreateSpecialization': {
                    'Romanian': "Creează o specializare nouă",
                    'English': "Create a new specialization"
                },

            },
        };
    }

    setSelectedOption(e) {
        this.setState({
            selectedOption: e,
        });
    }

    setHighlightedOption(e) {
        this.setState({
            highlightedOption: e,
        });
    }

    setSelectedSuperior(inputTeacher) {
        this.setState({
            superiorName: inputTeacher
        });
    }

    setSelectedTeacher(inputTeacher) {
        this.setState({
            teacherName: inputTeacher
        });
    }

    setHighlightedTeacher(inputTeacher) {
        this.setState({
            teacherHName: inputTeacher
        });
    }

    onChangeSpecializationName(e) {
        this.setState({
            specializationName: e.target.value
        });
    }

    onChangeSuperiorName(e) {
        this.setState({
            superiorName: e.target.value
        });
    }

    onChangeTeacherName(e) {
        this.setState({
            teacherName: e.target.value
        });
    }

    onChangeTeacherTitle(e) {
        this.setState({
            teacherTitle: e.target.value
        });
    }

    openAddSpecializationModal = () => this.setState({isAddSpecializationModalOpen: true});
    closeAddSpecializationModal = () => this.setState({isAddSpecializationModalOpen: false});

    openAddMemberModal(e) {
        this.getAvailableTeachers(this.state.selectedSpecialization.name);
        this.setState({isAddMemberModalOpen: true});

    }

    closeAddMemberModal(e) {
        this.getAvailableTeachers(this.state.selectedSpecialization.name);
        this.setState({
            isAddMemberModalOpen: false,
            superiorName: "",
        });
    }

    openDeleteMemberModal(e) {
        this.getAvailableTeachers(this.state.selectedSpecialization.name);
        this.setState({isDeleteMemberModalOpen: true});
    }

    closeDeleteMemberModal(e) {
        this.getAvailableTeachers();
        this.setState({
            isDeleteMemberModalOpen: false,
        });
    }

    openModifyMemberModal = () => this.setState({isModifyMemberModalOpen: true});

    closeModifyMemberModal = () => this.setState({
        isModifyMemberModalOpen: false,
        superiorName: "",
    });

    openUpdateSpecializationModal(e) {
        this.setState({
            isUpdateSpecializationModalOpen: true,
            specializationName: e.name,
            specializationUniqueId: e.uniqueId,
        });
    }

    closeUpdateSpecializationModal = () => this.setState({isUpdateSpecializationModalOpen: false});

    getSpecializations() {
        SpecializationService.getSpecializations().then(
            response => {
                this.setState({
                    specializations: response.data,
                    specializationsReady: true,
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
                isAdmin: user.roles.includes("ROLE_ADMIN"),
            });
        }

        this.getSpecializations();
    }

    getLanguagePreference() {
        let currentUser = AuthService.getCurrentUser();
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

    closeToast = () => this.setState({showToast: false});
    displayMessage = (message, error) => {
        this.setState({
            infoMessage: message,
            showToast: true,
            errorToast: error,
        });
        setTimeout(() => this.setState({showToast: false}), 5000);
    }

    getAvailableTeachers(specialization = "") {
        let teacherNames = [];

        if (specialization !== "") {
            TeacherService.getAllTeachers("", "", specialization).then(
                response => {
                    teacherNames = [];
                    for (let i = 0; i < response.data.length; i++)
                        teacherNames.push(response.data[i].username);

                    this.setState({
                        specializationTeachers: teacherNames,
                    });

                    this.forceUpdate();
                },
                error => {
                    this.displayMessage((error.response &&
                        error.response.data &&
                        error.response.data.message) ||
                        error.message ||
                        error.toString(), false);
                });
        }


        TeacherService.getAllTeachers("", "true", "").then(
            response => {
                teacherNames = [];

                for (let i = 0; i < response.data.length; i++)
                    teacherNames.push(response.data[i].username);

                this.setState({
                    availableTeachers: teacherNames,
                });

                this.forceUpdate();
            },
            error => {
                this.displayMessage((error.response &&
                    error.response.data &&
                    error.response.data.message) ||
                    error.message ||
                    error.toString(), false);
            });


    }

    deleteSpecialization(e) {
        if (window.confirm(this.state.messages['SpecializationDelete'][this.state.languagePreference] + e.name)) {
            SpecializationService.deleteSpecialization(e.uniqueId).then(
                response => {
                    this.displayMessage(response.data, false);
                    this.getSpecializations();
                },
                error => {
                });
        }
    }

    openSpecializationViewer(e) {
        this.setState({
            selectedSpecialization: e,
        });
        if (e == null)
            this.setState({
                specializationGraph: null,
            });
        else {
            this.refreshGraphData(e.name);
            this.getAvailableTeachers(e.name);
        }
    }

    refreshGraphData(inputSpecializationName) {
        SpecializationService.getGraphData(inputSpecializationName).then(
            response => {
                this.setState({
                    specializationGraph: response.data,
                });
            },
            error => {
                this.displayMessage(error.response.data, true);
            });
    }

    saveSpecialization(e) {
        SpecializationService.addSpecialization(this.state.specializationName).then(
            response => {
                this.displayMessage(response.data, false);
                this.setState({
                    specializationName: "",
                });
                this.getSpecializations();
                this.closeAddSpecializationModal();
            },
            error => {
                this.displayMessage(error.response.data, true);
            });
    }

    updateSpecialization(e) {
        SpecializationService.updateSpecialization(this.state.specializationName, this.state.specializationUniqueId).then(
            response => {
                this.displayMessage(response.data, false);
                this.setState({
                    specializationName: "",
                    specializationUniqueId: "",
                });
                this.getSpecializations();
                this.closeUpdateSpecializationModal();
            },
            error => {
                this.displayMessage(error.response.data, true);
            });
    }

    saveMember(e) {
        SpecializationService.addMemberToSpecialization(this.state.selectedSpecialization.name, this.state.teacherName, this.state.superiorName).then(
            response => {
                this.displayMessage(response.data, false);
                this.refreshGraphData(this.state.selectedSpecialization.name);
                this.setState({
                    teacherName: "",
                    superiorName: "",
                    availableTeachers: [],
                    specializationTeachers: [],
                });
                this.getSpecializations();
                this.closeAddMemberModal();
            },
            error => {
                this.displayMessage(error.response.data, true);
            });
    }

    modifySuperiorMember(e) {
        SpecializationService.modifyMemberFromSpecialization(this.state.selectedSpecialization.name, this.state.teacherName, this.state.superiorName).then(
            response => {
                this.displayMessage(response.data, false);
                this.refreshGraphData(this.state.selectedSpecialization.name);
                this.setState({
                    teacherName: "",
                    superiorName: "",
                });
                this.getSpecializations();
                this.closeModifyMemberModal();
            },
            error => {
                this.displayMessage(error.response.data, true);
            });
    }

    deleteMember(e) {
        SpecializationService.deleteMemberToSpecialization(this.state.selectedSpecialization.name, this.state.teacherName).then(
            response => {
                this.displayMessage(response.data, false);
                this.refreshGraphData(this.state.selectedSpecialization.name);
                this.setState({
                    teacherName: "",
                });
                this.getSpecializations();
                this.closeDeleteMemberModal();
                this.getAvailableTeachers(this.state.selectedSpecialization.name);
            },
            error => {
                this.displayMessage(error.response.data, true);
            });
    }

    selectTeacherTitle(e) {
        this.setState({
            teacherTitle: e,
        });
    }

    shortenData(inputDate) {
        if (inputDate == null)
            return "";
        return inputDate.substr(0, 16).replace("T", " ");
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
                    {(this.state.specializationsReady) ?
                        <div>

                            {(this.state.showToast) ?
                                <Row>
                                    <Toast onClose={this.closeToast} bg="danger"
                                           className={this.state.errorToast ? "bg-danger" : ""}>
                                        <Toast.Header>
                                            <img className="rounded me-2" alt=""/>
                                            <strong className="me-auto">Info message</strong>
                                        </Toast.Header>
                                        <Toast.Body>{this.state.infoMessage}</Toast.Body>
                                    </Toast>
                                </Row>
                                : null}

                            <Modal show={this.state.isAddSpecializationModalOpen}
                                   onHide={this.closeAddSpecializationModal} size="lg">
                                <Modal.Header closeButton>
                                    <Modal.Title>{this.state.messages['CreateSpecialization'][this.state.languagePreference]}</Modal.Title>
                                </Modal.Header>
                                <Modal.Body>
                                    <Form>
                                        <Form.Group>
                                            <Form.Label>{this.state.messages['SpecializationName'][this.state.languagePreference]}</Form.Label>
                                            <Form.Control
                                                onChange={this.onChangeSpecializationName}
                                                value={this.state.specializationName}
                                                type='text'/>
                                        </Form.Group>
                                    </Form>
                                </Modal.Body>
                                <Modal.Footer>
                                    <Button variant="secondary"
                                            onClick={this.closeAddSpecializationModal}>{this.state.messages['Close'][this.state.languagePreference]}</Button>
                                    <Button variant="primary" onClick={this.saveSpecialization}>{this.state.messages['Save'][this.state.languagePreference]}</Button>
                                </Modal.Footer>
                            </Modal>

                            <Modal show={this.state.isUpdateSpecializationModalOpen}
                                   onHide={this.closeUpdateSpecializationModal} size="lg">
                                <Modal.Header closeButton>
                                    <Modal.Title>{this.state.messages['UpdateSpecialization'][this.state.languagePreference]}</Modal.Title>
                                </Modal.Header>
                                <Modal.Body>
                                    <Form>
                                        <Form.Group>
                                            <Form.Label>{this.state.messages['SpecializationName'][this.state.languagePreference]}</Form.Label>
                                            <Form.Control
                                                onChange={this.onChangeSpecializationName}
                                                value={this.state.specializationName}
                                                type='text'/>
                                        </Form.Group>
                                    </Form>
                                </Modal.Body>
                                <Modal.Footer>
                                    <Button variant="secondary"
                                            onClick={this.closeUpdateSpecializationModal}>{this.state.messages['Close'][this.state.languagePreference]}</Button>
                                    <Button variant="primary"
                                            onClick={this.updateSpecialization}>{this.state.messages['Save'][this.state.languagePreference]}</Button>
                                </Modal.Footer>
                            </Modal>


                            {(this.state.selectedSpecialization == null) ?
                                <div>
                                    <Row>
                                        <Col lg="9">
                                            <h2>{this.state.messages['Specializations'][this.state.languagePreference]}</h2>
                                        </Col>
                                        <Col lg="3">
                                            {(this.state.isAdmin) ?
                                                <button className="btn btn-primary btn-block"
                                                        onClick={this.openAddSpecializationModal}>
                                                    <span>{this.state.messages['AddNewSpecialization'][this.state.languagePreference]}</span>
                                                </button>
                                                : null}
                                        </Col>
                                    </Row>

                                    {(this.state.specializations.length > 0) ?
                                        <Row>
                                            <Col lg="12">
                                                <Table striped bordered hover>
                                                    <thead>
                                                    <tr>
                                                        <th>#</th>
                                                        <th>{this.state.messages['Name'][this.state.languagePreference]}</th>
                                                        <th>{this.state.messages['TotalTeachers'][this.state.languagePreference]}</th>
                                                        {(this.state.isAdmin) ?
                                                            <th>{this.state.messages['Actions'][this.state.languagePreference]}</th>
                                                            : null}
                                                    </tr>
                                                    </thead>
                                                    {this.state.specializations.map((data, i) => (
                                                        <tbody>
                                                        <tr>
                                                            <td onClick={() => this.openSpecializationViewer(data)}>{i + 1}</td>
                                                            <td onClick={() => this.openSpecializationViewer(data)}>{data.name}</td>
                                                            <td onClick={() => this.openSpecializationViewer(data)}>{data.teachers.length}</td>
                                                            {(this.state.isAdmin) ?
                                                                <td>
                                                                    <div className="btn-toolbar">
                                                                        <Button className="btn btn-dark mx-2"
                                                                                onClick={() => this.deleteSpecialization(data)}><FontAwesomeIcon
                                                                            icon={faTrash}/></Button>
                                                                        <Button className="btn btn-warning mx-2"
                                                                                onClick={() => this.openUpdateSpecializationModal(data)}><FontAwesomeIcon
                                                                            icon={faEdit}/></Button>
                                                                    </div>
                                                                </td>
                                                                : null}
                                                        </tr>
                                                        </tbody>
                                                    ))}
                                                </Table>
                                            </Col>
                                        </Row> : null}
                                </div>
                                :
                                <div>
                                    <Modal show={this.state.isAddMemberModalOpen} onHide={this.closeAddMemberModal}
                                           size="lg">
                                        <Modal.Header closeButton>
                                            <Modal.Title>{this.state.messages['AddMember'][this.state.languagePreference]} {this.state.selectedSpecialization.name}</Modal.Title>
                                        </Modal.Header>
                                        <Modal.Body>
                                            <Form>
                                                <Form.Group>
                                                    <Form.Label>{this.state.messages['SpecializationName'][this.state.languagePreference]}</Form.Label>
                                                    <Form.Control
                                                        value={this.state.selectedSpecialization.name}
                                                        disabled={true}
                                                        type='text'/>
                                                </Form.Group>
                                                <Form.Group>
                                                    <Form.Label>{this.state.messages['TeacherName'][this.state.languagePreference]}</Form.Label>
                                                    <ComboBox
                                                        options={this.state.availableTeachers}
                                                        placeholder={this.state.messages['ChooseTeacher'][this.state.languagePreference]}
                                                        defaultIndex={4}
                                                        optionsListMaxHeight={300}
                                                        style={{
                                                            width: "100%",
                                                            margin: "0 auto",
                                                        }}
                                                        focusColor="#20C374"
                                                        renderOptions={(option) => (
                                                            <div className="comboBoxOption">{option}</div>
                                                        )}
                                                        onSelect={(option) => this.setSelectedTeacher(option)}
                                                        onChange={(event) => console.log(event.target.value)}
                                                        enableAutocomplete
                                                    />


                                                    {/*<Form.Control*/}
                                                    {/*    onChange={this.onChangeTeacherName}*/}
                                                    {/*    value={this.state.teacherName}*/}
                                                    {/*    type='text'/>*/}
                                                </Form.Group>
                                                <Form.Group>
                                                    <Form.Label>{this.state.messages['SuperiorName'][this.state.languagePreference]}</Form.Label>

                                                    <ComboBox
                                                        options={this.state.specializationTeachers}
                                                        placeholder={this.state.messages['ChooseSuperiorTeacher'][this.state.languagePreference]}
                                                        defaultIndex={4}
                                                        optionsListMaxHeight={300}
                                                        style={{
                                                            width: "100%",
                                                            margin: "0 auto",
                                                        }}
                                                        focusColor="#20C374"
                                                        renderOptions={(option) => (
                                                            <div className="comboBoxOption">{option}</div>
                                                        )}
                                                        onSelect={(option) => this.setSelectedSuperior(option)}
                                                        onChange={(event) => console.log(event.target.value)}
                                                        enableAutocomplete
                                                    />
                                                </Form.Group>
                                            </Form>
                                        </Modal.Body>
                                        <Modal.Footer>
                                            <Button variant="secondary"
                                                    onClick={this.closeAddMemberModal}>{this.state.messages['Close'][this.state.languagePreference]}</Button>
                                            <Button variant="primary"
                                                    onClick={this.saveMember}>{this.state.messages['Save'][this.state.languagePreference]}</Button>
                                        </Modal.Footer>
                                    </Modal>

                                    <Modal show={this.state.isDeleteMemberModalOpen}
                                           onHide={this.closeDeleteMemberModal} size="lg">
                                        <Modal.Header closeButton>
                                            <Modal.Title>{this.state.messages['RemoveMember'][this.state.languagePreference]} {this.state.selectedSpecialization.name}</Modal.Title>
                                        </Modal.Header>
                                        <Modal.Body>
                                            <Form>
                                                <Form.Group>
                                                    <Form.Label>{this.state.messages['SpecializationName'][this.state.languagePreference]}</Form.Label>
                                                    <Form.Control
                                                        value={this.state.selectedSpecialization.name}
                                                        disabled={true}
                                                        type='text'/>
                                                </Form.Group>
                                                <ComboBox
                                                    options={this.state.specializationTeachers}
                                                    placeholder={this.state.messages['ChooseTeacherSpecialization'][this.state.languagePreference]}
                                                    defaultIndex={4}
                                                    optionsListMaxHeight={300}
                                                    style={{
                                                        width: "100%",
                                                        margin: "0 auto",
                                                    }}
                                                    focusColor="#20C374"
                                                    renderOptions={(option) => (
                                                        <div className="comboBoxOption">{option}</div>
                                                    )}
                                                    onSelect={(option) => this.setSelectedTeacher(option)}
                                                    onChange={(event) => console.log(event.target.value)}
                                                    enableAutocomplete
                                                />
                                            </Form>
                                        </Modal.Body>
                                        <Modal.Footer>
                                            <Button variant="secondary"
                                                    onClick={this.closeDeleteMemberModal}>{this.state.messages['Close'][this.state.languagePreference]}</Button>
                                            <Button variant="danger"
                                                    onClick={this.deleteMember}>{this.state.messages['Delete'][this.state.languagePreference]}</Button>
                                        </Modal.Footer>
                                    </Modal>

                                    <Modal show={this.state.isModifyMemberModalOpen}
                                           onHide={this.closeModifyMemberModal} size="lg">
                                        <Modal.Header closeButton>
                                            <Modal.Title>{this.state.messages['ModifyMember'][this.state.languagePreference]} {this.state.selectedSpecialization.name}</Modal.Title>
                                        </Modal.Header>
                                        <Modal.Body>
                                            <Form>
                                                <Form.Group>
                                                    <Form.Label>{this.state.messages['SpecializationName'][this.state.languagePreference]}</Form.Label>
                                                    <Form.Control
                                                        value={this.state.selectedSpecialization.name}
                                                        disabled={true}
                                                        type='text'/>
                                                </Form.Group>
                                                <Form.Group>
                                                    <Form.Label>{this.state.messages['TeacherName'][this.state.languagePreference]}</Form.Label>
                                                    <ComboBox
                                                        options={this.state.specializationTeachers}
                                                        placeholder={this.state.messages['ChooseTeacherSpecialization'][this.state.languagePreference]}
                                                        defaultIndex={4}
                                                        optionsListMaxHeight={300}
                                                        style={{
                                                            width: "100%",
                                                            margin: "0 auto",
                                                        }}
                                                        focusColor="#20C374"
                                                        renderOptions={(option) => (
                                                            <div className="comboBoxOption">{option}</div>
                                                        )}
                                                        onSelect={(option) => this.setSelectedTeacher(option)}
                                                        onChange={(event) => console.log(event.target.value)}
                                                        enableAutocomplete
                                                    />
                                                </Form.Group>
                                                <Form.Group>
                                                    <Form.Label>{this.state.messages['SuperiorNameModify'][this.state.languagePreference]}</Form.Label>
                                                    <ComboBox
                                                        options={this.state.specializationTeachers}
                                                        placeholder={this.state.messages['ChooseTeacherSpecialization'][this.state.languagePreference]}
                                                        defaultIndex={4}
                                                        optionsListMaxHeight={300}
                                                        style={{
                                                            width: "100%",
                                                            margin: "0 auto",
                                                        }}
                                                        focusColor="#20C374"
                                                        renderOptions={(option) => (
                                                            <div className="comboBoxOption">{option}</div>
                                                        )}
                                                        onSelect={(option) => this.setSelectedSuperior(option)}
                                                        onChange={(event) => console.log(event.target.value)}
                                                        enableAutocomplete
                                                    />
                                                </Form.Group>
                                            </Form>
                                        </Modal.Body>
                                        <Modal.Footer>
                                            <Button variant="secondary"
                                                    onClick={this.closeModifyMemberModal}>{this.state.messages['Close'][this.state.languagePreference]}</Button>
                                            <Button variant="warning"
                                                    onClick={this.modifySuperiorMember}>{this.state.messages['Update'][this.state.languagePreference]}</Button>
                                        </Modal.Footer>
                                    </Modal>

                                    <Row>
                                        <Col lg="10">
                                            <h4>{this.state.messages['Specialization'][this.state.languagePreference]} {this.state.selectedSpecialization.name}</h4>
                                        </Col>
                                        <Col lg="2">
                                            <button className="btn btn-warning btn-block"
                                                    onClick={() => this.openSpecializationViewer(null)}>
                                                <span>{this.state.messages['Back'][this.state.languagePreference]}</span>
                                            </button>
                                        </Col>
                                    </Row>
                                    {(this.state.isAdmin) ?
                                        <Row style={{margin: 35}} className="text-center">
                                            <Col lg="3">
                                            </Col>
                                            <Col lg="2">
                                                <Button className="btn btn-primary mx-2"
                                                        onClick={() => this.openAddMemberModal()}><FontAwesomeIcon
                                                    icon={faPlus}/></Button>
                                            </Col>
                                            <Col lg="2">
                                                <Button className="btn btn-warning mx-2"
                                                        onClick={() => this.openModifyMemberModal()}><FontAwesomeIcon
                                                    icon={faProjectDiagram}/></Button>
                                            </Col>
                                            <Col lg="2">
                                                <Button className="btn btn-danger mx-2"
                                                        onClick={() => this.openDeleteMemberModal()}><FontAwesomeIcon
                                                    icon={faMinus}/></Button>
                                            </Col>
                                        </Row> : null}
                                    {(this.state.specializationGraph != null) ?
                                        <Row>

                                            <Chart
                                                chartType="OrgChart"
                                                data={this.state.specializationGraph}
                                                options={{
                                                    allowHtml: true
                                                }}
                                                width="100%"
                                                height="400px"
                                            />
                                        </Row>
                                        : null}
                                </div>}
                        </div> : null}
                </Container>
            </div>
        );
    }
}