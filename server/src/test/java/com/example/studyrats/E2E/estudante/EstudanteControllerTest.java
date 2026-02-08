package com.example.studyrats.E2E.estudante;

import com.example.studyrats.E2E.RequisicoesMock;
import com.example.studyrats.controller.StudentController;
import com.example.studyrats.dto.student.StudentPostPutRequestDTO;
import com.example.studyrats.dto.student.StudentResponseDTO;
import com.example.studyrats.model.Student;
import com.example.studyrats.repository.StudentRepository;
import com.example.studyrats.service.student.StudentService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Test de controller de estudante")
@Tag("prod")
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
        @DisplayName("Falha prevista ao criar sem autenticação")
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

    @Nested
    @DisplayName("Testes de getAll")
    class TestesDeGetAll {

        @Test
        @DisplayName("Falha prevista ao tentar obter todos sem autenticação")
        void falhaGetAllSemAuth() throws Exception {
            try {
                requisitor.performGetUnauthorized();
            } catch (AssertionError e) {
                fail("O endpoint nõa lançou 401 unauthorized - "+e.getMessage());
            }
        }

        @Test
        @DisplayName("Get all com lista vazia")
        @WithMockUser(username="firebaseUserId")
        void testeSemEstudantes() throws Exception {
            List<StudentResponseDTO> listaDeEstudantes = List.of();
            listaDeEstudantes = requisitor.performGetOK(listaDeEstudantes.getClass());
            assertEquals(0, listaDeEstudantes.size(), "Get all sem estudantes não retornou uma lista vazia");
        }

        @Test
        @DisplayName("Get all com 1 estudante")
        @WithMockUser(username="firebaseUserId")
        void testeUmEstudante() throws Exception {
            StudentPostPutRequestDTO body = new StudentPostPutRequestDTO("Test", "test@test", "123", "123");
            StudentResponseDTO estudanteCriado = serviceDoEstudante.criar(body);

            List<StudentResponseDTO> listaDeEstudantes = List.of();
            listaDeEstudantes = requisitor.performGetOK(listaDeEstudantes.getClass());
            StudentResponseDTO estudanteDaLista = listaDeEstudantes.get(0);

            assertEquals(1, listaDeEstudantes.size(), "Get all sem estudantes não retornou apenas um estudante");
            assertEquals(estudanteCriado.getName(), estudanteDaLista.getName(), "O nome não veio igual ao esperado");
            assertEquals(estudanteCriado.getEmail(), estudanteDaLista.getEmail(), "O email não veio igual ao esperado");
        }

        private String randomChars() {
            Random random = new Random();
            int size = 20;
            String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
            StringBuilder result = new StringBuilder(size);
            for (int i = 0; i < size; i++) {
                result.append(CHARS.charAt(random.nextInt(CHARS.length())));
            }

            return result.toString();
        }

        /*
        * Por falta de tempo irei deixar esse teste mas irei pensar numa estratégia melhro no futuro
        */
        @Test
        @DisplayName("Get all com X estudantes (existe uma probab baixissima desse teste falhar mesmo sem erros por causa da utilização de random)")
        @WithMockUser(username="firebaseUserId")
        void testeXEstudantes() throws Exception {
            List<StudentPostPutRequestDTO> estudantes = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                String pass = randomChars();
                StudentPostPutRequestDTO body = new StudentPostPutRequestDTO(randomChars(), randomChars(), pass, pass);
                try {
                    requisitor.performPostCreated(body);
                    estudantes.add(body);
                } catch (Exception ignored) {}
            }

            List<StudentResponseDTO> listaDeEstudantesDoGetall = List.of();
            listaDeEstudantesDoGetall = requisitor.performGetOK(listaDeEstudantesDoGetall.getClass());

            assertEquals(estudantes.size(), listaDeEstudantesDoGetall.size(), "Nem todos os estudantes foram adicionados");

            for (StudentResponseDTO estudanteDoGetall : listaDeEstudantesDoGetall) {
                Boolean encontrado = false;
                for (StudentPostPutRequestDTO estudanteDTO : estudantes) {
                    Boolean nomeIgual = estudanteDoGetall.getName().equals(estudanteDTO.getName());
                    Boolean emailIgual = estudanteDoGetall.getEmail().equals(estudanteDTO.getEmail());
                    if (nomeIgual && emailIgual) {
                        encontrado = true;
                        break;
                    }
                }
                if (!encontrado) {
                    fail("As listas não estão identicas");
                }
            }
        }

    }

}
