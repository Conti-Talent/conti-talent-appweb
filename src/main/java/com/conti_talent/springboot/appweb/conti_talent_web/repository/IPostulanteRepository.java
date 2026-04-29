package com.conti_talent.springboot.appweb.conti_talent_web.repository;

import com.conti_talent.springboot.appweb.conti_talent_web.model.Postulante;

import java.util.List;
import java.util.Optional;

public interface IPostulanteRepository {

    List<Postulante> findAll();

    Optional<Postulante> findById(String id);

    List<Postulante> findByOfertaId(String ofertaId);

    List<Postulante> findByUsuarioId(String usuarioId);

    Postulante save(Postulante postulante);

    void deleteById(String id);
}
