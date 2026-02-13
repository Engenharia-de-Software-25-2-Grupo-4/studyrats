package com.example.studyrats.service.GrupoDeEstudo;

import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.studyrats.dto.GrupoDeEstudo.GrupoDeEstudoPostPutRequestDTO;
import com.example.studyrats.dto.GrupoDeEstudo.GrupoDeEstudoResponseDTO;
import com.example.studyrats.dto.ConviteGrupo.ConvitePostRequestDTO;
import com.example.studyrats.exceptions.ConviteNaoEncontrado;
import com.example.studyrats.exceptions.EstudanteNaoEncontrado;
import com.example.studyrats.exceptions.GrupoNaoEncontrado;
import com.example.studyrats.model.ConviteGrupo;
import com.example.studyrats.model.GrupoDeEstudo;
import com.example.studyrats.model.MembroGrupo;
import com.example.studyrats.model.Estudante;
import com.example.studyrats.repository.ConviteGrupoRepository;
import com.example.studyrats.repository.GrupoDeEstudoRepository;
import com.example.studyrats.repository.MembroGrupoRepository;
import com.example.studyrats.repository.EstudanteRepository;
import com.example.studyrats.repository.SessaoDeEstudoRepository;

@Service
@Transactional
public class GrupoDeEstudoServiceImpl implements GrupoDeEstudoService {

    @Autowired
    private GrupoDeEstudoRepository grupoRepo;

    @Autowired
    private EstudanteRepository estudanteRepo;

    @Autowired
    private MembroGrupoRepository membroRepo;

    @Autowired
    private ConviteGrupoRepository conviteRepo;

    @Autowired
    private SessaoDeEstudoRepository sessaoRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public GrupoDeEstudoResponseDTO criarGrupo(GrupoDeEstudoPostPutRequestDTO dto, String uid) {
        Estudante estudante = estudanteRepo.findById(uid).orElseThrow(EstudanteNaoEncontrado::new);
        
        GrupoDeEstudo grupo = modelMapper.map(dto, GrupoDeEstudo.class);
        grupo.setAdmin(estudante);
        grupo = grupoRepo.save(grupo);

        MembroGrupo membro = new MembroGrupo();
        membro.setGrupo(grupo);
        membro.setEstudante(estudante);
        membro.setRole("ADMIN");
        membroRepo.save(membro);

        return modelMapper.map(grupo, GrupoDeEstudoResponseDTO.class);
    }

    @Override
    public GrupoDeEstudoResponseDTO buscarPorId(UUID id) {
        GrupoDeEstudo grupo = grupoRepo.findById(id).orElseThrow(GrupoNaoEncontrado::new);
        return modelMapper.map(grupo, GrupoDeEstudoResponseDTO.class);
    }

    @Override
    public GrupoDeEstudoResponseDTO atualizar(UUID id, GrupoDeEstudoPostPutRequestDTO dto, String uid) {
        GrupoDeEstudo grupo = grupoRepo.findById(id).orElseThrow(GrupoNaoEncontrado::new);
        
        if (!isAdmin(grupo, uid)) {
            throw new GrupoNaoEncontrado();
        }

        modelMapper.map(dto, grupo);
        grupo = grupoRepo.save(grupo);
        
        return modelMapper.map(grupo, GrupoDeEstudoResponseDTO.class);
    }

    @Override
    public void remover(UUID id, String uid) {
        GrupoDeEstudo grupo = grupoRepo.findById(id).orElseThrow(GrupoNaoEncontrado::new);
        
        if (!isAdmin(grupo, uid)) {
            throw new GrupoNaoEncontrado();
        }
        
        grupoRepo.delete(grupo);
    }

    @Override
    public List<GrupoDeEstudoResponseDTO> listarPorUsuario(String uid) {
        return grupoRepo.findByMembros_Estudante_FirebaseUid(uid)
            .stream()
            .map(g -> modelMapper.map(g, GrupoDeEstudoResponseDTO.class))
            .toList();
    }

    @Override
    public void convidar(UUID idGrupo, ConvitePostRequestDTO dto, String uid) {
        GrupoDeEstudo grupo = grupoRepo.findById(idGrupo).orElseThrow(GrupoNaoEncontrado::new);
        
        var opt = membroRepo.findByGrupo_IdAndEstudante_FirebaseUid(idGrupo, uid);
        if (opt.isEmpty()) {
            throw new GrupoNaoEncontrado();
        }
        
        Estudante convidante = estudanteRepo.findById(uid).orElseThrow(EstudanteNaoEncontrado::new);

        ConviteGrupo convite = new ConviteGrupo();
        convite.setGrupo(grupo);
        convite.setConvidante(convidante);
        convite.setUidConvidado(dto.getUidConvidado());
        convite.setStatus("PENDING");
        conviteRepo.save(convite);
    }

    @Override
    public void aceitarConvite(UUID idConvite, String uid) {
        ConviteGrupo convite = conviteRepo.findById(idConvite).orElseThrow(ConviteNaoEncontrado::new);
        
        if (!invitedMatches(convite, uid)) {
            throw new ConviteNaoEncontrado();
        }
        
        convite.setStatus("ACCEPTED");
        conviteRepo.save(convite);

        Estudante estudante = estudanteRepo.findById(uid).orElseThrow(EstudanteNaoEncontrado::new);
        
        MembroGrupo membro = new MembroGrupo();
        membro.setGrupo(convite.getGrupo());
        membro.setEstudante(estudante);
        membro.setRole("MEMBER");
        membroRepo.save(membro);
    }

    @Override
    public List<?> listarConvites(String uid) {
        return conviteRepo.findByUidConvidado(uid);
    }

    @Override
    public void removerCheckinInvalido(UUID idGrupo, UUID idSessao, String uid) {
        GrupoDeEstudo grupo = grupoRepo.findById(idGrupo).orElseThrow(GrupoNaoEncontrado::new);
        var sessao = sessaoRepo.findById(idSessao).orElseThrow(() -> new RuntimeException("Sessao nao encontrada"));
        
        if (!idGrupo.equals(sessao.getIdGrupo())) {
            throw new RuntimeException("Sessao nao pertence ao grupo");
        }
        
        boolean isAdmin = grupo.getAdmin() != null && grupo.getAdmin().getFirebaseUid().equals(uid);
        boolean isCriador = sessao.getCriador() != null && sessao.getCriador().getFirebaseUid().equals(uid);
        
        if (!isAdmin && !isCriador) {
            throw new GrupoNaoEncontrado();
        }
        
        sessaoRepo.delete(sessao);
    }

    private boolean isAdmin(GrupoDeEstudo grupo, String uid) {
        return grupo.getAdmin() != null && grupo.getAdmin().getFirebaseUid().equals(uid);
    }

    private boolean invitedMatches(ConviteGrupo convite, String uid) {
        return convite.getUidConvidado() != null && convite.getUidConvidado().equals(uid) && "PENDING".equals(convite.getStatus());
    }
}