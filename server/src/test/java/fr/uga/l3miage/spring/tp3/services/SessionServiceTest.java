package fr.uga.l3miage.spring.tp3.services;

import fr.uga.l3miage.spring.tp3.components.ExamComponent;
import fr.uga.l3miage.spring.tp3.components.SessionComponent;
import fr.uga.l3miage.spring.tp3.exceptions.technical.ExamNotFoundException;
import fr.uga.l3miage.spring.tp3.mappers.SessionMapper;
import fr.uga.l3miage.spring.tp3.models.EcosSessionEntity;
import fr.uga.l3miage.spring.tp3.models.EcosSessionProgrammationEntity;
import fr.uga.l3miage.spring.tp3.models.EcosSessionProgrammationStepEntity;
import fr.uga.l3miage.spring.tp3.request.SessionCreationRequest;
import fr.uga.l3miage.spring.tp3.request.SessionProgrammationCreationRequest;
import fr.uga.l3miage.spring.tp3.request.SessionProgrammationStepCreationRequest;
import fr.uga.l3miage.spring.tp3.responses.SessionResponse;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class SessionServiceTest {

    @Autowired
    private SessionService sessionService;

    @SpyBean
    private SessionMapper sessionMapper;

    @MockBean
    private ExamComponent examComponent;

    @MockBean
    private SessionComponent sessionComponent;

    @Test
    void testCreateSession() throws ExamNotFoundException {
        //Given
        SessionProgrammationStepCreationRequest sessionProgrammationStepCreationRequest = SessionProgrammationStepCreationRequest
                .builder()
                .id(1L)
                .code("code01")
                .description("description programmationStepCreation 01")
                .dateTime(LocalDateTime.of(2024, 3, 2, 8, 30))
                .build();

        SessionProgrammationCreationRequest sessionProgrammationCreationRequest = SessionProgrammationCreationRequest
                .builder()
                .id(1L)
                .label("label test01")
                .steps(Set.of(sessionProgrammationStepCreationRequest))
                .build();

        // j'ai tout d'abord besoin d'une request... qui contient les informations sur la request de creation de la request...
        SessionCreationRequest sessionCreationRequest = SessionCreationRequest
                .builder()
                .name("test01")
                .startDate(LocalDateTime.of(2024, 3, 10, 8, 30))
                .endDate(LocalDateTime.of(2024, 3, 10, 18, 30))
                .examsId(Set.of())
                .ecosSessionProgrammation(sessionProgrammationCreationRequest)
                .build();

        EcosSessionProgrammationStepEntity ecosSessionProgrammationStepEntity = sessionMapper.toEntity(sessionProgrammationStepCreationRequest);
        EcosSessionProgrammationEntity ecosSessionProgrammationEntity = sessionMapper.toEntity(sessionProgrammationCreationRequest);
        EcosSessionEntity ecosSessionEntity = sessionMapper.toEntity(sessionCreationRequest);

        ecosSessionProgrammationEntity.setEcosSessionProgrammationStepEntities(Set.of(ecosSessionProgrammationStepEntity));
        ecosSessionEntity.setEcosSessionProgrammationEntity(ecosSessionProgrammationEntity);
        ecosSessionEntity.setExamEntities(Set.of()); // j'ai définis que c'étais vide...


        when(examComponent.getAllById(same(Set.of()))).thenReturn(Set.of());
        when(sessionComponent.createSession(any(EcosSessionEntity.class))).thenReturn(ecosSessionEntity);

        SessionResponse sessionExpected = sessionMapper.toResponse(ecosSessionEntity);

        //When

        SessionResponse response = sessionService.createSession(sessionCreationRequest);

        //Then
        assertThat(response).usingRecursiveComparison().isEqualTo(sessionExpected);

        verify(sessionMapper, times(2)).toEntity(sessionProgrammationStepCreationRequest);
        verify(sessionMapper, times(2)).toEntity(sessionProgrammationCreationRequest);
        verify(sessionMapper, times(2)).toEntity(sessionCreationRequest);
        verify(sessionMapper, times(2)).toResponse(same(ecosSessionEntity));

        verify(examComponent, times(1)).getAllById(Set.of());
        verify(sessionComponent, times(1)).createSession(any(EcosSessionEntity.class));



    }

}
