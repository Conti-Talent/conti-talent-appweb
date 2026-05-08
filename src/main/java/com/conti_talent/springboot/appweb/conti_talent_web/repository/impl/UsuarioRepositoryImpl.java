package com.conti_talent.springboot.appweb.conti_talent_web.repository.impl;

import com.conti_talent.springboot.appweb.conti_talent_web.model.Usuario;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IUsuarioRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class UsuarioRepositoryImpl extends InMemoryCrudRepository<Usuario> implements IUsuarioRepository {
    @Override
    protected Long getId(Usuario entity) { return entity.getId(); }

    @Override
    protected void setId(Usuario entity, Long id) { entity.setId(id); }

    @Override
    public Optional<Usuario> findByEmailIgnoreCase(String email) {
        if (email == null) return Optional.empty();
        return findAll().stream()
                .filter(usuario -> email.equalsIgnoreCase(usuario.getEmail()))
                .findFirst();
    }

    @Override
    public boolean existsByEmailIgnoreCase(String email) {
        return findByEmailIgnoreCase(email).isPresent();
    }

    @Override
    public List<Usuario> findByRolId(Long rolId) {
        return findAll().stream()
                .filter(usuario -> Objects.equals(usuario.getRolId(), rolId))
                .toList();
    }
}
