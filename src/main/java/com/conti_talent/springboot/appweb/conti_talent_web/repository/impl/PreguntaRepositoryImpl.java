package com.conti_talent.springboot.appweb.conti_talent_web.repository.impl;

import com.conti_talent.springboot.appweb.conti_talent_web.model.Pregunta;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IPreguntaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository
public class PreguntaRepositoryImpl extends InMemoryCrudRepository<Pregunta> implements IPreguntaRepository {
    @Override
    protected Long getId(Pregunta entity) { return entity.getId(); }

    @Override
    protected void setId(Pregunta entity, Long id) { entity.setId(id); }

    @Override
    public List<Pregunta> findByOfertaId(Long ofertaId) {
        return findAll().stream()
                .filter(pregunta -> Objects.equals(pregunta.getOfertaId(), ofertaId))
                .toList();
    }

    @Override
    public void deleteByOfertaId(Long ofertaId) {
        findByOfertaId(ofertaId).forEach(pregunta -> deleteById(pregunta.getId()));
    }
}
