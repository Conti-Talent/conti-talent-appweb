package com.conti_talent.springboot.appweb.conti_talent_web.controller.api;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.AreaDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.service.AreaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/areas")
public class AreaRestController {

    private final AreaService service;

    public AreaRestController(AreaService service) {
        this.service = service;
    }

    @GetMapping
    public List<AreaDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public AreaDTO obtener(@PathVariable String id) {
        return service.obtener(id);
    }

    @GetMapping("/{id}/ofertas-count")
    public Map<String, Integer> contarOfertas(@PathVariable String id) {
        return Map.of("count", service.contarOfertas(id));
    }

    @PostMapping
    public ResponseEntity<AreaDTO> crear(@RequestBody AreaDTO body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(body));
    }

    @PutMapping("/{id}")
    public AreaDTO actualizar(@PathVariable String id, @RequestBody AreaDTO body) {
        return service.actualizar(id, body);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
