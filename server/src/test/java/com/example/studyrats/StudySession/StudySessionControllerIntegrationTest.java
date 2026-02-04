package com.example.studyrats.StudySession;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.example.studyrats.dto.studySession.StudySessionPostPutRequestDTO;
import com.example.studyrats.model.Student;
import com.example.studyrats.model.StudySession;
import com.example.studyrats.repository.StudentRepository;
import com.example.studyrats.repository.StudySessionRepository;
import tools.jackson.databind.ObjectMapper;


import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
class StudySessionControllerIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudySessionRepository studySessionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Student creator;
    private Student otherStudent;
    private UUID groupId;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        studySessionRepository.deleteAll();
        studentRepository.deleteAll();

        creator = new Student();
        creator.setName("Maria");
        creator.setEmail("maria@ccc.com");
        creator = studentRepository.save(creator);

        otherStudent = new Student();
        otherStudent.setName("Luiza");
        otherStudent.setEmail("luiza@ccc.com");
        otherStudent = studentRepository.save(otherStudent);

        groupId = UUID.randomUUID();
    }

    @Test
    @DisplayName("POST /studySessions/group/{groupId}/user/{userId} cria sessão com sucesso")
    void createStudySessionSuccess() throws Exception {
        StudySessionPostPutRequestDTO body = validStudySessionPayload("OAC", "RAM e ROM", "Estudo pra prova 3", 90);

        mockMvc.perform(post("/studySessions/group/{groupId}/user/{userId}", groupId, creator.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(body)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.session_id").isNotEmpty())
            .andExpect(jsonPath("$.title").value("Estudo pra prova 3"))
            .andExpect(jsonPath("$.subject").value("OAC"))
            .andExpect(jsonPath("$.topic").value("RAM e ROM"));
    }

    @Test
    @DisplayName("POST /studySessions/group/{groupId}/user/{userId} retorna 400 com body inválido")
    void createStudySessionValidationError() throws Exception {
        StudySessionPostPutRequestDTO body = validStudySessionPayload("IC", "Historia da computacao", "Estudo do assunto da primeira semana de ic", 0);

        mockMvc.perform(post("/studySessions/group/{groupId}/user/{userId}", groupId, creator.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(body)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /studySessions/{sessionId}/user/{userId} retorna sessão do criador")
    void getStudySessionByIdSuccess() throws Exception {
        StudySession session = createSession("Banco de Dados", "Projeto", creator, groupId);

        mockMvc.perform(get("/studySessions/{sessionId}/user/{userId}", session.getSessionId(), creator.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.session_id").value(session.getSessionId().toString()))
            .andExpect(jsonPath("$.title").value("Sessao inicial"))
            .andExpect(jsonPath("$.subject").value("Banco de Dados"))
            .andExpect(jsonPath("$.topic").value("Projeto"));
    }

    @Test
    @DisplayName("GET /studySessions/{id}/user/{userId} retorna 404 para sessão inexistente")
    void getSessionNotFound() throws Exception {
    mockMvc.perform(get("/studySessions/{id}/user/{userId}", UUID.randomUUID(), creator.getId()))
           .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /studySessions/{sessionId}/user/{userId} atualiza sessão")
    void updateStudySessionSuccess() throws Exception {
        StudySession session = createSession("FMCC", "TCR", creator, groupId);
        StudySessionPostPutRequestDTO body = validStudySessionPayload("FMCC2", "Teorema Chines do Resto", "Sessao atualizada", 90);

        mockMvc.perform(put("/studySessions/{sessionId}/user/{userId}", session.getSessionId(), creator.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(body)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.session_id").value(session.getSessionId().toString()))
            .andExpect(jsonPath("$.title").value("Sessao atualizada"))
            .andExpect(jsonPath("$.subject").value("FMCC2"))
            .andExpect(jsonPath("$.topic").value("Teorema Chines do Resto"));
    }

    @Test
    @DisplayName("DELETE /studySessions/{sessionId}/user/{userId} remove sessão")
    void deleteStudySessionSuccess() throws Exception {
        StudySession session = createSession("SO", "Escalonamento", creator, groupId);

        mockMvc.perform(delete("/studySessions/{sessionId}/user/{userId}", session.getSessionId(), creator.getId()))
            .andExpect(status().isNoContent());

        boolean exists = studySessionRepository.findById(session.getSessionId()).isPresent();
        org.junit.jupiter.api.Assertions.assertFalse(exists);
    }

    @Test
    @DisplayName("GET /studySessions/byGroup/{groupId}/user/{userId} lista sessões do grupo")
    void listStudySessionsByGroupSuccess() throws Exception {
        createSession("Algoritmos", "Busca", creator, groupId);
        createSession("Algoritmos", "Ordenacao", otherStudent, groupId);
        createSession("Calculo", "Derivadas", creator, UUID.randomUUID());

        mockMvc.perform(get("/studySessions/byGroup/{groupId}/user/{userId}", groupId, creator.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)));
    }

    private StudySession createSession(String subject, String topic, Student student, UUID targetGroupId) {
        StudySession session = StudySession.builder()
            .creator(student)
            .groupId(targetGroupId)
            .title("Sessao inicial")
            .description("Descricao teste")
            .startTime(LocalDateTime.of(2026, 2, 4, 20, 0))
            .durationMinutes(60)
            .urlPhoto("https://example.com/photo.jpg")
            .subject(subject)
            .topic(topic)
            .build();
        return studySessionRepository.save(session);
    }

    private StudySessionPostPutRequestDTO validStudySessionPayload(String subject, String topic, String title, int durationMinutes) {
        return StudySessionPostPutRequestDTO.builder()
            .title(title)
            .description("Revisao para a prova")
            .startTime(LocalDateTime.of(2026, 2, 10, 19, 0))
            .durationMinutes(durationMinutes)
            .urlPhoto("https://example.com/session.png")
            .subject(subject)
            .topic(topic)
            .build();
    }

    private String toJson(StudySessionPostPutRequestDTO dto) throws Exception {
        return objectMapper.writeValueAsString(dto);
    }
}
