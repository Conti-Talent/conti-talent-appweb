package com.conti_talent.springboot.appweb.conti_talent_web.mapper;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.EstadoDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Estado;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper manual entre la entidad Estado y su DTO. Sin logica de negocio.
 */
@Component
public class EstadoMapper {

    /** Convierte una entidad Estado a su DTO publico. */
    public EstadoDTO convertirADTO(Estado estado) {
        if (estado == null) return null;
        return new EstadoDTO(
                estado.getId(), estado.getCodigo(), estado.getNombre(),
                estado.getDescripcion(), estado.getOrden(), estado.isTerminal(),
                estado.isActivo(), estado.getCreadoEn());
    }

    /** Convierte una lista de entidades a una lista de DTOs ordenada por `orden`. */
    public List<EstadoDTO> convertirALista(List<Estado> estados) {
        if (estados == null) return List.of();
        return estados.stream()
                .sorted(Comparator.comparingInt(Estado::getOrden))
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
}
