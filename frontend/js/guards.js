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

/*
    Devuelve el nombre del rol del usuario.
    Sirve tanto si el backend devuelve rol como texto
    como si devuelve un objeto { nombreRol: 'ADMIN' }.
*/
function obtenerNombreRol(user) {
    if (!user || !user.rol) {
        return '';
    }

    if (typeof user.rol === 'string') {
        return user.rol.toUpperCase();
    }

    if (typeof user.rol === 'object' && user.rol.nombreRol) {
        return user.rol.nombreRol.toUpperCase();
    }

    return '';
}

/*
    Solo permite entrar si el usuario autenticado es admin.
*/
function requireAdmin() {
    if (!AuthService.isLoggedIn()) {
        window.location.replace('login.html');
        return;
    }

    const userInfoString = localStorage.getItem('userInfo');

    if (!userInfoString) {
        window.location.replace('login.html');
        return;
    }

    const userInfo = JSON.parse(userInfoString);
    const rol = obtenerNombreRol(userInfo);

    if (rol !== 'ADMIN' && rol !== 'ROLE_ADMIN') {
        window.location.replace('profile.html');
    }
}