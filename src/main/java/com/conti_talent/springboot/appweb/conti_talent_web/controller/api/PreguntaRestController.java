package com.conti_talent.springboot.appweb.conti_talent_web.controller.api;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.PreguntaDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.service.IPreguntaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/preguntas")
public class PreguntaRestController {

    private final IPreguntaService service;

    public PreguntaRestController(IPreguntaService service) {
        this.service = service;
    }

    /**
     * GET /api/preguntas                     -> admin (incluye `correcta`)
     * GET /api/preguntas?oferta=o1           -> admin filtrado por oferta
     * GET /api/preguntas?oferta=o1&publico=1 -> publico (sin `correcta`)
     */
    @GetMapping
    public List<PreguntaDTO> listar(@RequestParam(value = "oferta", required = false) String ofertaId,
                                    @RequestParam(value = "publico", required = false) Boolean publico) {
        if (ofertaId != null && !ofertaId.isBlank()) {
            return Boolean.TRUE.equals(publico)
                    ? service.listarPorOfertaPublico(ofertaId)
                    : service.listarPorOferta(ofertaId);
        }
        return service.listar();
    }

    @GetMapping("/{id}")
    public PreguntaDTO obtener(@PathVariable String id) {
        return service.obtener(id);
    }

    @PostMapping
    public ResponseEntity<PreguntaDTO> crear(@RequestBody PreguntaDTO body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(body));
    }

    @PutMapping("/{id}")
    public PreguntaDTO actualizar(@PathVariable String id, @RequestBody PreguntaDTO body) {
        return service.actualizar(id, body);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
