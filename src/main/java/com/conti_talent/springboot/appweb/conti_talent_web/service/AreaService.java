package com.conti_talent.springboot.appweb.conti_talent_web.service;

import com.conti_talent.springboot.appweb.conti_talent_web.model.Area;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.AreaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AreaService {

    private final AreaRepository repository;

    public AreaService(AreaRepository repository) {
        this.repository = repository;
    }

    public List<Area> listar() {
        return repository.findAll();
    }

    public Area crear(Area area) {
        return repository.save(area);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
