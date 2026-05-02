package com.conti_talent.springboot.appweb.conti_talent_web.repository;

import com.conti_talent.springboot.appweb.conti_talent_web.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface IUsuarioRepository {
    List<Usuario> findAll();
    Optional<Usuario> findById(Long id);
    Usuario save(Usuario usuario);
    boolean existsById(Long id);
    void deleteById(Long id);
    long count();

    Optional<Usuario> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
    List<Usuario> findByRolId(Long rolId);

    default Optional<Usuario> findByEmail(String email) { return findByEmailIgnoreCase(email); }
    default boolean existsByEmail(String email) { return existsByEmailIgnoreCase(email); }
}
