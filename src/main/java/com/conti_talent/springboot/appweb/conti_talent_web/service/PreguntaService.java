package com.conti_talent.springboot.appweb.conti_talent_web.service;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.PreguntaDTO;

import java.util.List;

public interface PreguntaService {

    /** Vista admin: incluye `correcta`. */
    List<PreguntaDTO> listar();

    List<PreguntaDTO> listarPorOferta(String ofertaId);

    /** Vista pública: omite `correcta`. */
    List<PreguntaDTO> listarPorOfertaPublico(String ofertaId);

    PreguntaDTO obtener(String id);

    PreguntaDTO crear(PreguntaDTO dto);

    PreguntaDTO actualizar(String id, PreguntaDTO dto);

    void eliminar(String id);
}
