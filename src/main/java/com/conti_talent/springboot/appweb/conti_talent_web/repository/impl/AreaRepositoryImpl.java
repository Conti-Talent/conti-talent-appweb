package com.conti_talent.springboot.appweb.conti_talent_web.repository.impl;

import com.conti_talent.springboot.appweb.conti_talent_web.model.Area;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IAreaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class AreaRepositoryImpl implements IAreaRepository {

    private static final String ID_PREFIX = "a";

    private final Map<String, Area> store = new ConcurrentHashMap<>();
    private final AtomicLong counter = new AtomicLong(0);

    @Override
    public List<Area> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Optional<Area> findById(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Area save(Area area) {
        if (area == null) return null;
        if (area.getId() == null || area.getId().isBlank()) {
            area.setId(ID_PREFIX + counter.incrementAndGet());
        } else {
            alignCounter(area.getId());
        }
        store.put(area.getId(), area);
        return area;
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
