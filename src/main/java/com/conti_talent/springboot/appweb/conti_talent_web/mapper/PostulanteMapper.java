package com.conti_talent.springboot.appweb.conti_talent_web.mapper;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.EstadoDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.PostulanteDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.request.PostularRequest;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Estado;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Postulante;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IEstadoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper de Postulante. Resuelve el Estado asociado al construir el DTO
 * publico, exponiendo tanto el codigo plano (compatible con el frontend
 * actual) como el detalle del estado (para paneles administrativos).
 */
@Component
public class PostulanteMapper {

    private final IEstadoRepository estadoRepository;
    private final EstadoMapper estadoMapper;

    public PostulanteMapper(IEstadoRepository estadoRepository, EstadoMapper estadoMapper) {
        this.estadoRepository = estadoRepository;
        this.estadoMapper = estadoMapper;
    }

    /** Convierte un Postulante a su DTO publico, resolviendo el estado por id. */
    public PostulanteDTO convertirADTO(Postulante postulante) {
        if (postulante == null) return null;

        Estado estadoActual = estadoRepository.buscarPorId(postulante.getEstadoId()).orElse(null);
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

    /** Convierte una lista de postulantes a una lista de DTOs. */
    public List<PostulanteDTO> convertirALista(List<Postulante> postulantes) {
        if (postulantes == null) return List.of();
        return postulantes.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    /**
     * Construye un Postulante a partir del request de postulacion.
     * El estadoId NO se asigna aqui porque depende del repo; lo asigna el
     * service tras resolver el estado POSTULADO.
     */
    public Postulante crearDesdeRequest(PostularRequest request) {
        if (request == null) return null;
        Postulante postulante = new Postulante();
        postulante.setUsuarioId(request.getUsuarioId());
        postulante.setOfertaId(request.getOfertaId());
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
