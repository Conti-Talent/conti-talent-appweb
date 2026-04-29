package com.conti_talent.springboot.appweb.conti_talent_web.repository.impl;

import com.conti_talent.springboot.appweb.conti_talent_web.model.Pregunta;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.PreguntaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryPreguntaRepository implements PreguntaRepository {

    private static final String ID_PREFIX = "q";

    private final Map<String, Pregunta> store = new ConcurrentHashMap<>();
    private final AtomicLong counter = new AtomicLong(0);

    @Override
    public List<Pregunta> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Optional<Pregunta> findById(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Pregunta> findByOfertaId(String ofertaId) {
        if (ofertaId == null) return List.of();
        return store.values().stream()
                .filter(q -> ofertaId.equals(q.getOfertaId()))
                .collect(Collectors.toList());
    }

    @Override
    public Pregunta save(Pregunta pregunta) {
        if (pregunta == null) return null;
        if (pregunta.getId() == null || pregunta.getId().isBlank()) {
            pregunta.setId(ID_PREFIX + counter.incrementAndGet());
        } else {
            alignCounter(pregunta.getId());
        }
        store.put(pregunta.getId(), pregunta);
        return pregunta;
    }

    @Override
    public void deleteById(String id) {
        if (id != null) store.remove(id);
    }

    @Override
    public void deleteByOfertaId(String ofertaId) {
        if (ofertaId == null) return;
        store.values().removeIf(q -> ofertaId.equals(q.getOfertaId()));
    }

    private void alignCounter(String id) {
        if (id == null || !id.startsWith(ID_PREFIX)) return;
        try {
            long n = Long.parseLong(id.substring(ID_PREFIX.length()));
            counter.updateAndGet(curr -> Math.max(curr, n));
        } catch (NumberFormatException ignored) { }
    }
}
