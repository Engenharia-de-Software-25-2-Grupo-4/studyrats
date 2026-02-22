package com.example.studyrats.E2E.sessaoDeEstudo;

import com.example.studyrats.RequisicoesMock;
import com.example.studyrats.dto.GrupoDeEstudo.GrupoDeEstudoPostPutRequestDTO;
import com.example.studyrats.dto.GrupoDeEstudo.GrupoDeEstudoResponseDTO;
import com.example.studyrats.dto.SessaoDeEstudo.SessaoDeEstudoPostPutRequestDTO;
import com.example.studyrats.dto.SessaoDeEstudo.SessaoDeEstudoResponseDTO;
import com.example.studyrats.dto.estudante.EstudantePostPutRequestDTO;
import com.example.studyrats.model.SessaoDeEstudo;
import com.example.studyrats.repository.EstudanteRepository;
import com.example.studyrats.repository.GrupoDeEstudoRepository;
import com.example.studyrats.repository.SessaoDeEstudoRepository;
import com.example.studyrats.service.firebase.FirebaseService;
import com.example.studyrats.util.Mensagens;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@Tag("prod")
@AutoConfigureMockMvc
@DisplayName("Test de controller de sessao de estudo")
public class SessaoDeEstudoControllerTest {

    @MockitoBean
    private FirebaseService firebaseService;
    @Autowired
    private SessaoDeEstudoRepository sessaoDeEstudoRepository;
    @Autowired
    private GrupoDeEstudoRepository grupoDeEstudoRepository;
    @Autowired
    private EstudanteRepository estudanteRepository;
    @Autowired
    private MockMvc driver;

    private RequisicoesMock requisitor;
    private RequisicoesMock requisitorGrupo;
    private RequisicoesMock requisitorEstudante;

    private String idQualquer = "00000000-eeee-dddd-aaaa-000000000000";
    private String tokenEstudante1 = "te1";
    private String tokenEstudante2 = "te2";
    private UUID grupo1Id = null;

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

    private void setup2Estudantes1Grupo() throws Exception {
        setarToken(tokenEstudante1);
        EstudantePostPutRequestDTO body = new EstudantePostPutRequestDTO(randomChars(), randomChars());
        requisitorEstudante.performPostCreated(body, tokenEstudante1);

        GrupoDeEstudoPostPutRequestDTO bodyGrupo =  GrupoDeEstudoPostPutRequestDTO.builder()
                .nome(randomChars())
                .descricao(randomChars())
                .fotoPerfil("foto.png")
                .regras("Sem spam e respeitar horários")
                .dataInicio(LocalDateTime.of(2026, 2, 19, 14, 0))
                .dataFim(LocalDateTime.of(2026, 12, 19, 16, 0))
                .build();
        GrupoDeEstudoResponseDTO grupo = requisitorGrupo.performPostCreated(GrupoDeEstudoResponseDTO.class, bodyGrupo, tokenEstudante1);
        grupo1Id = grupo.getId();

        setarToken(tokenEstudante2);
        body = new EstudantePostPutRequestDTO(randomChars(), randomChars());
        requisitorEstudante.performPostCreated(body, tokenEstudante2);
    }

    private void setupAluno2NoGrupo1() throws Exception {
        setarToken(tokenEstudante1);
        String convite = requisitorGrupo.performPostCreatedStringReturn(grupo1Id.toString()+"/convites/gerar", tokenEstudante1);
        setarToken(tokenEstudante2);
        requisitorGrupo.performPostOk("convites/"+convite+"/entrar", tokenEstudante2);
    }

