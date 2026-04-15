document.addEventListener('DOMContentLoaded', async () => {
    requireAuth();

    const logoutBtn = document.getElementById('logout-btn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', () => AuthService.logout());
    }

    document.getElementById('btn-cancelar-cambios').addEventListener('click', ocultarFormularioModificar);
    document.getElementById('btn-guardar-cambios').addEventListener('click', guardarModificacion);
    document.getElementById('lista-reservas').addEventListener('click', manejarAccionesReserva);

    await cargarMisReservas();
});

let idReservaModificando = null;

async function cargarMisReservas() {
    const mensaje = document.getElementById('mensaje-pagina');
    const contenedor = document.getElementById('lista-reservas-container');
    const lista = document.getElementById('lista-reservas');

    try {
        const reservas = await apiFetch('/reservations', 'GET');

        lista.innerHTML = '';

        if (!Array.isArray(reservas) || reservas.length === 0) {
            mensaje.textContent = 'No tienes ninguna reserva actualmente.';
            contenedor.hidden = true;
            return;
        }

        reservas.forEach((reserva) => {
            const tarjeta = document.createElement('article');
            tarjeta.className = 'reservation-card';

            const pistaId = reserva.pista?.idPista ?? '';
            const pistaNombre = reserva.pista?.nombre ?? 'No disponible';

            tarjeta.innerHTML = `
                <h3>${pistaNombre}</h3>
                <div class="reservation-meta">
                    <span>Fecha: ${reserva.fechaReserva}</span>
                    <span>Hora: ${reserva.horaInicio} - ${reserva.horaFin}</span>
                    <span>Duración: ${reserva.duracionMinutos} min</span>
                    <span>Estado: ${reserva.estado}</span>
                </div>
                <div class="card-actions">
                    <a class="button-link" href="pista.html?id=${pistaId}">Ver pista</a>
                    <button type="button" class="is-secondary" data-action="modify" data-id="${reserva.idReserva}" data-fecha="${reserva.fechaReserva}" data-hora="${reserva.horaInicio}" data-duracion="${reserva.duracionMinutos}" ${reserva.estado === 'CANCELADA' ? 'disabled' : ''}>Modificar</button>
                    <button type="button" class="is-danger" data-action="cancel" data-id="${reserva.idReserva}" ${reserva.estado === 'CANCELADA' ? 'disabled' : ''}>Cancelar</button>
                </div>
            `;

            lista.appendChild(tarjeta);
        });

        mensaje.textContent = '';
        contenedor.hidden = false;
    } catch (error) {
        mensaje.textContent = error.message;
    }
}

function manejarAccionesReserva(event) {
    const boton = event.target.closest('button');
    if (!boton) {
        return;
    }

    const action = boton.dataset.action;

    if (action === 'modify') {
        prepararModificar(
            boton.dataset.id,
            boton.dataset.fecha,
            boton.dataset.hora,
            boton.dataset.duracion
        );
    }

    if (action === 'cancel') {
        cancelarReserva(boton.dataset.id);
    }
}

async function cancelarReserva(id) {
    if (!confirm('¿Estás seguro de que deseas cancelar esta reserva?')) return;

    try {
        await apiFetch(`/reservations/${id}`, 'DELETE');
        alert('Reserva cancelada con éxito.');
        await cargarMisReservas();
    } catch (error) {
        alert('Error al cancelar: ' + error.message);
    }
}

function prepararModificar(id, fecha, hora, duracion) {
    idReservaModificando = id;
    document.getElementById('mod-id-reserva').textContent = `#${id}`;
    document.getElementById('mod-fecha').value = fecha;
    document.getElementById('mod-hora').value = (hora || '').substring(0, 5);
    document.getElementById('mod-duracion').value = duracion;
    document.getElementById('modificar-reserva-form').hidden = false;
    window.scrollTo({ top: document.body.scrollHeight, behavior: 'smooth' });
}

function ocultarFormularioModificar() {
    idReservaModificando = null;
    document.getElementById('modificar-reserva-form').hidden = true;
}

async function guardarModificacion() {
    if (!idReservaModificando) return;

    const nuevaFecha = document.getElementById('mod-fecha').value;
    const nuevaHora = document.getElementById('mod-hora').value;
    const nuevaDuracion = document.getElementById('mod-duracion').value;

    const payload = {
        fechaReserva: nuevaFecha,
        horaInicio: `${nuevaHora}:00`,
        duracionMinutos: parseInt(nuevaDuracion, 10)
    };

    try {
        await apiFetch(`/reservations/${idReservaModificando}`, 'PATCH', payload);
        alert('Reserva modificada con éxito.');
        ocultarFormularioModificar();
        await cargarMisReservas();
    } catch (error) {
        alert('Error al modificar: ' + error.message);
    }
}

