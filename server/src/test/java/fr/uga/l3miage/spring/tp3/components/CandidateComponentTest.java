package fr.uga.l3miage.spring.tp3.components;

import fr.uga.l3miage.spring.tp3.exceptions.technical.CandidateNotFoundException;
import fr.uga.l3miage.spring.tp3.models.CandidateEntity;
import fr.uga.l3miage.spring.tp3.models.CandidateEvaluationGridEntity;
import fr.uga.l3miage.spring.tp3.repositories.CandidateEvaluationGridRepository;
import fr.uga.l3miage.spring.tp3.repositories.CandidateRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class CandidateComponentTest {
    @Autowired
    private CandidateComponent candidateComponent;
    @MockBean
    private CandidateRepository candidateRepository;
    @MockBean
    private CandidateEvaluationGridRepository candidateEvaluationGridRepository;


    @Test
    void testGetCandidateNotFound(){
        //Given
        when(candidateRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when - then
        assertThrows(CandidateNotFoundException.class, () -> candidateComponent.getCandidatById(1L));
    }

    @Test
    void testGetCandidateFound(){
        //given
        CandidateEntity candidateEntity = CandidateEntity
                .builder()
                .id(5L)
                .firstname("candidate1")
                .email("candidate1@gmail.com")
                .build();
        when(candidateRepository.findById(anyLong())).thenReturn(Optional.of(candidateEntity));

        //when - then
        assertDoesNotThrow(()->candidateComponent.getCandidatById(5L));
    }
    @Test
    void testGetAllEliminatedCandidate(){
        // Given
        CandidateEntity candidate1 = CandidateEntity.builder().firstname("John").build();
        CandidateEntity candidate2 = CandidateEntity.builder().firstname("Alice").build();
        CandidateEntity candidate3 = CandidateEntity.builder().firstname("Bob").build();

        CandidateEvaluationGridEntity candidateEvaluationGrid = CandidateEvaluationGridEntity.builder().grade(4).candidateEntity(candidate1).build();
        CandidateEvaluationGridEntity candidateEvaluationGrid1 = CandidateEvaluationGridEntity.builder().grade(3).candidateEntity(candidate2).build();
        CandidateEvaluationGridEntity candidateEvaluationGrid2 = CandidateEvaluationGridEntity.builder().grade(10).candidateEntity(candidate3).build();

        candidateEvaluationGridRepository.save(candidateEvaluationGrid);
        candidateEvaluationGridRepository.save(candidateEvaluationGrid1);
        candidateEvaluationGridRepository.save(candidateEvaluationGrid2);

        when(candidateEvaluationGridRepository.findAllByGradeIsLessThanEqual(5))
                .thenReturn(Stream.of(candidateEvaluationGrid,candidateEvaluationGrid1)
                        .collect(Collectors.toSet()));

        // When
        Set<CandidateEntity> eliminatedCandidates = candidateComponent.getAllEliminatedCandidate();

        // Then
        assertEquals(2, eliminatedCandidates.size());
        assertTrue(eliminatedCandidates.contains(candidate1));
        assertTrue(eliminatedCandidates.contains(candidate2));
        assertFalse(eliminatedCandidates.contains(candidate3));

    }
}
