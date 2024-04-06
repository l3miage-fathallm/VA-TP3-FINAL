package fr.uga.l3miage.spring.tp3.controllers;

import fr.uga.l3miage.spring.tp3.components.ExamComponent;
import fr.uga.l3miage.spring.tp3.components.SessionComponent;
import fr.uga.l3miage.spring.tp3.controller.SessionController;
import fr.uga.l3miage.spring.tp3.exceptions.technical.ExamNotFoundException;
import fr.uga.l3miage.spring.tp3.models.EcosSessionEntity;
import fr.uga.l3miage.spring.tp3.repositories.EcosSessionRepository;
import fr.uga.l3miage.spring.tp3.request.SessionCreationRequest;
import fr.uga.l3miage.spring.tp3.request.SessionProgrammationCreationRequest;
import fr.uga.l3miage.spring.tp3.request.SessionProgrammationStepCreationRequest;
import fr.uga.l3miage.spring.tp3.responses.SessionResponse;

import fr.uga.l3miage.spring.tp3.responses.enums.SessionStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.Set;

@AutoConfigureTestDatabase
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect")
public class SessionControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private EcosSessionRepository ecosSessionRepository;

    @SpyBean
    private SessionComponent sessionComponent;

    @SpyBean
    private ExamComponent examComponent;

    @AfterEach
    public void clear() {
        ecosSessionRepository.deleteAll();
    }

    @Test
    void canCreateSessionWithoutExams() throws ExamNotFoundException {
        //Given
        final HttpHeaders headers = new HttpHeaders();

        final SessionProgrammationStepCreationRequest sessionProgrammationStepCreationRequest = SessionProgrammationStepCreationRequest.builder()
                .code("code01")
                .description("description programmationStepCreation 01")
                .dateTime(LocalDateTime.of(2024, 3, 2, 8, 30))
                .build();

        final SessionProgrammationCreationRequest sessionProgrammationCreationRequest = SessionProgrammationCreationRequest.builder()
                .label("label test01")
                .steps(Set.of(sessionProgrammationStepCreationRequest))
                .build();

        // j'ai tout d'abord besoin d'une request... qui contient les informations sur la request de creation de la request...
        final SessionCreationRequest sessionCreationRequest = SessionCreationRequest.builder()
                .name("test01")
                .startDate(LocalDateTime.of(2024, 3, 10, 8, 30))
                .endDate(LocalDateTime.of(2024, 3, 10, 18, 30))
                .examsId(Set.of())
                .ecosSessionProgrammation(sessionProgrammationCreationRequest)
                .build();

        //When
        ResponseEntity<SessionResponse> response = testRestTemplate.exchange("/api/sessions/create", HttpMethod.POST, new HttpEntity<SessionCreationRequest>(sessionCreationRequest, headers), SessionResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(ecosSessionRepository.count()).isEqualTo(1);

        verify(sessionComponent, times(1)).createSession(any(EcosSessionEntity.class));
        verify(examComponent, times(1)).getAllById(anySet());

    }

}
