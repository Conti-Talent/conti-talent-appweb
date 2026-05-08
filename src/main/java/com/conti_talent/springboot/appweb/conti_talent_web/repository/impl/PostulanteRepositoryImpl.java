package com.conti_talent.springboot.appweb.conti_talent_web.repository.impl;

import com.conti_talent.springboot.appweb.conti_talent_web.model.Postulante;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IPostulanteRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository
public class PostulanteRepositoryImpl extends InMemoryCrudRepository<Postulante> implements IPostulanteRepository {
    @Override
    protected Long getId(Postulante entity) { return entity.getId(); }

    @Override
    protected void setId(Postulante entity, Long id) { entity.setId(id); }

    @Override
    public List<Postulante> findByOfertaId(Long ofertaId) {
        return findAll().stream()
                .filter(postulante -> Objects.equals(postulante.getOfertaId(), ofertaId))
                .toList();
    }

    @Override
    public List<Postulante> findByUsuarioId(Long usuarioId) {
        return findAll().stream()
                .filter(postulante -> Objects.equals(postulante.getUsuarioId(), usuarioId))
                .toList();
    }

    @Override
    public List<Postulante> findByEstadoId(Long estadoId) {
        return findAll().stream()
                .filter(postulante -> Objects.equals(postulante.getEstadoId(), estadoId))
                .toList();
    }
}
