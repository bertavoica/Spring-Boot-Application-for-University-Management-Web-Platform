
export default function authUser() {
    return JSON.parse(localStorage.getItem('user')).username;
}