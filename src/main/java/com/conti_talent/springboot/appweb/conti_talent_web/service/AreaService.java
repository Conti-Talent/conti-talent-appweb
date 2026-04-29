package com.conti_talent.springboot.appweb.conti_talent_web.service;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.AreaDTO;

import java.util.List;

public interface AreaService {

    List<AreaDTO> listar();

    AreaDTO obtener(String id);

    AreaDTO crear(AreaDTO dto);

    AreaDTO actualizar(String id, AreaDTO dto);

    void eliminar(String id);

    int contarOfertas(String areaId);
}
