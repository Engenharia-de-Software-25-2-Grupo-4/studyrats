package com.example.studyrats.E2E.grupoDeEstudo;

import com.example.studyrats.E2E.RequisicoesMock;
import com.example.studyrats.dto.ConviteGrupo.ConvitePostRequestDTO;
import com.example.studyrats.dto.ConviteGrupo.ConviteResponseDTO;
import com.example.studyrats.dto.GrupoDeEstudo.GrupoDeEstudoPostPutRequestDTO;
import com.example.studyrats.dto.GrupoDeEstudo.GrupoDeEstudoResponseDTO;
import com.example.studyrats.dto.estudante.EstudantePostPutRequestDTO;
import com.example.studyrats.dto.estudante.EstudanteResponseDTO;
import com.example.studyrats.model.ConviteGrupo;
import com.example.studyrats.model.GrupoDeEstudo;
import com.example.studyrats.repository.ConviteGrupoRepository;
import com.example.studyrats.repository.EstudanteRepository;
import com.example.studyrats.repository.GrupoDeEstudoRepository;
import com.example.studyrats.repository.MembroGrupoRepository;
import com.example.studyrats.service.firebase.FirebaseService;
import com.example.studyrats.util.Mensagens;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Test de controller do grupo de estudo")
@Tag("prod")
public class GrupoDeEstudoControllerTest {

    @MockitoBean
    private FirebaseService firebaseService;
    @Autowired
    private GrupoDeEstudoRepository grupoDeEstudoRepository;
    @Autowired
    private EstudanteRepository estudanteRepository;
    @Autowired
    private MembroGrupoRepository membroGrupoRepository;
    @Autowired
    private ConviteGrupoRepository conviteGrupoRepository;
    @Autowired
    private MockMvc driver;

    private String tokenEstudante1 = "estudante1";
    private String tokenEstudante2 = "estudante2";
    private String idQualquer = "00000000-eeee-dddd-aaaa-000000000000";

    private RequisicoesMock requisitor;
    private RequisicoesMock requisitorEstudante;

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

    private void gerarRandomsGruposEstudantes(int totEstudantes, int totGruposPorEstudante) throws FirebaseAuthException {
        RequisicoesMock requisicoesMock = new RequisicoesMock(driver, "/grupos");
        String token;
        for (int i = 0; i < totEstudantes; i++) {
            token = randomChars();
            setarToken(token);
            EstudantePostPutRequestDTO body = new EstudantePostPutRequestDTO(randomChars(), randomChars());
            try {
                requisitorEstudante.performPostCreated(body, token);
            } catch (Exception ignored) {}

            for (int j = 0; j < totGruposPorEstudante; j++) {
                GrupoDeEstudoPostPutRequestDTO bodyGrupo = new GrupoDeEstudoPostPutRequestDTO(randomChars(), randomChars());
                try {
                    requisicoesMock.performPostCreated(bodyGrupo, token);
                } catch (Exception ignored) {}
            }
        }
    }

    @BeforeEach
    void setup() {
        String baseUrl = "/grupos";
        requisitor = new RequisicoesMock(driver, baseUrl);
        baseUrl = "/estudantes";
        requisitorEstudante = new RequisicoesMock(driver, baseUrl);

        conviteGrupoRepository.deleteAll();
        membroGrupoRepository.deleteAll();
        grupoDeEstudoRepository.deleteAll();
        estudanteRepository.deleteAll();
    }

    @Nested
    @DisplayName("Testes de criação")
    class TestesDeCriacao {

        @Test
        @DisplayName("Falha prevista ao criar sem autenticação")
        void falhaCriarSemAutenticacao() throws Exception {
            GrupoDeEstudoPostPutRequestDTO body = new GrupoDeEstudoPostPutRequestDTO("grupo legal", "coisas legais");
            try {
                requisitor.performPostUnauthorized(body);
            } catch (AssertionError e) {
                fail("A rota não retornou 401 unauthorized - " + e.getMessage());
            }
        }

        @Test
        @DisplayName("Falha prevista ao criar com token invalido")
        void falhaCriarTokenInvalido() throws Exception {
            GrupoDeEstudoPostPutRequestDTO body = new GrupoDeEstudoPostPutRequestDTO("grupo legal", "coisas legais");
            try {
                requisitor.performPostUnauthorized(body, "tokenInvalido");
            } catch (AssertionError e) {
                fail("A rota não retornou 401 unauthorized - " + e.getMessage());
            }
        }

