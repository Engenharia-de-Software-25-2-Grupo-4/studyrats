package com.example.studyrats.service.SessaoDeEstudo;

import com.example.studyrats.exceptions.EstudanteNaoEncontrado;
import com.example.studyrats.exceptions.GrupoNaoEncontrado;
import com.example.studyrats.model.MembroGrupo;
import org.springframework.stereotype.Service;

import com.example.studyrats.dto.SessaoDeEstudo.*;
import com.example.studyrats.model.Disciplina;
import com.example.studyrats.model.Topico;
import com.example.studyrats.model.Estudante;
import com.example.studyrats.model.GrupoDeEstudo;
import com.example.studyrats.model.SessaoDeEstudo;
import com.example.studyrats.repository.EstudanteRepository;
import com.example.studyrats.repository.GrupoDeEstudoRepository;
import com.example.studyrats.repository.MembroGrupoRepository;
import com.example.studyrats.repository.ComentarioSessaoRepository;
import com.example.studyrats.repository.DisciplinaRepository;
import com.example.studyrats.repository.ReacaoSessaoRepository;
import com.example.studyrats.repository.SessaoDeEstudoRepository;
import com.example.studyrats.repository.TopicoRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import com.example.studyrats.exceptions.SessaoDeEstudoNaoEncontrado;
import com.example.studyrats.exceptions.UsuarioNaoFazParteDoGrupoException;

import org.modelmapper.ModelMapper;

@Service
@Transactional
public class SessaoDeEstudoServiceImpl implements SessaoDeEstudoService {

    @Autowired
    public SessaoDeEstudoRepository sessaoDeEstudoRepository;
    @Autowired
    public EstudanteRepository studentRepository;
    @Autowired 
    public GrupoDeEstudoRepository grupoDeEstudoRepository;
    @Autowired
    public DisciplinaRepository disciplinaRepository;
    @Autowired
    public TopicoRepository topicoRepository;
    @Autowired
    public MembroGrupoRepository membroGrupoRepository;
    @Autowired
    public ComentarioSessaoRepository comentarioSessaoRepository;
    @Autowired
    public ReacaoSessaoRepository reacaoSessaoRepository;
    @Autowired
    public ModelMapper modelMapper;

    @Override
    public SessaoDeEstudoResponseDTO criarSessaoDeEstudos(UUID idGrupo, String idUsuario, SessaoDeEstudoPostPutRequestDTO sessaoDeEstudoPostPutRequestDTO) {
        Estudante student = studentRepository.findById(idUsuario).orElseThrow(EstudanteNaoEncontrado::new);
        GrupoDeEstudo grupo = grupoDeEstudoRepository.findById(idGrupo).orElseThrow(GrupoNaoEncontrado::new);
        MembroGrupo membro = membroGrupoRepository.findByGrupo_IdAndEstudante_FirebaseUid(idGrupo, idUsuario)
                .orElseThrow(UsuarioNaoFazParteDoGrupoException::new);
        String nomeDisciplina = sessaoDeEstudoPostPutRequestDTO.getDisciplina().toUpperCase();
        String nomeTopico = sessaoDeEstudoPostPutRequestDTO.getTopico().toUpperCase();

        disciplinaRepository.findByNome(nomeDisciplina).orElseGet(() -> disciplinaRepository.save(new Disciplina(nomeDisciplina.toUpperCase())));

        topicoRepository.findByNome(nomeTopico).orElseGet(() -> topicoRepository.save(new Topico(nomeTopico.toUpperCase())));

        SessaoDeEstudo sessaoDeEstudo = modelMapper.map(sessaoDeEstudoPostPutRequestDTO, SessaoDeEstudo.class);
        sessaoDeEstudo.setCriador(student);
        sessaoDeEstudo.setGrupoDeEstudo(grupo);
        sessaoDeEstudo.setDisciplina(nomeDisciplina);
        sessaoDeEstudo.setTopico(nomeTopico);
        sessaoDeEstudo = sessaoDeEstudoRepository.save(sessaoDeEstudo);

        membro.setQuantidadeCheckins(membro.getQuantidadeCheckins() + 1);
        membroGrupoRepository.save(membro);

        return toResponseDTO(sessaoDeEstudo, idUsuario);
    }

