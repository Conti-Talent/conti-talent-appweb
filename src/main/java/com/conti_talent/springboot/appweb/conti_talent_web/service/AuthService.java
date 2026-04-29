package com.conti_talent.springboot.appweb.conti_talent_web.service;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.UsuarioDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.auth.LoginRequest;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.auth.RegistroRequest;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.auth.SesionDTO;

public interface AuthService {

    /** Valida credenciales y devuelve la sesión. Lanza UnauthorizedException si fallan. */
    SesionDTO login(LoginRequest request);

    /** Registra un nuevo postulante y devuelve su DTO + sesión iniciada. */
    UsuarioDTO register(RegistroRequest request);

    /** Construye la sesión para un usuario ya autenticado (post-registro). */
    SesionDTO buildSession(UsuarioDTO usuario);
}
