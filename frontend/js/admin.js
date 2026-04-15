document.addEventListener('DOMContentLoaded', async () => {
    requireAdmin();

    const logoutBtn = document.getElementById('logout-btn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', () => AuthService.logout());
    }

    conectarEventos();

    await cargarDatosIniciales();
});

let pistasCache = [];
let usuariosCache = [];
let usuarioActualCargado = null;

async function cargarDatosIniciales() {
    const mensajeEstado = document.getElementById('mensaje-estado');

    try {
        await cargarPistasAdmin();
        await cargarUsuariosAdmin();
        await cargarReservasAdmin();
        mensajeEstado.textContent = '';
    } catch (error) {
        mensajeEstado.textContent = error.message;
    }
}

function conectarEventos() {
    document.getElementById('form-pista').addEventListener('submit', guardarPista);
    document.getElementById('btn-cancelar-pista').addEventListener('click', limpiarFormularioPista);
    document.getElementById('lista-pistas-admin').addEventListener('click', manejarAccionesPistas);

    document.getElementById('btn-cargar-usuario').addEventListener('click', cargarUsuarioSeleccionado);
    document.getElementById('form-usuario').addEventListener('submit', guardarUsuario);
    document.getElementById('btn-cancelar-usuario').addEventListener('click', limpiarFormularioUsuario);

    document.getElementById('btn-filtrar-reservas').addEventListener('click', aplicarFiltrosReservas);
    document.getElementById('btn-limpiar-filtros').addEventListener('click', limpiarFiltrosReservas);
}

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

async function cargarPistasAdmin() {
    const listaPistas = document.getElementById('lista-pistas-admin');
    const mensajePistas = document.getElementById('mensaje-pistas');
    const filtroPista = document.getElementById('filtro-pista');

    try {
        const pistas = await apiFetch('/courts', 'GET');
        pistasCache = pistas;

        listaPistas.innerHTML = '';
        filtroPista.innerHTML = '<option value="">Todas</option>';
        mensajePistas.textContent = '';

        if (!Array.isArray(pistas) || pistas.length === 0) {
            listaPistas.innerHTML = '<p>No hay pistas registradas.</p>';
            return;
        }

        pistas.forEach((pista) => {
            const tarjeta = document.createElement('article');
            tarjeta.className = 'tarjeta-pista';

            tarjeta.innerHTML = `
                <h3>${pista.nombre}</h3>
                <p>Ubicación: ${pista.ubicacion}</p>
                <p>Precio por hora: ${pista.precioHora} €</p>
                <p>Estado: ${pista.activa ? 'Activa' : 'Inactiva'}</p>
                <p>Fecha alta: ${pista.fechaAlta ? new Date(pista.fechaAlta).toLocaleDateString('es-ES') : 'No disponible'}</p>

                <button type="button" data-accion="editar" data-id="${pista.idPista}">
                    Editar
                </button>
                <button type="button" data-accion="eliminar" data-id="${pista.idPista}">
                    Eliminar
                </button>
            `;

            listaPistas.appendChild(tarjeta);

            const option = document.createElement('option');
            option.value = pista.idPista;
            option.textContent = pista.nombre;
            filtroPista.appendChild(option);
        });
    } catch (error) {
        mensajePistas.textContent = error.message;
    }
}

function manejarAccionesPistas(event) {
    const boton = event.target.closest('button');

    if (!boton) {
        return;
    }

    const accion = boton.dataset.accion;
    const idPista = boton.dataset.id;

    if (accion === 'editar') {
        cargarPistaEnFormulario(idPista);
    }

    if (accion === 'eliminar') {
        eliminarPista(idPista);
    }
}

function cargarPistaEnFormulario(idPista) {
    const pista = pistasCache.find((p) => String(p.idPista) === String(idPista));

    if (!pista) {
        return;
    }

    document.getElementById('id-pista').value = pista.idPista;
    document.getElementById('nombre-pista').value = pista.nombre;
    document.getElementById('ubicacion-pista').value = pista.ubicacion;
    document.getElementById('precio-pista').value = pista.precioHora;
    document.getElementById('activa-pista').checked = pista.activa;
}

