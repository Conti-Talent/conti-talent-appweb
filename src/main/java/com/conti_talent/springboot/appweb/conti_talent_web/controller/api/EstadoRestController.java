package com.conti_talent.springboot.appweb.conti_talent_web.controller.api;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.EstadoDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.service.IEstadoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints REST para el catalogo de estados del flujo de seleccion.
 *  GET /api/estados               -> listar todos
 *  GET /api/estados?activos=true  -> listar solo activos
 *  GET /api/estados/{id}          -> obtener uno
 */
@RestController
@RequestMapping("/api/estados")
public class EstadoRestController {

    private final IEstadoService estadoService;

    public EstadoRestController(IEstadoService estadoService) {
        this.estadoService = estadoService;
    }

    @GetMapping
    public List<EstadoDTO> listar(@RequestParam(value = "activos", required = false) Boolean soloActivos) {
        if (Boolean.TRUE.equals(soloActivos)) return estadoService.listarSoloActivos();
        return estadoService.listarTodos();
    }

    @GetMapping("/{id}")
    public EstadoDTO obtener(@PathVariable String id) {
        return estadoService.obtenerPorId(id);
    }
}
