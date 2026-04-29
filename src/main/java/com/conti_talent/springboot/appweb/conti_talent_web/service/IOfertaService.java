package com.conti_talent.springboot.appweb.conti_talent_web.service;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.OfertaDTO;

import java.util.List;

public interface IOfertaService {

    List<OfertaDTO> listar();

    List<OfertaDTO> listarPorArea(String areaId);

    List<OfertaDTO> destacadas();

    OfertaDTO obtener(String id);

    OfertaDTO crear(OfertaDTO dto);

    OfertaDTO actualizar(String id, OfertaDTO dto);

    void eliminar(String id);
}
