package com.conti_talent.springboot.appweb.conti_talent_web.repository.impl;

import com.conti_talent.springboot.appweb.conti_talent_web.model.Estado;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IEstadoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class EstadoRepositoryImpl extends InMemoryCrudRepository<Estado> implements IEstadoRepository {
    @Override
    protected Long getId(Estado entity) { return entity.getId(); }

    @Override
    protected void setId(Estado entity, Long id) { entity.setId(id); }

    @Override
    public Optional<Estado> findByCodigo(String codigo) {
        if (codigo == null) return Optional.empty();
        return findAll().stream()
                .filter(estado -> codigo.equalsIgnoreCase(estado.getCodigo()))
                .findFirst();
    }

    @Override
    public boolean existsByCodigo(String codigo) {
        return findByCodigo(codigo).isPresent();
    }
}
