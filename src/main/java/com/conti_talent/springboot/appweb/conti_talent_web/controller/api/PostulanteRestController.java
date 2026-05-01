package com.conti_talent.springboot.appweb.conti_talent_web.controller.api;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.PostulanteDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.request.PostularRequest;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.response.ApiResponse;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.ResourceNotFoundException;
import com.conti_talent.springboot.appweb.conti_talent_web.service.CvStorageService;
import com.conti_talent.springboot.appweb.conti_talent_web.service.IPostulanteService;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/postulantes")
public class PostulanteRestController {

    private final IPostulanteService postulanteService;
    private final CvStorageService cvStorageService;

    public PostulanteRestController(IPostulanteService postulanteService,
                                    CvStorageService cvStorageService) {
        this.postulanteService = postulanteService;
        this.cvStorageService = cvStorageService;
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

    @PostMapping("/{id}/cv")
    public ApiResponse<PostulanteDTO> subirCv(@PathVariable Long id,
                                              @RequestParam("cv") MultipartFile cv) {
        postulanteService.obtenerPorId(id);
        String storedFilename = cvStorageService.store(cv);
        PostulanteDTO actualizado = postulanteService.actualizarCv(id, storedFilename);
        return ApiResponse.ok(actualizado);
    }

    @GetMapping("/{id}/cv")
    public ResponseEntity<Resource> verCv(@PathVariable Long id) {
        PostulanteDTO postulante = postulanteService.obtenerPorId(id);
        if (postulante.getCv() == null || postulante.getCv().isBlank()) {
            throw new ResourceNotFoundException("El postulante no tiene CV registrado");
        }

        Resource resource = cvStorageService.load(postulante.getCv());
        ContentDisposition contentDisposition = dispositionFor(postulante.getCv());

        return ResponseEntity.ok()
                .contentType(cvStorageService.mediaType(postulante.getCv()))
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .body(resource);
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

    private ContentDisposition dispositionFor(String storedFilename) {
        ContentDisposition.Builder builder = cvStorageService.shouldOpenInline(storedFilename)
                ? ContentDisposition.inline()
                : ContentDisposition.attachment();
        return builder.filename(storedFilename, StandardCharsets.UTF_8).build();
    }
}
