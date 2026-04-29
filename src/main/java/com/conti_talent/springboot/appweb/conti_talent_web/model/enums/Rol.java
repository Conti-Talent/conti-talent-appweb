package com.conti_talent.springboot.appweb.conti_talent_web.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Roles soportados en la plataforma. Se serializan en minúscula
 * para mantener compatibilidad con el frontend ('admin', 'postulante').
 */
public enum Rol {

    ADMIN("admin"),
    POSTULANTE("postulante");

    private final String valor;

    Rol(String valor) {
        this.valor = valor;
    }

    @JsonValue
    public String getValor() {
        return valor;
    }

    @JsonCreator
    public static Rol from(String raw) {
        if (raw == null) return POSTULANTE;
        for (Rol r : values()) {
            if (r.valor.equalsIgnoreCase(raw) || r.name().equalsIgnoreCase(raw)) {
                return r;
            }
        }
        return POSTULANTE;
    }
}
