package com.conti_talent.springboot.appweb.conti_talent_web.service.impl;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.OfertaDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.BusinessException;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.ResourceNotFoundException;
import com.conti_talent.springboot.appweb.conti_talent_web.mapper.OfertaMapper;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Oferta;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.AreaRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.OfertaRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.PreguntaRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.service.OfertaService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OfertaServiceImpl implements OfertaService {

    private static final List<String> TIPOS_VALIDOS = List.of("Práctica", "Trabajo");

    private final OfertaRepository ofertaRepository;
    private final AreaRepository areaRepository;
    private final PreguntaRepository preguntaRepository;
    private final OfertaMapper mapper;

    public OfertaServiceImpl(OfertaRepository ofertaRepository,
                             AreaRepository areaRepository,
                             PreguntaRepository preguntaRepository,
                             OfertaMapper mapper) {
        this.ofertaRepository = ofertaRepository;
        this.areaRepository = areaRepository;
        this.preguntaRepository = preguntaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<OfertaDTO> listar() {
        return mapper.toDTOList(ofertaRepository.findAll());
    }

    @Override
    public List<OfertaDTO> listarPorArea(String areaId) {
        return mapper.toDTOList(ofertaRepository.findByAreaId(areaId));
    }

    @Override
    public List<OfertaDTO> destacadas() {
        return mapper.toDTOList(ofertaRepository.findFeatured());
    }

    @Override
    public OfertaDTO obtener(String id) {
        Oferta o = ofertaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Oferta no encontrada: " + id));
        return mapper.toDTO(o);
    }

    @Override
    public OfertaDTO crear(OfertaDTO dto) {
        validarDTO(dto, /*creando=*/ true);
        Oferta o = new Oferta();
        applyChanges(o, dto, /*nuevo=*/ true);
        o.setCreadaEn(System.currentTimeMillis());
        return mapper.toDTO(ofertaRepository.save(o));
    }

    @Override
    public OfertaDTO actualizar(String id, OfertaDTO dto) {
        Oferta o = ofertaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Oferta no encontrada: " + id));
        validarDTO(dto, /*creando=*/ false);
        applyChanges(o, dto, /*nuevo=*/ false);
        return mapper.toDTO(ofertaRepository.save(o));
    }

    @Override
    public void eliminar(String id) {
        if (ofertaRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Oferta no encontrada: " + id);
        }
        // limpieza referencial: preguntas asociadas se borran junto con la oferta
        preguntaRepository.deleteByOfertaId(id);
        ofertaRepository.deleteById(id);
    }

    /* ================ helpers ================ */

    private void validarDTO(OfertaDTO dto, boolean creando) {
        if (dto == null) throw new BusinessException("Datos de oferta requeridos");
        if (creando && isBlank(dto.getTitulo())) {
            throw new BusinessException("El título es obligatorio");
        }
        if (dto.getTipo() != null && !TIPOS_VALIDOS.contains(dto.getTipo())) {
            throw new BusinessException("Tipo inválido. Permitidos: " + TIPOS_VALIDOS);
        }
        if (dto.getAreaId() != null && !dto.getAreaId().isBlank()
                && areaRepository.findById(dto.getAreaId()).isEmpty()) {
            throw new BusinessException("Área inexistente: " + dto.getAreaId());
        }
    }

    private void applyChanges(Oferta o, OfertaDTO dto, boolean nuevo) {
        if (dto.getTitulo()      != null) o.setTitulo(dto.getTitulo().trim());
        if (dto.getTipo()        != null) o.setTipo(dto.getTipo());
        else if (nuevo) o.setTipo("Trabajo");
        if (dto.getAreaId()      != null) o.setAreaId(dto.getAreaId());
        if (dto.getModalidad()   != null) o.setModalidad(dto.getModalidad());
        if (dto.getUbicacion()   != null) o.setUbicacion(dto.getUbicacion());
        o.setVacantes(Math.max(1, dto.getVacantes() == 0 && nuevo ? 1 : dto.getVacantes()));
        o.setDestacada(dto.isDestacada());
        if (dto.getDescripcion() != null) o.setDescripcion(dto.getDescripcion().trim());
        if (dto.getRequisitos()  != null) o.setRequisitos(new ArrayList<>(dto.getRequisitos()));
        if (dto.getBeneficios()  != null) o.setBeneficios(new ArrayList<>(dto.getBeneficios()));
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
