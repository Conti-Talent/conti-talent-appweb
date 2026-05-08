package com.conti_talent.springboot.appweb.conti_talent_web.repository.impl;

import com.conti_talent.springboot.appweb.conti_talent_web.model.Area;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IAreaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class AreaRepositoryImpl extends InMemoryCrudRepository<Area> implements IAreaRepository {
    @Override
    protected Long getId(Area entity) { return entity.getId(); }

    @Override
    protected void setId(Area entity, Long id) { entity.setId(id); }
}
