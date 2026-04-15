function requireAuth() {
    if (!AuthService.isLoggedIn()) {
        window.location.replace('login.html');
    }
}

function requireNoAuth() {
    if (AuthService.isLoggedIn()) {
        window.location.replace('profile.html');
    }
}