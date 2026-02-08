package com.example.studyrats.E2E.estudante;

import com.example.studyrats.E2E.RequisicoesMock;
import com.example.studyrats.controller.StudentController;
import com.example.studyrats.dto.student.StudentPostPutRequestDTO;
import com.example.studyrats.dto.student.StudentResponseDTO;
import com.example.studyrats.model.Student;
import com.example.studyrats.repository.StudentRepository;
import com.example.studyrats.service.student.StudentService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Test de controller de estudante")
public class EstudanteControllerTest {

    @Autowired
    private StudentController controllerDoEstudante;
    @Autowired
    private StudentRepository repoDoEstudante;
    @Autowired
    private StudentService serviceDoEstudante;
    @Autowired
    private MockMvc driver;

    private RequisicoesMock requisitor;

    @BeforeEach
    void setUp() {
        String baseURL = "/students";
        requisitor = new RequisicoesMock(driver, baseURL);
        repoDoEstudante.deleteAll();
    }

    @Nested
    @DisplayName("Testes de criação")
    class TestesDeCriacao {

        @Test
        @DisplayName("Falha esperada ao criar sem autenticação")
        void falhaCriarSemAutenticacao() throws Exception {
            StudentPostPutRequestDTO body = new StudentPostPutRequestDTO("Test", "test@test", "123", "123");
            try {
                requisitor.performPostUnauthorized(body);
            } catch (AssertionError e) {
                fail("A rota não retornou 401 unauthorized - "+e.getMessage());
            }

            List<Student> todosOsEstudantes = repoDoEstudante.findAll();
            assertEquals(0, todosOsEstudantes.size(), "A rota foi bloqueada mas a lista de estudantes não está vazia");
        }

        @Test
        @Transactional
        @DisplayName("Sucesso ao criar com autenticação")
        @WithMockUser(username="firebaseUserId")
        void sucessoCriarComAutenticacao() throws Exception {
            StudentPostPutRequestDTO body = new StudentPostPutRequestDTO("Test", "test@test", "123", "123");
            StudentResponseDTO novoEstudante = null;
            Student estudante = null;
            try {
                novoEstudante = requisitor.performPostCreated(StudentResponseDTO.class, body);
                estudante = repoDoEstudante.findByEmail("test@test").orElse(null);
                assertNotNull(estudante, "O estudante não foi encontrado no repositorio (por email) após criacão");
                assertNotNull(novoEstudante, "O estudante não foi retornado na requisição");
            } catch (AssertionError e) {
                fail("Falha na rota (ou test) de criação de novo usuário");
            }
            assertEquals(estudante.getName(), novoEstudante.getName());
            assertEquals(estudante.getEmail(), novoEstudante.getEmail());
            assertEquals(0, estudante.getStudySessions().size(), "A lista de sessões de estudo não está vazia");
        }

        @Test
        @DisplayName("Falha prevista ao tentar criar estudante já cadastrado")
        @WithMockUser(username="firebaseUserId")
        void falhaCriarComConflito() throws Exception {
            StudentPostPutRequestDTO body = new StudentPostPutRequestDTO("Test", "test@test", "123", "123");
            StudentResponseDTO estudanteCriado = serviceDoEstudante.criar(body);

            try {
                requisitor.performPostConflict(body);
            } catch (AssertionError e) {
                fail("O endpoint não lançou 409 conflict ao tentar criar o mesmo usuário duas vezes");
            }
        }
    }
}
