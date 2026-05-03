package com.conti_talent.springboot.appweb.conti_talent_web.controller.api;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.PostulanteDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.request.EntrevistaRequest;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.request.EvaluacionPsicologicaRequest;
import com.conti_talent.springboot.appweb.conti_talent_web.service.IEvaluacionManualService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/postulantes/{postulanteId}")
public class EvaluacionManualRestController {

    private final IEvaluacionManualService evaluacionManualService;

    public EvaluacionManualRestController(IEvaluacionManualService evaluacionManualService) {
        this.evaluacionManualService = evaluacionManualService;
    }

    @PostMapping("/entrevistas")
    public PostulanteDTO registrarEntrevista(@PathVariable Long postulanteId,
                                             @RequestBody EntrevistaRequest request) {
        return evaluacionManualService.registrarEntrevista(postulanteId, request);
    }

    @PostMapping("/evaluaciones-psicologicas")
    public PostulanteDTO registrarEvaluacionPsicologica(@PathVariable Long postulanteId,
                                                        @RequestBody EvaluacionPsicologicaRequest request) {
        return evaluacionManualService.registrarEvaluacionPsicologica(postulanteId, request);
    }
}
