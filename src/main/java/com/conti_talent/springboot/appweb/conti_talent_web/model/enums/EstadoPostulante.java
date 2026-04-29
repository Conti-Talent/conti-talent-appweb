package com.conti_talent.springboot.appweb.conti_talent_web.model.enums;

import java.util.Arrays;
import java.util.List;

/**
 * Estados posibles del flujo de un postulante.
 * Orden lógico de avance:
 *   POSTULADO -> EN_EVALUACION -> APROBADO_TECNICO -> ENTREVISTA -> EVALUACION_PSICOLOGICA -> ACEPTADO
 * En cualquier momento puede pasar a RECHAZADO.
 */
public enum EstadoPostulante {

    POSTULADO,
    EN_EVALUACION,
    APROBADO_TECNICO,
    ENTREVISTA,
    EVALUACION_PSICOLOGICA,
    ACEPTADO,
    RECHAZADO;

    /** Orden canónico del flujo positivo (sin RECHAZADO). */
    private static final List<EstadoPostulante> FLUJO = Arrays.asList(
            POSTULADO, EN_EVALUACION, APROBADO_TECNICO,
            ENTREVISTA, EVALUACION_PSICOLOGICA, ACEPTADO
    );

    /**
     * Indica si la transición está permitida.
     * Reglas:
     *  - Cualquier estado puede ir a RECHAZADO (cierre temprano).
     *  - El flujo positivo solo puede avanzar (no retroceder), y solo puede saltar al
     *    estado inmediatamente siguiente. EN_EVALUACION -> APROBADO_TECNICO también
     *    puede dispararse automáticamente al guardar la evaluación técnica.
     *  - ACEPTADO y RECHAZADO son terminales.
     */
    public boolean puedeTransicionarA(EstadoPostulante destino) {
        if (destino == null) return false;
        if (this == destino) return true;
        if (this == ACEPTADO || this == RECHAZADO) return false;
        if (destino == RECHAZADO) return true;

        int origenIdx  = FLUJO.indexOf(this);
        int destinoIdx = FLUJO.indexOf(destino);
        if (origenIdx == -1 || destinoIdx == -1) return false;
        return destinoIdx == origenIdx + 1;
    }
}
