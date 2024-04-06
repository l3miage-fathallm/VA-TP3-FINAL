package fr.uga.l3miage.spring.tp3.services;

import fr.uga.l3miage.spring.tp3.components.CandidateComponent;
import fr.uga.l3miage.spring.tp3.exceptions.technical.CandidateNotFoundException;
import fr.uga.l3miage.spring.tp3.models.CandidateEntity;
import fr.uga.l3miage.spring.tp3.models.CandidateEvaluationGridEntity;
import fr.uga.l3miage.spring.tp3.models.ExamEntity;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.Set;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class CandidateServiceTest {
    @Autowired
    private CandidateService candidateService;

    @MockBean
    private CandidateComponent candidateComponent;

    @Test
    void getCandidateAverage() throws CandidateNotFoundException {
        // Given
        CandidateEntity candidateEntity1 = CandidateEntity.builder()
                .email("test01@test.com")
                .build();

        ExamEntity examEntity1 = ExamEntity.builder()
                .name("math")
                .weight(1)
                .build();


        CandidateEvaluationGridEntity candidateEvaluationGridEntity1 = CandidateEvaluationGridEntity.builder()
                .grade(10)
                .candidateEntity(candidateEntity1)
                .examEntity(examEntity1)
                .build();

        CandidateEvaluationGridEntity candidateEvaluationGridEntity2 = CandidateEvaluationGridEntity.builder()
                .grade(12)
                .candidateEntity(candidateEntity1)
                .examEntity(examEntity1)
                .build();

        CandidateEvaluationGridEntity candidateEvaluationGridEntity3 = CandidateEvaluationGridEntity.builder()
                .grade(8)
                .candidateEntity(candidateEntity1)
                .examEntity(examEntity1)
                .build();

        candidateEntity1.setCandidateEvaluationGridEntities(Set.of(candidateEvaluationGridEntity1, candidateEvaluationGridEntity2, candidateEvaluationGridEntity3));


        when(candidateComponent.getCandidatById(anyLong())).thenReturn(candidateEntity1);

        Double expectedResult = ( 8. + 12 + 10 ) / 3;

        // When
        Double result = candidateService.getCandidateAverage(0L);

        // Then

        assertThat(result).isEqualTo(expectedResult);

        verify(candidateComponent, times(1)).getCandidatById(anyLong());

    }
}