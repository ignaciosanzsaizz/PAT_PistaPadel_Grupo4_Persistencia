document.addEventListener('DOMContentLoaded', async () => {
    requireAuth();

    document.getElementById('logout-btn').addEventListener('click', () => AuthService.logout());

    await cargarMisReservas();

    // Eventos para el formulario de modificación
    document.getElementById('btn-cancelar-cambios').addEventListener('click', ocultarFormularioModificar);
    document.getElementById('btn-guardar-cambios').addEventListener('click', guardarModificacion);
});

let idReservaModificando = null;

async function cargarMisReservas() {
    const mensaje = document.getElementById('mensaje-pagina');
    const contenedor = document.getElementById('lista-reservas-container');
    const lista = document.getElementById('lista-reservas');

    try {
        const reservas = await apiFetch('/reservations', 'GET');

        lista.innerHTML = '';
        if (reservas.length === 0) {
            mensaje.textContent = 'No tienes ninguna reserva actualmente.';
            contenedor.hidden = true;
            return;
        }

        reservas.forEach(reserva => {
            const li = document.createElement('li');
            li.style.borderBottom = "1px solid #ccc";
            li.style.padding = "10px 0";
            li.innerHTML = `
                <strong>Pista:</strong> ${reserva.pista.nombre} <br>
                <strong>Fecha:</strong> ${reserva.fechaReserva} <br>
                <strong>Hora:</strong> ${reserva.horaInicio} - ${reserva.horaFin} (${reserva.duracionMinutos} min) <br>
                <strong>Estado:</strong> ${reserva.estado}
                <div style="margin-top: 10px;">
                    <button onclick="prepararModificar(${reserva.idReserva}, '${reserva.fechaReserva}', '${reserva.horaInicio}', ${reserva.duracionMinutos})" ${reserva.estado === 'CANCELADA' ? 'disabled' : ''}>Modificar</button>
                    <button onclick="cancelarReserva(${reserva.idReserva})" style="background-color: #dc3545; color: white;" ${reserva.estado === 'CANCELADA' ? 'disabled' : ''}>Cancelar</button>
                </div>
            `;
            lista.appendChild(li);
        });

        mensaje.textContent = '';
        contenedor.hidden = false;
    } catch (error) {
        mensaje.textContent = error.message;
    }
}

async function cancelarReserva(id) {
    if (!confirm('¿Estás seguro de que deseas cancelar esta reserva?')) return;

    try {
        await apiFetch(`/reservations/${id}`, 'DELETE');
        alert('Reserva cancelada con éxito.');
        cargarMisReservas(); // Recargar la lista
    } catch (error) {
        alert('Error al cancelar: ' + error.message);
    }
}

function prepararModificar(id, fecha, hora, duracion) {
    idReservaModificando = id;
    document.getElementById('mod-id-reserva').textContent = `#${id}`;
    document.getElementById('mod-fecha').value = fecha;

    // Convertir '10:00:00' a '10:00' para el input type="time"
    document.getElementById('mod-hora').value = hora.substring(0, 5);
    document.getElementById('mod-duracion').value = duracion;

    document.getElementById('modificar-reserva-form').hidden = false;
    window.scrollTo(0, document.body.scrollHeight);
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
        horaInicio: nuevaHora + ":00",
        duracionMinutos: parseInt(nuevaDuracion)
    };

    try {
        await apiFetch(`/reservations/${idReservaModificando}`, 'PATCH', payload);
        alert('Reserva modificada con éxito.');
        ocultarFormularioModificar();
        cargarMisReservas();
    } catch (error) {
        alert('Error al modificar: ' + error.message);
    }
}