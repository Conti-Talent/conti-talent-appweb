package com.conti_talent.springboot.appweb.conti_talent_web.repository;

import com.conti_talent.springboot.appweb.conti_talent_web.model.Postulante;

import java.util.List;
import java.util.Optional;

public interface IPostulanteRepository {
    List<Postulante> findAll();
    Optional<Postulante> findById(Long id);
    Postulante save(Postulante postulante);
    boolean existsById(Long id);
    void deleteById(Long id);
    long count();

    List<Postulante> findByOfertaId(Long ofertaId);
    List<Postulante> findByUsuarioId(Long usuarioId);
    List<Postulante> findByEstadoId(Long estadoId);
}
