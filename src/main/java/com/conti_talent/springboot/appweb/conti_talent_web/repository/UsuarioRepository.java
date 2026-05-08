package com.conti_talent.springboot.appweb.conti_talent_web.repository;

import com.conti_talent.springboot.appweb.conti_talent_web.model.Usuario;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UsuarioRepository {

    private final List<Usuario> lista = new ArrayList<>();
    private Long contador = 1L;

    public List<Usuario> findAll() {
        return lista;
    }

    public Optional<Usuario> findByEmail(String email) {
        return lista.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    public Usuario save(Usuario usuario) {
        if (usuario.getId() == null) {
            usuario.setId(contador++);
        }
        lista.add(usuario);
        return usuario;
    }
}
