package fr.uga.l3miage.spring.tp3.controllers;

import fr.uga.l3miage.spring.tp3.components.CandidateComponent;
import fr.uga.l3miage.spring.tp3.exceptions.CandidatNotFoundResponse;
import fr.uga.l3miage.spring.tp3.models.CandidateEntity;
import fr.uga.l3miage.spring.tp3.repositories.CandidateRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@AutoConfigureTestDatabase
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect")
class CandidateControllerTest {
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private CandidateRepository candidateRepository;
    @SpyBean
    private CandidateComponent candidateComponent;

    @AfterEach
    public void clear(){
        candidateRepository.deleteAll();
    }

    @Test
    void getNotFoundCandidateAverage(){
        //given
        final HttpHeaders headers = new HttpHeaders();

        final Map<String, Object> urlParams = new HashMap<>();
        urlParams.put("candidateId", "Le candidat qui n'existe pas");

        CandidatNotFoundResponse notFoundErrorResponseExpected = CandidatNotFoundResponse
                .builder()
                .uri("/api/candidates/Le%candidat%20qui%20n%27existe%20pas")
                .errorMessage("Le candidat [Le candidat qui n'existe pas] n'a pas été trouvé")
                .build();

        //when
        ResponseEntity<CandidatNotFoundResponse> response = testRestTemplate.exchange("/api/candidates/{candidateId}", HttpMethod.GET, new HttpEntity<>(null, headers), CandidatNotFoundResponse.class, urlParams);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).usingRecursiveComparison()
                .isEqualTo(notFoundErrorResponseExpected);

    }

}