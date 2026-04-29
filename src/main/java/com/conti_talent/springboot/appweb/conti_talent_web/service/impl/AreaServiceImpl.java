package com.conti_talent.springboot.appweb.conti_talent_web.service.impl;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.AreaDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.BusinessException;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.ResourceNotFoundException;
import com.conti_talent.springboot.appweb.conti_talent_web.mapper.AreaMapper;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Area;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.AreaRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.OfertaRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.service.AreaService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AreaServiceImpl implements AreaService {

    private final AreaRepository areaRepository;
    private final OfertaRepository ofertaRepository;
    private final AreaMapper mapper;

    public AreaServiceImpl(AreaRepository areaRepository,
                           OfertaRepository ofertaRepository,
                           AreaMapper mapper) {
        this.areaRepository = areaRepository;
        this.ofertaRepository = ofertaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<AreaDTO> listar() {
        return mapper.toDTOList(areaRepository.findAll());
    }

    @Override
    public AreaDTO obtener(String id) {
        Area a = areaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Área no encontrada: " + id));
        return mapper.toDTO(a);
    }

    @Override
    public AreaDTO crear(AreaDTO dto) {
        if (dto == null || isBlank(dto.getNombre())) {
            throw new BusinessException("El nombre del área es obligatorio");
        }
        Area a = new Area();
        a.setNombre(dto.getNombre().trim());
        a.setDescripcion(dto.getDescripcion() != null ? dto.getDescripcion().trim() : "");
        a.setIcono(dto.getIcono() != null ? dto.getIcono() : "🏢");
        a.setColor(dto.getColor() != null ? dto.getColor() : "#6366f1");
        return mapper.toDTO(areaRepository.save(a));
    }

    @Override
    public AreaDTO actualizar(String id, AreaDTO dto) {
        Area a = areaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Área no encontrada: " + id));
        if (dto.getNombre()      != null) a.setNombre(dto.getNombre().trim());
        if (dto.getDescripcion() != null) a.setDescripcion(dto.getDescripcion().trim());
        if (dto.getIcono()       != null) a.setIcono(dto.getIcono());
        if (dto.getColor()       != null) a.setColor(dto.getColor());
        return mapper.toDTO(areaRepository.save(a));
    }

    @Override
    public void eliminar(String id) {
        if (areaRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Área no encontrada: " + id);
        }
        if (!ofertaRepository.findByAreaId(id).isEmpty()) {
            throw new BusinessException("No se puede eliminar el área: tiene ofertas asociadas");
        }
        areaRepository.deleteById(id);
    }

    @Override
    public int contarOfertas(String areaId) {
        return ofertaRepository.findByAreaId(areaId).size();
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