async function guardarPista(event) {
    event.preventDefault();

    const idPista = document.getElementById('id-pista').value;
    const mensajePistas = document.getElementById('mensaje-pistas');

    const datosPista = {
        nombre: document.getElementById('nombre-pista').value,
        ubicacion: document.getElementById('ubicacion-pista').value,
        precioHora: Number(document.getElementById('precio-pista').value),
        activa: document.getElementById('activa-pista').checked,
        fechaAlta: new Date().toISOString()
    };

    try {
        if (idPista) {
            await apiFetch(`/courts/${idPista}`, 'PATCH', datosPista);
            mensajePistas.textContent = 'Pista actualizada correctamente.';
        } else {
            await apiFetch('/courts', 'POST', datosPista);
            mensajePistas.textContent = 'Pista creada correctamente.';
        }

        limpiarFormularioPista();
        await cargarPistasAdmin();
        await cargarReservasAdmin();
    } catch (error) {
        mensajePistas.textContent = error.message;
    }
}

async function eliminarPista(idPista) {
    const mensajePistas = document.getElementById('mensaje-pistas');
    const confirmar = confirm('¿Seguro que quieres eliminar esta pista?');

    if (!confirmar) {
        return;
    }

    try {
        await apiFetch(`/courts/${idPista}`, 'DELETE');
        mensajePistas.textContent = 'Pista eliminada correctamente.';
        await cargarPistasAdmin();
        await cargarReservasAdmin();
    } catch (error) {
        mensajePistas.textContent = error.message;
    }
}

function limpiarFormularioPista() {
    document.getElementById('form-pista').reset();
    document.getElementById('id-pista').value = '';
}

async function cargarUsuariosAdmin() {
    const listaUsuarios = document.getElementById('lista-usuarios-admin');
    const selectUsuario = document.getElementById('select-usuario');
    const filtroUsuario = document.getElementById('filtro-usuario');
    const mensajeUsuarios = document.getElementById('mensaje-usuarios');

    try {
        const usuarios = await apiFetch('/users', 'GET');
        usuariosCache = usuarios;

        listaUsuarios.innerHTML = '';
        selectUsuario.innerHTML = '<option value="">-- Selecciona un usuario --</option>';
        filtroUsuario.innerHTML = '<option value="">Todos</option>';
        mensajeUsuarios.textContent = '';

        if (!Array.isArray(usuarios) || usuarios.length === 0) {
            listaUsuarios.innerHTML = '<p>No hay usuarios registrados.</p>';
            return;
        }

        usuarios.forEach((user) => {
            const rolTexto = obtenerNombreRol(user);

            const tarjeta = document.createElement('article');
            tarjeta.className = 'tarjeta-pista';

            tarjeta.innerHTML = `
                <h3>${user.nombre} ${user.apellidos}</h3>
                <p>Email: ${user.email}</p>
                <p>Teléfono: ${user.telefono}</p>
                <p>Rol: ${rolTexto}</p>
                <p>Activo: ${user.activo ? 'Sí' : 'No'}</p>
            `;

            listaUsuarios.appendChild(tarjeta);

            const optionEditar = document.createElement('option');
            optionEditar.value = user.id;
            optionEditar.textContent = `${user.nombre} ${user.apellidos} (${user.email})`;
            selectUsuario.appendChild(optionEditar);

            const optionFiltro = document.createElement('option');
            optionFiltro.value = user.id;
            optionFiltro.textContent = `${user.nombre} ${user.apellidos}`;
            filtroUsuario.appendChild(optionFiltro);
        });
    } catch (error) {
        mensajeUsuarios.textContent = error.message;
    }
}

