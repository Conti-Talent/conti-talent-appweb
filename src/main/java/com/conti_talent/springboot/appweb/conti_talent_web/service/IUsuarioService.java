package com.conti_talent.springboot.appweb.conti_talent_web.service;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.UsuarioDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.request.UsuarioRequest;

import java.util.List;

public interface IUsuarioService {

    List<UsuarioDTO> listar();

    UsuarioDTO obtener(String id);

    UsuarioDTO crear(UsuarioRequest request);

    UsuarioDTO actualizar(String id, UsuarioRequest request);

    void eliminar(String id);
}
