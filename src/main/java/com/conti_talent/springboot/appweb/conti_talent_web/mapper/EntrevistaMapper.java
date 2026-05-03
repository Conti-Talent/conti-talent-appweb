package com.conti_talent.springboot.appweb.conti_talent_web.mapper;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.EntrevistaDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.model.EntrevistaPostulante;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EntrevistaMapper {
    public EntrevistaDTO convertirADTO(EntrevistaPostulante entrevista) {
        if (entrevista == null) return null;
        EntrevistaDTO dto = new EntrevistaDTO();
        dto.setId(entrevista.getId());
        dto.setPostulanteId(entrevista.getPostulanteId());
        dto.setFechaEntrevista(entrevista.getFechaEntrevista());
        dto.setResultado(entrevista.getResultado());
        dto.setObservacion(entrevista.getObservacion());
        dto.setUsuarioAdmin(entrevista.getUsuarioAdmin());
        dto.setCreadoEn(entrevista.getCreadoEn());
        return dto;
    }

    public List<EntrevistaDTO> convertirALista(List<EntrevistaPostulante> entrevistas) {
        if (entrevistas == null) return List.of();
        return entrevistas.stream().map(this::convertirADTO).toList();
    }
}
