const AuthService = {
    // Registro: envía nombre, apellidos, email, password, telefono [cite: 184-206]
    async register(userData) {
        return await apiFetch('/auth/register', 'POST', userData);
    },

    async login(email, password) {
        // Generar token Basic Auth: b64(email:password)
        const token = btoa(`${email}:${password}`);
        localStorage.setItem('userAuth', token);
        
        try {
            // Verificamos credenciales llamando a /auth/me
            const user = await this.getMe();
            localStorage.setItem('userInfo', JSON.stringify(user));
            return user;
        } catch (error) {
            this.logout();
            throw error;
        }
    },

    async getMe() {
        return await apiFetch('/auth/me', 'GET'); // 
    },

    logout() {
        localStorage.removeItem('userAuth');
        localStorage.removeItem('userInfo');
        window.location.href = 'login.html';
    },

    isLoggedIn() {
        return !!localStorage.getItem('userAuth');
    }
};