package com.conti_talent.springboot.appweb.conti_talent_web.mapper;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.RolDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Rol;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper manual entre la entidad Rol y su DTO. Sin logica de negocio.
 */
@Component
public class RolMapper {

    /** Convierte una entidad Rol a su DTO publico. */
    public RolDTO convertirADTO(Rol rol) {
        if (rol == null) return null;
        return new RolDTO(
                rol.getId(), rol.getCodigo(), rol.getNombre(),
                rol.getDescripcion(), rol.isActivo(), rol.getCreadoEn());
    }

    /** Convierte una lista de entidades a una lista de DTOs. */
    public List<RolDTO> convertirALista(List<Rol> roles) {
        if (roles == null) return List.of();
        return roles.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    /** Convierte un DTO a entidad (uso administrativo). */
    public Rol convertirAEntidad(RolDTO dto) {
        if (dto == null) return null;
        return new Rol(
                dto.getId(), dto.getCodigo(), dto.getNombre(),
                dto.getDescripcion(), dto.isActivo(), dto.getCreadoEn());
    }
}
