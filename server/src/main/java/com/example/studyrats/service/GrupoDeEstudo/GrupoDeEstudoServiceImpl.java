package com.example.studyrats.service.GrupoDeEstudo;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Comparator;
import com.example.studyrats.dto.GrupoDeEstudo.RankingGrupoResponseDTO;
import com.example.studyrats.dto.SessaoDeEstudo.SessaoDeEstudoResponseDTO;
import com.example.studyrats.exceptions.*;
import com.example.studyrats.model.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.studyrats.dto.ConviteGrupo.ConviteResponseDTO;
import com.example.studyrats.dto.GrupoDeEstudo.GrupoDeEstudoPostPutRequestDTO;
import com.example.studyrats.dto.GrupoDeEstudo.GrupoDeEstudoResponseDTO;
import com.example.studyrats.repository.ConviteGrupoRepository;
import com.example.studyrats.repository.GrupoDeEstudoRepository;
import com.example.studyrats.repository.MembroGrupoRepository;
import com.example.studyrats.repository.EstudanteRepository;
import com.example.studyrats.repository.SessaoDeEstudoRepository;
import org.springframework.web.bind.MethodArgumentNotValidException;

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

        if (grupoRepo.existsByNomeAndAdmin_FirebaseUid(dto.getNome(), uid)) {
            throw new GrupoJaExisteException();
        }

        if (dto.getDataInicio().isAfter(dto.getDataFim())) {
            throw new DataInvalida();
        }

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
    public List<RankingGrupoResponseDTO> obterRanking(UUID idGrupo) {

        grupoRepo.findById(idGrupo)
                .orElseThrow(GrupoNaoEncontrado::new);

        List<MembroGrupo> membros =
                membroRepo.findByGrupo_IdOrderByQuantidadeCheckinsDesc(idGrupo);

        return membros.stream()
                .map(m -> new RankingGrupoResponseDTO(
                        m.getEstudante().getNome(),
                        m.getEstudante().getFirebaseUid(),
                        m.getQuantidadeCheckins()
                ))
                .toList();
    }



    @Override
    public List<GrupoDeEstudoResponseDTO> listarPorUsuario(String uid) {
        return grupoRepo.findByMembros_Estudante_FirebaseUid(uid)
            .stream()
            .map(g -> modelMapper.map(g, GrupoDeEstudoResponseDTO.class))
            .toList();
    }

    @Override
    public List<SessaoDeEstudoResponseDTO> listarSessoesDoGrupo(UUID idGrupo, String uidUsuario) {
        GrupoDeEstudo grupo = grupoRepo.findById(idGrupo).orElseThrow(GrupoNaoEncontrado::new);

        if (!membroRepo.existsByGrupo_IdAndEstudante_FirebaseUid(idGrupo, uidUsuario)) {
            throw new UsuarioNaoFazParteDoGrupoException();
        }

        return grupo.getSessoes().stream()
                .sorted(Comparator.comparing(SessaoDeEstudo::getHorarioInicio).reversed())
                .map(sessao -> modelMapper.map(sessao, SessaoDeEstudoResponseDTO.class))
                .toList();
    }

    @Override
    public String gerarConviteLink(UUID idGrupo, String uidAdmin) {
        GrupoDeEstudo grupo = grupoRepo.findById(idGrupo)
                .orElseThrow(GrupoNaoEncontrado::new);

        if (!grupo.getAdmin().getFirebaseUid().equals(uidAdmin)) {
            throw new UsuarioNaoAdmin();
        }

        Estudante admin = estudanteRepo.findById(uidAdmin)
                .orElseThrow(EstudanteNaoEncontrado::new);

        ConviteGrupo convite = new ConviteGrupo();
        convite.setGrupo(grupo);
        convite.setCriador(admin);
        convite.setToken(UUID.randomUUID().toString());
        convite.setDataExpiracao(LocalDateTime.now().plusHours(48));
        convite.setAtivo(true);

        conviteRepo.save(convite);

        return convite.getToken();
    }


    @Override
    public ConviteResponseDTO validarConvite(String token, String uidUsuario) {
        ConviteGrupo convite = conviteRepo.findByToken(token)
                .orElseThrow(ConviteNaoEncontrado::new);

        if (!convite.isAtivo() || LocalDateTime.now().isAfter(convite.getDataExpiracao())) {
            throw new ConviteExpirado();
        }

        GrupoDeEstudo grupo = convite.getGrupo();

        boolean jaMembro = membroRepo.existsByGrupo_IdAndEstudante_FirebaseUid(grupo.getId(), uidUsuario);

        return new ConviteResponseDTO(
                grupo.getId(),
                grupo.getNome(),
                grupo.getDescricao(),
                jaMembro
        );
    }

    @Override
    public void aceitarConvite(String token, String uidUsuario) {
        ConviteGrupo convite = conviteRepo.findByToken(token)
                .orElseThrow(ConviteNaoEncontrado::new);

        if (!convite.isAtivo() || LocalDateTime.now().isAfter(convite.getDataExpiracao())) {
            throw new ConviteExpirado();
        }

        if (membroRepo.existsByGrupo_IdAndEstudante_FirebaseUid(convite.getGrupo().getId(), uidUsuario)) {
            throw new EstudanteJaParticipa();
        }

        Estudante estudante = estudanteRepo.findById(uidUsuario)
                .orElseThrow(EstudanteNaoEncontrado::new);

        MembroGrupo novoMembro = new MembroGrupo();
        novoMembro.setGrupo(convite.getGrupo());
        novoMembro.setEstudante(estudante);
        novoMembro.setRole("MEMBER");

        membroRepo.save(novoMembro);
    }

    @Override
    public void removerCheckinInvalido(UUID idGrupo, UUID idSessao, String uid) {
        GrupoDeEstudo grupo = grupoRepo.findById(idGrupo).orElseThrow(GrupoNaoEncontrado::new);
        var sessao = sessaoRepo.findById(idSessao).orElseThrow(SessaoDeEstudoNaoEncontrado::new);
        
        if (!idGrupo.equals(sessao.getGrupoDeEstudo().getId())) {
            throw new SessaoDeEstudoNaoEncontrado();
        }
        // if (!idGrupo.equals(sessao.getGrupo().getId())) {
        //     throw new SessaoDeEstudoNaoEncontrado();
        // }
        
        boolean isAdmin = grupo.getAdmin() != null && grupo.getAdmin().getFirebaseUid().equals(uid);
        boolean isCriador = sessao.getCriador() != null && sessao.getCriador().getFirebaseUid().equals(uid);
        
        if (!isAdmin && !isCriador) {
            throw new GrupoNaoEncontrado();
        }

        String uidCriadorSessao = sessao.getCriador().getFirebaseUid();

        membroRepo.findByGrupo_IdAndEstudante_FirebaseUid(idGrupo, uidCriadorSessao)
                .ifPresent(membro -> {
                    if (membro.getQuantidadeCheckins() > 0) {
                        membro.setQuantidadeCheckins(membro.getQuantidadeCheckins() - 1);
                        membroRepo.save(membro);
                    }
                });
        
        sessaoRepo.delete(sessao);
    }

    private boolean isAdmin(GrupoDeEstudo grupo, String uid) {
        return grupo.getAdmin() != null && grupo.getAdmin().getFirebaseUid().equals(uid);
    }
}