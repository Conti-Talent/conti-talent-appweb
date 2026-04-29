package com.conti_talent.springboot.appweb.conti_talent_web.repository;

import com.conti_talent.springboot.appweb.conti_talent_web.model.Area;

import java.util.List;
import java.util.Optional;

public interface AreaRepository {

    List<Area> findAll();

    Optional<Area> findById(String id);

    Area save(Area area);

    void deleteById(String id);
}
