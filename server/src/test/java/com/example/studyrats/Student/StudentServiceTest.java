package com.example.studyrats.Student;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.example.studyrats.dto.student.StudentPostPutRequestDTO;
import com.example.studyrats.model.Student;
import com.example.studyrats.repository.StudentRepository;
import tools.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
class StudentControllerIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        studentRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /students cria um novo estudante com sucesso")
    void createStudentSuccess() throws Exception {
        StudentPostPutRequestDTO body = validStudentPayload("João Silva", "joao@email.com", "senha123", "senha123");

        mockMvc.perform(post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(body)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNotEmpty())
            .andExpect(jsonPath("$.name").value("João Silva"))
            .andExpect(jsonPath("$.email").value("joao@email.com"));
    }

    @Test
    @DisplayName("POST /students retorna 400 se as senhas não coincidirem")
    void createStudentPasswordMismatch() throws Exception {
        StudentPostPutRequestDTO body = validStudentPayload("Erro", "erro@email.com", "senha123", "outrasenha");

        mockMvc.perform(post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(body)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /students/{id} retorna os dados do estudante)")
    void getStudentByIdSuccess() throws Exception {
        Student student = createStudent("Maria Souza", "maria@email.com");

        mockMvc.perform(get("/students/{id}", student.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(student.getId().toString()))
            .andExpect(jsonPath("$.name").value("Maria Souza"));
    }

    @Test
    @DisplayName("PUT /students/{id} atualiza o perfil do estudante")
    void updateStudentSuccess() throws Exception {
        Student student = createStudent("Jorge", "jorge@email.com");
        StudentPostPutRequestDTO body = validStudentPayload("Jorge Editado", "jorge@email.com", "1um2dois3tres", "1um2dois3tres");

        mockMvc.perform(put("/students/{id}", student.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(body)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Jorge Editado"));
    }

    @Test
    @DisplayName("DELETE /students/{id} remove a conta do usuário")
    void deleteStudentSuccess() throws Exception {
        Student student = createStudent("Para Deletar", "delete@email.com");

        mockMvc.perform(delete("/students/{id}", student.getId()))
            .andExpect(status().isNoContent());

        boolean exists = studentRepository.findById(student.getId()).isPresent();
        org.junit.jupiter.api.Assertions.assertFalse(exists);
    }

    private Student createStudent(String name, String email) {
        Student student = new Student();
        student.setName(name);
        student.setEmail(email);
        student.setPassword("teste123");
        return studentRepository.save(student);
    }

    private StudentPostPutRequestDTO validStudentPayload(String name, String email, String password, String confirm) {
        return StudentPostPutRequestDTO.builder()
            .name(name)
            .email(email)
            .password(password)
            .confirmPassword(confirm)
            .build();
    }

    private String toJson(Object dto) throws Exception {
        return objectMapper.writeValueAsString(dto);
    }
}