    @Override
    public SessaoDeEstudoResponseDTO visualizarSessaoDeEstudosPorId(UUID idSessao, String idUsuario) {
        SessaoDeEstudo sessaoDeEstudo = sessaoDeEstudoRepository.findById(idSessao).orElseThrow(SessaoDeEstudoNaoEncontrado::new);
        //validarCriador(sessaoDeEstudo, idUsuario);
        validaMembro(sessaoDeEstudo.getGrupoDeEstudo().getId(), idUsuario);
        return toResponseDTO(sessaoDeEstudo, idUsuario);
    }

    @Override
    public void removerSessaoDeEstudosPorId(UUID idSessao, String idUsuario) {
        SessaoDeEstudo sessaoDeEstudo = sessaoDeEstudoRepository.findById(idSessao).orElseThrow(SessaoDeEstudoNaoEncontrado::new);
        validarCriador(sessaoDeEstudo, idUsuario);
        validarCriador(sessaoDeEstudo, idUsuario);

        MembroGrupo membro = membroGrupoRepository.findByGrupo_IdAndEstudante_FirebaseUid(
                        sessaoDeEstudo.getGrupoDeEstudo().getId(), idUsuario)
                .orElseThrow(UsuarioNaoFazParteDoGrupoException::new);

        if (membro.getQuantidadeCheckins() > 0) {
            membro.setQuantidadeCheckins(membro.getQuantidadeCheckins() - 1);
            membroGrupoRepository.save(membro);
        }
        sessaoDeEstudoRepository.delete(sessaoDeEstudo); 
    }

    @Override
    public SessaoDeEstudoResponseDTO atualizarSessaoDeEstudosPorId(UUID idSessao, String idUsuario, SessaoDeEstudoPostPutRequestDTO sessaoDeEstudoPostPutRequestDTO) {
        SessaoDeEstudo sessaoDeEstudo = sessaoDeEstudoRepository.findById(idSessao).orElseThrow(SessaoDeEstudoNaoEncontrado::new);
        validarCriador(sessaoDeEstudo, idUsuario);

        modelMapper.map(sessaoDeEstudoPostPutRequestDTO, sessaoDeEstudo);
        sessaoDeEstudo = sessaoDeEstudoRepository.save(sessaoDeEstudo);
        return toResponseDTO(sessaoDeEstudo, idUsuario);
    }

    ////// SÓ FAZ SENTIDO SE HOUVER, NO PRÓPRIO PERFIL DO USUARIO, VIZUALIZAÇÃO DAS PRÓPRIAS SESSÕES. O ACESSO A SESSÕES É FEITO ATRAVÉS DO GRUPO!
    @Override
    public List<SessaoDeEstudoResponseDTO> listarSessaoDeEstudosPorUsuario(String idUsuario) {
        return sessaoDeEstudoRepository.findByCriador_FirebaseUid(idUsuario)
            .stream()
            .map(session -> toResponseDTO(session, idUsuario))
            .toList();
    }

    // filtra sessões do grupo por usuario
    @Override
    public List<SessaoDeEstudoResponseDTO> listarSessaoDeEstudosPorUsuarioEmGrupo(String idUsuario, UUID idGrupo) {
        validaMembro(idGrupo, idUsuario);
        List<SessaoDeEstudoResponseDTO> sessions = sessaoDeEstudoRepository.findByGrupoDeEstudo_IdAndCriador_FirebaseUid(idGrupo, idUsuario)
            .stream()
            .map(session -> toResponseDTO(session, idUsuario))
            .toList();
        return sessions;
    }

    // filtra sessões do grupo por disciplina
    @Override
    public List<SessaoDeEstudoResponseDTO> listarSessaoDeEstudosPorDisciplinaEmGrupo(String disciplina, UUID idGrupo, String idUsuario) {
        validaMembro(idGrupo, idUsuario);
        List<SessaoDeEstudoResponseDTO> sessions = sessaoDeEstudoRepository.findByGrupoDeEstudo_IdAndDisciplina(idGrupo, disciplina.toUpperCase())
            .stream()
            .map(session -> toResponseDTO(session, idUsuario))
            .toList();
        return sessions; 
    }

    // filtra sessões do grupo por topico
    @Override
    public List<SessaoDeEstudoResponseDTO> listarSessaoDeEstudosPorTopicoEmGrupo(String topico, UUID idGrupo, String idUsuario) {
        validaMembro(idGrupo, idUsuario);
        List<SessaoDeEstudoResponseDTO> sessions = sessaoDeEstudoRepository.findByGrupoDeEstudo_IdAndTopico(idGrupo, topico)
            .stream()
            .map(session -> toResponseDTO(session, idUsuario))
            .toList();
        return sessions;
    }

