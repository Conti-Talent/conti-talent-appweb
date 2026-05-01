package com.conti_talent.springboot.appweb.conti_talent_web.repository;

import com.conti_talent.springboot.appweb.conti_talent_web.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA de Usuario.
 * Spring Data deriva las queries automaticamente del nombre del metodo.
 */
@Repository
public interface IUsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    List<Usuario> findByRolId(Long rolId);

    /* ===== Atajos retro-compatibles con la API antigua ===== */

    default Optional<Usuario> findByEmail(String email) { return findByEmailIgnoreCase(email); }

    default boolean existsByEmail(String email) { return existsByEmailIgnoreCase(email); }
}
