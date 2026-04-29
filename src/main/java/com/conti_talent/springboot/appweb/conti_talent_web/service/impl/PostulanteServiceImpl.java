package com.conti_talent.springboot.appweb.conti_talent_web.service.impl;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.PostulanteDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.request.PostularRequest;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.BusinessException;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.EstadoInvalidoException;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.ResourceNotFoundException;
import com.conti_talent.springboot.appweb.conti_talent_web.mapper.PostulanteMapper;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Postulante;
import com.conti_talent.springboot.appweb.conti_talent_web.model.enums.EstadoPostulante;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IOfertaRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IPostulanteRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.service.IPostulanteService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostulanteServiceImpl implements IPostulanteService {

    private final IPostulanteRepository postulanteRepository;
    private final IOfertaRepository ofertaRepository;
    private final PostulanteMapper mapper;

    public PostulanteServiceImpl(IPostulanteRepository postulanteRepository,
                                 IOfertaRepository ofertaRepository,
                                 PostulanteMapper mapper) {
        this.postulanteRepository = postulanteRepository;
        this.ofertaRepository = ofertaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<PostulanteDTO> listar() {
        return mapper.toDTOList(postulanteRepository.findAll());
    }

    @Override
    public PostulanteDTO obtener(String id) {
        return mapper.toDTO(getOrThrow(id));
    }

    @Override
    public List<PostulanteDTO> listarPorOferta(String ofertaId) {
        return mapper.toDTOList(postulanteRepository.findByOfertaId(ofertaId));
    }

    @Override
    public List<PostulanteDTO> listarPorUsuario(String usuarioId) {
        return mapper.toDTOList(postulanteRepository.findByUsuarioId(usuarioId));
    }

    @Override
    public PostulanteDTO postular(PostularRequest req) {
        if (req == null) throw new BusinessException("Datos de postulación requeridos");
        if (isBlank(req.getOfertaId())) throw new BusinessException("ofertaId requerido");
        if (isBlank(req.getNombre()))   throw new BusinessException("Nombre requerido");
        if (isBlank(req.getEmail()))    throw new BusinessException("Email requerido");

        if (ofertaRepository.findById(req.getOfertaId()).isEmpty()) {
            throw new BusinessException("Oferta inexistente: " + req.getOfertaId());
        }
        Postulante p = mapper.fromRequest(req);
        p.setCreadoEn(System.currentTimeMillis());
        return mapper.toDTO(postulanteRepository.save(p));
    }

    @Override
    public PostulanteDTO cambiarEstado(String id, EstadoPostulante destino) {
        Postulante p = getOrThrow(id);
        if (destino == null) throw new BusinessException("Estado destino requerido");
        if (!p.getEstado().puedeTransicionarA(destino)) {
            throw new EstadoInvalidoException(
                    "Transición no permitida: " + p.getEstado() + " → " + destino);
        }
        p.setEstado(destino);
        return mapper.toDTO(postulanteRepository.save(p));
    }

    @Override
    public void eliminar(String id) {
        if (postulanteRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Postulante no encontrado: " + id);
        }
        postulanteRepository.deleteById(id);
    }

    @Override
    public PostulanteDTO softDelete(String id) {
        return cambiarEstado(id, EstadoPostulante.RECHAZADO);
    }

    @Override
    public List<PostulanteDTO> ranking(String ofertaId) {
        List<Postulante> data = isBlank(ofertaId)
                ? postulanteRepository.findAll()
                : postulanteRepository.findByOfertaId(ofertaId);
        return data.stream()
                .sorted(Comparator.comparingInt(Postulante::getPuntaje).reversed())
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    /* ============ helpers ============ */

    private Postulante getOrThrow(String id) {
        return postulanteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Postulante no encontrado: " + id));
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
