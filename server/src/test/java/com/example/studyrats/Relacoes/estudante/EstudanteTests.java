package com.example.studyrats.Relacoes.estudante;

import com.example.studyrats.RequisicoesMock;
import com.example.studyrats.dto.GrupoDeEstudo.GrupoDeEstudoPostPutRequestDTO;
import com.example.studyrats.dto.GrupoDeEstudo.GrupoDeEstudoResponseDTO;
import com.example.studyrats.dto.SessaoDeEstudo.SessaoDeEstudoPostPutRequestDTO;
import com.example.studyrats.dto.SessaoDeEstudo.SessaoDeEstudoResponseDTO;
import com.example.studyrats.dto.estudante.EstudantePostPutRequestDTO;
import com.example.studyrats.model.GrupoDeEstudo;
import com.example.studyrats.model.SessaoDeEstudo;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@Tag("prod")
@AutoConfigureMockMvc
@DisplayName("Test de relações do Estudante")
public class EstudanteTests {

    @MockitoBean
    private FirebaseService firebaseService;
    @Autowired
    private MockMvc driver;
    @Autowired
    private GrupoDeEstudoRepository grupoDeEstudoRepository;
    @Autowired
    private SessaoDeEstudoRepository sessaoDeEstudoRepository;

    private RequisicoesMock requisitorEstudante;
    private RequisicoesMock requisitorGrupo;
    private RequisicoesMock requisitorSessao;

    private String tokenEstudantePrincipal = "tokenE1";
    private String tokenEstudanteSecuntario = "tokenE2";

    private List<GrupoDeEstudoResponseDTO> grupos;
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

    private void setup2EstudantesEmNGrupos() throws Exception{
        int n = 10;
        String token;
        EstudantePostPutRequestDTO body;
        GrupoDeEstudoPostPutRequestDTO bodyGrupo;
        SessaoDeEstudoPostPutRequestDTO bodySessao;
        GrupoDeEstudoResponseDTO grupo;
        List<GrupoDeEstudoResponseDTO> grupos = new ArrayList<>();
        List<SessaoDeEstudoResponseDTO> sessoes = new ArrayList<>();

        setarToken(tokenEstudantePrincipal);
        body = new EstudantePostPutRequestDTO(randomChars(), randomChars());
        requisitorEstudante.performPostCreated(body, tokenEstudantePrincipal);

        setarToken(tokenEstudanteSecuntario);
        body = new EstudantePostPutRequestDTO(randomChars(), randomChars());
        requisitorEstudante.performPostCreated(body, tokenEstudanteSecuntario);

        for (int i = 0; i < n; i++) {
            token = randomChars();
            setarToken(token);
            body = new EstudantePostPutRequestDTO(randomChars(), randomChars());
            requisitorEstudante.performPostCreated(body, token);

            bodyGrupo = GrupoDeEstudoPostPutRequestDTO.builder()
                    .nome(randomChars())
                    .descricao(randomChars())
                    .fotoPerfil("foto.png")
                    .regras("Sem spam e respeitar horários")
                    .dataInicio(LocalDateTime.of(2026, 5, 19, 14, 0))
                    .dataFim(LocalDateTime.of(2026, 10, 19, 16, 0))
                    .build();
            grupo = requisitorGrupo.performPostCreated(GrupoDeEstudoResponseDTO.class, bodyGrupo, token);
            grupos.add(grupo);

            String convite = requisitorGrupo.performPostCreatedStringReturn(grupo.getId().toString()+"/convites/gerar", token);
            setarToken(tokenEstudantePrincipal);
            requisitorGrupo.performPostOk("convites/"+convite+"/entrar", tokenEstudantePrincipal);
            bodySessao = new SessaoDeEstudoPostPutRequestDTO(
                    randomChars(),
                    randomChars(),
                    LocalDateTime.now().plusDays(1),
                    120,
                    randomChars(),
                    randomChars(),
                    randomChars()
            );
            sessoes.add(requisitorSessao.performPostCreated(SessaoDeEstudoResponseDTO.class, bodySessao, grupo.getId().toString(), tokenEstudantePrincipal));

            setarToken(tokenEstudanteSecuntario);
            requisitorGrupo.performPostOk("convites/"+convite+"/entrar", tokenEstudanteSecuntario);
            bodySessao = new SessaoDeEstudoPostPutRequestDTO(
                    randomChars(),
                    randomChars(),
                    LocalDateTime.now().plusDays(1),
                    120,
                    randomChars(),
                    randomChars(),
                    randomChars()
            );
            sessoes.add(requisitorSessao.performPostCreated(SessaoDeEstudoResponseDTO.class, bodySessao, grupo.getId().toString(), tokenEstudanteSecuntario));
        }
        this.grupos = grupos;
        this.sessoes = sessoes;
    }

    @Test @Transactional
    @DisplayName("Deletar estudante mantém grupos, remove membros e sessões")
    void deletarEstudanteMantemGrupos() throws Exception {
        setup2EstudantesEmNGrupos();
        setarToken(tokenEstudantePrincipal);
        requisitorEstudante.performDeleteNoContent(tokenEstudantePrincipal);
        List<GrupoDeEstudo> gruposRecuperados = grupoDeEstudoRepository.findAll();
        assertEquals(grupos.size(), gruposRecuperados.size(), "O total de grupos recuperados difere do esperado");
        for (GrupoDeEstudo grupo : gruposRecuperados) {
            assertTrue(grupo.getMembros()
                    .stream()
                    .noneMatch(membroGrupo -> membroGrupo.getEstudante() == null || membroGrupo.getEstudante().getFirebaseUid().equals(tokenEstudantePrincipal)),
                    "O estudante deletado foi encontrado como membro em algum grupo");
        }
        List<SessaoDeEstudo> sessoesRecuperadas = sessaoDeEstudoRepository.findAll();
        long sessoesEsperadas = sessoes.stream()
                .filter(sessao -> !tokenEstudantePrincipal.equals(sessao.getIdCriador()))
                .count();
        assertEquals(sessoesEsperadas, sessoesRecuperadas.size());
        assertTrue(sessoesRecuperadas.stream()
                .noneMatch(sessaoDeEstudo -> sessaoDeEstudo.getCriador() == null || sessaoDeEstudo.getCriador().getFirebaseUid().equals(tokenEstudantePrincipal)),
                "O estudante deletado foi encontrado como criador de alguma sessão de estudo");
    }
}
