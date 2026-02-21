package com.example.studyrats.service.Topico;

import com.example.studyrats.dto.Topico.TopicoResponseDTO;
import com.example.studyrats.repository.TopicoRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TopicoServiceImpl implements TopicoService {

    private final TopicoRepository topicoRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<TopicoResponseDTO> listarTopicos() {
        return topicoRepository.findAll()
                .stream()
                .map(d -> modelMapper.map(d, TopicoResponseDTO.class))
                .toList();
    }
}