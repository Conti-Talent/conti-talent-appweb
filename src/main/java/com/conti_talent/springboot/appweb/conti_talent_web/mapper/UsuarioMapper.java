package com.conti_talent.springboot.appweb.conti_talent_web.mapper;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.RolDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.UsuarioDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.auth.SesionDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Rol;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Usuario;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IRolRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper manual de Usuario. Resuelve la entidad Rol asociada al construir
 * el DTO publico, evitando que las capas superiores tengan que hacer
 * lookups manuales. Garantiza que la password nunca abandone la capa de
 * dominio.
 */
@Component
public class UsuarioMapper {

    private final IRolRepository rolRepository;
    private final RolMapper rolMapper;

    public UsuarioMapper(IRolRepository rolRepository, RolMapper rolMapper) {
        this.rolRepository = rolRepository;
        this.rolMapper = rolMapper;
    }

    /** Convierte un Usuario a su DTO publico, embebiendo el RolDTO resuelto. */
    public UsuarioDTO convertirADTO(Usuario usuario) {
        if (usuario == null) return null;
        RolDTO rolEmbebido = rolRepository.buscarPorId(usuario.getRolId())
                .map(rolMapper::convertirADTO)
                .orElse(null);
        return new UsuarioDTO(
                usuario.getId(), usuario.getNombre(), usuario.getApellido(),
                usuario.getEmail(), usuario.getRolId(), rolEmbebido,
                usuario.isActivo(), usuario.getCreadoEn());
    }

    /** Convierte una lista de usuarios a una lista de DTOs. */
    public List<UsuarioDTO> convertirALista(List<Usuario> usuarios) {
        if (usuarios == null) return List.of();
        return usuarios.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    /**
     * Construye el SesionDTO consumido por el frontend tras login.
     * Resuelve el codigo del rol y lo expone en minuscula ('admin', 'postulante').
     */
    public SesionDTO convertirASesion(Usuario usuario) {
        if (usuario == null) return null;
        String codigoRolMinuscula = rolRepository.buscarPorId(usuario.getRolId())
                .map(Rol::getCodigo)
                .map(String::toLowerCase)
                .orElse("");
        return new SesionDTO(
                usuario.getId(), usuario.getNombre(), usuario.getApellido(),
                usuario.getEmail(), codigoRolMinuscula);
    }
}
