package com.conti_talent.springboot.appweb.conti_talent_web.repository.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

abstract class InMemoryCrudRepository<T> {

    private final ConcurrentMap<Long, T> data = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);

    protected abstract Long getId(T entity);

    protected abstract void setId(T entity, Long id);

    public List<T> findAll() {
        return new ArrayList<>(data.values());
    }

    public Optional<T> findById(Long id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(data.get(id));
    }

    public T save(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("La entidad no puede ser null");
        }
        Long id = getId(entity);
        if (id == null) {
            id = sequence.incrementAndGet();
            setId(entity, id);
        } else {
            Long entityId = id;
            sequence.updateAndGet(current -> Math.max(current, entityId));
        }
        data.put(id, entity);
        return entity;
    }

    public boolean existsById(Long id) {
        return id != null && data.containsKey(id);
    }

    public void deleteById(Long id) {
        if (id != null) data.remove(id);
    }

    public long count() {
        return data.size();
    }
}
