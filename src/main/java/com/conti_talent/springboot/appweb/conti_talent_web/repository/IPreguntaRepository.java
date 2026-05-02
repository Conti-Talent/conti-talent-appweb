package com.conti_talent.springboot.appweb.conti_talent_web.repository;

import com.conti_talent.springboot.appweb.conti_talent_web.model.Pregunta;

import java.util.List;
import java.util.Optional;

public interface IPreguntaRepository {
    List<Pregunta> findAll();
    Optional<Pregunta> findById(Long id);
    Pregunta save(Pregunta pregunta);
    boolean existsById(Long id);
    void deleteById(Long id);
    long count();

    List<Pregunta> findByOfertaId(Long ofertaId);
    void deleteByOfertaId(Long ofertaId);
}
