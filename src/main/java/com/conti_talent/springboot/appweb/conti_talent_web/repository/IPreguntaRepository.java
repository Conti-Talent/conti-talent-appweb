package com.conti_talent.springboot.appweb.conti_talent_web.repository;

import com.conti_talent.springboot.appweb.conti_talent_web.model.Pregunta;

import java.util.List;
import java.util.Optional;

public interface IPreguntaRepository {

    List<Pregunta> findAll();

    Optional<Pregunta> findById(String id);

    List<Pregunta> findByOfertaId(String ofertaId);

    Pregunta save(Pregunta pregunta);

    void deleteById(String id);

    void deleteByOfertaId(String ofertaId);
}
