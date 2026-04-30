package com.conti_talent.springboot.appweb.conti_talent_web.repository.impl;

import com.conti_talent.springboot.appweb.conti_talent_web.model.Usuario;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IUsuarioRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Implementacion in-memory thread-safe del UsuarioRepository.
 * Usa ConcurrentHashMap como almacenamiento principal y AtomicLong como
 * contador para simular auto-incremento.
 */
@Repository
public class UsuarioRepositoryImpl implements IUsuarioRepository {

    private static final String PREFIJO_ID = "u";

    private final Map<String, Usuario> almacen = new ConcurrentHashMap<>();
    private final AtomicLong contador = new AtomicLong(0);

    @Override
    public List<Usuario> findAll() {
        return new ArrayList<>(almacen.values());
    }

    @Override
    public Optional<Usuario> findById(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(almacen.get(id));
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        if (email == null) return Optional.empty();
        String emailNormalizado = email.trim().toLowerCase();
        return almacen.values().stream()
                .filter(u -> u.getEmail() != null && u.getEmail().equalsIgnoreCase(emailNormalizado))
                .findFirst();
    }

    @Override
    public List<Usuario> findByRolId(String rolId) {
        if (rolId == null) return List.of();
        return almacen.values().stream()
                .filter(u -> rolId.equals(u.getRolId()))
                .collect(Collectors.toList());
    }

    @Override
    public Usuario save(Usuario usuario) {
        if (usuario == null) return null;
        if (usuario.getId() == null || usuario.getId().isBlank()) {
            usuario.setId(generarNuevoId());
        } else {
            alinearContador(usuario.getId());
        }
        almacen.put(usuario.getId(), usuario);
        return usuario;
    }

    @Override
    public void deleteById(String id) {
        if (id != null) almacen.remove(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }

    private String generarNuevoId() {
        return PREFIJO_ID + contador.incrementAndGet();
    }

    private void alinearContador(String id) {
        if (id == null || !id.startsWith(PREFIJO_ID)) return;
        try {
            long numero = Long.parseLong(id.substring(PREFIJO_ID.length()));
            contador.updateAndGet(actual -> Math.max(actual, numero));
        } catch (NumberFormatException ignorado) { }
    }
}
