package com.example.studyrats.service.SessaoDeEstudo;

import org.springframework.stereotype.Service;

import com.example.studyrats.dto.SessaoDeEstudo.*;
import com.example.studyrats.model.Student;
import com.example.studyrats.model.SessaoDeEstudo;
import com.example.studyrats.repository.StudentRepository;
import com.example.studyrats.repository.SessaoDeEstudoRepository; 

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import com.example.studyrats.exceptions.AccessDeniedException;
import com.example.studyrats.exceptions.SessaoDeEstudoNotFoundException;
import com.example.studyrats.exceptions.StudentNotFoundException;

import org.modelmapper.ModelMapper;

@Service
@Transactional
public class SessaoDeEstudoServiceImpl implements SessaoDeEstudoService {

    @Autowired
    public SessaoDeEstudoRepository sessaoDeEstudoRepository; 
    
    @Autowired
    public StudentRepository studentRepository; 
    // @Autowired 
    // public StudyGroupRepository studyGroupRepository;
    @Autowired
    public ModelMapper modelMapper; 

    @Override
    public SessaoDeEstudoResponseDTO criarSessaoDeEstudos(UUID idGrupo, String idUsuario, SessaoDeEstudoPostPutRequestDTO sessaoDeEstudoPostPutRequestDTO) {
        Student student = studentRepository.findById(idUsuario).orElseThrow(() -> new StudentNotFoundException(idUsuario)); 
        //quando tiver crud de grupo, validar se o grupo existe e se o usuário faz parte dele 

        SessaoDeEstudo sessaoDeEstudo = modelMapper.map(sessaoDeEstudoPostPutRequestDTO, SessaoDeEstudo.class);
        sessaoDeEstudo.setCriador(student);
        sessaoDeEstudo.setIdGrupo(idGrupo); 

        sessaoDeEstudoRepository.save(sessaoDeEstudo);
        return modelMapper.map(sessaoDeEstudo, SessaoDeEstudoResponseDTO.class);
    }

    @Override
    public SessaoDeEstudoResponseDTO visualizarSessaoDeEstudosPorId(UUID idSessao, String idUsuario) {
        SessaoDeEstudo sessaoDeEstudo = sessaoDeEstudoRepository.findById(idSessao).orElseThrow(SessaoDeEstudoNotFoundException::new); 
        //quando tiver crud de grupo, validar se o grupo existe e se o usuário faz parte dele 
        validarCriador(sessaoDeEstudo, idUsuario); 

        return modelMapper.map(sessaoDeEstudo, SessaoDeEstudoResponseDTO.class);
    }

    @Override
    public void removerSessaoDeEstudosPorId(UUID idSessao, String idUsuario) {
        SessaoDeEstudo sessaoDeEstudo = sessaoDeEstudoRepository.findById(idSessao).orElseThrow(SessaoDeEstudoNotFoundException::new); 
        validarCriador(sessaoDeEstudo, idUsuario);

        sessaoDeEstudoRepository.delete(sessaoDeEstudo); 
    }

    @Override
    public SessaoDeEstudoResponseDTO atualizarSessaoDeEstudosPorId(UUID idSessao, String idUsuario, SessaoDeEstudoPostPutRequestDTO sessaoDeEstudoPostPutRequestDTO) {
        SessaoDeEstudo sessaoDeEstudo = sessaoDeEstudoRepository.findById(idSessao).orElseThrow(SessaoDeEstudoNotFoundException::new); 
        validarCriador(sessaoDeEstudo, idUsuario);

        modelMapper.map(sessaoDeEstudoPostPutRequestDTO, sessaoDeEstudo);
        sessaoDeEstudoRepository.save(sessaoDeEstudo);
        return modelMapper.map(sessaoDeEstudo, SessaoDeEstudoResponseDTO.class);
    }

