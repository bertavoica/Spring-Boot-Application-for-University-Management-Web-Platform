import React, { Component } from "react";
import { Switch, Route, Link } from "react-router-dom";
import "bootstrap/dist/css/bootstrap.min.css";
import "./App.css";

import AuthService from "./services/auth.service";

import Login from "./components/login.component";
import Register from "./components/register.component";
import Home from "./components/home.component";
import Profile from "./components/profile.component";
import BoardTeacher from "./components/board-teacher.component";
import BoardAdmin from "./components/board-admin.component";
import AssignmentsComponent from "./components/assignments.component";
import Specializations from "./components/specializations.component";
import UserService from "./services/user.service";
import {Row, Toast} from "react-bootstrap";

class App extends Component {
    constructor(props) {
        super(props);
        this.logOut = this.logOut.bind(this);

        this.state = {
            infoMessage: "",
            showToast: false,
            errorToast: false,
            isTeacher: false,
            isAdmin: false,
            isStudent: false,
            currentUser: undefined,
            languagePreference: "English",
            messages: {'Home': {'Romanian' : "Acasă",
                    'English' : "Home"},
                'TeacherBoard': {'Romanian' : "Meniul profesorilor",
                    'English' : "Teacher Board"},
                'Specializations': {'Romanian' : "Specializări",
                    'English' : "Specializations"},
                'Assignments': {'Romanian' : "Sarcini",
                    'English' : "Assignments"},
                'Specialization': {'Romanian' : "Specializare",
                    'English' : "Specialization"},
                'AdminBoard': {'Romanian' : "Meniul admnilor",
                    'English' : "Admin Board"},
                'Courses': {'Romanian' : "Cursuri",
                    'English' : "Courses"},
                'LogOut': {'Romanian' : "Deconectare",
                    'English' : "Log out"},
                'Login': {'Romanian' : "Autentificare",
                    'English' : "Login"},
                'SignUp': {'Romanian' : "Înregistrare",
                    'English' : "Sign up"}
            },
        };
    }

    componentDidMount() {
        const user = AuthService.getCurrentUser();

        if (user) {
            this.setState({
                currentUser: user,
                isTeacher: user.roles.includes("ROLE_TEACHER"),
                isAdmin: user.roles.includes("ROLE_ADMIN"),
                isStudent: user.roles.includes("ROLE_STUDENT"),
            });
        }

        this.getLanguagePreference();
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

    logOut() {
        AuthService.logout();
    }

    callApi = () => {
        alert('CALL!');
    }

    render() {
        const { currentUser, isTeacher, isAdmin, isStudent } = this.state;

        return (
            <div>
                <nav className="navbar navbar-expand navbar-dark bg-dark">
                    <Link to={"/"} className="navbar-brand">
                        etti
                    </Link>
                    <div className="navbar-nav mr-auto">
                        <li className="nav-item">
                            <Link to={"/home"} className="nav-link">
                                {this.state.messages['Home'][this.state.languagePreference]}
                            </Link>
                        </li>

                        {isTeacher && (
                            <li className="nav-item">
                                <Link to={"/teacher"} className="nav-link">
                                    {this.state.messages['TeacherBoard'][this.state.languagePreference]}
                                </Link>
                            </li>
                        )}

                        {(isTeacher || isAdmin) && (
                            <li className="nav-item">
                                <Link to={"/specializations"} className="nav-link">
                                    {this.state.messages['Specializations'][this.state.languagePreference]}
                                </Link>
                            </li>
                        )}

                        {(isTeacher || isStudent) && (
                            <li className="nav-item">
                                <Link to={"/assignments"} className="nav-link">
                                    {this.state.messages['Assignments'][this.state.languagePreference]}

                                </Link>
                            </li>
                        )}

                        {isAdmin && (
                            <li className="nav-item">
                                <Link to={"/admin"} className="nav-link">
                                    {this.state.messages['AdminBoard'][this.state.languagePreference]}
                                </Link>
                            </li>
                        )}

                    </div>

                    {currentUser ? (
                        <div className="navbar-nav ml-auto">
                            <li className="nav-item">
                                <Link to={"/profile"} className="nav-link">
                                    {currentUser.username}
                                </Link>
                            </li>
                            <li className="nav-item">
                                <a href="/login" className="nav-link" onClick={this.logOut}>
                                    {this.state.messages['LogOut'][this.state.languagePreference]}
                                </a>
                            </li>
                        </div>
                    ) : (
                        <div className="navbar-nav ml-auto">
                            <li className="nav-item">
                                <Link to={"/login"} className="nav-link">
                                    {this.state.messages['Login'][this.state.languagePreference]}
                                </Link>
                            </li>

                            <li className="nav-item">
                                <Link to={"/register"} className="nav-link">
                                    {this.state.messages['SignUp'][this.state.languagePreference]}
                                </Link>
                            </li>
                        </div>
                    )}
                </nav>

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

                <div className="container mt-3">
                    <Switch>
                        <Route exact path={["/", "/home"]} component={Home} />
                        <Route exact path="/login" component={Login} />
                        <Route exact path="/register" component={Register} />
                        <Route exact path="/profile" component={Profile} />
                        <Route path="/teacher" component={BoardTeacher} />
                        <Route path="/admin" component={BoardAdmin} />
                        <Route path="/assignments" component={AssignmentsComponent} />
                        <Route path="/specializations" component={Specializations} />
                    </Switch>
                </div>

            </div>
        );
    }
}

export default App;