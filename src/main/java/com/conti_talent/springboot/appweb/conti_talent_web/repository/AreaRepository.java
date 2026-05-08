package com.conti_talent.springboot.appweb.conti_talent_web.repository;

import com.conti_talent.springboot.appweb.conti_talent_web.model.Area;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class AreaRepository {

    private final List<Area> lista = new ArrayList<>();
    private Long contador = 1L;

    public List<Area> findAll() {
        return lista;
    }

    public Area save(Area area) {
        if (area.getId() == null) {
            area.setId(contador++);
        }
        lista.add(area);
        return area;
    }

    public void deleteById(Long id) {
        lista.removeIf(a -> a.getId().equals(id));
    }
}
