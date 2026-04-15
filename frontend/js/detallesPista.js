document.addEventListener('DOMContentLoaded', async () => {
    requireAuth();

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

    await cargarDetallePista(idPista);
    await cargarDisponibilidad(idPista);
});

function obtenerIdPistaDesdeUrl() {
    const params = new URLSearchParams(window.location.search);
    return params.get('id');
}

function establecerFechaPorDefecto() {
    const inputFecha = document.getElementById('fecha-disponibilidad');
    const hoy = new Date();
    const fechaLocal = new Date(hoy.getTime() - hoy.getTimezoneOffset() * 60000)
        .toISOString()
        .split('T')[0];

    inputFecha.value = fechaLocal;
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
        seccionDisponibilidad.hidden = false;
    } catch (error) {
        mensajePagina.textContent = error.message;
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