package com.conti_talent.springboot.appweb.conti_talent_web.repository;

import com.conti_talent.springboot.appweb.conti_talent_web.model.Usuario;

import java.util.List;
import java.util.Optional;

/**
 * Contrato del repositorio de Usuario. Definido como interfaz para que la
 * implementación pueda cambiar (in-memory hoy, JPA mañana) sin afectar la
 * capa de servicios.
 */
public interface UsuarioRepository {

    List<Usuario> findAll();

    Optional<Usuario> findById(String id);

    Optional<Usuario> findByEmail(String email);

    Usuario save(Usuario usuario);

    void deleteById(String id);

    boolean existsByEmail(String email);
}
