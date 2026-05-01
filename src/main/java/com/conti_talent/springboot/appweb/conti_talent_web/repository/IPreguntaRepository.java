package com.conti_talent.springboot.appweb.conti_talent_web.repository;

import com.conti_talent.springboot.appweb.conti_talent_web.model.Pregunta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface IPreguntaRepository extends JpaRepository<Pregunta, Long> {

    List<Pregunta> findByOfertaId(Long ofertaId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Pregunta p WHERE p.oferta.id = :ofertaId")
    void deleteByOfertaId(Long ofertaId);
}
