package com.conti_talent.springboot.appweb.conti_talent_web.service.impl;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.PostulanteDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.request.PostularRequest;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.BusinessException;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.EstadoInvalidoException;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.ResourceNotFoundException;
import com.conti_talent.springboot.appweb.conti_talent_web.mapper.PostulanteMapper;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Estado;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Postulante;
import com.conti_talent.springboot.appweb.conti_talent_web.model.enums.EstadoCodigo;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IEstadoRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IOfertaRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IPostulanteRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.service.IPostulanteService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostulanteServiceImpl implements IPostulanteService {

    private final IPostulanteRepository postulanteRepository;
    private final IOfertaRepository ofertaRepository;
    private final IEstadoRepository estadoRepository;
    private final PostulanteMapper postulanteMapper;

    public PostulanteServiceImpl(IPostulanteRepository postulanteRepository,
                                 IOfertaRepository ofertaRepository,
                                 IEstadoRepository estadoRepository,
                                 PostulanteMapper postulanteMapper) {
        this.postulanteRepository = postulanteRepository;
        this.ofertaRepository = ofertaRepository;
        this.estadoRepository = estadoRepository;
        this.postulanteMapper = postulanteMapper;
    }

    @Override
    public List<PostulanteDTO> listarTodos() {
        return postulanteMapper.convertirALista(postulanteRepository.findAll());
    }

    @Override
    public PostulanteDTO obtenerPorId(String id) {
        return postulanteMapper.convertirADTO(buscarPostulanteOFallar(id));
    }

    @Override
    public List<PostulanteDTO> listarPorOferta(String ofertaId) {
        return postulanteMapper.convertirALista(postulanteRepository.findByOfertaId(ofertaId));
    }

    @Override
    public List<PostulanteDTO> listarPorUsuario(String usuarioId) {
        return postulanteMapper.convertirALista(postulanteRepository.findByUsuarioId(usuarioId));
    }

    @Override
    public PostulanteDTO registrarPostulacion(PostularRequest request) {
        validarDatosPostulacion(request);
        if (ofertaRepository.findById(request.getOfertaId()).isEmpty()) {
            throw new BusinessException("Oferta inexistente: " + request.getOfertaId());
        }
        Estado estadoInicial = obtenerEstadoPorCodigoOFallar(EstadoCodigo.POSTULADO.name());

        Postulante nuevoPostulante = postulanteMapper.crearDesdeRequest(request);
        nuevoPostulante.setEstadoId(estadoInicial.getId());
        nuevoPostulante.setCreadoEn(System.currentTimeMillis());
        return postulanteMapper.convertirADTO(postulanteRepository.save(nuevoPostulante));
    }

    @Override
    public PostulanteDTO cambiarEstado(String idPostulante, String estadoDestino) {
        Postulante postulante = buscarPostulanteOFallar(idPostulante);
        if (esTextoVacio(estadoDestino)) {
            throw new BusinessException("Estado destino requerido");
        }
        Estado estadoActual  = obtenerEstadoPorIdOFallar(postulante.getEstadoId());
        Estado estadoNuevo   = resolverEstadoDestino(estadoDestino);

        EstadoCodigo codigoActual = EstadoCodigo.valueOf(estadoActual.getCodigo());
        EstadoCodigo codigoNuevo  = EstadoCodigo.valueOf(estadoNuevo.getCodigo());

        if (!codigoActual.puedeTransicionarA(codigoNuevo)) {
            throw new EstadoInvalidoException(
                    "Transicion no permitida: " + codigoActual + " -> " + codigoNuevo);
        }
        postulante.setEstadoId(estadoNuevo.getId());
        return postulanteMapper.convertirADTO(postulanteRepository.save(postulante));
    }

    @Override
    public void eliminar(String id) {
        if (postulanteRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Postulante no encontrado: " + id);
        }
        postulanteRepository.deleteById(id);
    }

    @Override
    public PostulanteDTO marcarComoRechazado(String id) {
        return cambiarEstado(id, EstadoCodigo.RECHAZADO.name());
    }

    @Override
    public List<PostulanteDTO> obtenerRankingPorPuntaje(String ofertaId) {
        List<Postulante> base = esTextoVacio(ofertaId)
                ? postulanteRepository.findAll()
                : postulanteRepository.findByOfertaId(ofertaId);
        return base.stream()
                .sorted(Comparator.comparingInt(Postulante::getPuntaje).reversed())
                .map(postulanteMapper::convertirADTO)
                .collect(Collectors.toList());
    }

    /* ============ Helpers privados ============ */

    private Postulante buscarPostulanteOFallar(String id) {
        return postulanteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Postulante no encontrado: " + id));
    }

    private Estado obtenerEstadoPorIdOFallar(String id) {
        return estadoRepository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estado no encontrado: " + id));
    }

    private Estado obtenerEstadoPorCodigoOFallar(String codigo) {
        return estadoRepository.buscarPorCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Estado con codigo '" + codigo + "' no encontrado"));
    }

    /**
     * Acepta tanto un id de estado (e1, e2...) como un codigo logico
     * (POSTULADO, EN_EVALUACION...). Devuelve la entidad correspondiente.
     */
    private Estado resolverEstadoDestino(String referencia) {
        return estadoRepository.buscarPorId(referencia)
                .orElseGet(() -> obtenerEstadoPorCodigoOFallar(referencia));
    }

    private static void validarDatosPostulacion(PostularRequest request) {
        if (request == null)                          throw new BusinessException("Datos de postulacion requeridos");
        if (esTextoVacio(request.getOfertaId()))      throw new BusinessException("ofertaId requerido");
        if (esTextoVacio(request.getNombre()))        throw new BusinessException("Nombre requerido");
        if (esTextoVacio(request.getEmail()))         throw new BusinessException("Email requerido");
    }

    private static boolean esTextoVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }
}
