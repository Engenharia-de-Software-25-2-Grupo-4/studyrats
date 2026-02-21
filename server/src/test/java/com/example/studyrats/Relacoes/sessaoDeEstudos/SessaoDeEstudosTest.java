package com.example.studyrats.Relacoes.sessaoDeEstudos;

import com.example.studyrats.RequisicoesMock;
import com.example.studyrats.dto.GrupoDeEstudo.GrupoDeEstudoPostPutRequestDTO;
import com.example.studyrats.dto.GrupoDeEstudo.GrupoDeEstudoResponseDTO;
import com.example.studyrats.dto.SessaoDeEstudo.SessaoDeEstudoPostPutRequestDTO;
import com.example.studyrats.dto.SessaoDeEstudo.SessaoDeEstudoResponseDTO;
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
                .fotoPerfil("foto.png")
                .regras("Sem spam e respeitar horários")
                .dataInicio(LocalDateTime.of(2026, 5, 19, 14, 0))
                .dataFim(LocalDateTime.of(2026, 10, 19, 16, 0))
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
            bodySessao = getRandomSessaoDeEstudo();
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
            assertTrue(sessaoDeEstudoRepository.findAll()
                    .stream()
                    .anyMatch(s -> sessao.getIdSessao().equals(s.getIdSessao())),
                    "Não foi possível recuperar a sessão direto do repositório antes da remoção");

            requisitorSessao.performDeleteNoContent(sessao.getIdCriador(), sessao.getIdSessao().toString());

            assertEquals(11, estudanteRepository.count(), "O total de estudantes recuperados não é igual ao total esperado");
            assertNotNull(estudanteRepository.findById(sessao.getIdCriador()), "Não foi possível encontrar o estudante criador da sessão deletada");
            estudanteRepository.findAll()
                            .forEach(e -> e.getStudySessions().forEach(s -> assertNotEquals(sessao.getIdSessao(), s.getIdSessao(), "A sessão deletada foi encontrada na lista de sessões do usuário")));
            assertEquals(1, grupoDeEstudoRepository.count());
            grupoDeEstudoRepository.findAll()
                            .forEach(gde -> gde.getSessoes().forEach(s -> assertNotEquals(sessao.getIdSessao(), s.getIdSessao(), "A sessão deletada foi encontrada na lista de sessões de algum grupo de estudo")));
            sessaoDeEstudoRepository.findAll()
                            .forEach(s -> assertNotEquals(sessao.getIdSessao(), s.getIdSessao(), "A sessão deletada foi recuperada via repositório."));
        }
    }

    private SessaoDeEstudoPostPutRequestDTO getRandomSessaoDeEstudo() {
        return new SessaoDeEstudoPostPutRequestDTO(
                randomChars(),
                randomChars(),
                LocalDateTime.now().plusDays(1),
                120,
                randomChars(),
                randomChars(),
                randomChars()
        );
    }

    private boolean sDoRepoEqualsBodySessao(SessaoDeEstudo sDoRepo, SessaoDeEstudoPostPutRequestDTO bodySessao) {
        return sDoRepo.getDescricao().equals(bodySessao.getDescricao()) &&
                sDoRepo.getDisciplina().equals(bodySessao.getDisciplina()) &&
                sDoRepo.getDuracaoMinutos().equals(bodySessao.getDuracaoMinutos()) &&
                sDoRepo.getHorarioInicio().equals(bodySessao.getHorarioInicio()) &&
                sDoRepo.getTitulo().equals(bodySessao.getTitulo()) &&
                sDoRepo.getTopico().equals(bodySessao.getTopico()) &&
                sDoRepo.getUrlFoto().equals(bodySessao.getUrlFoto());
    }

    private boolean sDoRepoEqualsSAntes(SessaoDeEstudo sDoRepo, SessaoDeEstudoResponseDTO s) {
        return sDoRepo.getCriador().getFirebaseUid().equals(s.getIdCriador()) &&
                sDoRepo.getDescricao().equals(s.getDescricao()) &&
                sDoRepo.getDisciplina().equals(s.getDisciplina()) &&
                sDoRepo.getDuracaoMinutos().equals(s.getDuracaoMinutos()) &&
                sDoRepo.getHorarioInicio().equals(s.getHorarioInicio()) &&
                sDoRepo.getTitulo().equals(s.getTitulo()) &&
                sDoRepo.getTopico().equals(s.getTopico()) &&
                sDoRepo.getUrlFoto().equals(s.getUrlFoto());
    }

    // São redundantes, mas auxiliam a leitura
    private boolean sDoGrupoEqualsSAntes(SessaoDeEstudo sDoGrupo, SessaoDeEstudoResponseDTO sAntes) {
        return sDoRepoEqualsSAntes(sDoGrupo, sAntes);
    }

    // São redundantes, mas auxiliam a leitura
    private boolean sDoEstudanteEqualsSAntes(SessaoDeEstudo sDoGrupo, SessaoDeEstudoResponseDTO sAntes) {
        return sDoRepoEqualsSAntes(sDoGrupo, sAntes);
    }

    @Test @Transactional
    @DisplayName("Atualizar Sessão de estudos altera a sessão correta e reflete nas outras classes")
    void atualizarSessaoRefleteNasClasses() throws Exception {
        List<SessaoDeEstudo> sessoesDoRepo;
        List<Estudante> estudantesDoRepo;
        List<GrupoDeEstudo> gruposDoRepo;
        Estudante estudante;
        setup1GrupoNEstudantesMembrosNSessoes();
        for (SessaoDeEstudoResponseDTO sAntes : sessoes) {
            String token = sAntes.getIdCriador();
            setarToken(token);
            SessaoDeEstudoPostPutRequestDTO bodySessao = getRandomSessaoDeEstudo();
            requisitorSessao.performPutOk(bodySessao, sAntes.getIdSessao().toString(), token);
            sessoesDoRepo = sessaoDeEstudoRepository.findAll();
            assertEquals(sessoes.size(), sessoesDoRepo.size(), "O repo trouxe uma quantidade de sessões diferente do esperado");
            assertTrue(sessoesDoRepo.stream().noneMatch(sDoRepo -> sDoRepoEqualsSAntes(sDoRepo, sAntes)), "Foi possível encontrar no repo os dados antigos após atualização.");

            sessoesDoRepo.forEach(sDoRepo -> {
                boolean idDiferente = !sDoRepo.getIdSessao().equals(sAntes.getIdSessao());
                if (idDiferente) {
                    assertFalse(sDoRepoEqualsBodySessao(sDoRepo, bodySessao), "Outra sessão possui os dados atualizados");
                } else {
                    assertTrue(sDoRepoEqualsBodySessao(sDoRepo, bodySessao), "A sessão do repo com o ID esperado não possui todas as informações atualizadas");
                }
            });

            gruposDoRepo = grupoDeEstudoRepository.findAll();
            assertEquals(1, gruposDoRepo.size(), "Atualizar sessão deletou algum grupo");
            gruposDoRepo.forEach(g -> {
                g.getSessoes().forEach(sDoGrupo -> {
                    assertFalse(sDoGrupoEqualsSAntes(sDoGrupo, sAntes), "Foi possível recuperar a sessão com dados de antes da atualização na lista de sessões do grupo");
                });
            });

            estudantesDoRepo = estudanteRepository.findAll();
            assertEquals(11, estudantesDoRepo.size(), "Atualizar sessão deletou algum estudante");
            estudantesDoRepo.forEach(e -> {
                e.getStudySessions().forEach(sDoEstudante -> {
                    assertFalse(sDoEstudanteEqualsSAntes(sDoEstudante, sAntes), "Foi possível recuperar a ses~sao com dados de antes da atualização na lista de sessões do estudante");
                });
            });
        }
    }
}
