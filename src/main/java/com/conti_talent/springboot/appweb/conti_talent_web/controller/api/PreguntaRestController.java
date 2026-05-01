package com.conti_talent.springboot.appweb.conti_talent_web.controller.api;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.PostulanteDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.PreguntaDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.auth.SesionDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.ResourceNotFoundException;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.UnauthorizedException;
import com.conti_talent.springboot.appweb.conti_talent_web.service.IPostulanteService;
import com.conti_talent.springboot.appweb.conti_talent_web.service.IPreguntaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/preguntas")
public class PreguntaRestController {

    private final IPreguntaService service;
    private final IPostulanteService postulanteService;

    public PreguntaRestController(IPreguntaService service,
                                  IPostulanteService postulanteService) {
        this.service = service;
        this.postulanteService = postulanteService;
    }

    /**
     * GET /api/preguntas                 -> admin (incluye `correcta`)
     * GET /api/preguntas?oferta=1        -> admin filtrado por oferta
     * GET /api/preguntas?oferta=1&publico=1 -> publico (sin `correcta`)
     */
    @GetMapping
    public List<PreguntaDTO> listar(@RequestParam(value = "oferta", required = false) Long ofertaId,
                                    @RequestParam(value = "publico", required = false) Boolean publico) {
        if (ofertaId != null) {
            return Boolean.TRUE.equals(publico)
                    ? service.listarPorOfertaPublico(ofertaId)
                    : service.listarPorOferta(ofertaId);
        }
        return service.listar();
    }

    @GetMapping("/publicas")
    public List<PreguntaDTO> listarPublicas(@RequestParam("oferta") Long ofertaId) {
        return service.listarPorOfertaPublico(ofertaId);
    }

    @GetMapping("/resueltas")
    public List<PreguntaDTO> listarResueltas(@RequestParam("postulante") Long postulanteId,
                                             HttpSession session) {
        SesionDTO sesion = (SesionDTO) session.getAttribute(AuthRestController.SESSION_ATTR);
        if (sesion == null) {
            throw new UnauthorizedException("No autenticado");
        }

        PostulanteDTO postulante = postulanteService.obtenerPorId(postulanteId);
        boolean esAdmin = "admin".equals(sesion.getRol());
        boolean esDuenio = postulante.getUsuarioId() != null && Objects.equals(postulante.getUsuarioId(), sesion.getId());
        if (!esAdmin && !esDuenio) {
            throw new AccessDeniedException("No tienes acceso a estas respuestas");
        }
        if (postulante.getRespuestas() == null || postulante.getRespuestas().isEmpty()) {
            throw new ResourceNotFoundException("El postulante no tiene respuestas registradas");
        }

        return service.listarPorOferta(postulante.getOfertaId());
    }

    @GetMapping("/{id}")
    public PreguntaDTO obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PostMapping
    public ResponseEntity<PreguntaDTO> crear(@RequestBody PreguntaDTO body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(body));
    }

    @PutMapping("/{id}")
    public PreguntaDTO actualizar(@PathVariable Long id, @RequestBody PreguntaDTO body) {
        return service.actualizar(id, body);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
