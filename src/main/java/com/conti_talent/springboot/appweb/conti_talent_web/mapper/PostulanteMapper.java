package com.conti_talent.springboot.appweb.conti_talent_web.mapper;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.PostulanteDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.request.PostularRequest;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Postulante;
import com.conti_talent.springboot.appweb.conti_talent_web.model.enums.EstadoPostulante;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PostulanteMapper {

    public PostulanteDTO toDTO(Postulante p) {
        if (p == null) return null;
        PostulanteDTO dto = new PostulanteDTO();
        dto.setId(p.getId());
        dto.setUsuarioId(p.getUsuarioId());
        dto.setOfertaId(p.getOfertaId());
        dto.setNombre(p.getNombre());
        dto.setEmail(p.getEmail());
        dto.setTelefono(p.getTelefono());
        dto.setExperiencia(p.getExperiencia());
        dto.setHabilidades(p.getHabilidades());
        dto.setCv(p.getCv());
        dto.setEstado(p.getEstado());
        dto.setPuntaje(p.getPuntaje());
        dto.setRespuestas(p.getRespuestas());
        dto.setCreadoEn(p.getCreadoEn());
        return dto;
    }

    public Postulante fromRequest(PostularRequest req) {
        if (req == null) return null;
        Postulante p = new Postulante();
        p.setUsuarioId(req.getUsuarioId());
        p.setOfertaId(req.getOfertaId());
        p.setNombre(safeTrim(req.getNombre()));
        p.setEmail(safeTrim(req.getEmail()));
        p.setTelefono(req.getTelefono() != null ? req.getTelefono() : "");
        p.setExperiencia(req.getExperiencia() != null ? req.getExperiencia() : "");
        p.setHabilidades(req.getHabilidades() != null ? req.getHabilidades() : "");
        p.setCv(req.getCv() != null ? req.getCv() : "");
        p.setEstado(EstadoPostulante.POSTULADO);
        p.setPuntaje(0);
        return p;
    }

    public List<PostulanteDTO> toDTOList(List<Postulante> postulantes) {
        if (postulantes == null) return List.of();
        return postulantes.stream().map(this::toDTO).collect(Collectors.toList());
    }

    private static String safeTrim(String s) {
        return s != null ? s.trim() : "";
    }
}
