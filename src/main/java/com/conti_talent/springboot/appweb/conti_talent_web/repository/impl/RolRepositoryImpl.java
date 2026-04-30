package com.conti_talent.springboot.appweb.conti_talent_web.repository.impl;

import com.conti_talent.springboot.appweb.conti_talent_web.model.Rol;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IRolRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implementacion en memoria del repositorio de Rol. Usa ConcurrentHashMap
 * y un contador AtomicLong para simular auto-incremento de ID.
 */
@Repository
public class RolRepositoryImpl implements IRolRepository {

    private static final String PREFIJO_ID = "r";

    private final Map<String, Rol> almacen = new ConcurrentHashMap<>();
    private final AtomicLong contador = new AtomicLong(0);

    @Override
    public List<Rol> listarTodos() {
        return new ArrayList<>(almacen.values());
    }

    @Override
    public Optional<Rol> buscarPorId(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(almacen.get(id));
    }

    @Override
    public Optional<Rol> buscarPorCodigo(String codigo) {
        if (codigo == null) return Optional.empty();
        return almacen.values().stream()
                .filter(rol -> codigo.equalsIgnoreCase(rol.getCodigo()))
                .findFirst();
    }

    @Override
    public Rol guardar(Rol rol) {
        if (rol == null) return null;
        if (rol.getId() == null || rol.getId().isBlank()) {
            rol.setId(generarNuevoId());
        } else {
            alinearContador(rol.getId());
        }
        almacen.put(rol.getId(), rol);
        return rol;
    }

    @Override
    public void eliminarPorId(String id) {
        if (id != null) almacen.remove(id);
    }

    @Override
    public boolean existePorCodigo(String codigo) {
        return buscarPorCodigo(codigo).isPresent();
    }

    private String generarNuevoId() {
        return PREFIJO_ID + contador.incrementAndGet();
    }

    private void alinearContador(String id) {
        if (id == null || !id.startsWith(PREFIJO_ID)) return;
        try {
            long numero = Long.parseLong(id.substring(PREFIJO_ID.length()));
            contador.updateAndGet(actual -> Math.max(actual, numero));
        } catch (NumberFormatException ignorado) { }
    }
}
