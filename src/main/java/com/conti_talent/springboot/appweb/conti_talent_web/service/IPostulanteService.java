package com.conti_talent.springboot.appweb.conti_talent_web.service;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.PostulanteDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.request.PostularRequest;

import java.util.List;

/**
 * Contrato del servicio de gestion de postulantes y su flujo de seleccion.
 */
public interface IPostulanteService {

    /** Lista todos los postulantes registrados. */
    List<PostulanteDTO> listarTodos();

    /** Recupera un postulante por su id. */
    PostulanteDTO obtenerPorId(String id);

    /** Lista los postulantes de una oferta concreta. */
    List<PostulanteDTO> listarPorOferta(String ofertaId);

    /** Lista los postulantes asociados a un usuario. */
    List<PostulanteDTO> listarPorUsuario(String usuarioId);

    /** Registra una nueva postulacion (estado inicial: POSTULADO). */
    PostulanteDTO registrarPostulacion(PostularRequest request);

    /**
     * Cambia el estado del postulante. El destino se identifica por su
     * codigo logico (POSTULADO, EN_EVALUACION, ENTREVISTA, ...) o por su
     * id de tabla — el service acepta ambos.
     */
    PostulanteDTO cambiarEstado(String idPostulante, String estadoDestino);

    /** Elimina fisicamente un postulante. */
    void eliminar(String id);

    /** Eliminacion logica: marca como RECHAZADO. */
    PostulanteDTO marcarComoRechazado(String id);

    /** Top postulantes ordenados por puntaje desc. ofertaId opcional. */
    List<PostulanteDTO> obtenerRankingPorPuntaje(String ofertaId);
}