        @Test
        @DisplayName("Sucesso ao criar um grupo de estudo")
        void SucessoCriarGrupoDeEstudo() throws Exception {
            setarToken(tokenEstudante1);
            EstudantePostPutRequestDTO body = new EstudantePostPutRequestDTO("dono do grupo", "dono@grupo");
            requisitorEstudante.performPostCreated(body, tokenEstudante1);

            GrupoDeEstudoPostPutRequestDTO bodyGrupo = new GrupoDeEstudoPostPutRequestDTO("grupo legal", "coisas legais");
            GrupoDeEstudoResponseDTO grupoResponse = requisitor.performPostCreated(GrupoDeEstudoResponseDTO.class, bodyGrupo, tokenEstudante1);
            assertEquals(1, grupoDeEstudoRepository.findAll().size());
            GrupoDeEstudo grupoDoRepo = grupoDeEstudoRepository.findById(grupoResponse.getId()).orElse(null);
            if (grupoDoRepo == null) {
                fail("Sem retorno ao buscar o grupo no repo com o ID do grupoResponseDTO");
            }
            assertEquals(bodyGrupo.getNome(), grupoResponse.getNome(), "O response não possui mesmo nome que o grupo do body");
            assertEquals(bodyGrupo.getNome(), grupoDoRepo.getNome(), "O grupo do repo não possui o mesmo nome que o grupo do body");

            assertEquals(bodyGrupo.getDescricao(), grupoResponse.getDescricao(), "O response não possui mesma descrição que o grupo do body");
            assertEquals(bodyGrupo.getDescricao(), grupoDoRepo.getDescricao(), "O grupo do repo não possui mesma descrição que o grupo do body");

            assertEquals(grupoDoRepo.getId(), grupoResponse.getId(), "O grupo do repo não possui mesmo id que o grupo do response");
            assertEquals(grupoDoRepo.getAdmin().getFirebaseUid(), grupoResponse.getAdmin().getFirebaseUid(), "O id do admin do grupo não é o mesmo ID do criador do grupo");
        }

        @Test
        @DisplayName("Falha ao criar outro grupo com mesmo nome (MESMO ESTUDANTE)")
        void UmGrupoPorNomeMesmoEstudante() throws Exception {
            setarToken(tokenEstudante1);
            EstudantePostPutRequestDTO body = new EstudantePostPutRequestDTO("dono do grupo", "dono@grupo");
            requisitorEstudante.performPostCreated(body, tokenEstudante1);

            GrupoDeEstudoPostPutRequestDTO bodyGrupo = new GrupoDeEstudoPostPutRequestDTO("grupo legal", "coisas legais");
            requisitor.performPostCreated(bodyGrupo, tokenEstudante1);
            assertEquals(1, grupoDeEstudoRepository.findAll().size());

            bodyGrupo = new GrupoDeEstudoPostPutRequestDTO("grupo legal", "coisas legais de novo");
            try{
                requisitor.performPostConflict(bodyGrupo, tokenEstudante1);
            } catch (AssertionError e) {
                fail(Mensagens.NAO_RETORNOU_CONFLICT+e.getMessage());
            }
            assertEquals(1, grupoDeEstudoRepository.findAll().size());
        }

        @Test
        @DisplayName("Sucesso ao criar outro grupo com mesmo nome (ESTUDANTE DIFERENTE)")
        void VariosGruposPorNomeVariosEstudantes() throws Exception {
            setarToken(tokenEstudante1);
            EstudantePostPutRequestDTO body = new EstudantePostPutRequestDTO("dono do grupo", "dono@grupo");
            requisitorEstudante.performPostCreated(body, tokenEstudante1);

            GrupoDeEstudoPostPutRequestDTO bodyGrupo = new GrupoDeEstudoPostPutRequestDTO("grupo legal", "coisas legais");
            requisitor.performPostCreated(bodyGrupo, tokenEstudante1);
            assertEquals(1, grupoDeEstudoRepository.findAll().size());

            setarToken(tokenEstudante2);
            body = new EstudantePostPutRequestDTO("dono do grupo 2", "dono@grupo2");
            requisitorEstudante.performPostCreated(body, tokenEstudante2);

            bodyGrupo = new GrupoDeEstudoPostPutRequestDTO("grupo legal", "coisas legais");
            GrupoDeEstudoResponseDTO grupoResponse = requisitor.performPostCreated(GrupoDeEstudoResponseDTO.class, bodyGrupo, tokenEstudante2);
            assertEquals(2, grupoDeEstudoRepository.findAll().size());

            GrupoDeEstudo grupoAluno2Repo = grupoDeEstudoRepository.findById(grupoResponse.getId()).orElse(null);
            assertEquals(tokenEstudante2, grupoAluno2Repo.getAdmin().getFirebaseUid());
        }

