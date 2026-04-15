package edu.comillas.icai.gitt.pat.spring.jpa3.E2E.controller;

import edu.comillas.icai.gitt.pat.spring.jpa3.repos.PistaRepository;
import edu.comillas.icai.gitt.pat.spring.jpa3.repos.UsuarioRepository;
import edu.comillas.icai.gitt.pat.spring.jpa3.service.DisponibilidadService;
import edu.comillas.icai.gitt.pat.spring.jpa3.service.ReservaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservaControllerTest.class)
public class ReservaControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockitoBean
    ReservaService reservaService;
    @MockitoBean
    DisponibilidadService disponibilidadService;
    @MockitoBean
    UsuarioRepository usuarioRepository;
    @MockitoBean
    PistaRepository pistaRepository;

    @Test
    @WithMockUser(username = "ana@test.com", roles = "USER")
    void disponibilidadPista_devuelve200() throws Exception {
        when(disponibilidadService.calcularDisponibilidad(1L, LocalDate.of(2026, 3, 20)))
                .thenReturn(List.of("10:00", "11:30"));

        mockMvc.perform(get("/pistaPadel/courts/1/availability")
                        .param("date", "2026-03-20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("10:00"))
                .andExpect(jsonPath("$[1]").value("11:30"));
    }



}

