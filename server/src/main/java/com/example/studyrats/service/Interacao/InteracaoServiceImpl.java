package com.example.studyrats.service.Interacao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.studyrats.dto.Interacao.Comentario.ComentarioSessaoPostPutRequestDTO;
import com.example.studyrats.dto.Interacao.Comentario.ComentarioSessaoResponseDTO;
import com.example.studyrats.dto.Interacao.Reacao.ReacaoSessaoResponseDTO;
import com.example.studyrats.exceptions.ComentarioNaoEncontradoException;
import com.example.studyrats.exceptions.EstudanteNaoEncontrado;
import com.example.studyrats.exceptions.SessaoDeEstudoNaoEncontrado;
import com.example.studyrats.exceptions.UsuarioNaoFazParteDoGrupoException;
import com.example.studyrats.exceptions.UsuarioNaoTemPermissaoParaDeletarComentarioSessaoException;
import com.example.studyrats.model.ComentarioSessao;
import com.example.studyrats.model.Estudante;
import com.example.studyrats.model.ReacaoSessao;
import com.example.studyrats.model.SessaoDeEstudo;
import com.example.studyrats.repository.ComentarioSessaoRepository;
import com.example.studyrats.repository.EstudanteRepository;
import com.example.studyrats.repository.MembroGrupoRepository;
import com.example.studyrats.repository.ReacaoSessaoRepository;
import com.example.studyrats.repository.SessaoDeEstudoRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class InteracaoServiceImpl implements InteracaoService {

    @Autowired
    private ReacaoSessaoRepository reacaoSessaoRepository; 

    @Autowired
    private ComentarioSessaoRepository comentarioSessaoRepository;

    @Autowired
    private SessaoDeEstudoRepository sessaoDeEstudoRepository;

    @Autowired
    private EstudanteRepository estudanteRepository;

    @Autowired
    private MembroGrupoRepository membroGrupoRepository;


    @Override
    public ComentarioSessaoResponseDTO criarComentarioSessao(UUID idSessao, ComentarioSessaoPostPutRequestDTO comentarioSessaoPostPutRequestDTO, String firebaseUid) { 
        Estudante autor = estudanteRepository.findById(firebaseUid).orElseThrow(EstudanteNaoEncontrado::new);
        SessaoDeEstudo sessao = sessaoDeEstudoRepository.findById(idSessao).orElseThrow(SessaoDeEstudoNaoEncontrado::new); 

        validarMembroDoGrupo(sessao, firebaseUid);

        ComentarioSessao comentario = ComentarioSessao.builder()
                .sessaoDeEstudo(sessao)
                .autor(autor)
                .texto(comentarioSessaoPostPutRequestDTO.getTexto())
                .horarioComentario(LocalDateTime.now())
                .build();

        ComentarioSessao salvo = comentarioSessaoRepository.save(comentario);

        return ComentarioSessaoResponseDTO.builder()
                .id_comentario(salvo.getId_comentario())
                .firebaseUid_autor(autor.getFirebaseUid())
                .nome_autor(autor.getNome())
                .texto(salvo.getTexto())
                .horarioComentario(salvo.getHorarioComentario())
                .build();  
    }

    @Override
    public List<ComentarioSessaoResponseDTO> listarComentariosSessao(UUID idSessao, String firebaseUid) {
        estudanteRepository.findById(firebaseUid).orElseThrow(EstudanteNaoEncontrado::new);
        SessaoDeEstudo sessao = sessaoDeEstudoRepository.findById(idSessao).orElseThrow(SessaoDeEstudoNaoEncontrado::new);
        validarMembroDoGrupo(sessao, firebaseUid);

        List<ComentarioSessao> comentarios = comentarioSessaoRepository.findBySessaoDeEstudoId_sessaoOrderByHorarioComentarioAsc(idSessao);

        return comentarios.stream().map(comentario -> ComentarioSessaoResponseDTO.builder()
                .id_comentario(comentario.getId_comentario())
                .firebaseUid_autor(comentario.getAutor().getFirebaseUid())
                .nome_autor(comentario.getAutor().getNome())
                .texto(comentario.getTexto())
                .horarioComentario(comentario.getHorarioComentario())
                .build()).toList();
    }

    @Override
    public void deletarComentarioSessao(UUID idComentario, String firebaseUid) {
        estudanteRepository.findById(firebaseUid).orElseThrow(EstudanteNaoEncontrado::new);
        ComentarioSessao comentario = comentarioSessaoRepository.findById(idComentario)
                .orElseThrow(ComentarioNaoEncontradoException::new);

        SessaoDeEstudo sessao = comentario.getSessaoDeEstudo();

        boolean ehAutor = comentario.getAutor().getFirebaseUid().equals(firebaseUid);
        boolean ehAdminDoGrupo = sessao.getGrupoDeEstudo().getAdmin() != null
                && sessao.getGrupoDeEstudo().getAdmin().getFirebaseUid().equals(firebaseUid);

        if (!ehAutor && !ehAdminDoGrupo) {
            validarMembroDoGrupo(sessao, firebaseUid);
            throw new UsuarioNaoTemPermissaoParaDeletarComentarioSessaoException();
        }

        comentarioSessaoRepository.delete(comentario);
    }

    @Override
    public ReacaoSessaoResponseDTO reagirOuTirarReacaoSessao(UUID idSessao, String firebaseUid) {
        Estudante estudante = estudanteRepository.findById(firebaseUid).orElseThrow(EstudanteNaoEncontrado::new);
        SessaoDeEstudo sessao = sessaoDeEstudoRepository.findById(idSessao).orElseThrow(SessaoDeEstudoNaoEncontrado::new);
        validarMembroDoGrupo(sessao, firebaseUid);

        boolean reagiu = reacaoSessaoRepository.existsBySessaoDeEstudoId_sessaoAndAutorFirebaseUid(idSessao, firebaseUid);

        if (reagiu) {
            reacaoSessaoRepository.deleteBySessaoDeEstudoId_sessaoAndAutorFirebaseUid(idSessao, firebaseUid);
            reagiu = false;
        } else {
            ReacaoSessao reacao = ReacaoSessao.builder()
                    .sessaoDeEstudo(sessao)
                    .autor(estudante)
                    .horarioReacao(LocalDateTime.now())
                    .build();
            reacaoSessaoRepository.save(reacao);
            reagiu = true;
        }

        long totalReacoes = reacaoSessaoRepository.countBySessaoDeEstudoId_sessao(idSessao);

        return ReacaoSessaoResponseDTO.builder()
                .reagiu(reagiu)
                .totalReacoes(totalReacoes)
                .build();
    }

    @Override
    public ReacaoSessaoResponseDTO listarReacoesSessao(UUID idSessao, String firebaseUid) {
        estudanteRepository.findById(firebaseUid).orElseThrow(EstudanteNaoEncontrado::new);
        SessaoDeEstudo sessao = sessaoDeEstudoRepository.findById(idSessao).orElseThrow(SessaoDeEstudoNaoEncontrado::new);
        validarMembroDoGrupo(sessao, firebaseUid);

        boolean reagiu = reacaoSessaoRepository.existsBySessaoDeEstudoId_sessaoAndAutorFirebaseUid(idSessao, firebaseUid);
        long totalReacoes = reacaoSessaoRepository.countBySessaoDeEstudoId_sessao(idSessao);

        return ReacaoSessaoResponseDTO.builder()
                .reagiu(reagiu)
                .totalReacoes(totalReacoes)
                .build();
    }

    private void validarMembroDoGrupo(SessaoDeEstudo sessao, String firebaseUid) {
        UUID idGrupo = sessao.getGrupoDeEstudo().getId();
        if (!membroGrupoRepository.existsByGrupo_IdAndEstudante_FirebaseUid(idGrupo, firebaseUid)) {
            throw new UsuarioNaoFazParteDoGrupoException();
        }
    } 
}
