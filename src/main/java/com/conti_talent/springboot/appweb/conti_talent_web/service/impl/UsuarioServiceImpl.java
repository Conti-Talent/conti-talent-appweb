package com.conti_talent.springboot.appweb.conti_talent_web.service.impl;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.UsuarioDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.request.UsuarioRequest;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.BusinessException;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.ResourceNotFoundException;
import com.conti_talent.springboot.appweb.conti_talent_web.mapper.UsuarioMapper;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Usuario;
import com.conti_talent.springboot.appweb.conti_talent_web.model.enums.Rol;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.UsuarioRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.service.UsuarioService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository repository;
    private final UsuarioMapper mapper;

    public UsuarioServiceImpl(UsuarioRepository repository, UsuarioMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<UsuarioDTO> listar() {
        return mapper.toDTOList(repository.findAll());
    }

    @Override
    public UsuarioDTO obtener(String id) {
        Usuario u = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + id));
        return mapper.toDTO(u);
    }

    @Override
    public UsuarioDTO crear(UsuarioRequest req) {
        validateNew(req);
        if (repository.existsByEmail(req.getEmail())) {
            throw new BusinessException("Ya existe un usuario con ese correo");
        }
        Usuario u = new Usuario();
        u.setNombre(req.getNombre().trim());
        u.setApellido(req.getApellido() != null ? req.getApellido().trim() : "");
        u.setEmail(req.getEmail().trim());
        u.setPassword(req.getPassword());
        u.setRol(req.getRol() != null ? req.getRol() : Rol.POSTULANTE);
        u.setActivo(req.getActivo() == null || req.getActivo());
        u.setCreadoEn(System.currentTimeMillis());
        return mapper.toDTO(repository.save(u));
    }

    @Override
    public UsuarioDTO actualizar(String id, UsuarioRequest req) {
        Usuario u = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + id));

        if (req.getNombre()   != null) u.setNombre(req.getNombre().trim());
        if (req.getApellido() != null) u.setApellido(req.getApellido().trim());
        if (req.getEmail()    != null) {
            String newEmail = req.getEmail().trim();
            if (!newEmail.equalsIgnoreCase(u.getEmail()) && repository.existsByEmail(newEmail)) {
                throw new BusinessException("Ya existe un usuario con ese correo");
            }
            u.setEmail(newEmail);
        }
        if (req.getPassword() != null && !req.getPassword().isEmpty()) u.setPassword(req.getPassword());
        if (req.getRol()      != null) u.setRol(req.getRol());
        if (req.getActivo()   != null) u.setActivo(req.getActivo());

        return mapper.toDTO(repository.save(u));
    }

    @Override
    public void eliminar(String id) {
        if (repository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Usuario no encontrado: " + id);
        }
        repository.deleteById(id);
    }

    private static void validateNew(UsuarioRequest r) {
        if (r == null || isBlank(r.getNombre()) || isBlank(r.getEmail()) || isBlank(r.getPassword())) {
            throw new BusinessException("Nombre, email y password son obligatorios");
        }
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
