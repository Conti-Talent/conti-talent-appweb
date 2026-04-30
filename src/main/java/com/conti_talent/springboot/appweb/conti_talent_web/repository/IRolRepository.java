package com.conti_talent.springboot.appweb.conti_talent_web.repository;

import com.conti_talent.springboot.appweb.conti_talent_web.model.Rol;

import java.util.List;
import java.util.Optional;

/**
 * Contrato de persistencia para Rol. Definido como interfaz para permitir
 * intercambiar la implementacion (memoria hoy, JPA mañana) sin afectar a
 * services ni controllers.
 */
public interface IRolRepository {

    List<Rol> listarTodos();

    Optional<Rol> buscarPorId(String id);

    Optional<Rol> buscarPorCodigo(String codigo);

    Rol guardar(Rol rol);

    void eliminarPorId(String id);

    boolean existePorCodigo(String codigo);
}