    // retorna todas as sessões de um grupo. questionamento, os grupos são todos públicos? se sim, não há porque validar
    @Override
    public List<SessaoDeEstudoResponseDTO> listarSessaoDeEstudosPorGrupo(UUID idGrupo, String idUsuario) { 
        validaMembro(idGrupo, idUsuario);
        List<SessaoDeEstudoResponseDTO> sessions = sessaoDeEstudoRepository.findByGrupoDeEstudo_Id(idGrupo)
            .stream()
            .map(session -> toResponseDTO(session, idUsuario))
            .toList();
        return sessions; 
    } 

    // retorna todas as sessões de um grupo ordenadas em ordem cronológica para o feed
    @Override
    public List<SessaoDeEstudoResponseDTO> listarSessaoDeEstudosPorGrupoCronologicamente(UUID idGrupo, String idUsuario) { 
        validaMembro(idGrupo, idUsuario);
        List<SessaoDeEstudoResponseDTO> sessions = sessaoDeEstudoRepository.findByGrupoDeEstudo_Id(idGrupo)
            .stream()
            .sorted(Comparator.comparing(SessaoDeEstudo::getHorarioInicio))
            .map(session -> toResponseDTO(session, idUsuario))
            .toList();
        return sessions;
    }     

////// OS DOIS MÉTODOS A SEGUIR SÓ FAZEM SENTIDO SE HOUVER, NO PRÓPRIO PERFIL DO USUARIO, FILTRAGEM DAS PRÓPRIAS SESSÕES. O ACESSO A SESSÃO É FEITO ATRAVÉS DO GRUPO!
    @Override
    public List<SessaoDeEstudoResponseDTO> listarSessaoDeEstudosDeUsuarioPorDisciplina(String idUsuario, String disciplina) { 
        List<SessaoDeEstudoResponseDTO> sessions = sessaoDeEstudoRepository.findByCriador_FirebaseUidAndDisciplina(idUsuario, disciplina.toUpperCase())
            .stream()
            .map(session -> toResponseDTO(session, idUsuario))
            .toList();
        return sessions;
    }

    @Override
    public List<SessaoDeEstudoResponseDTO> listarSessaoDeEstudosDeUsuarioPorTopico(String idUsuario, String topico) { 
        List<SessaoDeEstudoResponseDTO> sessions = sessaoDeEstudoRepository.findByCriador_FirebaseUidAndTopico(idUsuario, topico)
            .stream()
            .map(session -> toResponseDTO(session, idUsuario))
            .toList();
        return sessions;
    }

    private SessaoDeEstudoResponseDTO toResponseDTO(SessaoDeEstudo sessao, String idUsuario) {
        UUID idSessao = sessao.getIdSessao();
        Long totalComentarios = comentarioSessaoRepository.countBySessaoDeEstudoIdSessao(idSessao);
        Long totalReacoes = reacaoSessaoRepository.countBySessaoDeEstudoIdSessao(idSessao);
        boolean reagiu = reacaoSessaoRepository.existsBySessaoDeEstudoIdSessaoAndAutorFirebaseUid(idSessao, idUsuario);

        return SessaoDeEstudoResponseDTO.builder()
            .idSessao(idSessao)
            .idCriador(sessao.getCriador().getFirebaseUid())
            .nomeCriador(sessao.getCriador().getNome())
            .titulo(sessao.getTitulo())
            .descricao(sessao.getDescricao())
            .horarioInicio(sessao.getHorarioInicio())
            .duracaoMinutos(sessao.getDuracaoMinutos())
            .urlFoto(sessao.getUrlFoto())
            .disciplina(sessao.getDisciplina())
            .topico(sessao.getTopico())
            .totalComentarios(totalComentarios)
            .totalReacoes(totalReacoes)
            .reagiu(reagiu)
            .build();
    }

    private void validarCriador(SessaoDeEstudo sessaoDeEstudo, String idUsuario) {
        if (sessaoDeEstudo.getCriador() == null || !sessaoDeEstudo.getCriador().getFirebaseUid().equals(idUsuario)) {
            throw new SessaoDeEstudoNaoEncontrado();
        }
    }

    private void validaMembro(UUID idGrupo, String idUsuario){
        if (!membroGrupoRepository.existsByGrupo_IdAndEstudante_FirebaseUid(idGrupo, idUsuario)) {
            throw new UsuarioNaoFazParteDoGrupoException(); 
        }
        return;
    }
}
