package com.example.studyrats.service.SessaoDeEstudo;

import com.example.studyrats.exceptions.EstudanteNaoEncontrado;
import com.example.studyrats.exceptions.GrupoNaoEncontrado;
import org.springframework.stereotype.Service;

import com.example.studyrats.dto.SessaoDeEstudo.*;
import com.example.studyrats.model.Estudante;
import com.example.studyrats.model.GrupoDeEstudo;
import com.example.studyrats.model.SessaoDeEstudo;
import com.example.studyrats.repository.EstudanteRepository;
import com.example.studyrats.repository.GrupoDeEstudoRepository;
import com.example.studyrats.repository.MembroGrupoRepository;
import com.example.studyrats.repository.SessaoDeEstudoRepository; 

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

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
    public MembroGrupoRepository membroGrupoRepository;
    @Autowired
    public ModelMapper modelMapper; 

    @Override
    public SessaoDeEstudoResponseDTO criarSessaoDeEstudos(UUID idGrupo, String idUsuario, SessaoDeEstudoPostPutRequestDTO sessaoDeEstudoPostPutRequestDTO) {
        Estudante student = studentRepository.findById(idUsuario).orElseThrow(EstudanteNaoEncontrado::new);
        GrupoDeEstudo grupo = grupoDeEstudoRepository.findById(idGrupo).orElseThrow(GrupoNaoEncontrado::new);
        boolean usuarioMembroDoGrupo = membroGrupoRepository.existsByGrupo_IdAndEstudante_FirebaseUid(idGrupo, idUsuario);

        if (!usuarioMembroDoGrupo) {
            throw new UsuarioNaoFazParteDoGrupoException(); 
        }
        SessaoDeEstudo sessaoDeEstudo = modelMapper.map(sessaoDeEstudoPostPutRequestDTO, SessaoDeEstudo.class);
        sessaoDeEstudo.setCriador(student);
        sessaoDeEstudo.setGrupoDeEstudo(grupo);

        sessaoDeEstudo = sessaoDeEstudoRepository.save(sessaoDeEstudo);
        return modelMapper.map(sessaoDeEstudo, SessaoDeEstudoResponseDTO.class);
    }

    @Override
    public SessaoDeEstudoResponseDTO visualizarSessaoDeEstudosPorId(UUID idSessao, String idUsuario) {
        SessaoDeEstudo sessaoDeEstudo = sessaoDeEstudoRepository.findById(idSessao).orElseThrow(SessaoDeEstudoNaoEncontrado::new);
        validarCriador(sessaoDeEstudo, idUsuario); 

        return modelMapper.map(sessaoDeEstudo, SessaoDeEstudoResponseDTO.class);
    }

    @Override
    public void removerSessaoDeEstudosPorId(UUID idSessao, String idUsuario) {
        SessaoDeEstudo sessaoDeEstudo = sessaoDeEstudoRepository.findById(idSessao).orElseThrow(SessaoDeEstudoNaoEncontrado::new);
        validarCriador(sessaoDeEstudo, idUsuario);

        sessaoDeEstudoRepository.delete(sessaoDeEstudo); 
    }

    @Override
    public SessaoDeEstudoResponseDTO atualizarSessaoDeEstudosPorId(UUID idSessao, String idUsuario, SessaoDeEstudoPostPutRequestDTO sessaoDeEstudoPostPutRequestDTO) {
        SessaoDeEstudo sessaoDeEstudo = sessaoDeEstudoRepository.findById(idSessao).orElseThrow(SessaoDeEstudoNaoEncontrado::new);
        validarCriador(sessaoDeEstudo, idUsuario);

        modelMapper.map(sessaoDeEstudoPostPutRequestDTO, sessaoDeEstudo);
        sessaoDeEstudo = sessaoDeEstudoRepository.save(sessaoDeEstudo);
        return modelMapper.map(sessaoDeEstudo, SessaoDeEstudoResponseDTO.class);
    }

    @Override
    public List<SessaoDeEstudoResponseDTO> listarSessaoDeEstudosPorUsuario(String idUsuario) {
        return sessaoDeEstudoRepository.findByCriador_FirebaseUid(idUsuario)
            .stream()
            .map(session -> modelMapper.map(session, SessaoDeEstudoResponseDTO.class))
            .toList();
    }

    @Override
    public List<SessaoDeEstudoResponseDTO> listarSessaoDeEstudosPorUsuarioEmGrupo(String idUsuario, UUID idGrupo) {
        boolean usuarioMembroDoGrupo = membroGrupoRepository.existsByGrupo_IdAndEstudante_FirebaseUid(idGrupo, idUsuario);
        
        if (!usuarioMembroDoGrupo) {
            throw new UsuarioNaoFazParteDoGrupoException(); 
        }
        List<SessaoDeEstudoResponseDTO> sessions = sessaoDeEstudoRepository.findByGrupoDeEstudo_IdAndCriador_FirebaseUid(idGrupo, idUsuario)
            .stream()
            .map(session -> modelMapper.map(session, SessaoDeEstudoResponseDTO.class))
            .toList();
        return sessions;
    }

    @Override
    public List<SessaoDeEstudoResponseDTO> listarSessaoDeEstudosPorDisciplinaEmGrupo(String disciplina, UUID idGrupo, String idUsuario) {
        boolean usuarioMembroDoGrupo = membroGrupoRepository.existsByGrupo_IdAndEstudante_FirebaseUid(idGrupo, idUsuario);
        
        if (!usuarioMembroDoGrupo) {
            throw new UsuarioNaoFazParteDoGrupoException(); 
        }
        List<SessaoDeEstudoResponseDTO> sessions = sessaoDeEstudoRepository.findByGrupoDeEstudo_IdAndDisciplina(idGrupo, disciplina)
            .stream()
            .map(session -> modelMapper.map(session, SessaoDeEstudoResponseDTO.class))
            .toList();
        return sessions; 
    }

    @Override
    public List<SessaoDeEstudoResponseDTO> listarSessaoDeEstudosPorTopicoEmGrupo(String topico, UUID idGrupo, String idUsuario) {
        boolean usuarioMembroDoGrupo = membroGrupoRepository.existsByGrupo_IdAndEstudante_FirebaseUid(idGrupo, idUsuario);
        
        if (!usuarioMembroDoGrupo) {
            throw new UsuarioNaoFazParteDoGrupoException(); 
        }
        List<SessaoDeEstudoResponseDTO> sessions = sessaoDeEstudoRepository.findByGrupoDeEstudo_IdAndTopico(idGrupo, topico)
            .stream()
            .map(session -> modelMapper.map(session, SessaoDeEstudoResponseDTO.class))
            .toList();
        return sessions;
    }

    @Override
    public List<SessaoDeEstudoResponseDTO> listarSessaoDeEstudosPorGrupo(UUID idGrupo, String idUsuario) { 
        boolean usuarioMembroDoGrupo = membroGrupoRepository.existsByGrupo_IdAndEstudante_FirebaseUid(idGrupo, idUsuario);
        
        if (!usuarioMembroDoGrupo) {
            throw new UsuarioNaoFazParteDoGrupoException(); 
        }
        List<SessaoDeEstudoResponseDTO> sessions = sessaoDeEstudoRepository.findByGrupoDeEstudo_Id(idGrupo)
            .stream()
            .map(session -> modelMapper.map(session, SessaoDeEstudoResponseDTO.class))
            .toList();
        return sessions; 
    } 

    @Override
    public List<SessaoDeEstudoResponseDTO> listarSessaoDeEstudosDeUsuarioPorDisciplina(String idUsuario, String disciplina) { 
        List<SessaoDeEstudoResponseDTO> sessions = sessaoDeEstudoRepository.findByCriador_FirebaseUidAndDisciplina(idUsuario, disciplina)
            .stream()
            .map(session -> modelMapper.map(session, SessaoDeEstudoResponseDTO.class))
            .toList();
        return sessions;
    }

    @Override
    public List<SessaoDeEstudoResponseDTO> listarSessaoDeEstudosDeUsuarioPorTopico(String idUsuario, String topico) { 
        List<SessaoDeEstudoResponseDTO> sessions = sessaoDeEstudoRepository.findByCriador_FirebaseUidAndTopico(idUsuario, topico)
            .stream()
            .map(session -> modelMapper.map(session, SessaoDeEstudoResponseDTO.class))
            .toList();
        return sessions;
    }
    
    private void validarCriador(SessaoDeEstudo sessaoDeEstudo, String idUsuario) {
        if (sessaoDeEstudo.getCriador() == null || !sessaoDeEstudo.getCriador().getFirebaseUid().equals(idUsuario)) {
            throw new SessaoDeEstudoNaoEncontrado();
        }
    }
}
