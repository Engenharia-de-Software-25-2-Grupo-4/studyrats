package com.example.studyrats.Relacoes.sessaoDeEstudos;

import com.example.studyrats.RequisicoesMock;
import com.example.studyrats.dto.GrupoDeEstudo.GrupoDeEstudoPostPutRequestDTO;
import com.example.studyrats.dto.GrupoDeEstudo.GrupoDeEstudoResponseDTO;
import com.example.studyrats.dto.SessaoDeEstudo.SessaoDeEstudoPostPutRequestDTO;
import com.example.studyrats.dto.SessaoDeEstudo.SessaoDeEstudoResponseDTO;
import com.example.studyrats.dto.estudante.EstudantePostPutRequestDTO;
import com.example.studyrats.model.Estudante;
import com.example.studyrats.model.SessaoDeEstudo;
import com.example.studyrats.repository.EstudanteRepository;
import com.example.studyrats.repository.GrupoDeEstudoRepository;
import com.example.studyrats.repository.SessaoDeEstudoRepository;
import com.example.studyrats.service.firebase.FirebaseService;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@Tag("prod")
@AutoConfigureMockMvc
@DisplayName("Test de relações da Sessão de estudos")
public class SessaoDeEstudosTest {

    @MockitoBean
    private FirebaseService firebaseService;
    @Autowired
    private MockMvc driver;
    @Autowired
    private GrupoDeEstudoRepository grupoDeEstudoRepository;
    @Autowired
    private SessaoDeEstudoRepository sessaoDeEstudoRepository;
    @Autowired
    private EstudanteRepository estudanteRepository;

    private RequisicoesMock requisitorEstudante;
    private RequisicoesMock requisitorGrupo;
    private RequisicoesMock requisitorSessao;

    private String tokenEstudantePrincipal = "tokenE1";
    private String tokenEstudanteSecuntadio = "tokenE2";
    private UUID idGrupo;
    private List<SessaoDeEstudoResponseDTO> sessoes;

    @BeforeEach
    void setup() {
        String baseUrl = "/estudantes";
        requisitorEstudante = new RequisicoesMock(driver, baseUrl);

        baseUrl = "/grupos";
        requisitorGrupo = new RequisicoesMock(driver, baseUrl);

        baseUrl = "/sessaoDeEstudo";
        requisitorSessao = new RequisicoesMock(driver, baseUrl);
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

    private void setarToken(String token) throws FirebaseAuthException {
        FirebaseToken firebaseTokenMock = Mockito.mock(FirebaseToken.class);
        when(firebaseTokenMock.getUid()).thenReturn(token);
        when(firebaseService.verifyToken(Mockito.eq(token))).thenReturn(firebaseTokenMock);
    }

    private void setup1GrupoNEstudantesMembrosNSessoes() throws Exception {
        int numMembros = 10;
        String token;
        EstudantePostPutRequestDTO body;
        GrupoDeEstudoPostPutRequestDTO bodyGrupo;
        GrupoDeEstudoResponseDTO grupo;
        SessaoDeEstudoPostPutRequestDTO bodySessao;
        List<SessaoDeEstudoResponseDTO> sessoes = new ArrayList<>();

        setarToken(tokenEstudantePrincipal);
        body = new EstudantePostPutRequestDTO(randomChars(), randomChars());
        requisitorEstudante.performPostCreated(body, tokenEstudantePrincipal);
        bodyGrupo = GrupoDeEstudoPostPutRequestDTO.builder()
                .nome(randomChars())
                .descricao(randomChars())
                .build();
        grupo = requisitorGrupo.performPostCreated(GrupoDeEstudoResponseDTO.class, bodyGrupo, tokenEstudantePrincipal);
        idGrupo = grupo.getId();
        String convite = requisitorGrupo.performPostCreatedStringReturn(grupo.getId().toString()+"/convites/gerar", tokenEstudantePrincipal);

        for (int i = 0; i < numMembros; i++) {
            token = randomChars();
            setarToken(token);
            body = new EstudantePostPutRequestDTO(randomChars(), randomChars());
            requisitorEstudante.performPostCreated(body, token);
            requisitorGrupo.performPostOk("convites/"+convite+"/entrar", token);
            bodySessao = new SessaoDeEstudoPostPutRequestDTO(
                    randomChars(),
                    randomChars(),
                    LocalDateTime.now().plusDays(1),
                    120,
                    randomChars(),
                    randomChars(),
                    randomChars()
            );
            sessoes.add(requisitorSessao.performPostCreated(SessaoDeEstudoResponseDTO.class, bodySessao, idGrupo.toString(), token));
        }
        this.sessoes = sessoes;
    }

    @Test @Transactional
    @DisplayName("Deletar Sessão de estudos mantém grupos e estudantes")
    void deletarSessaoMantemGruposEEstudantes() throws Exception {
        setup1GrupoNEstudantesMembrosNSessoes();
        List<Estudante> estudantes;
        Estudante estudante;
        for (SessaoDeEstudoResponseDTO sessao : sessoes) {
            setarToken(sessao.getIdCriador());
            assertTrue(sessaoDeEstudoRepository
                    .findAll()
                    .stream()
                    .anyMatch(s -> sessao.getIdSessao().equals(s.getIdSessao())),
                    "Não foi possível recuperar a sessão direto do repositório antes da remoção");
            requisitorSessao.performDeleteNoContent(sessao.getIdCriador(), sessao.getIdSessao().toString());
            assertEquals(11, estudanteRepository.count());
            assertEquals(1, grupoDeEstudoRepository.count());
            sessaoDeEstudoRepository
                    .findAll()
                    .forEach(s -> assertNotEquals(sessao.getIdSessao(), s.getIdSessao()));
        }
    }
}
