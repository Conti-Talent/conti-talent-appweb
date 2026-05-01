package com.conti_talent.springboot.appweb.conti_talent_web.repository;

import com.conti_talent.springboot.appweb.conti_talent_web.model.Postulante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPostulanteRepository extends JpaRepository<Postulante, Long> {

    List<Postulante> findByOfertaId(Long ofertaId);

    List<Postulante> findByUsuarioId(Long usuarioId);

    List<Postulante> findByEstadoId(Long estadoId);
}
