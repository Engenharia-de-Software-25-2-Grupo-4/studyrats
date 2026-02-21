package com.example.studyrats.service.estudante;

import com.example.studyrats.exceptions.UIDJaCadastrado;
import com.example.studyrats.model.GrupoDeEstudo;
import com.example.studyrats.model.MembroGrupo;
import com.example.studyrats.model.SessaoDeEstudo;
import com.example.studyrats.repository.ComentarioSessaoRepository;
import com.example.studyrats.repository.ConviteGrupoRepository;
import com.example.studyrats.repository.GrupoDeEstudoRepository;
import com.example.studyrats.repository.MembroGrupoRepository;
import com.example.studyrats.repository.ReacaoSessaoRepository;
import com.example.studyrats.repository.SessaoDeEstudoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.studyrats.dto.estudante.EstudantePostPutRequestDTO;
import com.example.studyrats.dto.estudante.EstudanteResponseDTO;
import com.example.studyrats.exceptions.EmailJaCadastrado;
import com.example.studyrats.exceptions.EstudanteNaoEncontrado;
import com.example.studyrats.model.Estudante;
import com.example.studyrats.repository.EstudanteRepository;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;

@Service
public class EstudanteServiceImpl implements EstudanteService {

    @Autowired
    EstudanteRepository estudanteRepository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    MembroGrupoRepository membroGrupoRepository;
    @Autowired
    SessaoDeEstudoRepository sessaoDeEstudoRepository;
    @Autowired
    GrupoDeEstudoRepository grupoDeEstudoRepository;
    @Autowired
    ConviteGrupoRepository conviteGrupoRepository;
    @Autowired
    ComentarioSessaoRepository comentarioSessaoRepository;
    @Autowired
    ReacaoSessaoRepository reacaoSessaoRepository;

    @Override
    public EstudanteResponseDTO criar(EstudantePostPutRequestDTO dto, String uid) {
        if (estudanteRepository.existsByEmail(dto.getEmail())) {
            throw new EmailJaCadastrado();
        } else if (estudanteRepository.existsById(uid)) {
            throw new UIDJaCadastrado();
        }

        Estudante estudante = modelMapper.map(dto, Estudante.class);
        estudante.setFirebaseUid(uid);
        
        Estudante salvo = estudanteRepository.save(estudante);
        return modelMapper.map(salvo, EstudanteResponseDTO.class);
    }

    @Override
    public List<EstudanteResponseDTO> listarTodos() {
        return estudanteRepository.findAll().stream()
                .map(s -> modelMapper.map(s, EstudanteResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public EstudanteResponseDTO buscarPorId(String firebaseUid) {
        Estudante estudante = estudanteRepository.findById(firebaseUid)
                .orElseThrow(EstudanteNaoEncontrado::new);
        
        return modelMapper.map(estudante, EstudanteResponseDTO.class);
    }

    @Override
    public EstudanteResponseDTO atualizar(String firebaseUid, EstudantePostPutRequestDTO dto) {
        if (!estudanteRepository.existsById(firebaseUid)) {
            throw new EstudanteNaoEncontrado();
        }

        Estudante estudante = modelMapper.map(dto, Estudante.class);
        estudante.setFirebaseUid(firebaseUid);
        
        Estudante atualizado = estudanteRepository.save(estudante);
        return modelMapper.map(atualizado, EstudanteResponseDTO.class);
    }

    @Override
    @Transactional
    public void excluir(String firebaseUid) {
        Estudante estudante = estudanteRepository.findById(firebaseUid)
                .orElseThrow(EstudanteNaoEncontrado::new);

        reacaoSessaoRepository.deleteByAutor_FirebaseUid(firebaseUid);
        comentarioSessaoRepository.deleteByAutor_FirebaseUid(firebaseUid);
        conviteGrupoRepository.deleteByCriador_FirebaseUid(firebaseUid);

        List<GrupoDeEstudo> gruposAdministradosPorEstudanteDeletado = grupoDeEstudoRepository.findByAdmin_FirebaseUid(firebaseUid);
        for (GrupoDeEstudo grupo : gruposAdministradosPorEstudanteDeletado) {
            conviteGrupoRepository.deleteByGrupo_Id(grupo.getId());
            grupoDeEstudoRepository.delete(grupo);
        }

        List<SessaoDeEstudo> sessoesCriadasPorEstudanteDeletado = sessaoDeEstudoRepository.findByCriador_FirebaseUid(firebaseUid);
        for (SessaoDeEstudo sessao : sessoesCriadasPorEstudanteDeletado) {
            reacaoSessaoRepository.deleteBySessaoDeEstudoIdSessao(sessao.getIdSessao());
            comentarioSessaoRepository.deleteBySessaoDeEstudoIdSessao(sessao.getIdSessao());
            sessaoDeEstudoRepository.delete(sessao);
        }

        List<MembroGrupo> vinculosEmMembros = membroGrupoRepository.findByEstudante_FirebaseUid(firebaseUid);
        if (!vinculosEmMembros.isEmpty()) {
            membroGrupoRepository.deleteAll(vinculosEmMembros);
        }

        estudanteRepository.delete(estudante);
    }
}
