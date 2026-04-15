const API_BASE_URL = 'https://tu-backend.render.com/pistaPadel'; // URL de vuestro backend

async function apiFetch(endpoint, method = 'GET', body = null) {
    const headers = { 'Content-Type': 'application/json' };

    // Recuperar credenciales Basic Auth de localStorage
    const auth = localStorage.getItem('userAuth');
    if (auth) {
        headers['Authorization'] = `Basic ${auth}`;
    }

    const config = { method, headers };
    if (body) config.body = JSON.stringify(body);

    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, config);

        if (!response.ok) {
            // Manejo sistemático de errores según la tarea 
            let userMessage = 'Ha ocurrido un error inesperado.';
            
            switch (response.status) {
                case 400: userMessage = 'Datos enviados incorrectos. Revisa el formulario.'; break;
                case 401: userMessage = 'No autorizado. Email o contraseña incorrectos.'; break;
                case 409: userMessage = 'Conflicto: El recurso (email o reserva) ya existe.'; break;
                case 500: userMessage = 'Error interno del servidor. Inténtalo más tarde.'; break;
            }
            throw new Error(userMessage);
        }

        if (response.status === 204) return null;
        return await response.json();
    } catch (error) {
        throw error; // Propaga el error con el mensaje amigable
    }
}