package fr.uga.l3miage.spring.tp3.components;

import fr.uga.l3miage.spring.tp3.models.EcosSessionEntity;
import fr.uga.l3miage.spring.tp3.models.EcosSessionProgrammationEntity;
import fr.uga.l3miage.spring.tp3.models.EcosSessionProgrammationStepEntity;
import fr.uga.l3miage.spring.tp3.repositories.EcosSessionProgrammationRepository;
import fr.uga.l3miage.spring.tp3.repositories.EcosSessionProgrammationStepRepository;
import fr.uga.l3miage.spring.tp3.repositories.EcosSessionRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.Set;

@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class SessionComponentTest {

    @Autowired
    private SessionComponent sessionComponent;

    @MockBean
    private EcosSessionRepository ecosSessionRepository;

    @MockBean
    private EcosSessionProgrammationRepository ecosSessionProgrammationRepository;

    @MockBean
    EcosSessionProgrammationStepRepository ecosSessionProgrammationStepRepository;

    @Test
    void testCreateSession() {
        //Given une ecoSessionEntity...
        EcosSessionProgrammationStepEntity ecosSessionProgrammationStepEntity = EcosSessionProgrammationStepEntity
                .builder()
                .code("001")
                .description("EcoSessionProgrammationStepDescriptionTest01")
                .build();

        EcosSessionProgrammationStepEntity ecosSessionProgrammationStepEntity1 = EcosSessionProgrammationStepEntity
                .builder()
                .code("002")
                .description("EcoSessionProgrammationStepDescriptionTest02")
                .build();

        EcosSessionProgrammationEntity ecosSessionProgrammationEntity = EcosSessionProgrammationEntity
                .builder()
                .label("EcosSessionProgrammationTest01")
                .ecosSessionProgrammationStepEntities(Set.of(ecosSessionProgrammationStepEntity,ecosSessionProgrammationStepEntity1 ))
                .build();

        EcosSessionEntity ecosSessionEntity = EcosSessionEntity
                .builder()
                .name("EcosSessionTest01")
                .ecosSessionProgrammationEntity(ecosSessionProgrammationEntity)
                .build();

        ecosSessionProgrammationStepEntity.setEcosSessionProgrammationEntity(ecosSessionProgrammationEntity);
        ecosSessionProgrammationStepEntity1.setEcosSessionProgrammationEntity(ecosSessionProgrammationEntity);

        when(ecosSessionProgrammationRepository.save(any(EcosSessionProgrammationEntity.class))).thenReturn(ecosSessionProgrammationEntity);
        when(ecosSessionRepository.save(any(EcosSessionEntity.class))).thenReturn(ecosSessionEntity);
        when(ecosSessionProgrammationStepRepository.saveAll(anySet())).thenReturn(Arrays.asList(ecosSessionProgrammationStepEntity, ecosSessionProgrammationStepEntity1));

        //When
        EcosSessionEntity ecosSessionEntityResult = sessionComponent.createSession(ecosSessionEntity);

        //Then
        assertNotNull(ecosSessionEntityResult);
        assertEquals("EcosSessionTest01", ecosSessionEntityResult.getName());


        verify(ecosSessionProgrammationRepository).save(any(EcosSessionProgrammationEntity.class));
        verify(ecosSessionRepository).save(any(EcosSessionEntity.class));
        verify(ecosSessionProgrammationStepRepository).saveAll(anySet());



    }
}