        @Test
        @DisplayName("Sucesso multiplos grupos por estudante")
        void multiplosGruposPorEstudantes() throws Exception {
            int totalDeEstudantes = 100;
            int totalDeGruposPorEstudante = 10;
            gerarRandomsGruposEstudantes(totalDeEstudantes, totalDeGruposPorEstudante);

            List<GrupoDeEstudo> grupos = grupoDeEstudoRepository.findAll();
            assertEquals(totalDeEstudantes*totalDeGruposPorEstudante, grupos.size(), "O repositório não trouxe a quantia esperada de grupos");
        }
    }

    @Nested
    @DisplayName("Testes de get by ID")
    class TestesDeGetByID {

        @Test
        @DisplayName("Falha prevista ao tentar sem autenticação e banco vazio")
        void falhaSemAuth() throws Exception {
            try {
                requisitor.performGetUnauthorized(idQualquer);
            } catch (AssertionError e) {
                fail(Mensagens.NAO_RETORNOU_UNAUTHORIZED);
            }
        }

        @Test
        @DisplayName("Falha prevista ao tentar sem autenticacao e banco povoado")
        void falhaSemAuthBancoPovoado() throws Exception {
            gerarRandomsGruposEstudantes(100, 20);
            try {
                requisitor.performGetUnauthorized(idQualquer);
            } catch (AssertionError e) {
                fail(Mensagens.NAO_RETORNOU_UNAUTHORIZED);
            }
        }

        @Test
        @DisplayName("Falha prevista ao tentar com token invalido e banco vazio")
        void falharTokenInvalido() throws Exception {
            try {
                requisitor.performGetUnauthorized(idQualquer, "tokenFeio");
            } catch (AssertionError e) {
                fail(Mensagens.NAO_RETORNOU_UNAUTHORIZED);
            }
        }

        @Test
        @DisplayName("Falha prevista ao tentar com token invalido e banco povoado")
        void falharTokenInvalidoBancoPovoado() throws Exception {
            gerarRandomsGruposEstudantes(100, 20);
            try {
                requisitor.performGetUnauthorized(idQualquer, "tokenFeio");
            } catch (AssertionError e) {
                fail(Mensagens.NAO_RETORNOU_UNAUTHORIZED);
            }
        }

        @Test
        @DisplayName("Get sem grupo cadastrado")
        void getSemGrupoCadastrado() throws Exception {
            String token = "token";
            setarToken(token);
            EstudantePostPutRequestDTO body = new EstudantePostPutRequestDTO(randomChars(), randomChars());
            requisitorEstudante.performPostCreated(body, token);
            try {
                requisitor.performGetNotFound(idQualquer, token);
            } catch (AssertionError e) {
                fail(Mensagens.NAO_RETORNOU_NOT_FOUND+e.getMessage());
            }
        }

        @Test
        @DisplayName("Get de id inexistente com banco povoado")
        void getSemGrupoBancoPovoado() throws Exception {
            gerarRandomsGruposEstudantes(100, 10);
            String token = "token";
            setarToken(token);
            EstudantePostPutRequestDTO body = new EstudantePostPutRequestDTO(randomChars(), randomChars());
            requisitorEstudante.performPostCreated(body, token);
            try {
                requisitor.performGetNotFound(idQualquer, token);
            } catch (AssertionError e) {
                fail(Mensagens.NAO_RETORNOU_NOT_FOUND+e.getMessage());
            }
        }

