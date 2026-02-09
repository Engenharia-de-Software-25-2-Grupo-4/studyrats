package com.example.studyrats.E2E.estudante;

import ch.qos.logback.classic.Logger;
import com.example.studyrats.E2E.RequisicoesMock;
import com.example.studyrats.controller.EstudanteController;
import com.example.studyrats.dto.estudante.EstudantePostPutRequestDTO;
import com.example.studyrats.dto.estudante.EstudanteResponseDTO;
import com.example.studyrats.model.Estudante;
import com.example.studyrats.repository.EstudanteRepository;
import com.example.studyrats.service.estudante.EstudanteService;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.io.OutputStream;
import java.io.PrintStream;
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
    private EstudanteController controllerDoEstudante;
    @Autowired
    private EstudanteRepository repoDoEstudante;
    @Autowired
    private EstudanteService serviceDoEstudante;
    @Autowired
    private MockMvc driver;

    private RequisicoesMock requisitor;

    @BeforeEach
    void setUp() {
        String baseURL = "/students";
        requisitor = new RequisicoesMock(driver, baseURL);
        repoDoEstudante.deleteAll();
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

    private void generateRandoms(int size) {

        for (int i = 0; i < size; i++) {
            String pass = randomChars();
            EstudantePostPutRequestDTO body = new EstudantePostPutRequestDTO(randomChars(), randomChars());
            try {
                requisitor.performPostCreated(body);
            } catch (Exception ignored) {}
        }
    }

    @Nested
    @DisplayName("Testes de criação")
    class TestesDeCriacao {

        @Test
        @DisplayName("Falha prevista ao criar sem autenticação")
        void falhaCriarSemAutenticacao() throws Exception {
            EstudantePostPutRequestDTO body = new EstudantePostPutRequestDTO("Test", "test@test");
            try {
                requisitor.performPostUnauthorized(body);
            } catch (AssertionError e) {
                fail("A rota não retornou 401 unauthorized - "+e.getMessage());
            }

            List<Estudante> todosOsEstudantes = repoDoEstudante.findAll();
            assertEquals(0, todosOsEstudantes.size(), "A rota foi bloqueada mas a lista de estudantes não está vazia");
        }

        @Test
        @Transactional
        @DisplayName("Sucesso ao criar com autenticação")
        @WithMockUser(username="firebaseUserId")
        void sucessoCriarComAutenticacao() throws Exception {
            EstudantePostPutRequestDTO body = new EstudantePostPutRequestDTO("Test", "test@test");
            EstudanteResponseDTO novoEstudante = null;
            Estudante estudante = null;
            try {
                novoEstudante = requisitor.performPostCreated(EstudanteResponseDTO.class, body);
                estudante = repoDoEstudante.findByEmail("test@test").orElse(null);
                assertNotNull(estudante, "O estudante não foi encontrado no repositorio (por email) após criacão");
                assertNotNull(novoEstudante, "O estudante não foi retornado na requisição");
            } catch (AssertionError e) {
                fail("Falha na rota (ou test) de criação de novo usuário");
            }
            assertEquals(estudante.getNome(), novoEstudante.getNome());
            assertEquals(estudante.getEmail(), novoEstudante.getEmail());
            assertEquals(0, estudante.getStudySessions().size(), "A lista de sessões de estudo não está vazia");
        }

        @Test
        @DisplayName("Falha prevista ao tentar criar estudante já cadastrado")
        @WithMockUser(username="firebaseUserId")
        void falhaCriarComConflito() throws Exception {
            EstudantePostPutRequestDTO body = new EstudantePostPutRequestDTO("Test", "test@test");
            EstudanteResponseDTO estudanteCriado = serviceDoEstudante.criar(body);

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
        @DisplayName("Falha prevista ao tentar sem autenticação")
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
            List<EstudanteResponseDTO> listaDeEstudantes = List.of();
            listaDeEstudantes = requisitor.performGetOK(listaDeEstudantes.getClass());
            assertEquals(0, listaDeEstudantes.size(), "Get all sem estudantes não retornou uma lista vazia");
        }

        @Test
        @DisplayName("Get all com 1 estudante")
        @WithMockUser(username="firebaseUserId")
        void testeUmEstudante() throws Exception {
            EstudantePostPutRequestDTO body = new EstudantePostPutRequestDTO("Test", "test@test");
            EstudanteResponseDTO estudanteCriado = serviceDoEstudante.criar(body);

            List<EstudanteResponseDTO> listaDeEstudantes = List.of();
            listaDeEstudantes = requisitor.performGetOK(new TypeReference<List<EstudanteResponseDTO>>() {});
            EstudanteResponseDTO estudanteDaLista = listaDeEstudantes.get(0);

            assertEquals(1, listaDeEstudantes.size(), "Get all sem estudantes não retornou apenas um estudante");
            assertEquals(estudanteCriado.getNome(), estudanteDaLista.getNome(), "O nome não veio igual ao esperado");
            assertEquals(estudanteCriado.getEmail(), estudanteDaLista.getEmail(), "O email não veio igual ao esperado");
        }

        /*
        * Por falta de tempo irei deixar esse teste mas irei pensar numa estratégia melhro no futuro
        */
        @Test
        @DisplayName("Get all com X estudantes (existe uma probab baixissima desse teste falhar mesmo sem erros por causa da utilização de random)")
        @WithMockUser(username="firebaseUserId")
        void testeXEstudantes() throws Exception {
            List<EstudantePostPutRequestDTO> estudantes = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                String pass = randomChars();
                EstudantePostPutRequestDTO body = new EstudantePostPutRequestDTO(randomChars(), randomChars());
                try {
                    requisitor.performPostCreated(body);
                    estudantes.add(body);
                } catch (Exception ignored) {}
            }

            List<EstudanteResponseDTO> listaDeEstudantesDoGetall = List.of();
            listaDeEstudantesDoGetall = requisitor.performGetOK(new TypeReference<List<EstudanteResponseDTO>>() {});

            assertEquals(estudantes.size(), listaDeEstudantesDoGetall.size(), "Nem todos os estudantes foram adicionados");

            for (EstudanteResponseDTO estudanteDoGetall : listaDeEstudantesDoGetall) {
                Boolean encontrado = false;
                for (EstudantePostPutRequestDTO estudanteDTO : estudantes) {
                    System.out.println(estudanteDoGetall.getFirebaseUid());
                    Boolean nomeIgual = estudanteDoGetall.getNome().equals(estudanteDTO.getNome());
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

    @Nested
    @DisplayName("Tests de get by id")
    class TestesgetById {

        @Test
        @DisplayName("Falha prevista ao tentar sem autenticação e banco vazio")
        void falhaSemAuth() throws Exception {
            try {
                requisitor.performGetUnauthorized("idQualquer");
            } catch (AssertionError e) {
                fail("O endpoint não lançou 401 unauthorized - "+e.getMessage());
            }
        }

        @Test
        @DisplayName("Falha prevista ao tentar sem autenticação e banco povoado")
        void falhaSemAuthPovoado() throws Exception {
            generateRandoms(100);
            try {
                requisitor.performGetUnauthorized("idQualquer");
            } catch (AssertionError e) {
                fail("O endpoint não lançou 401 unauthorized - "+e.getMessage());
            }
        }

        @Test
        @DisplayName("Get sem estudante cadastrado")
        @WithMockUser(username="firebaseUserId")
        void testeSemEstudante() throws Exception {
            try {
                requisitor.performGetNotFound("idQualquer");
            } catch (AssertionError e) {
                fail("O endpoint não lançou 404 not found - "+e.getMessage());
            }
        }

        @Test
        @DisplayName("Get de id inexistente com banco povoado")
        @WithMockUser(username="firebaseUserId")
        void testeSemIdExistente() throws Exception {
            generateRandoms(100);
            try {
                requisitor.performGetNotFound("idQualquer");
            } catch (AssertionError e) {
                fail("O endpoint não lançou 404 not found - "+e.getMessage());
            }
        }

        @Test
        @DisplayName("get de id que existe")
        @WithMockUser(username="firebaseUserId")
        void testeComIdExistente() throws Exception {
            generateRandoms(100);
            List<EstudanteResponseDTO> todos = serviceDoEstudante.listarTodos();
            EstudanteResponseDTO estudanteAlvo = todos.get(0);

            EstudanteResponseDTO estudanteDaReq = requisitor.performGetOK(EstudanteResponseDTO.class, estudanteAlvo.getFirebaseUid().toString());
            assertEquals(estudanteAlvo, estudanteDaReq, "O estudante recuperado é diferente do esperado");
        }

    }

}
