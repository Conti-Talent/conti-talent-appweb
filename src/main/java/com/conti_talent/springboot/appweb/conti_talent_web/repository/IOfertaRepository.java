package com.conti_talent.springboot.appweb.conti_talent_web.repository;

import com.conti_talent.springboot.appweb.conti_talent_web.model.Oferta;

import java.util.List;
import java.util.Optional;

public interface IOfertaRepository {
    List<Oferta> findAll();
    Optional<Oferta> findById(Long id);
    Oferta save(Oferta oferta);
    boolean existsById(Long id);
    void deleteById(Long id);
    long count();

    List<Oferta> findByAreaId(Long areaId);
    List<Oferta> findByDestacadaTrue();

    default List<Oferta> findFeatured() { return findByDestacadaTrue(); }
}