async function cargarUsuarioSeleccionado() {
    const idUsuario = document.getElementById('select-usuario').value;
    const mensajeUsuarios = document.getElementById('mensaje-usuarios');

    if (!idUsuario) {
        mensajeUsuarios.textContent = 'Selecciona primero un usuario.';
        return;
    }

    try {
        const user = await apiFetch(`/users/${idUsuario}`, 'GET');
        usuarioActualCargado = user;

        document.getElementById('id-usuario').value = user.id;
        document.getElementById('nombre-usuario').value = user.nombre || '';
        document.getElementById('apellidos-usuario').value = user.apellidos || '';
        document.getElementById('email-usuario').value = user.email || '';
        document.getElementById('telefono-usuario').value = user.telefono || '';
        document.getElementById('activo-usuario').checked = !!user.activo;
        document.getElementById('rol-usuario').value = obtenerNombreRol(user);

        mensajeUsuarios.textContent = 'Usuario cargado correctamente.';
    } catch (error) {
        mensajeUsuarios.textContent = error.message;
    }
}

async function guardarUsuario(event) {
    event.preventDefault();

    const idUsuario = document.getElementById('id-usuario').value;
    const mensajeUsuarios = document.getElementById('mensaje-usuarios');

    if (!idUsuario) {
        mensajeUsuarios.textContent = 'Primero debes cargar un usuario.';
        return;
    }

    const cambios = {
        nombre: document.getElementById('nombre-usuario').value,
        apellidos: document.getElementById('apellidos-usuario').value,
        email: document.getElementById('email-usuario').value,
        telefono: document.getElementById('telefono-usuario').value,
        activo: document.getElementById('activo-usuario').checked
    };

    try {
        await apiFetch(`/users/${idUsuario}`, 'PATCH', cambios);
        mensajeUsuarios.textContent = 'Usuario actualizado correctamente.';
        await cargarUsuariosAdmin();
    } catch (error) {
        mensajeUsuarios.textContent = error.message;
    }
}

function limpiarFormularioUsuario() {
    document.getElementById('form-usuario').reset();
    document.getElementById('id-usuario').value = '';
    usuarioActualCargado = null;
}

async function cargarReservasAdmin(date = '', courtId = '', userId = '') {
    const listaReservas = document.getElementById('lista-reservas-admin');
    const mensajeReservas = document.getElementById('mensaje-reservas');

    try {
        let endpoint = '/admin/reservations';
        const params = new URLSearchParams();

        if (date) {
            params.append('date', date);
        }

        if (courtId) {
            params.append('courtId', courtId);
        }

        if (userId) {
            params.append('userId', userId);
        }

        if (params.toString()) {
            endpoint += `?${params.toString()}`;
        }

        const reservas = await apiFetch(endpoint, 'GET');

        listaReservas.innerHTML = '';
        mensajeReservas.textContent = '';

        if (!Array.isArray(reservas) || reservas.length === 0) {
            listaReservas.innerHTML = '<p>No hay reservas con esos filtros.</p>';
            return;
        }

        reservas.forEach((reserva) => {
            const tarjeta = document.createElement('article');
            tarjeta.className = 'tarjeta-pista';

            const pistaNombre = reserva.pista ? reserva.pista.nombre : 'No disponible';
            const usuarioNombre = reserva.usuario
                ? `${reserva.usuario.nombre} ${reserva.usuario.apellidos}`
                : 'No disponible';

            tarjeta.innerHTML = `
                <h3>Reserva ${reserva.id || ''}</h3>
                <p>Fecha: ${reserva.fechaReserva || 'No disponible'}</p>
                <p>Hora inicio: ${reserva.horaInicio || 'No disponible'}</p>
                <p>Duración: ${reserva.duracionMinutos || 'No disponible'} min</p>
                <p>Pista: ${pistaNombre}</p>
                <p>Usuario: ${usuarioNombre}</p>
            `;

            listaReservas.appendChild(tarjeta);
        });
    } catch (error) {
        mensajeReservas.textContent = error.message;
    }
}

async function aplicarFiltrosReservas() {
    const fecha = document.getElementById('filtro-fecha').value;
    const courtId = document.getElementById('filtro-pista').value;
    const userId = document.getElementById('filtro-usuario').value;

    await cargarReservasAdmin(fecha, courtId, userId);
}

async function limpiarFiltrosReservas() {
    document.getElementById('filtro-fecha').value = '';
    document.getElementById('filtro-pista').value = '';
    document.getElementById('filtro-usuario').value = '';

    await cargarReservasAdmin();
}