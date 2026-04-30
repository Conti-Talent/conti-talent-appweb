package com.conti_talent.springboot.appweb.conti_talent_web.controller.api;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.PostulanteDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.request.PostularRequest;
import com.conti_talent.springboot.appweb.conti_talent_web.service.IPostulanteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/postulantes")
public class PostulanteRestController {

    private final IPostulanteService postulanteService;

    public PostulanteRestController(IPostulanteService postulanteService) {
        this.postulanteService = postulanteService;
    }

    /**
     * GET /api/postulantes                       -> todos
     * GET /api/postulantes?oferta=o1             -> por oferta
     * GET /api/postulantes?usuario=u2            -> por usuario
     */
    @GetMapping
    public List<PostulanteDTO> listar(@RequestParam(value = "oferta",  required = false) String ofertaId,
                                      @RequestParam(value = "usuario", required = false) String usuarioId) {
        if (ofertaId  != null && !ofertaId.isBlank())  return postulanteService.listarPorOferta(ofertaId);
        if (usuarioId != null && !usuarioId.isBlank()) return postulanteService.listarPorUsuario(usuarioId);
        return postulanteService.listarTodos();
    }

    @GetMapping("/{id}")
    public PostulanteDTO obtener(@PathVariable String id) {
        return postulanteService.obtenerPorId(id);
    }

    @PostMapping
    public ResponseEntity<PostulanteDTO> postular(@RequestBody PostularRequest body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postulanteService.registrarPostulacion(body));
    }

    /**
     * PATCH /api/postulantes/{id}/estado
     * Body: { "estado": "ENTREVISTA" }   <- codigo logico
     *   o:  { "estadoId": "e4" }         <- id de tabla
     * Valida transicion legal (HTTP 409 si no lo es).
     */
    @PatchMapping("/{id}/estado")
    public PostulanteDTO cambiarEstado(@PathVariable String id, @RequestBody Map<String, String> body) {
        String referenciaEstado = body.containsKey("estadoId") ? body.get("estadoId") : body.get("estado");
        return postulanteService.cambiarEstado(id, referenciaEstado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        postulanteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    /** Eliminacion logica: marca al postulante como RECHAZADO. */
    @PostMapping("/{id}/rechazar")
    public PostulanteDTO rechazar(@PathVariable String id) {
        return postulanteService.marcarComoRechazado(id);
    }
}
