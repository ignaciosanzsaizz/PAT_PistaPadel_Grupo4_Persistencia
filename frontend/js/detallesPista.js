document.addEventListener('DOMContentLoaded', async () => {
    renderSessionLinks();

    const logoutBtn = document.getElementById('logout-btn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', () => AuthService.logout());
    }

    const idPista = obtenerIdPistaDesdeUrl();

    if (!idPista) {
        document.getElementById('mensaje-pagina').textContent = 'ID de pista no válido.';
        return;
    }

    establecerFechaPorDefecto();

    document
        .getElementById('btn-consultar-disponibilidad')
        .addEventListener('click', () => cargarDisponibilidad(idPista));

    document
        .getElementById('btn-reservar-pista')
        .addEventListener('click', () => reservarPista(idPista));

    await cargarDetallePista(idPista);
    await cargarDisponibilidad(idPista);
    configurarBloqueReserva();
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

function obtenerIdPistaDesdeUrl() {
    const params = new URLSearchParams(window.location.search);
    return params.get('id');
}

function establecerFechaPorDefecto() {
    const inputFecha = document.getElementById('fecha-disponibilidad');
    const inputReservaFecha = document.getElementById('reserva-fecha');
    const inputReservaHora = document.getElementById('reserva-hora');
    const hoy = new Date();
    const fechaLocal = new Date(hoy.getTime() - hoy.getTimezoneOffset() * 60000)
        .toISOString()
        .split('T')[0];

    inputFecha.value = fechaLocal;
    inputReservaFecha.value = fechaLocal;
    inputReservaHora.value = '10:00';
}

function configurarBloqueReserva() {
    const seccionReserva = document.getElementById('seccion-reserva');

    if (AuthService.isLoggedIn()) {
        seccionReserva.hidden = false;
        return;
    }

    seccionReserva.hidden = false;
    seccionReserva.innerHTML = `
        <h3>Reservar esta pista</h3>
        <p class="section-lead">Inicia sesión para reservar una pista y gestionar tus horarios.</p>
        <div class="card-actions">
            <a class="button-link is-primary" href="login.html">Entrar</a>
            <a class="button-link" href="register.html">Crear cuenta</a>
        </div>
    `;
}

async function cargarDetallePista(idPista) {
    const mensajePagina = document.getElementById('mensaje-pagina');
    const detallePista = document.getElementById('detalle-pista');
    const seccionDisponibilidad = document.getElementById('seccion-disponibilidad');

    try {
        const pista = await apiFetch(`/courts/${idPista}`, 'GET');

        document.getElementById('nombre-pista').textContent = pista.nombre;
        document.getElementById('ubicacion-pista').textContent = `Ubicación: ${pista.ubicacion}`;
        document.getElementById('precio-pista').textContent = `Precio por hora: ${pista.precioHora} €`;
        document.getElementById('estado-pista').textContent = `Estado: ${pista.activa ? 'Activa' : 'Inactiva'}`;

        const fechaAlta = pista.fechaAlta
            ? new Date(pista.fechaAlta).toLocaleDateString('es-ES')
            : 'No disponible';

        document.getElementById('fecha-alta-pista').textContent = `Fecha de alta: ${fechaAlta}`;

        mensajePagina.textContent = '';
        detallePista.hidden = false;
        document.getElementById('seccion-reserva').hidden = false;
        seccionDisponibilidad.hidden = false;
    } catch (error) {
        mensajePagina.textContent = error.message;
    }
}

async function reservarPista(idPista) {
    if (!AuthService.isLoggedIn()) {
        window.location.href = 'login.html';
        return;
    }

    const mensajeReserva = document.getElementById('mensaje-reserva');
    const fecha = document.getElementById('reserva-fecha').value;
    const hora = document.getElementById('reserva-hora').value;
    const duracion = document.getElementById('reserva-duracion').value;

    if (!fecha || !hora || !duracion) {
        mensajeReserva.textContent = 'Completa fecha, hora y duración.';
        return;
    }

    mensajeReserva.textContent = 'Creando reserva...';

    try {
        await apiFetch('/reservations', 'POST', {
            pista: { idPista: Number(idPista) },
            fechaReserva: fecha,
            horaInicio: `${hora}:00`,
            duracionMinutos: Number(duracion)
        });

        mensajeReserva.textContent = 'Reserva creada correctamente. Puedes verla en "Mis reservas".';
    } catch (error) {
        mensajeReserva.textContent = error.message;
    }
}

async function cargarDisponibilidad(idPista) {
    const fecha = document.getElementById('fecha-disponibilidad').value;
    const mensajeDisponibilidad = document.getElementById('mensaje-disponibilidad');
    const listaDisponibilidad = document.getElementById('lista-disponibilidad');

    if (!fecha) {
        mensajeDisponibilidad.textContent = 'Selecciona una fecha.';
        return;
    }

    mensajeDisponibilidad.textContent = 'Consultando disponibilidad...';
    listaDisponibilidad.innerHTML = '';

    try {
        const disponibilidad = await apiFetch(
            `/courts/${idPista}/availability?date=${fecha}`,
            'GET'
        );

        mensajeDisponibilidad.textContent = '';

        if (!Array.isArray(disponibilidad) || disponibilidad.length === 0) {
            mensajeDisponibilidad.textContent = 'No hay huecos libres para esa fecha.';
            return;
        }

        disponibilidad.forEach((tramo) => {
            const li = document.createElement('li');
            li.textContent = tramo;
            listaDisponibilidad.appendChild(li);
        });
    } catch (error) {
        mensajeDisponibilidad.textContent = error.message;
    }
}