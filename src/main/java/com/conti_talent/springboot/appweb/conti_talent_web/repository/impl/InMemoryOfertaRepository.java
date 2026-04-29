package com.conti_talent.springboot.appweb.conti_talent_web.repository.impl;

import com.conti_talent.springboot.appweb.conti_talent_web.model.Oferta;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.OfertaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryOfertaRepository implements OfertaRepository {

    private static final String ID_PREFIX = "o";

    private final Map<String, Oferta> store = new ConcurrentHashMap<>();
    private final AtomicLong counter = new AtomicLong(0);

    @Override
    public List<Oferta> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Optional<Oferta> findById(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Oferta> findByAreaId(String areaId) {
        if (areaId == null) return List.of();
        return store.values().stream()
                .filter(o -> areaId.equals(o.getAreaId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Oferta> findFeatured() {
        return store.values().stream()
                .filter(Oferta::isDestacada)
                .collect(Collectors.toList());
    }

    @Override
    public Oferta save(Oferta oferta) {
        if (oferta == null) return null;
        if (oferta.getId() == null || oferta.getId().isBlank()) {
            oferta.setId(ID_PREFIX + counter.incrementAndGet());
        } else {
            alignCounter(oferta.getId());
        }
        store.put(oferta.getId(), oferta);
        return oferta;
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
