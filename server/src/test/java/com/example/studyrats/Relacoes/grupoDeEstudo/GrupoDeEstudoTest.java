package com.example.studyrats.Relacoes.grupoDeEstudo;

import com.example.studyrats.RequisicoesMock;
import com.example.studyrats.dto.GrupoDeEstudo.GrupoDeEstudoPostPutRequestDTO;
import com.example.studyrats.dto.GrupoDeEstudo.GrupoDeEstudoResponseDTO;
import com.example.studyrats.dto.SessaoDeEstudo.SessaoDeEstudoPostPutRequestDTO;
import com.example.studyrats.dto.estudante.EstudantePostPutRequestDTO;
import com.example.studyrats.model.Estudante;
import com.example.studyrats.model.GrupoDeEstudo;
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
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Test de relações do grupo de estudo")
@Tag("prod")
public class GrupoDeEstudoTest {

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

        setarToken(tokenEstudantePrincipal);
        body = new EstudantePostPutRequestDTO(randomChars(), randomChars());
        requisitorEstudante.performPostCreated(body, tokenEstudantePrincipal);
        bodyGrupo = GrupoDeEstudoPostPutRequestDTO.builder()
                .nome(randomChars())
                .descricao(randomChars())
                .fotoPerfil("foto.png")
                .regras("Sem spam e respeitar horários")
                .dataInicio(LocalDateTime.of(2026, 5, 19, 14, 0))
                .dataFim(LocalDateTime.of(2026, 10, 19, 16, 0))
                .build();
        GrupoDeEstudoPostPutRequestDTO bodyGrupo2 = GrupoDeEstudoPostPutRequestDTO.builder()
                .nome(randomChars())
                .descricao(randomChars())
                .fotoPerfil("foto.png")
                .regras("Sem spam e respeitar horários")
                .dataInicio(LocalDateTime.of(2026, 5, 19, 14, 0))
                .dataFim(LocalDateTime.of(2026, 10, 19, 16, 0))
                .build();
        grupo = requisitorGrupo.performPostCreated(GrupoDeEstudoResponseDTO.class, bodyGrupo, tokenEstudantePrincipal);
        GrupoDeEstudoResponseDTO grupo2 = requisitorGrupo.performPostCreated(GrupoDeEstudoResponseDTO.class, bodyGrupo2, tokenEstudantePrincipal);
        idGrupo = grupo.getId();
        UUID idGrupo2 = grupo2.getId();
        String convite = requisitorGrupo.performPostCreatedStringReturn(idGrupo.toString()+"/convites/gerar", tokenEstudantePrincipal);
        String convite2 = requisitorGrupo.performPostCreatedStringReturn(idGrupo2.toString()+"/convites/gerar", tokenEstudantePrincipal);

        for (int i = 0; i < numMembros; i++) {
            token = randomChars();
            setarToken(token);
            body = new EstudantePostPutRequestDTO(randomChars(), randomChars());
            requisitorEstudante.performPostCreated(body, token);
            requisitorGrupo.performPostOk("convites/"+convite+"/entrar", token);
            requisitorGrupo.performPostOk("convites/"+convite2+"/entrar", token);
            bodySessao = new SessaoDeEstudoPostPutRequestDTO(
                    randomChars(),
                    randomChars(),
                    LocalDateTime.now().plusDays(1),
                    120,
                    randomChars(),
                    randomChars(),
                    randomChars()
            );
            requisitorSessao.performPostCreated(bodySessao, idGrupo.toString(), token);
            requisitorSessao.performPostCreated(bodySessao, idGrupo2.toString(), token);
        }
    }

    @Test @Transactional
    @DisplayName("Deletar grupo mantém estudantes e remove sessões")
    void deletarGrupoMantemEstudanteRemoveSessoes() throws Exception {
        setup1GrupoNEstudantesMembrosNSessoes();
        setarToken(tokenEstudantePrincipal);
        requisitorGrupo.performDeleteNoContent(tokenEstudantePrincipal, idGrupo.toString());

        List<GrupoDeEstudo> grupos = grupoDeEstudoRepository.findAll();
        List<Estudante> estudantes = estudanteRepository.findAll();
        List<SessaoDeEstudo> sessoes = sessaoDeEstudoRepository.findAll();
        assertEquals(1, grupos.size(), "Deveria existir 1 grupo após o delete");
        assertEquals(11, estudantes.size(), "Deveriam existir 11 estudantes após a exclusão do grupo");
        assertEquals(10, sessoes.size(), "Deveriam existir 10 sessões de um grupo após a exclusão do outro grupo");

        grupos.forEach(
                g -> assertNotEquals(g.getId(), idGrupo, "Foi possível recuperar o grupo deletado pelo repo")
        );
        estudantes.forEach(
                e -> e.getStudySessions().forEach(
                        se -> assertNotEquals(se.getGrupoDeEstudo().getId(), idGrupo, "Foi possível recuperar o grupo de estudos pela lista de sessões de estudo de um estudante")
                )
        );
        sessoes.forEach(
                se -> assertNotEquals(se.getGrupoDeEstudo().getId(), idGrupo, "Foi possível recuperar o grupo pelas sessões de estudo")
        );
    }
}