        @Test
        @DisplayName("Get de id que existe")
        void getIdExiste() throws Exception {
            gerarRandomsGruposEstudantes(100, 10);
            String token = "token";
            setarToken(token);
            EstudantePostPutRequestDTO body = new EstudantePostPutRequestDTO(randomChars(), randomChars());
            requisitorEstudante.performPostCreated(body, token);

            GrupoDeEstudoPostPutRequestDTO bodyGrupo = new GrupoDeEstudoPostPutRequestDTO(randomChars(), randomChars());
            GrupoDeEstudoResponseDTO grupoTarget = requisitor.performPostCreated(GrupoDeEstudoResponseDTO.class, bodyGrupo, token);

            GrupoDeEstudoResponseDTO grupoResponse = null;
            try {
                grupoResponse = requisitor.performGetOK(GrupoDeEstudoResponseDTO.class, grupoTarget.getId().toString(), token);
            } catch (AssertionError e) {
                fail("O endpoint não retornou o grupo cadastrado");
            }
            assertEquals(grupoTarget.getId(), grupoResponse.getId());
            assertEquals(grupoTarget.getNome(), grupoResponse.getNome());
            assertEquals(grupoTarget.getDescricao(), grupoResponse.getDescricao());
            assertEquals(grupoTarget.getAdmin().getFirebaseUid(), grupoResponse.getAdmin().getFirebaseUid());
            // Assegurar que o equals está funcionando também
            // assertEquals(grupoTarget, grupoResponse);
        }
    }

    @Nested
    @DisplayName("Testes de deletar grupo")
    class TestesDeDelete {

        @Test
        @DisplayName("Falha prevista ao tentar sem autenticacao banco vazio")
        void falhaDeleteSemAuth() throws Exception {
            try {
                requisitor.performDeleteUnauthorized();
            } catch (AssertionError e) {
                fail(Mensagens.NAO_RETORNOU_UNAUTHORIZED);
            }
        }

        @Test
        @DisplayName("Falha prevista ao tentar sem autenticação banco povoado")
        void falhaDeleteSemAuthBancoPovoado() throws Exception {
            gerarRandomsGruposEstudantes(100, 10);
            try {
                requisitor.performDeleteUnauthorized();
            } catch (AssertionError e) {
                fail(Mensagens.NAO_RETORNOU_UNAUTHORIZED);
            }
        }

        @Test
        @DisplayName("Falha prevista ao tentar com token invalido banco vazio")
        void falhaDeleteTokenInvalido() throws Exception {
            try {
                requisitor.performDeleteUnauthorized(idQualquer, "tokenQualquer");
            } catch (AssertionError e) {
                fail(Mensagens.NAO_RETORNOU_UNAUTHORIZED);
            }
        }

        @Test
        @DisplayName("Falha prevista ao tentar com token invalido banco povoado")
        void falhaDeleteTokenInvalidoBancoPovoado() throws Exception {
            gerarRandomsGruposEstudantes(100, 10);
            try {
                requisitor.performDeleteUnauthorized(idQualquer, "tokenQualquer");
            } catch (AssertionError e) {
                fail(Mensagens.NAO_RETORNOU_UNAUTHORIZED);
            }
        }

        @Test
        @DisplayName("Sucesso ao deletar (Apenas ele em banco)")
        void sucessoAoDeletar() throws Exception {
            String token = "token";
            setarToken(token);
            EstudantePostPutRequestDTO body = new EstudantePostPutRequestDTO("Test", "test@test");
            EstudanteResponseDTO estudante = requisitorEstudante.performPostCreated(EstudanteResponseDTO.class, body, token);

            GrupoDeEstudoPostPutRequestDTO bodyGrupo = new GrupoDeEstudoPostPutRequestDTO(randomChars(), randomChars());
            GrupoDeEstudoResponseDTO grupoTarget = requisitor.performPostCreated(GrupoDeEstudoResponseDTO.class, bodyGrupo, token);

            try {
                requisitor.performDeleteNoContent(token, grupoTarget.getId().toString());
            } catch (AssertionError e) {
                fail(Mensagens.NAO_RETORNOU_NO_CONTENT+e.getMessage());
            }

            List<GrupoDeEstudo> grupos = grupoDeEstudoRepository.findAll();
            assertEquals(0, grupos.size());
            EstudanteResponseDTO estudantePos = requisitorEstudante.performGetOK(EstudanteResponseDTO.class, token, token);
            assertEquals(estudante.getFirebaseUid(), estudantePos.getFirebaseUid());
        }

