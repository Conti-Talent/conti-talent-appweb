package com.conti_talent.springboot.appweb.conti_talent_web.repository.impl;

import com.conti_talent.springboot.appweb.conti_talent_web.model.Oferta;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IOfertaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository
public class OfertaRepositoryImpl extends InMemoryCrudRepository<Oferta> implements IOfertaRepository {
    @Override
    protected Long getId(Oferta entity) { return entity.getId(); }

    @Override
    protected void setId(Oferta entity, Long id) { entity.setId(id); }

    @Override
    public List<Oferta> findByAreaId(Long areaId) {
        return findAll().stream()
                .filter(oferta -> Objects.equals(oferta.getAreaId(), areaId))
                .toList();
    }

    @Override
    public List<Oferta> findByDestacadaTrue() {
        return findAll().stream()
                .filter(Oferta::isDestacada)
                .toList();
    }
}
