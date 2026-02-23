package com.example.studyrats.Relacoes.estudante;

import com.example.studyrats.RequisicoesMock;
import com.example.studyrats.dto.GrupoDeEstudo.GrupoDeEstudoPostPutRequestDTO;
import com.example.studyrats.dto.GrupoDeEstudo.GrupoDeEstudoResponseDTO;
import com.example.studyrats.dto.estudante.EstudantePostPutRequestDTO;
import com.example.studyrats.model.GrupoDeEstudo;
import com.example.studyrats.repository.GrupoDeEstudoRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
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

    private RequisicoesMock requisitorEstudante;
    private RequisicoesMock requisitorGrupo;

    private String tokenEstudantePrincipal = "tokenE1";
    private String tokenEstudanteSecuntadio = "tokenE2";

    @BeforeEach
    void setup() {
        String baseUrl = "/estudantes";
        requisitorEstudante = new RequisicoesMock(driver, baseUrl);

        baseUrl = "/grupos";
        requisitorGrupo = new RequisicoesMock(driver, baseUrl);
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

    private List<GrupoDeEstudoResponseDTO> setup2EstudantesEmNGrupos() throws Exception{
        int n = 10;
        String token;
        EstudantePostPutRequestDTO body;
        GrupoDeEstudoPostPutRequestDTO bodyGrupo;
        GrupoDeEstudoResponseDTO grupo;
        List<GrupoDeEstudoResponseDTO> grupos = new ArrayList<>();
        setarToken(tokenEstudantePrincipal);
        body = new EstudantePostPutRequestDTO(randomChars(), randomChars());
        requisitorEstudante.performPostCreated(body, tokenEstudantePrincipal);

        setarToken(tokenEstudanteSecuntadio);
        body = new EstudantePostPutRequestDTO(randomChars(), randomChars());
        requisitorEstudante.performPostCreated(body, tokenEstudanteSecuntadio);

        for (int i = 0; i < n; i++) {
            token = randomChars();
            setarToken(token);
            body = new EstudantePostPutRequestDTO(randomChars(), randomChars());
            requisitorEstudante.performPostCreated(body, token);

            bodyGrupo = new GrupoDeEstudoPostPutRequestDTO(randomChars(), randomChars());
            grupo = requisitorGrupo.performPostCreated(GrupoDeEstudoResponseDTO.class, bodyGrupo, token);
            grupos.add(grupo);

            String convite = requisitorGrupo.performPostCreatedStringReturn(grupo.getId().toString()+"/convites/gerar", token);
            requisitorGrupo.performPostOk("convites/"+convite+"/entrar", tokenEstudantePrincipal);
            requisitorGrupo.performPostOk("convites/"+convite+"/entrar", tokenEstudanteSecuntadio);
        }
        return grupos;
    }

    @Test @Transactional
    @DisplayName("Deletar estudante mantém grupos e remove membros")
    void deletarEstudanteMantemGrupos() throws Exception {
        List<GrupoDeEstudoResponseDTO> gruposCriados = setup2EstudantesEmNGrupos();
        setarToken(tokenEstudantePrincipal);
        requisitorEstudante.performDeleteNoContent(tokenEstudantePrincipal);
        List<GrupoDeEstudo> gruposRecuperados = grupoDeEstudoRepository.findAll();
        assertEquals(gruposCriados.size(), gruposRecuperados.size());
        for (GrupoDeEstudo grupo : gruposRecuperados) {
            if (!grupo.getMembros().stream().noneMatch(membroGrupo -> membroGrupo.getEstudante().getFirebaseUid().equals(tokenEstudantePrincipal))) {
                fail("O estudante foi deletado do sistema mas ainda consta como membro");
                grupo.getMembros().forEach(membroGrupo -> System.out.println(membroGrupo.getEstudante().getFirebaseUid()));
            }
        }
    }
}