        @Test
        @DisplayName("Sucesso ao deletar Banco povoado")
        void sucessoAoDeletarBancoPovoado() throws Exception {
            int totalDeEstudantes = 100;
            int totalDeGruposPorEstudante = 10;
            gerarRandomsGruposEstudantes(totalDeEstudantes, totalDeGruposPorEstudante);
            String token = "token";
            setarToken(token);
            EstudantePostPutRequestDTO body = new EstudantePostPutRequestDTO("Test", "test@test");
            EstudanteResponseDTO estudante = requisitorEstudante.performPostCreated(EstudanteResponseDTO.class, body, token);

            GrupoDeEstudoPostPutRequestDTO bodyGrupo = new GrupoDeEstudoPostPutRequestDTO(randomChars(), randomChars());
            GrupoDeEstudoResponseDTO grupoTarget = requisitor.performPostCreated(GrupoDeEstudoResponseDTO.class, bodyGrupo, token);

            List<GrupoDeEstudo> grupos = grupoDeEstudoRepository.findAll();
            assertEquals((totalDeEstudantes*totalDeGruposPorEstudante)+1, grupos.size(), "Findall não trouxe o total de grupos esperados");

            try {
                requisitor.performDeleteNoContent(token, grupoTarget.getId().toString());
            } catch (AssertionError e) {
                fail(Mensagens.NAO_RETORNOU_NO_CONTENT+e.getMessage());
            }

            grupos = grupoDeEstudoRepository.findAll();
            assertEquals((totalDeEstudantes*totalDeGruposPorEstudante), grupos.size(), "Findall não trouxe o total de grupos esperados");

            try {
                requisitor.performGetNotFound(grupoTarget.getId().toString(), token);
            } catch (AssertionError e) {
                fail("O get não retornou not found após deletar o grupo pesquisado");
            }

            estudante = requisitorEstudante.performGetOK(EstudanteResponseDTO.class, token, token);
            assertNotNull(estudante, "Não foi possível recuperar o estudante após deletar o grupo");
        }

        @Test
        @DisplayName("Falha esperada ao tentar deletar grupo de outro estudante")
        void falhaDeletarGrupoDeOutro() throws Exception {
            setarToken(tokenEstudante1);
            EstudantePostPutRequestDTO body = new EstudantePostPutRequestDTO("Test", "test@test");
            EstudanteResponseDTO estudante1 = requisitorEstudante.performPostCreated(EstudanteResponseDTO.class, body, tokenEstudante1);

            GrupoDeEstudoPostPutRequestDTO bodyGrupo = new GrupoDeEstudoPostPutRequestDTO(randomChars(), randomChars());
            GrupoDeEstudoResponseDTO grupoTarget1 = requisitor.performPostCreated(GrupoDeEstudoResponseDTO.class, bodyGrupo, tokenEstudante1);

            setarToken(tokenEstudante2);
            body = new EstudantePostPutRequestDTO("Test", "teswt@test");
            EstudanteResponseDTO estudante2 = requisitorEstudante.performPostCreated(EstudanteResponseDTO.class, body, tokenEstudante2);

            bodyGrupo = new GrupoDeEstudoPostPutRequestDTO(randomChars(), randomChars());
            GrupoDeEstudoResponseDTO grupoTarget2 = requisitor.performPostCreated(GrupoDeEstudoResponseDTO.class, bodyGrupo, tokenEstudante2);

            setarToken(tokenEstudante1);
            try {
                requisitor.performDeleteNotFound(grupoTarget2.getId().toString(), tokenEstudante1);
            } catch (AssertionError e) {
                fail(Mensagens.NAO_RETORNOU_NOT_FOUND+e.getMessage());
            }

            List<GrupoDeEstudo> grupos = grupoDeEstudoRepository.findAll();
            assertEquals(2, grupos.size(), "Findall não trouxe o total de grupos esperados");

            try {
                requisitor.performGetOK(grupoTarget1.getId().toString(), tokenEstudante1);
                setarToken(tokenEstudante2);
                requisitor.performGetOK(grupoTarget2.getId().toString(), tokenEstudante2);
            } catch (AssertionError e) {
                fail("Falha ao recuperar um dos grupos que tentou deletar indevicamente");
            }
        }

    }

