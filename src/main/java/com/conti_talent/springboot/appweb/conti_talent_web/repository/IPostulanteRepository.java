package com.conti_talent.springboot.appweb.conti_talent_web.repository;

import com.conti_talent.springboot.appweb.conti_talent_web.model.Postulante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPostulanteRepository extends JpaRepository<Postulante, Long> {

    @Query("SELECT p FROM Postulante p WHERE p.oferta.id = :ofertaId")
    List<Postulante> findByOfertaId(Long ofertaId);

    @Query("SELECT p FROM Postulante p WHERE p.usuario.id = :usuarioId")
    List<Postulante> findByUsuarioId(Long usuarioId);

    @Query("SELECT p FROM Postulante p WHERE p.estado.id = :estadoId")
    List<Postulante> findByEstadoId(Long estadoId);
}