    @BeforeEach
    void setup() {
        String baseUrl = "/estudantes";
        requisitorEstudante = new RequisicoesMock(driver, baseUrl);

        baseUrl = "/grupos";
        requisitorGrupo = new RequisicoesMock(driver, baseUrl);

        baseUrl = "/sessaoDeEstudo";
        requisitor = new RequisicoesMock(driver, baseUrl);
    }
/*
    @Nested
    @DisplayName("Testes de criacao")
    class TestesDeCriacaoo {

        @Test @Transactional
        @DisplayName("Falha prevista sem permissão")
        void falhaPrevistaFirebase() throws Exception {
            try {
                requisitor.performPostUnauthorized(idQualquer);
                requisitor.performPostUnauthorized(idQualquer, "tokenInvalido");
            } catch (AssertionError e) {
                fail(Mensagens.NAO_RETORNOU_UNAUTHORIZED+e.getMessage());
            }
        }

        @Test @Transactional
        @DisplayName("Sucesso")
        void criarComSucesso() throws Exception {
            setup2Estudantes1Grupo();
        }
    }
*/
    @Nested
    @DisplayName("Testes de Autenticação")
    class TestesDeAutenticacao {

        @Test
        @DisplayName("POST - Sem token deve retornar 401")
        void postSemToken() throws Exception {
            try {
                requisitor.performPostAccessDenied(idQualquer);
            } catch (AssertionError e) {
                fail(Mensagens.NAO_RETORNOU_UNAUTHORIZED + e.getMessage());
            }
        }

        @Test
        @DisplayName("POST - Token inválido deve retornar 401")
        void postTokenInvalido() throws Exception {
            try {
                requisitor.performPostUnauthorized(idQualquer, "tokenInvalido");
            } catch (AssertionError e) {
                fail(Mensagens.NAO_RETORNOU_UNAUTHORIZED + e.getMessage());
            }
        }

        @Test
        @DisplayName("GET por ID - Sem token deve retornar 401")
        void getPorIdSemToken() throws Exception {
            try {
                requisitor.performGetAcessDenied(idQualquer);
            } catch (AssertionError e) {
                fail(Mensagens.NAO_RETORNOU_UNAUTHORIZED + e.getMessage());
            }
        }

        @Test
        @DisplayName("GET por ID - Token inválido deve retornar 401")
        void getPorIdTokenInvalido() throws Exception {
            try {
                requisitor.performGetUnauthorized(idQualquer, "tokenInvalido");
            } catch (AssertionError e) {
                fail(Mensagens.NAO_RETORNOU_UNAUTHORIZED + e.getMessage());
            }
        }

        @Test
        @DisplayName("GET lista - Sem token deve retornar 401")
        void getListaSemToken() throws Exception {
            try {
                requisitor.performGetAcessDenied();
            } catch (AssertionError e) {
                fail(Mensagens.NAO_RETORNOU_UNAUTHORIZED + e.getMessage());
            }
        }

        @Test
        @DisplayName("GET lista - Token inválido deve retornar 401")
        void getListaTokenInvalido() throws Exception {
            try {
                requisitor.performGetUnauthorizedToken("tokenInvalido");
            } catch (AssertionError e) {
                fail(Mensagens.NAO_RETORNOU_UNAUTHORIZED + e.getMessage());
            }
        }

        @Test
        @DisplayName("PUT - Sem token deve retornar 401")
        void putSemToken() throws Exception {
            try {
                requisitor.performPutAccessDenied(idQualquer);
            } catch (AssertionError e) {
                fail(Mensagens.NAO_RETORNOU_UNAUTHORIZED + e.getMessage());
            }
        }

        @Test
        @DisplayName("PUT - Token inválido deve retornar 401")
        void putTokenInvalido() throws Exception {
            try {
                requisitor.performPutUnauthorized(idQualquer, "tokenInvalido");
            } catch (AssertionError e) {
                fail(Mensagens.NAO_RETORNOU_UNAUTHORIZED + e.getMessage());
            }
        }

        @Test
        @DisplayName("DELETE - Sem token deve retornar 401")
        void deleteSemToken() throws Exception {
            try {
                requisitor.performDeleteUnauthorized(idQualquer);
            } catch (AssertionError e) {
                fail(Mensagens.NAO_RETORNOU_UNAUTHORIZED + e.getMessage());
            }
        }

