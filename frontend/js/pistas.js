document.addEventListener('DOMContentLoaded', async () => {
    renderSessionLinks();

    const logoutBtn = document.getElementById('logout-btn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', () => AuthService.logout());
    }

    await cargarPistas();
});

function renderSessionLinks() {
    const container = document.getElementById('session-links');
    if (!container) {
        return;
    }

    if (AuthService.isLoggedIn()) {
        container.innerHTML = `
            <a href="profile.html">Mi perfil</a>
            <a href="mis-reservas.html">Mis reservas</a>
            <a class="js-admin-only" href="admin.html">Admin</a>
            <button id="logout-btn" class="logout-btn" type="button">Cerrar sesión</button>
        `;
    } else {
        container.innerHTML = `
            <a href="login.html">Entrar</a>
            <a class="is-primary" href="register.html">Crear cuenta</a>
        `;
    }

    AuthService.applyNavigationVisibility();
}

async function cargarPistas() {
    const mensajeEstado = document.getElementById('mensaje-estado');
    const listaPistas = document.getElementById('lista-pistas');

    try {
        const pistas = await apiFetch('/courts', 'GET');

        mensajeEstado.textContent = '';
        listaPistas.innerHTML = '';

        if (!Array.isArray(pistas) || pistas.length === 0) {
            listaPistas.innerHTML = '<div class="empty-state">No hay pistas disponibles.</div>';
            return;
        }

        pistas.forEach((pista) => {
            const tarjeta = document.createElement('article');
            tarjeta.className = 'tarjeta-pista';

            const nombre = document.createElement('h2');
            nombre.textContent = pista.nombre;

            const ubicacion = document.createElement('p');
            ubicacion.textContent = `Ubicación: ${pista.ubicacion}`;

            const precio = document.createElement('p');
            precio.textContent = `Precio por hora: ${pista.precioHora} €`;

            const estado = document.createElement('p');
            estado.textContent = `Estado: ${pista.activa ? 'Activa' : 'Inactiva'}`;

            const enlace = document.createElement('a');
            enlace.href = `pista.html?id=${pista.idPista}`;
            enlace.textContent = 'Ver detalle';
            enlace.className = 'button-link is-primary';

            const acciones = document.createElement('div');
            acciones.className = 'card-actions';

            tarjeta.appendChild(nombre);
            tarjeta.appendChild(ubicacion);
            tarjeta.appendChild(precio);
            tarjeta.appendChild(estado);
            acciones.appendChild(enlace);
            tarjeta.appendChild(acciones);

            listaPistas.appendChild(tarjeta);
        });
    } catch (error) {
        mensajeEstado.textContent = error.message;
    }
}