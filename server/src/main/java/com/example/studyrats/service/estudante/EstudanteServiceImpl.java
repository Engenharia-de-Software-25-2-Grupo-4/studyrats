package com.example.studyrats.service.estudante;

import com.example.studyrats.exceptions.UIDJaCadastrado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.studyrats.dto.estudante.EstudantePostPutRequestDTO;
import com.example.studyrats.dto.estudante.EstudanteResponseDTO;
import com.example.studyrats.exceptions.EmailJaCadastrado;
import com.example.studyrats.exceptions.PasswordMismatchException;
import com.example.studyrats.exceptions.EstudanteNaoEncontrado;
import com.example.studyrats.model.Estudante;
import com.example.studyrats.repository.EstudanteRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;

@Service
public class EstudanteServiceImpl implements EstudanteService {

    @Autowired
    EstudanteRepository estudanteRepository;
    @Autowired
    ModelMapper modelMapper;

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
    public void excluir(String firebaseUid) {
        if (!estudanteRepository.existsById(firebaseUid)) {
            throw new EstudanteNaoEncontrado();
        }
        estudanteRepository.deleteById(firebaseUid);
    }
}