package com.conti_talent.springboot.appweb.conti_talent_web.repository;

import com.conti_talent.springboot.appweb.conti_talent_web.model.Usuario;

import java.util.List;
import java.util.Optional;

/**
 * Contrato del repositorio de Usuario. Implementacion intercambiable
 * (memoria hoy, JPA mañana) sin afectar la capa de servicios.
 */
public interface IUsuarioRepository {

    List<Usuario> findAll();

    Optional<Usuario> findById(String id);

    Optional<Usuario> findByEmail(String email);

    List<Usuario> findByRolId(String rolId);

    Usuario save(Usuario usuario);

    void deleteById(String id);

    boolean existsByEmail(String email);
}
