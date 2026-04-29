package com.conti_talent.springboot.appweb.conti_talent_web.mapper;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.PreguntaDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Pregunta;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PreguntaMapper {

    /** DTO completo (uso administrativo). Incluye `correcta`. */
    public PreguntaDTO toAdminDTO(Pregunta q) {
        if (q == null) return null;
        PreguntaDTO dto = new PreguntaDTO();
        dto.setId(q.getId());
        dto.setOfertaId(q.getOfertaId());
        dto.setPregunta(q.getPregunta());
        dto.setOpciones(q.getOpciones());
        dto.setCorrecta(q.getCorrecta());
        return dto;
    }

    /** DTO público (uso al postular): omite la respuesta correcta. */
    public PreguntaDTO toPublicDTO(Pregunta q) {
        PreguntaDTO dto = toAdminDTO(q);
        if (dto != null) dto.setCorrecta(null);
        return dto;
    }

    public Pregunta toEntity(PreguntaDTO dto) {
        if (dto == null) return null;
        int correcta = dto.getCorrecta() != null ? dto.getCorrecta() : -1;
        return new Pregunta(dto.getId(), dto.getOfertaId(), dto.getPregunta(), dto.getOpciones(), correcta);
    }

    public List<PreguntaDTO> toAdminDTOList(List<Pregunta> preguntas) {
        if (preguntas == null) return List.of();
        return preguntas.stream().map(this::toAdminDTO).collect(Collectors.toList());
    }

    public List<PreguntaDTO> toPublicDTOList(List<Pregunta> preguntas) {
        if (preguntas == null) return List.of();
        return preguntas.stream().map(this::toPublicDTO).collect(Collectors.toList());
    }
}
