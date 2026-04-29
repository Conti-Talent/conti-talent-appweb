package com.conti_talent.springboot.appweb.conti_talent_web.mapper;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.UsuarioDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.auth.SesionDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Usuario;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper manual entre Usuario (entidad) y sus DTOs públicos.
 * Garantiza que la password nunca abandone la capa de dominio.
 */
@Component
public class UsuarioMapper {

    public UsuarioDTO toDTO(Usuario u) {
        if (u == null) return null;
        return new UsuarioDTO(
                u.getId(), u.getNombre(), u.getApellido(),
                u.getEmail(), u.getRol(), u.isActivo(), u.getCreadoEn());
    }

    public List<UsuarioDTO> toDTOList(List<Usuario> usuarios) {
        if (usuarios == null) return List.of();
        return usuarios.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public SesionDTO toSesionDTO(Usuario u) {
        if (u == null) return null;
        return new SesionDTO(u.getId(), u.getNombre(), u.getApellido(), u.getEmail(), u.getRol());
    }
}
