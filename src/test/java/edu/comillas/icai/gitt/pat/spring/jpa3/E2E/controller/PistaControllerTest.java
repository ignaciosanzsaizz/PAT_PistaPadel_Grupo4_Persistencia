package edu.comillas.icai.gitt.pat.spring.jpa3.E2E.controller;
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

        List<Pista> pistas = List.of(pista1, pista2);

        when(pistaService.listarPistas()).thenReturn(pistas);

        mockMvc.perform(get("/pistaPadel/courts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(pistas.size()))
                .andExpect(jsonPath("$[0].nombre").value("Central"))
                .andExpect(jsonPath("$[1].nombre").value("Exterior"));
    }
}

