package com.conti_talent.springboot.appweb.conti_talent_web.repository;

import com.conti_talent.springboot.appweb.conti_talent_web.model.Area;

import java.util.List;
import java.util.Optional;

public interface IAreaRepository {
    List<Area> findAll();
    Optional<Area> findById(Long id);
    Area save(Area area);
    boolean existsById(Long id);
    void deleteById(Long id);
    long count();
}