    @Override
    public List<SessaoDeEstudoResponseDTO> listarSessaoDeEstudosPorUsuario(String idUsuario) {
        List<SessaoDeEstudoResponseDTO> sessions = sessaoDeEstudoRepository.findByCriador_Id(idUsuario)
            .stream()
            .map(session -> modelMapper.map(session, SessaoDeEstudoResponseDTO.class))
            .toList();
        return sessions;
    }

    @Override
    public List<SessaoDeEstudoResponseDTO> listarSessaoDeEstudosPorUsuarioEmGrupo(String idUsuario, UUID idGrupo) {
        // verificar se usuário é do grupo quando tiver crud de grupo
        List<SessaoDeEstudoResponseDTO> sessions = sessaoDeEstudoRepository.findByIdGrupoAndCriador_Id(idGrupo, idUsuario)
            .stream()
            .map(session -> modelMapper.map(session, SessaoDeEstudoResponseDTO.class))
            .toList();
        return sessions;
    }

    @Override
    public List<SessaoDeEstudoResponseDTO> listarSessaoDeEstudosPorDisciplinaEmGrupo(String disciplina, UUID idGrupo, String idUsuario) {
        // verificar se usuário é do grupo quando tiver crud de grupo
        List<SessaoDeEstudoResponseDTO> sessions = sessaoDeEstudoRepository.findByIdGrupoAndDisciplina(idGrupo, disciplina)
            .stream()
            .map(session -> modelMapper.map(session, SessaoDeEstudoResponseDTO.class))
            .toList();
        return sessions; 
    }

    @Override
    public List<SessaoDeEstudoResponseDTO> listarSessaoDeEstudosPorTopicoEmGrupo(String topico, UUID idGrupo, String idUsuario) {
        // verificar se usuário é do grupo quando tiver crud de grupo
        List<SessaoDeEstudoResponseDTO> sessions = sessaoDeEstudoRepository.findByIdGrupoAndTopico(idGrupo, topico)
            .stream()
            .map(session -> modelMapper.map(session, SessaoDeEstudoResponseDTO.class))
            .toList();
        return sessions;
    }

    @Override
    public List<SessaoDeEstudoResponseDTO> listarSessaoDeEstudosPorGrupo(UUID idGrupo, String idUsuario) { 
        // verificar se usuário é do grupo quando tiver crud de grupo
        List<SessaoDeEstudoResponseDTO> sessions = sessaoDeEstudoRepository.findByIdGrupo(idGrupo)
            .stream()
            .map(session -> modelMapper.map(session, SessaoDeEstudoResponseDTO.class))
            .toList();
        return sessions;
    } 

    // private StudySessionResponseDTO toDTO(StudySession session) {
    // return StudySessionResponseDTO.builder()
    //     .sessionId(session.getSessionId())
    //     .creatorId(session.getCreator().getId())  
    //     .creatorName(session.getCreator().getName())
    //     .title(session.getTitle())
    //     .build();
    // }

    @Override
    public List<SessaoDeEstudoResponseDTO> listarSessaoDeEstudosDeUsuarioPorDisciplina(String idUsuario, String disciplina) { 
        List<SessaoDeEstudoResponseDTO> sessions = sessaoDeEstudoRepository.findByCriador_IdAndDisciplina(idUsuario, disciplina)
            .stream()
            .map(session -> modelMapper.map(session, SessaoDeEstudoResponseDTO.class))
            .toList();
        return sessions;
    }

    @Override
    public List<SessaoDeEstudoResponseDTO> listarSessaoDeEstudosDeUsuarioPorTopico(String idUsuario, String topico) { 
        List<SessaoDeEstudoResponseDTO> sessions = sessaoDeEstudoRepository.findByCriador_IdAndTopico(idUsuario, topico)
            .stream()
            .map(session -> modelMapper.map(session, SessaoDeEstudoResponseDTO.class))
            .toList();
        return sessions;
    }
    
    private void validarCriador(SessaoDeEstudo sessaoDeEstudo, String idUsuario) {
        if (sessaoDeEstudo.getCriador() == null || !sessaoDeEstudo.getCriador().getId().equals(idUsuario)) {
            throw new AccessDeniedException();
        }
    }
} 
