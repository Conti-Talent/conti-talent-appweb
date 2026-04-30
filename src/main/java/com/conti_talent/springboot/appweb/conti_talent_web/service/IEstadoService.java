package com.conti_talent.springboot.appweb.conti_talent_web.service;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.EstadoDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Estado;

import java.util.List;

/**
 * Contrato del servicio de gestion de estados del flujo de seleccion.
 */
public interface IEstadoService {

    /** Lista todos los estados ordenados por su atributo `orden`. */
    List<EstadoDTO> listarTodos();

    /** Lista solo estados activos. */
    List<EstadoDTO> listarSoloActivos();

    /** Recupera un estado por su id. */
    EstadoDTO obtenerPorId(String id);

    /** Recupera la entidad por su codigo logico (POSTULADO, EN_EVALUACION, ...). */
    Estado obtenerEntidadPorCodigo(String codigo);

    /** Verifica si la transicion desde un estado a otro es legal. */
    boolean puedeTransicionar(String idEstadoOrigen, String idEstadoDestino);
}
