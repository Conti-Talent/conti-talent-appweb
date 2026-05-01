package com.conti_talent.springboot.appweb.conti_talent_web.mapper;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.EstadoDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.PostulanteDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.request.PostularRequest;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Estado;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Postulante;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper de Postulante. El estado se resuelve directo desde la entidad
 * (relacion JPA EAGER), sin necesidad de lookup adicional.
 */
@Component
public class PostulanteMapper {

    private final EstadoMapper estadoMapper;

    public PostulanteMapper(EstadoMapper estadoMapper) {
        this.estadoMapper = estadoMapper;
    }

    public PostulanteDTO convertirADTO(Postulante postulante) {
        if (postulante == null) return null;

        Estado estadoActual = postulante.getEstado();
        EstadoDTO estadoDTO = estadoMapper.convertirADTO(estadoActual);
        String codigoEstado = estadoActual != null ? estadoActual.getCodigo() : null;

        PostulanteDTO dto = new PostulanteDTO();
        dto.setId(postulante.getId());
        dto.setUsuarioId(postulante.getUsuarioId());
        dto.setOfertaId(postulante.getOfertaId());
        dto.setNombre(postulante.getNombre());
        dto.setEmail(postulante.getEmail());
        dto.setTelefono(postulante.getTelefono());
        dto.setExperiencia(postulante.getExperiencia());
        dto.setHabilidades(postulante.getHabilidades());
        dto.setCv(postulante.getCv());
        dto.setEstadoId(postulante.getEstadoId());
        dto.setEstado(codigoEstado);
        dto.setEstadoDetalle(estadoDTO);
        dto.setPuntaje(postulante.getPuntaje());
        dto.setRespuestas(postulante.getRespuestas());
        dto.setCreadoEn(postulante.getCreadoEn());
        return dto;
    }

    public List<PostulanteDTO> convertirALista(List<Postulante> postulantes) {
        if (postulantes == null) return List.of();
        return postulantes.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    /**
     * Construye un Postulante desde el request. Las relaciones (usuario,
     * oferta, estado) las setea el service tras resolverlas via repos.
     */
    public Postulante crearDesdeRequest(PostularRequest request) {
        if (request == null) return null;
        Postulante postulante = new Postulante();
        postulante.setNombre(textoSeguro(request.getNombre()));
        postulante.setEmail(textoSeguro(request.getEmail()));
        postulante.setTelefono(textoSeguro(request.getTelefono()));
        postulante.setExperiencia(textoSeguro(request.getExperiencia()));
        postulante.setHabilidades(textoSeguro(request.getHabilidades()));
        postulante.setCv(textoSeguro(request.getCv()));
        postulante.setPuntaje(0);
        return postulante;
    }

    private static String textoSeguro(String texto) {
        return texto != null ? texto.trim() : "";
    }
}
