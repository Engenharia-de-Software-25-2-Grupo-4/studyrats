package com.example.studyrats.E2E.sessaoDeEstudo;

import com.example.studyrats.E2E.RequisicoesMock;
import com.example.studyrats.dto.GrupoDeEstudo.GrupoDeEstudoPostPutRequestDTO;
import com.example.studyrats.dto.estudante.EstudantePostPutRequestDTO;
import com.example.studyrats.repository.EstudanteRepository;
import com.example.studyrats.repository.GrupoDeEstudoRepository;
import com.example.studyrats.repository.SessaoDeEstudoRepository;
import com.example.studyrats.service.firebase.FirebaseService;
import com.example.studyrats.util.Mensagens;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

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

        GrupoDeEstudoPostPutRequestDTO bodyGrupo = new GrupoDeEstudoPostPutRequestDTO(randomChars(), randomChars());
        requisitorGrupo.performPostCreated(bodyGrupo, tokenEstudante1);

        setarToken(tokenEstudante2);
        body = new EstudantePostPutRequestDTO(randomChars(), randomChars());
        requisitorEstudante.performPostCreated(body, tokenEstudante2);
    }

    @BeforeEach
    void setup() {
        String baseUrl = "/estudantes";
        requisitorEstudante = new RequisicoesMock(driver, baseUrl);

        baseUrl = "/grupos";
        requisitorGrupo = new RequisicoesMock(driver, baseUrl);

        baseUrl = "sessaoDeEstudo";
        requisitor = new RequisicoesMock(driver, baseUrl);
    }

    @Nested
    @DisplayName("Testes de criacao")
    class TestesDeCriacao {

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
}
