package com.example.studyrats.service.Disciplina;

import com.example.studyrats.dto.Disciplina.DisciplinaResponseDTO;
import com.example.studyrats.repository.DisciplinaRepository;
import lombok.AllArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DisciplinaServiceImpl implements DisciplinaService {

    @Autowired
    private final DisciplinaRepository disciplinaRepository;
    @Autowired
    private final ModelMapper modelMapper;

    @Override
    public List<DisciplinaResponseDTO> listarDisciplinas() {
        return disciplinaRepository.findAll()
                .stream()
                .map(d -> modelMapper.map(d, DisciplinaResponseDTO.class))
                .toList();
    }
}