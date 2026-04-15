document.addEventListener('DOMContentLoaded', async () => {
    requireAuth();

    const logoutBtn = document.getElementById('logout-btn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', () => AuthService.logout());
    }

    await cargarPistas();
});

async function cargarPistas() {
    const mensajeEstado = document.getElementById('mensaje-estado');
    const listaPistas = document.getElementById('lista-pistas');

    try {
        const pistas = await apiFetch('/courts', 'GET');

        mensajeEstado.textContent = '';
        listaPistas.innerHTML = '';

        if (!Array.isArray(pistas) || pistas.length === 0) {
            mensajeEstado.textContent = 'No hay pistas disponibles.';
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

            tarjeta.appendChild(nombre);
            tarjeta.appendChild(ubicacion);
            tarjeta.appendChild(precio);
            tarjeta.appendChild(estado);
            tarjeta.appendChild(enlace);

            listaPistas.appendChild(tarjeta);
        });
    } catch (error) {
        mensajeEstado.textContent = error.message;
    }
}