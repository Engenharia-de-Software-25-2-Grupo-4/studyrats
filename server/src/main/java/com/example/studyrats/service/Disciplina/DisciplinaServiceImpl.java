package com.example.studyrats.service.Disciplina;

import com.example.studyrats.dto.Disciplina.DisciplinaResponseDTO;
import com.example.studyrats.dto.SessaoDeEstudo.SessaoDeEstudoResponseDTO;
import com.example.studyrats.repository.DisciplinaRepository;
import com.example.studyrats.service.SessaoDeEstudo.SessaoDeEstudoService;
import lombok.AllArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DisciplinaServiceImpl implements DisciplinaService {

    @Autowired
    private final DisciplinaRepository disciplinaRepository;
    @Autowired
    private final SessaoDeEstudoService sessaoDeEstudoService;
    @Autowired
    private final ModelMapper modelMapper;

    @Override
    public List<DisciplinaResponseDTO> listarDisciplinas() {
        return disciplinaRepository.findAll()
                .stream()
                .map(d -> modelMapper.map(d, DisciplinaResponseDTO.class))
                .toList();
    }
////////Não há validação de Usuario pois isso já é feito nos métodos de Sessão De Estudo utilizados
    @Override
    public List<DisciplinaResponseDTO> listarDisciplinasPorUsuario(String idUsuario) {
        List<SessaoDeEstudoResponseDTO> sessoesDoUsuario = sessaoDeEstudoService.listarSessaoDeEstudosPorUsuario(idUsuario);
        
        return sessoesDoUsuario.stream()
            .map(SessaoDeEstudoResponseDTO::getDisciplina)
            .distinct()
            .map(nome -> {
                DisciplinaResponseDTO dto = new DisciplinaResponseDTO();
                dto.setNome(nome);
                return dto;
            })
            .collect(Collectors.toList());
    }
            
    
    @Override 
    public List<DisciplinaResponseDTO> listarDisciplinasPorGrupo(UUID idGrupo, String idUsuario){
        List<SessaoDeEstudoResponseDTO> sessoesDoUsuario = sessaoDeEstudoService.listarSessaoDeEstudosPorGrupo(idGrupo, idUsuario);
        
        return sessoesDoUsuario.stream()
            .map(SessaoDeEstudoResponseDTO::getDisciplina)
            .distinct()
            .map(nome -> {
                DisciplinaResponseDTO dto = new DisciplinaResponseDTO();
                dto.setNome(nome);
                return dto;
            })
            .collect(Collectors.toList());
    }       

    @Override 
    public List<DisciplinaResponseDTO> listarDisciplinasPorUsuarioEmGrupo(String idUsuario, UUID idGrupo){
        List<SessaoDeEstudoResponseDTO> sessoesDoUsuario = sessaoDeEstudoService.listarSessaoDeEstudosPorUsuarioEmGrupo(idUsuario, idGrupo);
        
        return sessoesDoUsuario.stream()
            .map(SessaoDeEstudoResponseDTO::getDisciplina)
            .distinct()
            .map(nome -> {
                DisciplinaResponseDTO dto = new DisciplinaResponseDTO();
                dto.setNome(nome);
                return dto;
            })
            .collect(Collectors.toList());
    }  

    }