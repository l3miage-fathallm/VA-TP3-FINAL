package fr.uga.l3miage.spring.tp3.controllers;

import fr.uga.l3miage.spring.tp3.exceptions.CandidatNotFoundResponse;
import fr.uga.l3miage.spring.tp3.repositories.CandidateRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
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

    @AfterEach
    public void clear(){
        candidateRepository.deleteAll();
    }

    @Test
    void getNotFoundCandidateAverage(){
        //given
        final HttpHeaders headers = new HttpHeaders();

        final Map<String, Long> urlParams = new HashMap<>();
        urlParams.put("candidateId", 100L);

        CandidatNotFoundResponse notFoundErrorResponseExpected = CandidatNotFoundResponse
                .builder()
                .candidateId(100L)
                .uri("/api/candidates/100/average")
                .errorMessage("Le candidat [100] n'a pas été trouvé")
                .build();

        //when
        ResponseEntity<CandidatNotFoundResponse> response = testRestTemplate.exchange("/api/candidates/{candidateId}/average", HttpMethod.GET, new HttpEntity<>(null, headers), CandidatNotFoundResponse.class, urlParams);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).usingRecursiveComparison()
                .isEqualTo(notFoundErrorResponseExpected);

    }
}