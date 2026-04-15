function requireAuth() {
    if (!AuthService.isAuthenticated()) {
        window.location.replace('login.html');
    }
}

function requireNoAuth() {
    // Si ya está logueado, no tiene sentido que vea el login o registro
    if (AuthService.isAuthenticated()) {
        window.location.replace('profile.html');
    }
}