    @Nested
    @DisplayName("Testes de getAll por estudante")
    class TestesGetAlPorEstudante {

        @Test
        @DisplayName("Falha prevista ao tentar sem autenticação banco vazio")
        void falhaGetAllSemAuth() throws Exception {
            try {
                requisitor.performGetUnauthorized();
            } catch (AssertionError e) {
                fail(Mensagens.NAO_RETORNOU_UNAUTHORIZED+e.getMessage());
            }
        }

        @Test
        @DisplayName("Falha prevista ao tentar sem autenticação banco povoado")
        void falhaGetAllSemAuthBancoPovoado() throws Exception {
            gerarRandomsGruposEstudantes(100, 10);
            try {
                requisitor.performGetUnauthorized();
            } catch (AssertionError e) {
                fail(Mensagens.NAO_RETORNOU_UNAUTHORIZED+e.getMessage());
            }
        }

        @Test
        @DisplayName("Falha prevista ao tentar token invalido banco vazio")
        void falhaGetAllTokenInvalido() throws Exception {
            try {
                String token = "token";
                requisitor.performGetUnauthorized("", token);
            } catch (AssertionError e) {
                fail(Mensagens.NAO_RETORNOU_UNAUTHORIZED+e.getMessage());
            }
        }

        @Test
        @DisplayName("Falha prevista ao tentar token invalido banco p")
        void falhaGetAllTokenInvalidoBancoPovoado() throws Exception {
            gerarRandomsGruposEstudantes(100, 10);
            try {
                String token = "token";
                requisitor.performGetUnauthorized("", token);
            } catch (AssertionError e) {
                fail(Mensagens.NAO_RETORNOU_UNAUTHORIZED+e.getMessage());
            }
        }

        @Test
        @DisplayName("Sucesso com banco povoado")
        void sucessoComBancoPovoado() throws Exception {
            gerarRandomsGruposEstudantes(100, 10);
            setarToken(tokenEstudante1);
            EstudantePostPutRequestDTO body = new EstudantePostPutRequestDTO("dono do grupo", "dono@grupo");
            requisitorEstudante.performPostCreated(body, tokenEstudante1);

            GrupoDeEstudoPostPutRequestDTO bodyGrupo = new GrupoDeEstudoPostPutRequestDTO("grupo legal", "coisas legais");
            GrupoDeEstudoResponseDTO grupoResponse1 = requisitor.performPostCreated(GrupoDeEstudoResponseDTO.class, bodyGrupo, tokenEstudante1);
            bodyGrupo = new GrupoDeEstudoPostPutRequestDTO("grupo legal 2", "coisas legais");
            GrupoDeEstudoResponseDTO grupoResponse2 = requisitor.performPostCreated(GrupoDeEstudoResponseDTO.class, bodyGrupo, tokenEstudante1);

            List<GrupoDeEstudoResponseDTO> grupos = requisitor.performGetOK(new TypeReference<List<GrupoDeEstudoResponseDTO>>() {}, tokenEstudante1);
            assertEquals(2, grupos.size());

            setarToken(tokenEstudante2);
            body = new EstudantePostPutRequestDTO("dono do grupo", "dono@grupo2");
            requisitorEstudante.performPostCreated(body, tokenEstudante2);
            grupos = requisitor.performGetOK(new TypeReference<List<GrupoDeEstudoResponseDTO>>() {}, tokenEstudante2);
            assertEquals(0, grupos.size());
        }

    }

    @Nested
    @DisplayName("Testes de convidar, listar e aceitar")
    class TestesConvidarUsuario {

        private List<EstudanteResponseDTO> gerarNEstudantes(int n) throws Exception {
            List<EstudanteResponseDTO> estudantes = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                String token = randomChars();
                setarToken(token);
                EstudantePostPutRequestDTO body = new EstudantePostPutRequestDTO(randomChars(), randomChars());
                estudantes.add(requisitorEstudante.performPostCreated(EstudanteResponseDTO.class, body, token));
            }
            return estudantes;
        }