        @Test
        @DisplayName("DELETE - Token inválido deve retornar 401")
        void deleteTokenInvalido() throws Exception {
            try {
                requisitor.performDeleteUnauthorized(idQualquer, "tokenInvalido");
            } catch (AssertionError e) {
                fail(Mensagens.NAO_RETORNOU_UNAUTHORIZED + e.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("Testes de criação")
    class TestesDeCriacao {

        private SessaoDeEstudoPostPutRequestDTO criarBodyValido() {
            SessaoDeEstudoPostPutRequestDTO body = new SessaoDeEstudoPostPutRequestDTO();
            body.setTitulo("Sessao Teste");
            body.setDescricao("Descricao Teste");
            body.setHorarioInicio(LocalDateTime.now().plusDays(1));
            body.setDuracaoMinutos(120);
            body.setUrlFoto("http://teste.com/foto.png");
            body.setDisciplina("Matemática");
            body.setTopico("Derivadas");
            return body;
        }

        @Test
        @Transactional
        @DisplayName("Sucesso - Criar sessão válida")
        void criarComSucesso() throws Exception {

            setup2Estudantes1Grupo();
            setarToken(tokenEstudante1);

            SessaoDeEstudoPostPutRequestDTO body = criarBodyValido();

            requisitor.performPostCreated(body, grupo1Id.toString(), tokenEstudante1);

            assertEquals(1, sessaoDeEstudoRepository.count());
        }

        @Test
        @Transactional
        @DisplayName("Falha - Aluno não membro tentando criar")
        void criarNaoSendoMembro() throws Exception {
            setup2Estudantes1Grupo();
            setarToken(tokenEstudante2);
            SessaoDeEstudoPostPutRequestDTO body = criarBodyValido();
            requisitor.performPostNotFound(body, grupo1Id.toString(), tokenEstudante2);
            assertEquals(0, sessaoDeEstudoRepository.count());
        }

        @Test
        @Transactional
        @DisplayName("Sucesso - Aluno membro não admin criando")
        void criarNaoSendoAdmin() throws Exception {
            setup2Estudantes1Grupo();
            setupAluno2NoGrupo1();
            setarToken(tokenEstudante2);
            SessaoDeEstudoPostPutRequestDTO body = criarBodyValido();
            requisitor.performPostCreated(body, grupo1Id.toString(), tokenEstudante2);
            assertEquals(1, sessaoDeEstudoRepository.count());

            setarToken(tokenEstudante1);

            body = criarBodyValido();

            requisitor.performPostCreated(body, grupo1Id.toString(), tokenEstudante1);

            assertEquals(2, sessaoDeEstudoRepository.count());
        }

        @Test
        @Transactional
        @DisplayName("Sucesso - 2 sessões")
        void criar2Sessoes() throws Exception {
            setup2Estudantes1Grupo();
            setupAluno2NoGrupo1();
            setarToken(tokenEstudante2);
            SessaoDeEstudoPostPutRequestDTO body = criarBodyValido();
            requisitor.performPostCreated(body, grupo1Id.toString(), tokenEstudante2);
            assertEquals(1, sessaoDeEstudoRepository.count());
        }

        @Test
        @Transactional
        @DisplayName("Falha - Grupo não exsite")
        void grupoNaoExiste() throws Exception {
            setup2Estudantes1Grupo();
            setupAluno2NoGrupo1();
            setarToken(tokenEstudante2);
            SessaoDeEstudoPostPutRequestDTO body = criarBodyValido();
            requisitor.performPostNotFound(body, idQualquer, tokenEstudante2);
            assertEquals(0, sessaoDeEstudoRepository.count());
        }

        @Test
        @Transactional
        @DisplayName("Falha - Título nulo")
        void criarTituloNulo() throws Exception {

            setup2Estudantes1Grupo();
            setarToken(tokenEstudante1);

            SessaoDeEstudoPostPutRequestDTO body = criarBodyValido();
            body.setTitulo(null);

            requisitor.performPostBadRequest(body, grupo1Id.toString(), tokenEstudante1);

            assertEquals(0, sessaoDeEstudoRepository.count());
        }

        @Test
        @Transactional
        @DisplayName("Falha - Título em branco")
        void criarTituloEmBranco() throws Exception {

            setup2Estudantes1Grupo();
            setarToken(tokenEstudante1);

            SessaoDeEstudoPostPutRequestDTO body = criarBodyValido();
            body.setTitulo("");

            requisitor.performPostBadRequest(body, grupo1Id.toString(), tokenEstudante1);
        }

        @Test
        @Transactional
        @DisplayName("Falha - Horário início nulo")
        void criarHorarioInicioNulo() throws Exception {

            setup2Estudantes1Grupo();
            setarToken(tokenEstudante1);

            SessaoDeEstudoPostPutRequestDTO body = criarBodyValido();
            body.setHorarioInicio(null);

            requisitor.performPostBadRequest(body, grupo1Id.toString(), tokenEstudante1);
        }

        @Test
        @Transactional
        @DisplayName("Falha - Duração nula")
        void criarDuracaoNula() throws Exception {

            setup2Estudantes1Grupo();
            setarToken(tokenEstudante1);

            SessaoDeEstudoPostPutRequestDTO body = criarBodyValido();
            body.setDuracaoMinutos(null);

            requisitor.performPostBadRequest(body, grupo1Id.toString(), tokenEstudante1);
        }

        @Test
        @Transactional
        @DisplayName("Falha - Duração menor que 1")
        void criarDuracaoInvalida() throws Exception {

            setup2Estudantes1Grupo();
            setarToken(tokenEstudante1);

            SessaoDeEstudoPostPutRequestDTO body = criarBodyValido();
            body.setDuracaoMinutos(0);

            requisitor.performPostBadRequest(body, grupo1Id.toString(), tokenEstudante1);
        }

        @Test
        @Transactional
        @DisplayName("Falha - Disciplina nula")
        void criarDisciplinaNula() throws Exception {

            setup2Estudantes1Grupo();
            setarToken(tokenEstudante1);

            SessaoDeEstudoPostPutRequestDTO body = criarBodyValido();
            body.setDisciplina(null);

            requisitor.performPostBadRequest(body, grupo1Id.toString(), tokenEstudante1);
        }

        @Test
        @Transactional
        @DisplayName("Falha - Disciplina em branco")
        void criarDisciplinaEmBranco() throws Exception {

            setup2Estudantes1Grupo();
            setarToken(tokenEstudante1);

            SessaoDeEstudoPostPutRequestDTO body = criarBodyValido();
            body.setDisciplina("");

            requisitor.performPostBadRequest(body, grupo1Id.toString(), tokenEstudante1);
        }

        @Test
        @Transactional
        @DisplayName("Falha - Tópico nulo")
        void criarTopicoNulo() throws Exception {

            setup2Estudantes1Grupo();
            setarToken(tokenEstudante1);

            SessaoDeEstudoPostPutRequestDTO body = criarBodyValido();
            body.setTopico(null);

            requisitor.performPostBadRequest(body, grupo1Id.toString(), tokenEstudante1);
        }

        @Test
        @Transactional
        @DisplayName("Falha - Tópico em branco")
        void criarTopicoEmBranco() throws Exception {

            setup2Estudantes1Grupo();
            setarToken(tokenEstudante1);

            SessaoDeEstudoPostPutRequestDTO body = criarBodyValido();
            body.setTopico("");

            requisitor.performPostBadRequest(body, grupo1Id.toString(), tokenEstudante1);
        }
    }

    @Nested
    @DisplayName("Testes de Privacidade")
    class TestesDePrivacidade {

        private SessaoDeEstudoPostPutRequestDTO criarBodyValido() {
            SessaoDeEstudoPostPutRequestDTO body = new SessaoDeEstudoPostPutRequestDTO();
            body.setTitulo("Sessao Privada");
            body.setDescricao("Apenas eu posso ver");
            body.setHorarioInicio(LocalDateTime.now().plusDays(1));
            body.setDuracaoMinutos(60);
            body.setDisciplina("Segurança");
            body.setTopico("Privacidade");
            return body;
        }

        @Test
        @Transactional
        @DisplayName("Falha - Estudante não pode visualizar sessão de outro")
        void visualizarSessaoAlheia() throws Exception {
            setup2Estudantes1Grupo();
            
            setarToken(tokenEstudante1);
            SessaoDeEstudoResponseDTO sessaoDono = requisitor.performPostCreated(
                SessaoDeEstudoResponseDTO.class, criarBodyValido(), grupo1Id.toString(), tokenEstudante1
            );

            setarToken(tokenEstudante2);
            try {
                requisitor.performGetNotFound(sessaoDono.getIdSessao().toString(), tokenEstudante2);
            } catch (AssertionError e) {
                fail("A rota permitiu que um estudante visse a sessão de outro ou não retornou 404");
            }
        }

        @Test
        @Transactional
        @DisplayName("Falha - Estudante não pode atualizar sessão de outro")
        void atualizarSessaoAlheia() throws Exception {
            setup2Estudantes1Grupo();
            
            setarToken(tokenEstudante1);
            SessaoDeEstudoResponseDTO sessaoDono = requisitor.performPostCreated(
                SessaoDeEstudoResponseDTO.class, criarBodyValido(), grupo1Id.toString(), tokenEstudante1
            );

            setarToken(tokenEstudante2);
            SessaoDeEstudoPostPutRequestDTO bodyUpdate = criarBodyValido();
            bodyUpdate.setTitulo("Título Malicioso");

            try {
                requisitor.performPutNotFound(bodyUpdate, tokenEstudante2, sessaoDono.getIdSessao().toString());
            } catch (AssertionError e) {
                fail("A rota permitiu que um estudante atualizasse a sessão de outro");
            }

            setarToken(tokenEstudante1);
            SessaoDeEstudoResponseDTO sessaoOriginal = requisitor.performGetOK(
                SessaoDeEstudoResponseDTO.class, sessaoDono.getIdSessao().toString(), tokenEstudante1
            );
            assertEquals("Sessao Privada", sessaoOriginal.getTitulo());
        }

        @Test
        @Transactional
        @DisplayName("Falha - Estudante não pode deletar sessão de outro")
        void deletarSessaoAlheia() throws Exception {
            setup2Estudantes1Grupo();
            
            setarToken(tokenEstudante1);
            SessaoDeEstudoResponseDTO sessaoDono = requisitor.performPostCreated(
                SessaoDeEstudoResponseDTO.class, criarBodyValido(), grupo1Id.toString(), tokenEstudante1
            );

            // Estudante 2 tenta deletar
            setarToken(tokenEstudante2);
            try {
                requisitor.performDeleteNotFound(sessaoDono.getIdSessao().toString(), tokenEstudante2);
            } catch (AssertionError e) {
                fail("A rota permitiu que um estudante deletasse a sessão de outro");
            }

            assertEquals(1, sessaoDeEstudoRepository.count());
        }
    }

    @Nested
    @DisplayName("Testes de Listagens e Filtros")
    class TestesDeListagem {

        private SessaoDeEstudoPostPutRequestDTO criarBody(String titulo, String disciplina, String topico) {
            SessaoDeEstudoPostPutRequestDTO body = new SessaoDeEstudoPostPutRequestDTO();
            body.setTitulo(titulo);
            body.setDescricao("Descrição do teste");
            body.setHorarioInicio(LocalDateTime.now().plusDays(1));
            body.setDuracaoMinutos(60);
            body.setDisciplina(disciplina);
            body.setTopico(topico);
            return body;
        }

        @Test
        @Transactional
        @DisplayName("Sucesso - Listar todas as sessões apenas do utilizador autenticado")
        void listarSessoesDoUtilizador() throws Exception {
            setup2Estudantes1Grupo();
            setupAluno2NoGrupo1();

            setarToken(tokenEstudante1);
            requisitor.performPostCreated(criarBody("Sessão 1", "Matemática", "Cálculo"), grupo1Id.toString(), tokenEstudante1);
            requisitor.performPostCreated(criarBody("Sessão 2", "Física", "Mecânica"), grupo1Id.toString(), tokenEstudante1);

            setarToken(tokenEstudante2);
            requisitor.performPostCreated(criarBody("Sessão Alheia", "Química", "Orgânica"), grupo1Id.toString(), tokenEstudante2);

            setarToken(tokenEstudante1);
            List<SessaoDeEstudoResponseDTO> lista = requisitor.performGetOK(
                new TypeReference<List<SessaoDeEstudoResponseDTO>>() {}, tokenEstudante1
            );

            assertEquals(2, lista.size(), "O utilizador deveria ver apenas sua única sessão");
            assertTrue(lista.stream().anyMatch(s -> s.getTitulo().equals("Sessão 1")));
            assertTrue(lista.stream().anyMatch(s -> s.getTitulo().equals("Sessão 2")));

            setarToken(tokenEstudante2);
            lista = requisitor.performGetOK(
                    new TypeReference<List<SessaoDeEstudoResponseDTO>>() {}, tokenEstudante2
            );

            assertEquals(1, lista.size(), "O utilizador deveria ver apenas sua única sessão num grupo com 3");
            assertTrue(lista.stream().anyMatch(s -> s.getTitulo().equals("Sessão Alheia")));

            setarToken(tokenEstudante1);
            lista = requisitor.performGetOK(
                    new TypeReference<List<SessaoDeEstudoResponseDTO>>() {}, tokenEstudante1
            );

            assertEquals(2, lista.size(), "O utilizador deveria ver apenas as suas 2 sessões");
            assertTrue(lista.stream().anyMatch(s -> s.getTitulo().equals("Sessão 1")));
            assertTrue(lista.stream().anyMatch(s -> s.getTitulo().equals("Sessão 2")));

        }

        @Test
        @Transactional
        @DisplayName("Falha - Filtro de grupo que não existe")
        void filtrarGrupoNaoExiste() throws Exception {
            setup2Estudantes1Grupo();
            setupAluno2NoGrupo1();
            setarToken(tokenEstudante1);

            requisitor.performPostCreated(criarBody("Sessão A", "Java", "Streams"), grupo1Id.toString(), tokenEstudante1);
            requisitor.performPostCreated(criarBody("Sessão B", "Java", "Optional"), grupo1Id.toString(), tokenEstudante1);
            requisitor.performPostCreated(criarBody("Sessão C", "Python", "Flask"), grupo1Id.toString(), tokenEstudante1);

            requisitor.performGetNotFound("bySubject/Python/grupo/"+idQualquer, tokenEstudante2);
        }

        @Test
        @Transactional
        @DisplayName("Sucesso - Filtro de disciplina que não existe com outras sessões em banco")
        void filtrarDisciplinaNaoExiste() throws Exception {
            setup2Estudantes1Grupo();
            setupAluno2NoGrupo1();
            setarToken(tokenEstudante1);

            requisitor.performPostCreated(criarBody("Sessão A", "Java", "Streams"), grupo1Id.toString(), tokenEstudante1);
            requisitor.performPostCreated(criarBody("Sessão B", "Java", "Optional"), grupo1Id.toString(), tokenEstudante1);
            requisitor.performPostCreated(criarBody("Sessão C", "Python", "Flask"), grupo1Id.toString(), tokenEstudante1);

            List<SessaoDeEstudoResponseDTO> lista = requisitor.performGetOK(
                    new TypeReference<List<SessaoDeEstudoResponseDTO>>() {},
                    "bySubject/SeiLaVey/grupo/"+grupo1Id.toString(),
                    tokenEstudante1
            );

            assertEquals(0, lista.size(), "A lista deveria ter vindo vazia");
        }

        @Test
        @Transactional
        @DisplayName("Sucesso - Filtrar sessões por Disciplina")
        void filtrarPorDisciplina() throws Exception {
            setup2Estudantes1Grupo();
            setupAluno2NoGrupo1();
            setarToken(tokenEstudante2);

            requisitor.performPostCreated(criarBody("Sessão A2", "Java", "Streams"), grupo1Id.toString(), tokenEstudante2);
            requisitor.performPostCreated(criarBody("Sessão B2", "Java", "Optional"), grupo1Id.toString(), tokenEstudante2);
            requisitor.performPostCreated(criarBody("Sessão C2", "Java", "Flask"), grupo1Id.toString(), tokenEstudante2);
            requisitor.performPostCreated(criarBody("Sessão A22", "Java", "Streams"), grupo1Id.toString(), tokenEstudante2);
            requisitor.performPostCreated(criarBody("Sessão B22", "Java", "Optional"), grupo1Id.toString(), tokenEstudante2);
            requisitor.performPostCreated(criarBody("Sessão C22", "Python", "Flask"), grupo1Id.toString(), tokenEstudante2);

            List<SessaoDeEstudoResponseDTO> resultado = requisitor.performGetOK(
                    new TypeReference<List<SessaoDeEstudoResponseDTO>>() {}, "bySubject/Java/grupo/"+grupo1Id.toString(), tokenEstudante2
            );

            assertEquals(5, resultado.size());
            assertTrue(resultado.stream().allMatch(s -> s.getDisciplina().equals("Java".toUpperCase())));

            resultado = requisitor.performGetOK(
                    new TypeReference<List<SessaoDeEstudoResponseDTO>>() {}, "bySubject/Python/grupo/"+grupo1Id.toString(), tokenEstudante2
            );

            assertEquals(1, resultado.size());
            assertTrue(resultado.stream().allMatch(s -> s.getDisciplina().equals("Python".toUpperCase())));

            setarToken(tokenEstudante1);

            requisitor.performPostCreated(criarBody("Sessão A", "Java", "Streams"), grupo1Id.toString(), tokenEstudante1);
            requisitor.performPostCreated(criarBody("Sessão B", "Java", "Optional"), grupo1Id.toString(), tokenEstudante1);
            requisitor.performPostCreated(criarBody("Sessão C", "Python", "Flask"), grupo1Id.toString(), tokenEstudante1);

            resultado = requisitor.performGetOK(
                new TypeReference<List<SessaoDeEstudoResponseDTO>>() {}, "bySubject/Java/grupo/"+grupo1Id.toString(), tokenEstudante1
            );

            assertEquals(7, resultado.size());
            assertTrue(resultado.stream().allMatch(s -> s.getDisciplina().equals("Java".toUpperCase())));
        }

        @Test
        @Transactional
        @DisplayName("Sucesso - Filtrar sessões por Tópico") // Aparentemente não vão mais utilizar
        void filtrarPorTopico() throws Exception {
            setup2Estudantes1Grupo();
            setarToken(tokenEstudante1);

            requisitor.performPostCreated(criarBody("Aula 1", "História", "Brasil"), grupo1Id.toString(), tokenEstudante1);
            requisitor.performPostCreated(criarBody("Aula 2", "Geografia", "Brasil"), grupo1Id.toString(), tokenEstudante1);
            requisitor.performPostCreated(criarBody("Aula 3", "História", "Europa"), grupo1Id.toString(), tokenEstudante1);

            List<SessaoDeEstudoResponseDTO> resultado = requisitor.performGetOK(
                new TypeReference<List<SessaoDeEstudoResponseDTO>>() {}, "byTopic/Brasil", tokenEstudante1
            );

            assertEquals(2, resultado.size());
            assertTrue(resultado.stream().allMatch(s -> s.getTopico().equals("Brasil".toUpperCase())));
        }

        @Test
        @Transactional
        @DisplayName("Sucesso - Retornar lista vazia se não houver sessões")
        void retornarListaVazia() throws Exception {
            setup2Estudantes1Grupo();
            setarToken(tokenEstudante1);

            List<SessaoDeEstudoResponseDTO> lista = requisitor.performGetOK(
                new TypeReference<List<SessaoDeEstudoResponseDTO>>() {}, tokenEstudante1
            );

            assertNotNull(lista);
            assertEquals(0, lista.size(), "Deveria retornar uma lista vazia e não erro");
        }
    }

    @Nested
    @DisplayName("Testes de sucesso")
    class TestesCaminhosFelizes {

        private SessaoDeEstudoPostPutRequestDTO criarPayloadValido() {
            SessaoDeEstudoPostPutRequestDTO body = new SessaoDeEstudoPostPutRequestDTO();
            body.setTitulo("Revisão de PSoft");
            body.setDescricao("Estudando para a prova de Design Patterns");
            body.setHorarioInicio(LocalDateTime.now().plusDays(2));
            body.setDuracaoMinutos(120);
            body.setDisciplina("Projeto de Software");
            body.setTopico("Decorator Pattern");
            return body;
        }

        @Test
        @Transactional
        @DisplayName("Sucesso - Buscar sessão por ID")
        void visualizarSessaoSucesso() throws Exception {
            setup2Estudantes1Grupo();
            setarToken(tokenEstudante1);
            
            SessaoDeEstudoResponseDTO criada = requisitor.performPostCreated(
                SessaoDeEstudoResponseDTO.class, criarPayloadValido(), grupo1Id.toString(), tokenEstudante1
            );

            SessaoDeEstudoResponseDTO buscada = requisitor.performGetOK(
                SessaoDeEstudoResponseDTO.class, criada.getIdSessao().toString(), tokenEstudante1
            );

            assertNotNull(buscada);
            assertEquals(criada.getIdSessao(), buscada.getIdSessao());
            assertEquals("Revisão de PSoft", buscada.getTitulo());
        }

        @Test
        @Transactional
        @DisplayName("Sucesso - Atualizar dados da própria sessão")
        void atualizarSessaoSucesso() throws Exception {
            setup2Estudantes1Grupo();
            setarToken(tokenEstudante1);
            
            SessaoDeEstudoResponseDTO criada = requisitor.performPostCreated(
                SessaoDeEstudoResponseDTO.class, criarPayloadValido(), grupo1Id.toString(), tokenEstudante1
            );

            SessaoDeEstudoPostPutRequestDTO bodyUpdate = criarPayloadValido();
            bodyUpdate.setTitulo("Título Atualizado");
            bodyUpdate.setDuracaoMinutos(180);

            SessaoDeEstudoResponseDTO atualizada = requisitor.performPutOk(
                SessaoDeEstudoResponseDTO.class, bodyUpdate, criada.getIdSessao().toString(), tokenEstudante1
            );

            assertEquals("Título Atualizado", atualizada.getTitulo());
            assertEquals(180, atualizada.getDuracaoMinutos());
            
            SessaoDeEstudo noBanco = sessaoDeEstudoRepository.findById(criada.getIdSessao()).get();
            assertEquals("Título Atualizado", noBanco.getTitulo());
        }

        @Test
        @Transactional
        @DisplayName("Sucesso - Remover a própria sessão")
        void removerSessaoSucesso() throws Exception {
            setup2Estudantes1Grupo();
            setarToken(tokenEstudante1);
            
            SessaoDeEstudoResponseDTO criada = requisitor.performPostCreated(
                SessaoDeEstudoResponseDTO.class, criarPayloadValido(), grupo1Id.toString(), tokenEstudante1
            );

            assertEquals(1, sessaoDeEstudoRepository.count());

            requisitor.performDeleteNoContent(tokenEstudante1, criada.getIdSessao().toString());

            assertEquals(0, sessaoDeEstudoRepository.count());
            requisitor.performGetNotFound(criada.getIdSessao().toString(), tokenEstudante1);
        }
    }

}
