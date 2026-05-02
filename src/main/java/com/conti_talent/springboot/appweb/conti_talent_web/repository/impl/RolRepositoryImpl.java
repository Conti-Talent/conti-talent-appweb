package com.conti_talent.springboot.appweb.conti_talent_web.repository.impl;

import com.conti_talent.springboot.appweb.conti_talent_web.model.Rol;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IRolRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class RolRepositoryImpl extends InMemoryCrudRepository<Rol> implements IRolRepository {
    @Override
    protected Long getId(Rol entity) { return entity.getId(); }

    @Override
    protected void setId(Rol entity, Long id) { entity.setId(id); }

    @Override
    public Optional<Rol> findByCodigo(String codigo) {
        if (codigo == null) return Optional.empty();
        return findAll().stream()
                .filter(rol -> codigo.equalsIgnoreCase(rol.getCodigo()))
                .findFirst();
    }

    @Override
    public boolean existsByCodigo(String codigo) {
        return findByCodigo(codigo).isPresent();
    }
}