        private List<List<GrupoDeEstudoResponseDTO>> gerarNEstudantesComGrupos(int nEstudantes, int maxGrupos) throws Exception {
            List<List<GrupoDeEstudoResponseDTO>> gruposPorAdmin = new ArrayList<>();
            Random rd = new Random();
            for (int i = 0; i < nEstudantes; i++) {
                String token = randomChars();
                setarToken(token);
                EstudantePostPutRequestDTO body = new EstudantePostPutRequestDTO(randomChars(), randomChars());
                requisitorEstudante.performPostCreated(body, token);

                List<GrupoDeEstudoResponseDTO> gruposDesseAdmin = new ArrayList<>();
                int totalDeGrupos = rd.nextInt(1, maxGrupos);
                for (int j = 0; j < totalDeGrupos; j++) {
                    GrupoDeEstudoPostPutRequestDTO bodyGrupo = new GrupoDeEstudoPostPutRequestDTO(randomChars(), randomChars());
                    gruposDesseAdmin.add(requisitor.performPostCreated(GrupoDeEstudoResponseDTO.class, bodyGrupo, token));
                }
                gruposPorAdmin.add(gruposDesseAdmin);
            }
            return gruposPorAdmin;
        }

        private List<List<String>> gerarNConvitesPorGrupo(List<List<GrupoDeEstudoResponseDTO>> grupos) throws Exception {
            List<List<String>> convitesPorAdminEGrupos = new ArrayList<>();
            Random rd = new Random();
            for (List<GrupoDeEstudoResponseDTO> gruposDoAdmin : grupos) {
                List<String> convitesPorGrupos = new ArrayList<>();
                String token = gruposDoAdmin.get(0).getAdmin().getFirebaseUid();
                setarToken(token);
                for (GrupoDeEstudoResponseDTO grupo : gruposDoAdmin) {
                    convitesPorGrupos.add(requisitor.performPostCreatedStringReturn(grupo.getId().toString()+"/convites/gerar", token));
                }
                convitesPorAdminEGrupos.add(convitesPorGrupos);
            }
            return convitesPorAdminEGrupos;
        }

        private void assertConvitesEqualsGrupo(List<List<String>> convitesPorAdmin, List<List<GrupoDeEstudoResponseDTO>> gruposPorAdmin) throws Exception {
            List<GrupoDeEstudoResponseDTO> gruposDoAdmin;
            List<String> convitesDoAdmin;
            ConviteResponseDTO dadosDoGrupoViaConvite=null;
            GrupoDeEstudoResponseDTO grupoTarget;
            String conviteTarget;

            for (int adminIdx = 0; adminIdx < convitesPorAdmin.size(); adminIdx++) {
                gruposDoAdmin = gruposPorAdmin.get(adminIdx);
                convitesDoAdmin = convitesPorAdmin.get(adminIdx);
                String token = gruposDoAdmin.get(0).getAdmin().getFirebaseUid();
                setarToken(token);
                for (int grupoIdx = 0; grupoIdx < gruposDoAdmin.size(); grupoIdx++) {
                    grupoTarget = gruposDoAdmin.get(grupoIdx);
                    conviteTarget = convitesDoAdmin.get(grupoIdx);
                    try {
                        dadosDoGrupoViaConvite = requisitor.performGetOK(ConviteResponseDTO.class, "convites/"+conviteTarget, token);
                    } catch (AssertionError e) {
                        fail("Houve uma falha ao tentar recuperar dados de um grupo via convite - "+e.getMessage());
                    }

                    assertTrue(dadosDoGrupoViaConvite.isJaMembro(), "O admin do grupo não está definido como membro do grupo");
                    assertEquals(dadosDoGrupoViaConvite.getIdGrupo(), grupoTarget.getId(), "O id do grupo recuperado é diferente do esperado");
                    assertEquals(dadosDoGrupoViaConvite.getNomeGrupo(), grupoTarget.getNome(), "O nome do grupo é diferente do esperado");
                    assertEquals(dadosDoGrupoViaConvite.getDescricaoGrupo(), grupoTarget.getDescricao(), "A descrição do grupo é diferente do esperado");
                }
            }
        }

        private <T> List<T> escolherN(List<T> lista, int totalParaEscolher) {
            List<T> escolhas = new ArrayList<>();
            Random rd = new Random();
            int i = 0;
            while (escolhas.size() < totalParaEscolher) {
                if (rd.nextInt(100) >= 5 && !escolhas.contains(lista.get(i))) { escolhas.add(lista.get(i)); }
                i = (i + 1) % lista.size();
            }
            return escolhas;
        }

