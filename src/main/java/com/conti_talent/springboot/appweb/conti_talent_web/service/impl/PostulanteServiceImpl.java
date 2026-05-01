package com.conti_talent.springboot.appweb.conti_talent_web.service.impl;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.PostulanteDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.request.PostularRequest;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.BusinessException;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.EstadoInvalidoException;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.ResourceNotFoundException;
import com.conti_talent.springboot.appweb.conti_talent_web.mapper.PostulanteMapper;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Estado;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Oferta;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Postulante;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Usuario;
import com.conti_talent.springboot.appweb.conti_talent_web.model.enums.EstadoCodigo;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IEstadoRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IOfertaRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IPostulanteRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IUsuarioRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.service.IPostulanteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostulanteServiceImpl implements IPostulanteService {

    private final IPostulanteRepository postulanteRepository;
    private final IOfertaRepository ofertaRepository;
    private final IUsuarioRepository usuarioRepository;
    private final IEstadoRepository estadoRepository;
    private final PostulanteMapper postulanteMapper;

    public PostulanteServiceImpl(IPostulanteRepository postulanteRepository,
                                 IOfertaRepository ofertaRepository,
                                 IUsuarioRepository usuarioRepository,
                                 IEstadoRepository estadoRepository,
                                 PostulanteMapper postulanteMapper) {
        this.postulanteRepository = postulanteRepository;
        this.ofertaRepository = ofertaRepository;
        this.usuarioRepository = usuarioRepository;
        this.estadoRepository = estadoRepository;
        this.postulanteMapper = postulanteMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostulanteDTO> listarTodos() {
        return postulanteMapper.convertirALista(postulanteRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public PostulanteDTO obtenerPorId(Long id) {
        return postulanteMapper.convertirADTO(buscarPostulanteOFallar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostulanteDTO> listarPorOferta(Long ofertaId) {
        return postulanteMapper.convertirALista(postulanteRepository.findByOfertaId(ofertaId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostulanteDTO> listarPorUsuario(Long usuarioId) {
        return postulanteMapper.convertirALista(postulanteRepository.findByUsuarioId(usuarioId));
    }

    @Override
    @Transactional
    public PostulanteDTO registrarPostulacion(PostularRequest request) {
        validarDatosPostulacion(request);
        Oferta oferta = ofertaRepository.findById(request.getOfertaId())
                .orElseThrow(() -> new BusinessException("Oferta inexistente: " + request.getOfertaId()));
        Estado estadoInicial = obtenerEstadoPorCodigoOFallar(EstadoCodigo.POSTULADO.name());

        Postulante nuevoPostulante = postulanteMapper.crearDesdeRequest(request);
        nuevoPostulante.setOferta(oferta);
        nuevoPostulante.setEstado(estadoInicial);
        if (request.getUsuarioId() != null) {
            Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                    .orElseThrow(() -> new BusinessException("Usuario inexistente: " + request.getUsuarioId()));
            nuevoPostulante.setUsuario(usuario);
        }
        nuevoPostulante.setCreadoEn(System.currentTimeMillis());
        return postulanteMapper.convertirADTO(postulanteRepository.save(nuevoPostulante));
    }

    @Override
    @Transactional
    public PostulanteDTO cambiarEstado(Long idPostulante, String estadoDestino) {
        Postulante postulante = buscarPostulanteOFallar(idPostulante);
        if (esTextoVacio(estadoDestino)) {
            throw new BusinessException("Estado destino requerido");
        }
        Estado estadoActual = postulante.getEstado();
        Estado estadoNuevo  = resolverEstadoDestino(estadoDestino);

        EstadoCodigo codigoActual = EstadoCodigo.valueOf(estadoActual.getCodigo());
        EstadoCodigo codigoNuevo  = EstadoCodigo.valueOf(estadoNuevo.getCodigo());

        if (!codigoActual.puedeTransicionarA(codigoNuevo)) {
            throw new EstadoInvalidoException(
                    "Transicion no permitida: " + codigoActual + " -> " + codigoNuevo);
        }
        postulante.setEstado(estadoNuevo);
        return postulanteMapper.convertirADTO(postulanteRepository.save(postulante));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!postulanteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Postulante no encontrado: " + id);
        }
        postulanteRepository.deleteById(id);
    }

    @Override
    @Transactional
    public PostulanteDTO marcarComoRechazado(Long id) {
        return cambiarEstado(id, EstadoCodigo.RECHAZADO.name());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostulanteDTO> obtenerRankingPorPuntaje(Long ofertaId) {
        List<Postulante> base = ofertaId == null
                ? postulanteRepository.findAll()
                : postulanteRepository.findByOfertaId(ofertaId);
        return base.stream()
                .sorted(Comparator.comparingInt(Postulante::getPuntaje).reversed())
                .map(postulanteMapper::convertirADTO)
                .collect(Collectors.toList());
    }

    /* ============ Helpers privados ============ */

    private Postulante buscarPostulanteOFallar(Long id) {
        return postulanteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Postulante no encontrado: " + id));
    }

    private Estado obtenerEstadoPorCodigoOFallar(String codigo) {
        return estadoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Estado con codigo '" + codigo + "' no encontrado"));
    }

    /** Acepta tanto un id numerico como un codigo logico (POSTULADO, ENTREVISTA...). */
    private Estado resolverEstadoDestino(String referencia) {
        try {
            Long idEstado = Long.parseLong(referencia);
            return estadoRepository.findById(idEstado)
                    .orElseThrow(() -> new ResourceNotFoundException("Estado no encontrado: " + idEstado));
        } catch (NumberFormatException e) {
            return obtenerEstadoPorCodigoOFallar(referencia);
        }
    }

    private static void validarDatosPostulacion(PostularRequest request) {
        if (request == null)                          throw new BusinessException("Datos de postulacion requeridos");
        if (request.getOfertaId() == null)            throw new BusinessException("ofertaId requerido");
        if (esTextoVacio(request.getNombre()))        throw new BusinessException("Nombre requerido");
        if (esTextoVacio(request.getEmail()))         throw new BusinessException("Email requerido");
    }

    private static boolean esTextoVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }
}
