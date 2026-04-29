package com.conti_talent.springboot.appweb.conti_talent_web.mapper;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.AreaDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Area;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AreaMapper {

    public AreaDTO toDTO(Area a) {
        if (a == null) return null;
        return new AreaDTO(a.getId(), a.getNombre(), a.getDescripcion(), a.getIcono(), a.getColor());
    }

    public Area toEntity(AreaDTO dto) {
        if (dto == null) return null;
        return new Area(dto.getId(), dto.getNombre(), dto.getDescripcion(), dto.getIcono(), dto.getColor());
    }

    public List<AreaDTO> toDTOList(List<Area> areas) {
        if (areas == null) return List.of();
        return areas.stream().map(this::toDTO).collect(Collectors.toList());
    }
}
