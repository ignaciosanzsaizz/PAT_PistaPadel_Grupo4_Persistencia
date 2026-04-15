package edu.comillas.icai.pista_padel.e2e;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class PistaPadelE2EFullTest {

    @Autowired
    TestRestTemplate client;

    private HttpHeaders jsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private String registerUser(String email) {
        String body = """
                {
                  "nombre": "User",
                  "email": "%s",
                  "password": "aaaaaaA1"
                }
                """.formatted(email);

        client.exchange(
                "/pistaPadel/auth/register",
                HttpMethod.POST,
                new HttpEntity<>(body, jsonHeaders()),
                String.class
        );

        return email;
    }

    private String login(String email) {
        String body = """
                {
                  "email": "%s",
                  "password": "aaaaaaA1"
                }
                """.formatted(email);

        ResponseEntity<String> response = client.exchange(
                "/pistaPadel/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(body, jsonHeaders()),
                String.class
        );

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        return response.getBody();
    }

    @Test
    void createCourtTest() {

        String adminEmail = "admin@email.com";
        registerUser(adminEmail);
        String token = login(adminEmail);

        HttpHeaders headers = jsonHeaders();
        headers.setBearerAuth(token);

        String body = """
                {
                  "nombre": "Pista 1",
                  "ubicacion": "Interior",
                  "precioHora": 20,
                  "activa": true
                }
                """;

        ResponseEntity<String> response = client.exchange(
                "/pistaPadel/courts",
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                String.class
        );

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void createReservationTest() {

        String email = "user@email.com";
        registerUser(email);
        String token = login(email);

        HttpHeaders headers = jsonHeaders();
        headers.setBearerAuth(token);

        String courtBody = """
                {
                  "nombre": "Pista 1",
                  "ubicacion": "Exterior",
                  "precioHora": 25,
                  "activa": true
                }
                """;

        client.exchange("/pistaPadel/courts", HttpMethod.POST,
                new HttpEntity<>(courtBody, headers), String.class);

        String reservationBody = """
                {
                  "courtId": 1,
                  "date": "2026-03-10",
                  "startTime": "18:00",
                  "durationMinutes": 60
                }
                """;

        ResponseEntity<String> response = client.exchange(
                "/pistaPadel/reservations",
                HttpMethod.POST,
                new HttpEntity<>(reservationBody, headers),
                String.class
        );

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void reservationConflictTest() {

        String email = "user@email.com";
        registerUser(email);
        String token = login(email);

        HttpHeaders headers = jsonHeaders();
        headers.setBearerAuth(token);

        String courtBody = """
                {
                  "nombre": "Pista 2",
                  "ubicacion": "Interior",
                  "precioHora": 20,
                  "activa": true
                }
                """;

        client.exchange("/pistaPadel/courts", HttpMethod.POST,
                new HttpEntity<>(courtBody, headers), String.class);

        String reservation = """
                {
                  "courtId": 1,
                  "date": "2026-03-10",
                  "startTime": "18:00",
                  "durationMinutes": 60
                }
                """;

        client.exchange("/pistaPadel/reservations",
                HttpMethod.POST, new HttpEntity<>(reservation, headers), String.class);

        ResponseEntity<String> conflict = client.exchange(
                "/pistaPadel/reservations",
                HttpMethod.POST,
                new HttpEntity<>(reservation, headers),
                String.class
        );

        Assertions.assertEquals(HttpStatus.CONFLICT, conflict.getStatusCode());
    }

    @Test
    void getReservationsTest() {

        String email = "user@email.com";
        registerUser(email);
        String token = login(email);

        HttpHeaders headers = jsonHeaders();
        headers.setBearerAuth(token);

        ResponseEntity<String> response = client.exchange(
                "/pistaPadel/reservations",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void availabilityTest() {

        ResponseEntity<String> response = client.exchange(
                "/pistaPadel/availability?date=2026-03-10",
                HttpMethod.GET,
                null,
                String.class
        );

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}