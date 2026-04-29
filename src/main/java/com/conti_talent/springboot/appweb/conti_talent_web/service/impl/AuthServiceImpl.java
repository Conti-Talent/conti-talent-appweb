package com.conti_talent.springboot.appweb.conti_talent_web.service.impl;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.UsuarioDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.auth.LoginRequest;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.auth.RegistroRequest;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.auth.SesionDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.BusinessException;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.UnauthorizedException;
import com.conti_talent.springboot.appweb.conti_talent_web.mapper.UsuarioMapper;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Usuario;
import com.conti_talent.springboot.appweb.conti_talent_web.model.enums.Rol;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IUsuarioRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.service.IAuthService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements IAuthService {

    private final IUsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    public AuthServiceImpl(IUsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
    }

    @Override
    public SesionDTO login(LoginRequest request) {
        if (request == null || isBlank(request.getEmail()) || isBlank(request.getPassword())) {
            throw new UnauthorizedException("Credenciales inválidas");
        }
        Usuario user = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Credenciales inválidas o cuenta desactivada"));
        if (!user.isActivo() || !user.getPassword().equals(request.getPassword())) {
            throw new UnauthorizedException("Credenciales inválidas o cuenta desactivada");
        }
        return usuarioMapper.toSesionDTO(user);
    }

    @Override
    public UsuarioDTO register(RegistroRequest request) {
        if (request == null || isBlank(request.getEmail()) || isBlank(request.getPassword())) {
            throw new BusinessException("Email y password son obligatorios");
        }
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Ya existe una cuenta con este correo");
        }
        Usuario u = new Usuario();
        u.setNombre(safe(request.getNombre()));
        u.setApellido(safe(request.getApellido()));
        u.setEmail(request.getEmail().trim());
        u.setPassword(request.getPassword());
        u.setRol(Rol.POSTULANTE);
        u.setActivo(true);
        u.setCreadoEn(System.currentTimeMillis());
        Usuario saved = usuarioRepository.save(u);
        return usuarioMapper.toDTO(saved);
    }

    @Override
    public SesionDTO buildSession(UsuarioDTO usuario) {
        if (usuario == null) return null;
        return new SesionDTO(usuario.getId(), usuario.getNombre(), usuario.getApellido(),
                usuario.getEmail(), usuario.getRol());
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
