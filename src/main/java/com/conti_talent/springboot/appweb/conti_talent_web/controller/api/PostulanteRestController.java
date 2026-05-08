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
     * GET /api/postulantes                -> todos
     * GET /api/postulantes?oferta=1       -> por oferta
     * GET /api/postulantes?usuario=2      -> por usuario
     */
    @GetMapping
    public List<PostulanteDTO> listar(@RequestParam(value = "oferta",  required = false) Long ofertaId,
                                      @RequestParam(value = "usuario", required = false) Long usuarioId) {
        if (ofertaId  != null) return postulanteService.listarPorOferta(ofertaId);
        if (usuarioId != null) return postulanteService.listarPorUsuario(usuarioId);
        return postulanteService.listarTodos();
    }

    @GetMapping("/{id}")
    public PostulanteDTO obtener(@PathVariable Long id) {
        return postulanteService.obtenerPorId(id);
    }

    @PostMapping
    public ResponseEntity<PostulanteDTO> postular(@RequestBody PostularRequest body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postulanteService.registrarPostulacion(body));
    }

    /**
     * PATCH /api/postulantes/{id}/estado
     * Body: { "estado": "ENTREVISTA" }   <- codigo logico
     *   o:  { "estadoId": "4" }          <- id numerico de tabla
     */
    @PatchMapping("/{id}/estado")
    public PostulanteDTO cambiarEstado(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String referenciaEstado = body.containsKey("estadoId") ? body.get("estadoId") : body.get("estado");
        return postulanteService.cambiarEstado(id, referenciaEstado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        postulanteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    /** Eliminacion logica: marca al postulante como RECHAZADO. */
    @PostMapping("/{id}/rechazar")
    public PostulanteDTO rechazar(@PathVariable Long id) {
        return postulanteService.marcarComoRechazado(id);
    }
}
