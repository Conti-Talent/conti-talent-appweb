package com.conti_talent.springboot.appweb.conti_talent_web.controller.api;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.PostulanteDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.request.PostularRequest;
import com.conti_talent.springboot.appweb.conti_talent_web.model.enums.EstadoPostulante;
import com.conti_talent.springboot.appweb.conti_talent_web.service.PostulanteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/postulantes")
public class PostulanteRestController {

    private final PostulanteService service;

    public PostulanteRestController(PostulanteService service) {
        this.service = service;
    }

    /**
     * GET /api/postulantes                       → todos
     * GET /api/postulantes?oferta=o1             → por oferta
     * GET /api/postulantes?usuario=u2            → por usuario
     */
    @GetMapping
    public List<PostulanteDTO> listar(@RequestParam(value = "oferta",  required = false) String ofertaId,
                                      @RequestParam(value = "usuario", required = false) String usuarioId) {
        if (ofertaId  != null && !ofertaId.isBlank())  return service.listarPorOferta(ofertaId);
        if (usuarioId != null && !usuarioId.isBlank()) return service.listarPorUsuario(usuarioId);
        return service.listar();
    }

    @GetMapping("/{id}")
    public PostulanteDTO obtener(@PathVariable String id) {
        return service.obtener(id);
    }

    @PostMapping
    public ResponseEntity<PostulanteDTO> postular(@RequestBody PostularRequest body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.postular(body));
    }

    /**
     * PATCH /api/postulantes/{id}/estado
     * Body: { "estado": "ENTREVISTA" }
     * Valida transición legal (lanza 400 si no lo es).
     */
    @PatchMapping("/{id}/estado")
    public PostulanteDTO cambiarEstado(@PathVariable String id, @RequestBody Map<String, String> body) {
        EstadoPostulante destino = EstadoPostulante.valueOf(body.get("estado"));
        return service.cambiarEstado(id, destino);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    /** Eliminación lógica → estado RECHAZADO. */
    @PostMapping("/{id}/rechazar")
    public PostulanteDTO rechazar(@PathVariable String id) {
        return service.softDelete(id);
    }
}
