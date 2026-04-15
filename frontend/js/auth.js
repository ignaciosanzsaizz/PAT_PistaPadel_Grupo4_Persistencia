const AuthService = {
    async register(userData) {
        return await apiFetch('/auth/register', 'POST', userData);
    },

    async login(email, password) {
        const token = btoa(`${email}:${password}`);
        localStorage.setItem('userAuth', token);
        
        try {
            const user = await this.getMe();
            localStorage.setItem('userInfo', JSON.stringify(user));
            return user;
        } catch (error) {
            localStorage.removeItem('userAuth');
            localStorage.removeItem('userInfo');
            throw error;
        }
    },

    async getMe() {
        return await apiFetch('/auth/me', 'GET');
    },

    logout() {
        localStorage.removeItem('userAuth');
        localStorage.removeItem('userInfo');
        window.location.replace('index.html');
    },

    isLoggedIn() {
        return !!localStorage.getItem('userAuth');
    },

    getStoredUser() {
        const raw = localStorage.getItem('userInfo');
        if (!raw) {
            return null;
        }

        try {
            return JSON.parse(raw);
        } catch (error) {
            return null;
        }
    },

    getRoleName() {
        const user = this.getStoredUser();

        if (!user || !user.rol) {
            return '';
        }

        if (typeof user.rol === 'string') {
            return user.rol.toUpperCase();
        }

        if (typeof user.rol === 'object' && user.rol.nombreRol) {
            return String(user.rol.nombreRol).toUpperCase();
        }

        return '';
    },

    isAdmin() {
        const role = this.getRoleName();
        return role === 'ADMIN' || role === 'ROLE_ADMIN';
    },

    applyNavigationVisibility() {
        const isLogged = this.isLoggedIn();
        const isAdmin = this.isAdmin();

        document.querySelectorAll('.js-auth-only').forEach((el) => {
            el.classList.toggle('is-hidden', !isLogged);
        });

        document.querySelectorAll('.js-guest-only').forEach((el) => {
            el.classList.toggle('is-hidden', isLogged);
        });

        document.querySelectorAll('.js-admin-only').forEach((el) => {
            el.classList.toggle('is-hidden', !isAdmin);
        });

        document.querySelectorAll('.js-non-admin-only').forEach((el) => {
            el.classList.toggle('is-hidden', isAdmin);
        });
    }
};

document.addEventListener('DOMContentLoaded', () => {
    AuthService.applyNavigationVisibility();
});
