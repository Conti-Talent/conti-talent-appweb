package com.conti_talent.springboot.appweb.conti_talent_web.service;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.RolDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Rol;

import java.util.List;

/**
 * Contrato del servicio de gestion de roles del sistema.
 */
public interface IRolService {

    /** Lista todos los roles disponibles (incluye inactivos). */
    List<RolDTO> listarTodos();

    /** Lista solo los roles activos (utilidad para combos del admin). */
    List<RolDTO> listarSoloActivos();

    /** Recupera un rol por su id. Lanza ResourceNotFoundException si no existe. */
    RolDTO obtenerPorId(String id);

    /** Recupera la entidad por su codigo logico (ADMIN, POSTULANTE). */
    Rol obtenerEntidadPorCodigo(String codigo);

    /** Crea un nuevo rol. */
    RolDTO crear(RolDTO dto);

    /** Actualiza un rol existente. */
    RolDTO actualizar(String id, RolDTO dto);

    /** Elimina un rol por su id. */
    void eliminar(String id);
}
