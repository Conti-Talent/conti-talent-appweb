package com.conti_talent.springboot.appweb.conti_talent_web.repository.impl;

import com.conti_talent.springboot.appweb.conti_talent_web.model.Estado;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IEstadoRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implementacion en memoria del repositorio de Estado.
 */
@Repository
public class EstadoRepositoryImpl implements IEstadoRepository {

    private static final String PREFIJO_ID = "e";

    private final Map<String, Estado> almacen = new ConcurrentHashMap<>();
    private final AtomicLong contador = new AtomicLong(0);

    @Override
    public List<Estado> listarTodos() {
        return new ArrayList<>(almacen.values());
    }

    @Override
    public Optional<Estado> buscarPorId(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(almacen.get(id));
    }

    @Override
    public Optional<Estado> buscarPorCodigo(String codigo) {
        if (codigo == null) return Optional.empty();
        return almacen.values().stream()
                .filter(estado -> codigo.equalsIgnoreCase(estado.getCodigo()))
                .findFirst();
    }

    @Override
    public Estado guardar(Estado estado) {
        if (estado == null) return null;
        if (estado.getId() == null || estado.getId().isBlank()) {
            estado.setId(generarNuevoId());
        } else {
            alinearContador(estado.getId());
        }
        almacen.put(estado.getId(), estado);
        return estado;
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
