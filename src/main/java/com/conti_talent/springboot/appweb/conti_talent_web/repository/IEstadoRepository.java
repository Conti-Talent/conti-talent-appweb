package com.conti_talent.springboot.appweb.conti_talent_web.repository;

import com.conti_talent.springboot.appweb.conti_talent_web.model.Estado;

import java.util.List;
import java.util.Optional;

/**
 * Contrato de persistencia para Estado.
 */
public interface IEstadoRepository {

    List<Estado> listarTodos();

    Optional<Estado> buscarPorId(String id);

    Optional<Estado> buscarPorCodigo(String codigo);

    Estado guardar(Estado estado);

    void eliminarPorId(String id);

    boolean existePorCodigo(String codigo);
}
