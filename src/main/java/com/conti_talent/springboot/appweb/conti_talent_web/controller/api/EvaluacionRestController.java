package com.conti_talent.springboot.appweb.conti_talent_web.controller.api;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.EvaluacionDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.request.EvaluacionRequest;
import com.conti_talent.springboot.appweb.conti_talent_web.service.EvaluacionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/evaluaciones")
public class EvaluacionRestController {

    private final EvaluacionService service;

    public EvaluacionRestController(EvaluacionService service) {
        this.service = service;
    }

    /**
     * POST /api/evaluaciones
     * Body: { "postulanteId": "p1", "respuestas": { "q1": 1, "q2": 0, ... } }
     * Calcula puntaje, evita doble evaluación y mueve estado automáticamente.
     */
    @PostMapping
    public ResponseEntity<EvaluacionDTO> calificar(@RequestBody EvaluacionRequest body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.calificar(body));
    }

    @GetMapping("/postulante/{postulanteId}")
    public EvaluacionDTO obtenerPorPostulante(@PathVariable String postulanteId) {
        return service.obtenerPorPostulante(postulanteId);
    }
}
