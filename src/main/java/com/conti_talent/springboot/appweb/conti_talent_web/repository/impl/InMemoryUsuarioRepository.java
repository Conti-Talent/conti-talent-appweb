package com.conti_talent.springboot.appweb.conti_talent_web.repository.impl;

import com.conti_talent.springboot.appweb.conti_talent_web.model.Usuario;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.UsuarioRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implementación in-memory thread-safe del UsuarioRepository.
 * Usa un ConcurrentHashMap como almacenamiento principal y un AtomicLong como
 * contador para simular auto-incremento (los IDs nuevos quedan como "u<n>"
 * para mantener la convención del seed.js — opacos para el frontend).
 */
@Repository
public class InMemoryUsuarioRepository implements UsuarioRepository {

    private static final String ID_PREFIX = "u";

    private final Map<String, Usuario> store = new ConcurrentHashMap<>();
    private final AtomicLong counter = new AtomicLong(0);

    @Override
    public List<Usuario> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Optional<Usuario> findById(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        if (email == null) return Optional.empty();
        String needle = email.trim().toLowerCase();
        return store.values().stream()
                .filter(u -> u.getEmail() != null && u.getEmail().equalsIgnoreCase(needle))
                .findFirst();
    }

    @Override
    public Usuario save(Usuario usuario) {
        if (usuario == null) return null;
        if (usuario.getId() == null || usuario.getId().isBlank()) {
            usuario.setId(nextId());
        } else {
            // Si el seed inserta IDs explícitos, mantenemos el contador alineado.
            alignCounter(usuario.getId());
        }
        store.put(usuario.getId(), usuario);
        return usuario;
    }

    @Override
    public void deleteById(String id) {
        if (id != null) store.remove(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }

    private String nextId() {
        return ID_PREFIX + counter.incrementAndGet();
    }

    private void alignCounter(String id) {
        if (id == null || !id.startsWith(ID_PREFIX)) return;
        try {
            long n = Long.parseLong(id.substring(ID_PREFIX.length()));
            counter.updateAndGet(curr -> Math.max(curr, n));
        } catch (NumberFormatException ignored) { /* ID custom: no alineamos */ }
    }
}
