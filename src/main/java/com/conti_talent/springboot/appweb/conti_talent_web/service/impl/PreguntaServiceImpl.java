package com.conti_talent.springboot.appweb.conti_talent_web.service.impl;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.PreguntaDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.BusinessException;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.ResourceNotFoundException;
import com.conti_talent.springboot.appweb.conti_talent_web.mapper.PreguntaMapper;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Pregunta;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.OfertaRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.PreguntaRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.service.PreguntaService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PreguntaServiceImpl implements PreguntaService {

    private final PreguntaRepository preguntaRepository;
    private final OfertaRepository ofertaRepository;
    private final PreguntaMapper mapper;

    public PreguntaServiceImpl(PreguntaRepository preguntaRepository,
                               OfertaRepository ofertaRepository,
                               PreguntaMapper mapper) {
        this.preguntaRepository = preguntaRepository;
        this.ofertaRepository = ofertaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<PreguntaDTO> listar() {
        return mapper.toAdminDTOList(preguntaRepository.findAll());
    }

    @Override
    public List<PreguntaDTO> listarPorOferta(String ofertaId) {
        return mapper.toAdminDTOList(preguntaRepository.findByOfertaId(ofertaId));
    }

    @Override
    public List<PreguntaDTO> listarPorOfertaPublico(String ofertaId) {
        return mapper.toPublicDTOList(preguntaRepository.findByOfertaId(ofertaId));
    }

    @Override
    public PreguntaDTO obtener(String id) {
        Pregunta q = preguntaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pregunta no encontrada: " + id));
        return mapper.toAdminDTO(q);
    }

    @Override
    public PreguntaDTO crear(PreguntaDTO dto) {
        validar(dto);
        Pregunta q = new Pregunta();
        q.setOfertaId(dto.getOfertaId());
        q.setPregunta(dto.getPregunta().trim());
        q.setOpciones(new ArrayList<>(dto.getOpciones()));
        q.setCorrecta(dto.getCorrecta());
        return mapper.toAdminDTO(preguntaRepository.save(q));
    }

    @Override
    public PreguntaDTO actualizar(String id, PreguntaDTO dto) {
        Pregunta q = preguntaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pregunta no encontrada: " + id));
        if (dto.getOfertaId() != null) q.setOfertaId(dto.getOfertaId());
        if (dto.getPregunta() != null) q.setPregunta(dto.getPregunta().trim());
        if (dto.getOpciones() != null) q.setOpciones(new ArrayList<>(dto.getOpciones()));
        if (dto.getCorrecta() != null) q.setCorrecta(dto.getCorrecta());
        if (q.getCorrecta() < 0 || q.getCorrecta() >= q.getOpciones().size()) {
            throw new BusinessException("El índice de la respuesta correcta está fuera de rango");
        }
        return mapper.toAdminDTO(preguntaRepository.save(q));
    }

    @Override
    public void eliminar(String id) {
        if (preguntaRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Pregunta no encontrada: " + id);
        }
        preguntaRepository.deleteById(id);
    }

    private void validar(PreguntaDTO dto) {
        if (dto == null) throw new BusinessException("Datos de la pregunta requeridos");
        if (isBlank(dto.getOfertaId())) throw new BusinessException("ofertaId requerido");
        if (ofertaRepository.findById(dto.getOfertaId()).isEmpty()) {
            throw new BusinessException("Oferta inexistente: " + dto.getOfertaId());
        }
        if (isBlank(dto.getPregunta())) throw new BusinessException("Texto de la pregunta requerido");
        if (dto.getOpciones() == null || dto.getOpciones().size() < 2) {
            throw new BusinessException("Se requieren al menos 2 opciones");
        }
        if (dto.getCorrecta() == null || dto.getCorrecta() < 0 || dto.getCorrecta() >= dto.getOpciones().size()) {
            throw new BusinessException("Índice de respuesta correcta inválido");
        }
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
