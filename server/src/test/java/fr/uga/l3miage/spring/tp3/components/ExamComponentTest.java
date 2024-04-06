
package fr.uga.l3miage.spring.tp3.components;

import fr.uga.l3miage.spring.tp3.exceptions.technical.CandidateNotFoundException;
import fr.uga.l3miage.spring.tp3.exceptions.technical.ExamNotFoundException;
import fr.uga.l3miage.spring.tp3.models.CandidateEntity;
import fr.uga.l3miage.spring.tp3.models.ExamEntity;
import fr.uga.l3miage.spring.tp3.models.SkillEntity;
import fr.uga.l3miage.spring.tp3.repositories.ExamRepository;
import fr.uga.l3miage.spring.tp3.repositories.SkillRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)

public class ExamComponentTest {
    @Autowired
    private ExamComponent examComponent;
    @MockBean
    private ExamRepository examRepository;
    @MockBean
    private SkillRepository skillRepository;

    @Test
    void testGetAllNotFound(){
        //Given
        when(examRepository.findAllById(anySet())).thenReturn(List.of());

        //when - then
        assertThrows(ExamNotFoundException.class, () -> examComponent.getAllById(Set.of(1L, 5L, 9L)));
    }
    @Test
    void testGetAllFound(){
        //given
        ExamEntity examEntity = ExamEntity
                .builder()
                .name("test01")
                .build();

        ExamEntity examEntity2 = ExamEntity
                .builder()
                .name("test02")
                .build();

        when(examRepository.findAllById(Set.of(0L, 1L))).thenReturn(List.of(examEntity, examEntity2));

        //when - then
        assertDoesNotThrow(()->examComponent.getAllById(Set.of(0L, 1L)));
    }

    @Test
    void testGetAllCardioExam() {
        // Given
        SkillEntity skillEntity = SkillEntity.builder().name("cardio").build();
        ExamEntity exam1 = ExamEntity.builder().name("Exam 1").skillEntities(Set.of(skillEntity)).build();
        ExamEntity exam2 = ExamEntity.builder().name("Exam 2").skillEntities(Set.of(skillEntity)).build();

        when(skillRepository.findByNameLike(anyString())).thenReturn(Optional.of(skillEntity));
        when(examRepository.findAllBySkillEntitiesContaining(any(SkillEntity.class))).thenReturn(Set.of(exam1, exam2));

        // When - then
        assertDoesNotThrow(() -> examComponent.getAllCardioExam());
    }

}