        private void assertGruposContemGrupo(List<GrupoDeEstudoResponseDTO> grupos, GrupoDeEstudoResponseDTO grupoAlvo) {
            for (GrupoDeEstudoResponseDTO grupo : grupos) {
                if (grupoAlvo.equals(grupo)) {
                    return;
                }
            }
            fail("A lista de grupos do estudante não continha todos os grupos que ele participava");
        }

        private void convidarNEstudantesPorGrupo(List<EstudanteResponseDTO> estudantes, List<List<String>> convites, List<List<GrupoDeEstudoResponseDTO>> grupos, int maxConvites) throws Exception {
            Map<String, List<GrupoDeEstudoResponseDTO>> gruposPorEstudante = new HashMap<>();
            Random rd = new Random();
            List<String> convitesDoAdmin;
            List<GrupoDeEstudoResponseDTO> gruposDoAdmin;
            ConviteResponseDTO dadosDoGrupoViaConvite=null;
            String conviteAlvo;
            GrupoDeEstudoResponseDTO grupoAlvo;
            for (int adminIdx = 0; adminIdx < convites.size(); adminIdx++) {
                convitesDoAdmin = convites.get(adminIdx);
                gruposDoAdmin = grupos.get(adminIdx);
                for (int grupoIdx = 0; grupoIdx < convitesDoAdmin.size(); grupoIdx++) {
                    conviteAlvo = convitesDoAdmin.get(grupoIdx);
                    grupoAlvo = gruposDoAdmin.get(grupoIdx);
                    List<EstudanteResponseDTO> escolhidos = escolherN(estudantes, rd.nextInt(1, maxConvites));
                    for (EstudanteResponseDTO estudante : escolhidos) {
                        String token = estudante.getFirebaseUid();
                        setarToken(token);
                        dadosDoGrupoViaConvite = requisitor.performGetOK(ConviteResponseDTO.class, "convites/"+conviteAlvo, token);
                        assertFalse(dadosDoGrupoViaConvite.isJaMembro(), "O estudante não entrou no grupo mas já está como membro");
                        dadosDoGrupoViaConvite = requisitor.performGetOK(ConviteResponseDTO.class, "convites/"+conviteAlvo, token);
                        assertFalse(dadosDoGrupoViaConvite.isJaMembro(), "O estudante apenas validou o convite mas já está como membro");
                        try {
                            requisitor.performPostOk("convites/"+conviteAlvo+"/entrar", token);
                        } catch (AssertionError e) {
                            fail("Falha ao tentar entrar no grupo via convite"+e.getMessage());
                        }
                        dadosDoGrupoViaConvite = requisitor.performGetOK(ConviteResponseDTO.class, "convites/"+conviteAlvo, token);
                        assertTrue(dadosDoGrupoViaConvite.isJaMembro(), "O estudante deveria ter sido setado como membro");

                        List<GrupoDeEstudoResponseDTO> gruposDoGet = requisitor.performGetOK(new TypeReference<List<GrupoDeEstudoResponseDTO>>() {}, token);
                        assertGruposContemGrupo(gruposDoGet, grupoAlvo);
                    }
                }
            }
        }

        @Test
        @DisplayName("Teste geral N grupos, M convites, criar, comparar e entrar")
        void sucessoMultiplosGrupos() throws Exception {
            int totalDeEstudantes = 100;
            int totalDeEstudantesComGrupos = 5;
            int maximoDeGruposPorDono = 10;
            int maximoConvitesParaEstudantes = 10;

            List<EstudanteResponseDTO> estudantes = gerarNEstudantes(totalDeEstudantes);
            List<List<GrupoDeEstudoResponseDTO>> grupos = gerarNEstudantesComGrupos(totalDeEstudantesComGrupos, maximoDeGruposPorDono);
            List<List<String>> convites = gerarNConvitesPorGrupo(grupos);
            assertConvitesEqualsGrupo(convites, grupos);
            convidarNEstudantesPorGrupo(estudantes, convites, grupos, maximoConvitesParaEstudantes);
        }
    }

    @Nested
    @DisplayName("Teste de remover checkin invalido")
    class TesteRemoverCheckinInvalido {

    }
}
