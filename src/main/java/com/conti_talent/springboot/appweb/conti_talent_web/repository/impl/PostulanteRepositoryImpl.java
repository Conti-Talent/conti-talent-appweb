package com.conti_talent.springboot.appweb.conti_talent_web.repository.impl;

import com.conti_talent.springboot.appweb.conti_talent_web.model.Postulante;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IPostulanteRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class PostulanteRepositoryImpl implements IPostulanteRepository {

    private static final String ID_PREFIX = "p";

    private final Map<String, Postulante> store = new ConcurrentHashMap<>();
    private final AtomicLong counter = new AtomicLong(0);

    @Override
    public List<Postulante> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Optional<Postulante> findById(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Postulante> findByOfertaId(String ofertaId) {
        if (ofertaId == null) return List.of();
        return store.values().stream()
                .filter(p -> ofertaId.equals(p.getOfertaId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Postulante> findByUsuarioId(String usuarioId) {
        if (usuarioId == null) return List.of();
        return store.values().stream()
                .filter(p -> usuarioId.equals(p.getUsuarioId()))
                .collect(Collectors.toList());
    }

    @Override
    public Postulante save(Postulante postulante) {
        if (postulante == null) return null;
        if (postulante.getId() == null || postulante.getId().isBlank()) {
            postulante.setId(ID_PREFIX + counter.incrementAndGet());
        } else {
            alignCounter(postulante.getId());
        }
        store.put(postulante.getId(), postulante);
        return postulante;
    }

    @Override
    public void deleteById(String id) {
        if (id != null) store.remove(id);
    }

    private void alignCounter(String id) {
        if (id == null || !id.startsWith(ID_PREFIX)) return;
        try {
            long n = Long.parseLong(id.substring(ID_PREFIX.length()));
            counter.updateAndGet(curr -> Math.max(curr, n));
        } catch (NumberFormatException ignored) { }
    }
}
