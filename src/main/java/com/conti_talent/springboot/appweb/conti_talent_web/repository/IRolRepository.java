package com.conti_talent.springboot.appweb.conti_talent_web.repository;

import com.conti_talent.springboot.appweb.conti_talent_web.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio JPA del catalogo de roles.
 * Mantiene los nombres de metodos en español que ya consumen los services.
 */
@Repository
public interface IRolRepository extends JpaRepository<Rol, Long> {

    Optional<Rol> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    /* ===== Atajos en español para no romper signatures de los services ===== */

    default java.util.List<Rol> listarTodos() { return findAll(); }

    default Optional<Rol> buscarPorId(Long id) { return findById(id); }

    default Optional<Rol> buscarPorCodigo(String codigo) { return findByCodigo(codigo); }

    default Rol guardar(Rol rol) { return save(rol); }

    default void eliminarPorId(Long id) { deleteById(id); }

    default boolean existePorCodigo(String codigo) { return existsByCodigo(codigo); }
}
