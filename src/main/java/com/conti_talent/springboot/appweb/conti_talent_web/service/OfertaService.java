package com.conti_talent.springboot.appweb.conti_talent_web.service;

import com.conti_talent.springboot.appweb.conti_talent_web.model.Oferta;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.OfertaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OfertaService {

    private final OfertaRepository repository;

    public OfertaService(OfertaRepository repository) {
        this.repository = repository;
    }

    public List<Oferta> listar() {
        return repository.findAll();
    }

    public Oferta crear(Oferta oferta) {
        return repository.save(oferta);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
