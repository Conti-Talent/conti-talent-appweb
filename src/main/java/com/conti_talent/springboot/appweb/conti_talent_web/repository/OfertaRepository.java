package com.conti_talent.springboot.appweb.conti_talent_web.repository;

import com.conti_talent.springboot.appweb.conti_talent_web.model.Oferta;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class OfertaRepository {

    private final List<Oferta> lista = new ArrayList<>();
    private Long contador = 1L;

    public List<Oferta> findAll() {
        return lista;
    }

    public Oferta save(Oferta oferta) {
        if (oferta.getId() == null) {
            oferta.setId(contador++);
        }
        lista.add(oferta);
        return oferta;
    }

    public void deleteById(Long id) {
        lista.removeIf(o -> o.getId().equals(id));
    }
}
