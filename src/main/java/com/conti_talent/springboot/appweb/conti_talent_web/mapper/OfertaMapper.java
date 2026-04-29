package com.conti_talent.springboot.appweb.conti_talent_web.mapper;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.OfertaDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Oferta;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OfertaMapper {

    public OfertaDTO toDTO(Oferta o) {
        if (o == null) return null;
        OfertaDTO dto = new OfertaDTO();
        dto.setId(o.getId());
        dto.setTitulo(o.getTitulo());
        dto.setTipo(o.getTipo());
        dto.setAreaId(o.getAreaId());
        dto.setModalidad(o.getModalidad());
        dto.setUbicacion(o.getUbicacion());
        dto.setVacantes(o.getVacantes());
        dto.setDestacada(o.isDestacada());
        dto.setDescripcion(o.getDescripcion());
        dto.setRequisitos(o.getRequisitos());
        dto.setBeneficios(o.getBeneficios());
        dto.setCreadaEn(o.getCreadaEn());
        return dto;
    }

    public Oferta toEntity(OfertaDTO dto) {
        if (dto == null) return null;
        return new Oferta(
                dto.getId(), dto.getTitulo(), dto.getTipo(), dto.getAreaId(),
                dto.getModalidad(), dto.getUbicacion(), dto.getVacantes(),
                dto.isDestacada(), dto.getDescripcion(),
                dto.getRequisitos(), dto.getBeneficios(), dto.getCreadaEn());
    }

    public List<OfertaDTO> toDTOList(List<Oferta> ofertas) {
        if (ofertas == null) return List.of();
        return ofertas.stream().map(this::toDTO).collect(Collectors.toList());
    }
}
