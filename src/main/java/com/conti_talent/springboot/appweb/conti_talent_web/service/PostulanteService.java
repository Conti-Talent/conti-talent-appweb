package com.conti_talent.springboot.appweb.conti_talent_web.service;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.PostulanteDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.request.PostularRequest;
import com.conti_talent.springboot.appweb.conti_talent_web.model.enums.EstadoPostulante;

import java.util.List;

public interface PostulanteService {

    List<PostulanteDTO> listar();

    PostulanteDTO obtener(String id);

    List<PostulanteDTO> listarPorOferta(String ofertaId);

    List<PostulanteDTO> listarPorUsuario(String usuarioId);

    PostulanteDTO postular(PostularRequest request);

    /** Cambia el estado validando que la transición sea legal. */
    PostulanteDTO cambiarEstado(String id, EstadoPostulante destino);

    void eliminar(String id);

    /** Eliminación lógica → estado RECHAZADO. */
    PostulanteDTO softDelete(String id);

    /** Ranking ordenado descendente por puntaje. ofertaId opcional. */
    List<PostulanteDTO> ranking(String ofertaId);
}
