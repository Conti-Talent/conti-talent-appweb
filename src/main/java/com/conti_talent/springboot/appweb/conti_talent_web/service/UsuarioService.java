package com.conti_talent.springboot.appweb.conti_talent_web.service;

import com.conti_talent.springboot.appweb.conti_talent_web.model.Usuario;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    public List<Usuario> listar() {
        return repository.findAll();
    }

    public Usuario registrar(Usuario usuario) {
        usuario.setRol("postulante");
        usuario.setActivo(true);
        return repository.save(usuario);
    }

    public Optional<Usuario> login(String email, String password) {
        return repository.findByEmail(email)
                .filter(u -> u.getPassword().equals(password) && u.isActivo());
    }

    public Usuario guardar(Usuario usuario) {
        return repository.save(usuario);
    }
}
