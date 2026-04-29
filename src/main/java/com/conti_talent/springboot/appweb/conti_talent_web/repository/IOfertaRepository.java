package com.conti_talent.springboot.appweb.conti_talent_web.repository;

import com.conti_talent.springboot.appweb.conti_talent_web.model.Oferta;

import java.util.List;
import java.util.Optional;

public interface IOfertaRepository {

    List<Oferta> findAll();

    Optional<Oferta> findById(String id);

    List<Oferta> findByAreaId(String areaId);

    List<Oferta> findFeatured();

    Oferta save(Oferta oferta);

    void deleteById(String id);
}
