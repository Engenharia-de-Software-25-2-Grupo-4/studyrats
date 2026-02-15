package com.example.studyrats.E2E.estudante;

import com.example.studyrats.E2E.RequisicoesMock;
import com.example.studyrats.controller.EstudanteController;
import com.example.studyrats.dto.estudante.EstudantePostPutRequestDTO;
import com.example.studyrats.dto.estudante.EstudanteResponseDTO;
import com.example.studyrats.model.Estudante;
import com.example.studyrats.repository.EstudanteRepository;
import com.example.studyrats.service.estudante.EstudanteService;
import com.example.studyrats.service.firebase.FirebaseService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Test de controller de estudante")
@ActiveProfiles("test")
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

    @MockitoBean
    private FirebaseService firebaseService;

    private RequisicoesMock requisitor;

    @BeforeEach
    void setUp() {
        String baseURL = "/estudantes";
        requisitor = new RequisicoesMock(driver, baseURL);
        repoDoEstudante.deleteAll();
    }

    private void setarToken(String token) throws FirebaseAuthException {
        FirebaseToken firebaseTokenMock = Mockito.mock(FirebaseToken.class);
        when(firebaseTokenMock.getUid()).thenReturn(token);
        when(firebaseService.verifyToken(Mockito.eq(token))).thenReturn(firebaseTokenMock);
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

    private String randomChars(int size) {
        Random random = new Random();
        String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder result = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            result.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }

        return result.toString();
    }

    private void generateRandoms(int size) throws FirebaseAuthException {
        for (int i = 0; i < size; i++) {
            String token = randomChars();
            setarToken(token);

            EstudantePostPutRequestDTO body = new EstudantePostPutRequestDTO(randomChars(), randomChars());
            try {
                requisitor.performPostCreated(body, token);
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
        @DisplayName("Falha prevista ao criar com token inválido")
        void falhaCriarTokenInvalido() throws Exception {
            EstudantePostPutRequestDTO body = new EstudantePostPutRequestDTO("Test", "test@test");
            try {
                requisitor.performPostUnauthorized(body, "invalidToken");
            } catch (AssertionError e) {
                fail("A rota não retornou 401 unauthorized - "+e.getMessage());
            }

            List<Estudante> todosOsEstudantes = repoDoEstudante.findAll();
            assertEquals(0, todosOsEstudantes.size(), "A rota foi bloqueada mas a lista de estudantes não está vazia");
        }

        @Test
        @Transactional
        @DisplayName("Sucesso ao criar com autenticação")
        void sucessoCriarComAutenticacao() throws Exception {
            String randomToken = "randomToken";
            setarToken(randomToken);

            EstudantePostPutRequestDTO body = new EstudantePostPutRequestDTO("Test", "test@test");
            EstudanteResponseDTO novoEstudante = null;
            Estudante estudante = null;
            try {
                novoEstudante = requisitor.performPostCreated(EstudanteResponseDTO.class, body, randomToken);
                System.out.println(novoEstudante.getFirebaseUid());
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
            String token = "token";
            setarToken(token);
            EstudantePostPutRequestDTO body = new EstudantePostPutRequestDTO("Test", "test@test");
            EstudanteResponseDTO estudanteCriado = requisitor.performPostCreated(EstudanteResponseDTO.class, body, token);
            try {
                requisitor.performPostConflict(body, token);
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
        @DisplayName("Falha prevista ao tentar autenticação inválida")
        void falhaGetAllTokenInvalido() throws Exception {
            try {
                requisitor.performGetUnauthorized("invalidToken");
            } catch (AssertionError e) {
                fail("O endpoint nõa lançou 401 unauthorized - "+e.getMessage());
            }
        }

        @Test
        @DisplayName("Get all com lista vazia")
        void testeSemEstudantes() throws Exception {
            String token = "token";
            setarToken(token);
            List<EstudanteResponseDTO> listaDeEstudantes = List.of();
            listaDeEstudantes = requisitor.performGetOK(listaDeEstudantes.getClass(), token);
            assertEquals(0, listaDeEstudantes.size(), "Get all sem estudantes não retornou uma lista vazia");
        }

        @Test
        @DisplayName("Get all com 1 estudante")
        void testeUmEstudante() throws Exception {
            String token = "tokenV";
            setarToken(token);
            EstudantePostPutRequestDTO body = new EstudantePostPutRequestDTO("Test", "test@test");
            EstudanteResponseDTO estudanteCriado = requisitor.performPostCreated(EstudanteResponseDTO.class, body, token);

            List<EstudanteResponseDTO> listaDeEstudantes = List.of();
            listaDeEstudantes = requisitor.performGetOK(new TypeReference<List<EstudanteResponseDTO>>() {}, token);
            EstudanteResponseDTO estudanteDaLista = listaDeEstudantes.get(0);

            assertEquals(1, listaDeEstudantes.size(), "Get all sem estudantes não retornou apenas um estudante");
            assertEquals(estudanteCriado.getFirebaseUid(), estudanteDaLista.getFirebaseUid(), "O token não veio igual ao esperado");
            assertEquals(token, estudanteCriado.getFirebaseUid(), "O token não veio igual ao esperado");
            assertEquals(estudanteCriado.getNome(), estudanteDaLista.getNome(), "O nome não veio igual ao esperado");
            assertEquals(estudanteCriado.getEmail(), estudanteDaLista.getEmail(), "O email não veio igual ao esperado");
        }

        /*
        * Por falta de tempo irei deixar esse teste, mas irei pensar numa estratégia melhor no futuro.
        * Entretanto, existem 62^20 combinações únicas nessa geração e estaríamos extraíndo apenas 100.
        * A probabilidade de uma repetição é quase 0
        */
        @Test
        @DisplayName("Get all com X estudantes (existe uma probab baixissima desse teste falhar mesmo sem erros por causa da utilização de random)")
        void testeXEstudantes() throws Exception {
            List<EstudantePostPutRequestDTO> estudantes = new ArrayList<>();
            String token = null;
            for (int i = 0; i < 100; i++) {
                token = randomChars();
                setarToken(token);
                EstudantePostPutRequestDTO body = new EstudantePostPutRequestDTO(randomChars(), randomChars());
                try {
                    requisitor.performPostCreated(body, token);
                    estudantes.add(body);
                } catch (Exception ignored) {}
            }

            List<EstudanteResponseDTO> listaDeEstudantesDoGetall = List.of();
            listaDeEstudantesDoGetall = requisitor.performGetOK(new TypeReference<List<EstudanteResponseDTO>>() {}, token);

            assertEquals(estudantes.size(), listaDeEstudantesDoGetall.size(), "Nem todos os estudantes foram adicionados");

            for (EstudanteResponseDTO estudanteDoGetall : listaDeEstudantesDoGetall) {
                boolean encontrado = false;
                for (EstudantePostPutRequestDTO estudanteDTO : estudantes) {
                    System.out.println(estudanteDoGetall.getFirebaseUid());
                    boolean nomeIgual = estudanteDoGetall.getNome().equals(estudanteDTO.getNome());
                    boolean emailIgual = estudanteDoGetall.getEmail().equals(estudanteDTO.getEmail());
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
    @DisplayName("Testes de get by id")
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
        @DisplayName("Falha prevista ao tentar token invalido e banco vazio")
        void falhaTokenInvalido() throws Exception {
            try {
                String token = "tokenInvalido";
                requisitor.performGetUnauthorized("idQualquer", token);
            } catch (AssertionError e) {
                fail("O endpoint não lançou 401 unauthorized - "+e.getMessage());
            }
        }

        @Test
        @DisplayName("Falha prevista ao tentar token invalido e banco povoado")
        void falhaTokenInvalidoPovoado() throws Exception {
            generateRandoms(100);
            try {
                String token = "tokenInvalido";
                requisitor.performGetUnauthorized("idQualquer", token);
            } catch (AssertionError e) {
                fail("O endpoint não lançou 401 unauthorized - "+e.getMessage());
            }
        }

        @Test
        @DisplayName("Get sem estudante cadastrado")
        void testeSemEstudante() throws Exception {
            try {
                String token = "token";
                setarToken(token);
                requisitor.performGetNotFound("idQualquer", token);
            } catch (AssertionError e) {
                fail("O endpoint não lançou 404 not found - "+e.getMessage());
            }
        }

        @Test
        @DisplayName("Get de id inexistente com banco povoado")
        void testeSemIdExistente() throws Exception {
            generateRandoms(100);
            try {
                String token = "token";
                setarToken(token);
                requisitor.performGetNotFound("idQualquer", token);
            } catch (AssertionError e) {
                fail("O endpoint não lançou 404 not found - "+e.getMessage());
            }
        }

        @Test
        @DisplayName("get de id que existe")
        void testeComIdExistente() throws Exception {
            generateRandoms(100);
            List<EstudanteResponseDTO> todos = serviceDoEstudante.listarTodos();
            EstudanteResponseDTO estudanteAlvo = todos.get(0);

            String token = "token";
            setarToken(token);

            EstudanteResponseDTO estudanteDaReq = requisitor.performGetOK(EstudanteResponseDTO.class, estudanteAlvo.getFirebaseUid().toString(), token);
            assertEquals(estudanteAlvo, estudanteDaReq, "O estudante recuperado é diferente do esperado");
        }

    }

    @Nested
    @DisplayName("Testes de atualizacao")
    class TestesAtualizacao {

        @Test
        @DisplayName("Falha prevista ao tentar sem autenticacao e banco vazio")
        void falhaSemAuth() throws Exception {
            try {
                String token = "invalidToken";
                requisitor.performPutUnauthorized(token);
            } catch (AssertionError e) {
                fail("O endpoint não lançou 401 unauthorized - "+e.getMessage());
            }
        }

        @Test
        @DisplayName("Falha prevista ao tentar sem autenticacao e banco vazio")
        void falhaSemAuthBancoPovoado() throws Exception {
            generateRandoms(10);
            try {
                String token = "invalidToken";
                requisitor.performPutUnauthorized(token);
            } catch (AssertionError e) {
                fail("O endpoint não lançou 401 unauthorized - "+e.getMessage());
            }
        }

        @Test
        @DisplayName("Falha prevista ao tentar token invalido e banco vazio")
        void falhaTokenInvalido() throws Exception {
            try {
                String token = "invalidToken";
                requisitor.performPutUnauthorized(token, token);
            } catch (AssertionError e) {
                fail("O endpoint não lançou 401 unauthorized - "+e.getMessage());
            }
        }

        @Test
        @DisplayName("Falha prevista ao tentar token invalido e banco povoado")
        void falhaTokenInvalidoBancoPovoado() throws Exception {
            generateRandoms(10);
            try {
                String token = "invalidToken";
                requisitor.performPutUnauthorized(token, token);
            } catch (AssertionError e) {
                fail("O endpoint não lançou 401 unauthorized - "+e.getMessage());
            }
        }

        private String[] testeBaseAtualizar(int totalDeEstudantes, String token) throws Exception {
            setarToken(token);

            EstudantePostPutRequestDTO body = new EstudantePostPutRequestDTO("Test", "test@test");
            EstudanteResponseDTO estudanteOriginal = requisitor.performPostCreated(EstudanteResponseDTO.class, body, token);

            String novoEmail = "test@melhor";
            String novoNome = "testMelhor";
            body.setEmail(novoEmail);
            body.setNome(novoNome);

            EstudanteResponseDTO estudanteAtualizado = requisitor.performPutOk(EstudanteResponseDTO.class, body, token);
            List<EstudanteResponseDTO> todosOsEstudantes = requisitor.performGetOK(new TypeReference<List<EstudanteResponseDTO>>() {}, token);
            assertEquals(totalDeEstudantes, todosOsEstudantes.size(), "A atualização não manteve apenas 1 estudante");

            assertNotEquals(estudanteOriginal.getNome(), estudanteAtualizado.getNome(), "O nome não foi atualizado");
            assertNotEquals(estudanteOriginal.getEmail(), estudanteAtualizado.getEmail(), "O email não foi atualizado");
            assertEquals(estudanteOriginal.getFirebaseUid(), estudanteAtualizado.getFirebaseUid(), "O firebaseId mudou indevidamente");
            assertEquals(novoNome, estudanteAtualizado.getNome());
            assertEquals(novoEmail, estudanteAtualizado.getEmail());
            assertEquals(token, estudanteAtualizado.getFirebaseUid());
            assertNotEquals(estudanteOriginal, estudanteAtualizado); // Executar isso para verificar se o equals funciona também.

            novoEmail = "test@ruim";
            novoNome = "testRuim";
            body.setEmail(novoEmail);
            body.setNome(novoNome);

            estudanteAtualizado = requisitor.performPutOk(EstudanteResponseDTO.class, body, token);
            todosOsEstudantes = requisitor.performGetOK(new TypeReference<List<EstudanteResponseDTO>>() {}, token);
            assertEquals(totalDeEstudantes, todosOsEstudantes.size(), "A atualização não manteve apenas 1 estudante");

            assertNotEquals(estudanteOriginal.getNome(), estudanteAtualizado.getNome(), "O nome não foi atualizado");
            assertNotEquals(estudanteOriginal.getEmail(), estudanteAtualizado.getEmail(), "O email não foi atualizado");
            assertEquals(estudanteOriginal.getFirebaseUid(), estudanteAtualizado.getFirebaseUid(), "O firebaseId mudou indevidamente");
            assertEquals(novoNome, estudanteAtualizado.getNome());
            assertEquals(novoEmail, estudanteAtualizado.getEmail());
            assertEquals(token, estudanteAtualizado.getFirebaseUid());
            assertNotEquals(estudanteOriginal, estudanteAtualizado); // Executar isso para verificar se o equals funciona também.

            return new String[] {novoNome, novoEmail};
        }

        @Test
        @DisplayName("Sucesso ao atualizar (apenas ele em banco)")
        void sucessoAoAtualizar() throws Exception {
            String token = "targetUser";
            testeBaseAtualizar(1, token);
        }

        @Test
        @DisplayName("Sucesso ao atualizar banco povoado")
        void sucessoAoAtualizarBancoPovoado() throws Exception {
            String token = "targetUser";
            generateRandoms(99);
            String[] novosNomeEmail = testeBaseAtualizar(100, token);
            String novoNome = novosNomeEmail[0];
            String novoEmail = novosNomeEmail[1];

            List<EstudanteResponseDTO> todosOsEstudantes = requisitor.performGetOK(new TypeReference<List<EstudanteResponseDTO>>() {}, token);
            todosOsEstudantes.removeIf(e -> token.equals(e.getFirebaseUid()));
            assertEquals(99, todosOsEstudantes.size());
            for (EstudanteResponseDTO e : todosOsEstudantes) {
                assertNotEquals(novoNome, e.getNome());
                assertNotEquals(novoEmail, e.getEmail());
            }
        }
    }

    @Nested
    @DisplayName("Testes de deletar")
    class TestesDeletar {

        @Test
        @DisplayName("Falha prevista ao tentar sem autenticação banco vazio")
        void falhaGetAllSemAuth() throws Exception {
            try {
                requisitor.performDeleteUnauthorized();
            } catch (AssertionError e) {
                fail("O endpoint nõa lançou 401 unauthorized - "+e.getMessage());
            }
        }

        @Test
        @DisplayName("Falha prevista ao tentar sem autenticação banco povoado")
        void falhaGetAllSemAuthComBanco() throws Exception {
            generateRandoms(100);
            try {
                requisitor.performDeleteUnauthorized();
            } catch (AssertionError e) {
                fail("O endpoint nõa lançou 401 unauthorized - "+e.getMessage());
            }
        }

        @Test
        @DisplayName("Falha prevista ao tentar token invalido banco vazio")
        void falhaGetAllTokenInvalido() throws Exception {
            try {
                String token = "tokenInvalido";
                requisitor.performDeleteUnauthorized(token);
            } catch (AssertionError e) {
                fail("O endpoint nõa lançou 401 unauthorized - "+e.getMessage());
            }
        }

        @Test
        @DisplayName("Falha prevista ao tentar token invalido banco povoado")
        void falhaGetAllTokenInvalidoBancoPovoado() throws Exception {
            generateRandoms(100);
            try {
                String token = "tokenInvalido";
                requisitor.performDeleteUnauthorized(token);
            } catch (AssertionError e) {
                fail("O endpoint nõa lançou 401 unauthorized - "+e.getMessage());
            }
        }

        private void testeBaseDeletar(int totalDeEstudantes, String token) throws Exception {
            setarToken(token);

            EstudantePostPutRequestDTO body = new EstudantePostPutRequestDTO("Test", "test@test");
            EstudanteResponseDTO estudanteOriginal = requisitor.performPostCreated(EstudanteResponseDTO.class, body, token);

            requisitor.performDeleteNoContent(token);
            List<EstudanteResponseDTO> todosOsEstudantes = requisitor.performGetOK(new TypeReference<List<EstudanteResponseDTO>>() {}, token);
            assertEquals(totalDeEstudantes-1, todosOsEstudantes.size(), "O estudante foi retornado após remoção");
            try {
                requisitor.performGetNotFound(token, token);
            } catch (AssertionError e) {
                fail("O get não retornou 404 not found após remoção do estudante");
            }

            try {
                requisitor.performDeleteNotFound(token);
            } catch (AssertionError e) {
                fail("O delete não retornou 404 not found após remover sem ele no banco");
            }
            todosOsEstudantes = requisitor.performGetOK(new TypeReference<List<EstudanteResponseDTO>>() {}, token);
            assertEquals(totalDeEstudantes-1, todosOsEstudantes.size(), "O estudante foi retornado após remoção");
        }

        @Test
        @DisplayName("Sucesso ao deletar (apenas ele e sem ele em banco)")
        void sucessoAoDeletar() throws Exception {
            String token = "targetUser";
            testeBaseDeletar(1, token);
        }

        @Test
        @DisplayName("Sucesso ao deletar com e sem ele num  banco povoado")
        void sucessoAoDeletarBancoPovoado() throws Exception {
            String token = "OIE";
            generateRandoms(99);
            testeBaseDeletar(100, token);
        }
    }
}
