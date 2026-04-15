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
            li.style.marginBottom = "5px";
            // Extraer la hora limpia del tramo devuelto, asumiendo formato "10:00" o similar
            const horaLimpia = tramo.substring(0, 5);

            li.innerHTML = `
                <span style="display:inline-block; width: 100px;">${tramo}</span>
                <button onclick="realizarReserva(${idPista}, '${fecha}', '${horaLimpia}')">Reservar</button>
            `;
            listaDisponibilidad.appendChild(li);
        });
    } catch (error) {
        mensajeDisponibilidad.textContent = error.message;
    }
}

// Nueva función que hace el POST para crear la reserva
window.realizarReserva = async function(idPista, fecha, hora) {
    const duracion = prompt("¿Cuántos minutos quieres reservar? (ej. 60, 90, 120)", "60");
    if (!duracion) return; // Si el usuario cancela el prompt

    const payload = {
        pista: { idPista: parseInt(idPista) },
        fechaReserva: fecha,
        horaInicio: hora + ":00", // Añadimos segundos porque Java LocalTime lo espera
        duracionMinutos: parseInt(duracion)
    };

    try {
        await apiFetch('/reservations', 'POST', payload);
        alert('¡Reserva creada con éxito!');
        window.location.href = 'mis-reservas.html'; // Redirigir a "Mis Reservas"
    } catch (error) {
        alert('Error al reservar: ' + error.message);
    }
}