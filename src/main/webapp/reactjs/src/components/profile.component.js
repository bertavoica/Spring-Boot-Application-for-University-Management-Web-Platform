import React, { Component } from "react";
import { Redirect } from "react-router-dom";
import AuthService from "../services/auth.service";
import UserService from "../services/user.service";
import StudentService from "../services/student.service";
import {Badge, Col, Container, Row, Toast} from "react-bootstrap";
import ComboBox from "react-responsive-combo-box";
import 'react-responsive-combo-box/dist/index.css'
import authUser from "../services/auth-user";

export default class Profile extends Component {

    constructor(props) {
        super(props);

        this.state = {
            redirect: null,
            educationCycle: null,
            specialization: null,
            userReady: false,
            currentUser: { username: "" },
            options: [
                "Romanian",
                "English"
            ],
            selectedOption: "",
            languagePreference: "",
            showToast: false,
            errorToast: false,
            infoMessage: "",
            isTeacher: false,
            isAdmin: false,
            isStudent: false,
            messages: {'Header': {'Romanian' : "Profilul lui",
                                  'English' : "Profile of"},
                       'Registered': {'Romanian' : "Adresă de email înregistrată",
                                      'English' : "Registered email"},
                       'Roles': {'Romanian' : "Drepturi",
                                 'English' : "Authorities"},
                       'Language': {'Romanian' : "Limba preferată",
                                    'English' : "Language preference"},
                       'Specialization': {'Romanian' : "Specializare",
                                          'English' : "Specialization"},
                       'Cycle': {'Romanian' : "Ciclu educațonal",
                                 'English' : "Education cycle"},
                       'Bachelor': {'Romanian' : "Licenta",
                                    'English' : "Bachelor"},
                       'Master': {'Romanian' : "Master",
                                  'English' : "Master"},
                       'Doctorale': {'Romanian' : "Doctorat",
                                     'English' : "Doctorale"},
                       'Unknown': {'Romanian' : "Necunoscut",
                                   'English' : "Unknown"},
                       'Teacher': {'Romanian' : "Profesor",
                                   'English' : "Teacher"}

                                 },
        };
    }

    displayMessage = (message, error) => {
        this.setState({
            infoMessage: message,
            showToast: true,
            errorToast: error,
        });
        setTimeout(() => this.setState({showToast: false}), 5000);
    }

    setSelectedOption(e) {
        this.setState({
            selectedOption: e,
        });

        UserService.updateLanguage(authUser(), e).then(
            response => {
                this.getLanguagePreference();
                window.location.reload();
            },
            error => {
                this.displayMessage((error.response &&
                    error.response.data &&
                    error.response.data.message) ||
                    error.message ||
                    error.toString(), true);
            });
    }

    componentDidMount() {

        const currentUser = AuthService.getCurrentUser();

        if (!currentUser) this.setState({ redirect: "/home" });

        if (currentUser) {
            this.setState({
                isTeacher: currentUser.roles.includes("ROLE_TEACHER"),
                isStudent: currentUser.roles.includes("ROLE_STUDENT"),
                isAdmin: currentUser.roles.includes("ROLE_ADMIN"),
            });
        }

        this.getLanguagePreference();

        if (currentUser.roles.includes("ROLE_STUDENT")) {
            this.getStudentDetails();
        }

    }

    getLanguagePreference() {
        let currentUser = AuthService.getCurrentUser();
        UserService.getLanguage(currentUser.username).then(
            response => {
                this.setState({
                    languagePreference: response.data,
                    userReady: true,
                    currentUser: currentUser,

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

    getStudentDetails() {
        const currentUser = AuthService.getCurrentUser();

        StudentService.getDetails(currentUser.username).then(
            response => {
                this.setState({
                    educationCycle: response.data.educationCycle,
                    specialization: response.data.specialization,
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

    render() {
        if (this.state.redirect) {
            return <Redirect to={this.state.redirect} />
        }

        const { currentUser, educationCycle, specialization } = this.state;

        return (
            <Container>
                {(this.state.userReady) ?
                    <div>
                        <style type="text/css">
                            {`
                        .toast {
                            position:fixed;
                            top:70px;
                            right:40px;
                        }
                    `}
                        </style>

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
                        <Row>
                            <Col lg="12">
                                <header className="jumbotron">
                                    <h3>
                                        {this.state.messages['Header'][this.state.languagePreference]} <strong>{currentUser.username}</strong>
                                    </h3>
                                </header>
                            </Col>
                        </Row>
                        <Row>
                            <Col lg="12">
                                <p>
                                    <strong>{this.state.messages['Registered'][this.state.languagePreference]}:</strong>{" "}
                                    {currentUser.email}
                                </p>
                            </Col>
                        </Row>
                        {this.state.isStudent ?
                            <div>
                                <Row>
                                    <Col lg="12">
                                        <p>
                                            <strong>{this.state.messages['Cycle'][this.state.languagePreference]}:</strong>{" "}
                                            {
                                                educationCycle === "BACHELOR" ?
                                                    <Badge variant="primary">{this.state.messages['Bachelor'][this.state.languagePreference]}</Badge>
                                                    : educationCycle === "MASTER" ?
                                                    <Badge variant="info">{this.state.messages['Master'][this.state.languagePreference]}</Badge>
                                                    : educationCycle === "DOCTORAL" ?
                                                    <Badge variant="info">{this.state.messages['Doctoral'][this.state.languagePreference]}</Badge>
                                                    :
                                                    <Badge variant="secondary">{this.state.messages['Unknown'][this.state.languagePreference]}</Badge>
                                            }
                                        </p>
                                    </Col>
                                </Row>
                                <Row>
                                    <Col lg="3">
                                        <p>
                                            <strong>{this.state.messages['Specialization'][this.state.languagePreference]}:</strong>{" "}
                                            {specialization}
                                        </p>
                                    </Col>
                                </Row>
                            </div>
                        : null }

                        <Row>
                            <Col lg="12">
                                {(this.state.isAdmin || this.state.isTeacher) ?
                                    <div>
                                        <strong>{this.state.messages['Roles'][this.state.languagePreference]}:</strong>
                                        <ul>
                                            {currentUser.roles &&
                                            currentUser.roles.map((role, index) => <li key={index}>{
                                                role === "ROLE_STUDENT" ? "Student"
                                                    : role === "ROLE_TEACHER" ? this.state.messages['Teacher'][this.state.languagePreference]
                                                    : role === "ROLE_ADMIN" ? "Admin"
                                                                : "Unknown"
                                            }</li>
                                            )}
                                        </ul>
                                    </div>
                                    : null }
                            </Col>
                        </Row>
                        <Row>
                            <Col lg="3">
                                <p>
                                    <strong>{this.state.messages['Language'][this.state.languagePreference]}:</strong>{" "}
                                </p>
                                <ComboBox
                                    options={this.state.options}
                                    defaultIndex={4}
                                    defaultValue={this.state.languagePreference}
                                    autocomplete="off"
                                    optionsListMaxHeight={300}
                                    style={{
                                        width: "350px",
                                        margin: "0 auto"
                                    }}
                                    focusColor="#20C374"
                                    renderOptions={(option) => (
                                        <div className="comboBoxOption">{option}</div>
                                    )}
                                    onSelect={(option) => this.setSelectedOption(option)}
                                    onChange={(event) => console.log(event.target.value)}
                                    enableAutocomplete
                                />
                            </Col>
                        </Row>
                        <Row>

                        </Row>
                    </div>: null}
            </Container>
        );
    }
}