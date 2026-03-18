package edu.comillas.icai.gitt.pat.spring.jpa3.E2E.controller;

// Comprobamos que el número de elementos devueltos en el JSON
// coincide con el tamaño de la lista simulada por el servicio

import edu.comillas.icai.gitt.pat.spring.jpa3.controller.PistaController;
import edu.comillas.icai.gitt.pat.spring.jpa3.entity.Pista;
import edu.comillas.icai.gitt.pat.spring.jpa3.service.PistaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PistaController.class)
public class PistaControllerTest {
    @Autowired
    MockMvc mockMvc;
    
    @MockitoBean
    PistaService pistaService;

    @Test
    @WithMockUser(username = "ana@test.com", roles = "USER")
    void listarPistas_devuelve200_y_lista_json() throws Exception {

        // Creamos dos pistas simuladas que devolverá el servicio
        Pista pista1 = new Pista();
        pista1.idPista = 1L;
        pista1.nombre = "Central";
        pista1.ubicacion = "Madrid";
        pista1.precioHora = 20.0;
        pista1.activa = true;
        pista1.fechaAlta = new Date();

        Pista pista2 = new Pista();
        pista2.idPista = 2L;
        pista2.nombre = "Exterior";
        pista2.ubicacion = "Madrid";
        pista2.precioHora = 18.0;
        pista2.activa = true;
        pista2.fechaAlta = new Date();

        // Guardamos las pistas en una lista
        List<Pista> pistas = List.of(pista1, pista2);

        // Simulamos la respuesta del servicio
        when(pistaService.listarPistas()).thenReturn(pistas);

        // Simulamos una petición GET al endpoint de listado de pistas
        mockMvc.perform(get("/pistaPadel/courts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(pistas.size()))
                .andExpect(jsonPath("$[0].nombre").value("Central"))
                .andExpect(jsonPath("$[1].nombre").value("Exterior"));
    }
}

