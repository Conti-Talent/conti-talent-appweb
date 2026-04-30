package com.conti_talent.springboot.appweb.conti_talent_web.service.impl;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.MetricasDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.response.RankingItemDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Estado;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Oferta;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Postulante;
import com.conti_talent.springboot.appweb.conti_talent_web.model.enums.EstadoCodigo;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IEstadoRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IMetricasRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IOfertaRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IPostulanteRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.service.IMetricasService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MetricasServiceImpl implements IMetricasService {

    private final IMetricasRepository metricasRepository;
    private final IPostulanteRepository postulanteRepository;
    private final IOfertaRepository ofertaRepository;
    private final IEstadoRepository estadoRepository;

    public MetricasServiceImpl(IMetricasRepository metricasRepository,
                               IPostulanteRepository postulanteRepository,
                               IOfertaRepository ofertaRepository,
                               IEstadoRepository estadoRepository) {
        this.metricasRepository = metricasRepository;
        this.postulanteRepository = postulanteRepository;
        this.ofertaRepository = ofertaRepository;
        this.estadoRepository = estadoRepository;
    }

    @Override
    public MetricasDTO obtenerDashboard() {
        return metricasRepository.load();
    }

    @Override
    public List<RankingItemDTO> ranking(String ofertaId, int limite) {
        Map<String, String> codigosEstadoPorId = construirMapaCodigosEstado();
        String idEstadoRechazado = obtenerIdEstadoPorCodigo(EstadoCodigo.RECHAZADO.name());

        List<Postulante> base = (ofertaId == null || ofertaId.isBlank())
                ? postulanteRepository.findAll()
                : postulanteRepository.findByOfertaId(ofertaId);

        Map<String, String> titulosOferta = ofertaRepository.findAll().stream()
                .collect(Collectors.toMap(Oferta::getId, Oferta::getTitulo, (a, b) -> a));

        List<Postulante> ordenados = base.stream()
                .filter(p -> idEstadoRechazado == null || !idEstadoRechazado.equals(p.getEstadoId()))
                .sorted(Comparator.comparingInt(Postulante::getPuntaje).reversed())
                .limit(Math.max(1, limite))
                .collect(Collectors.toList());

        List<RankingItemDTO> filas = new ArrayList<>(ordenados.size());
        for (int i = 0; i < ordenados.size(); i++) {
            Postulante postulante = ordenados.get(i);
            filas.add(new RankingItemDTO(
                    i + 1,
                    postulante.getId(),
                    postulante.getNombre(),
                    postulante.getOfertaId(),
                    titulosOferta.getOrDefault(postulante.getOfertaId(), "—"),
                    codigosEstadoPorId.getOrDefault(postulante.getEstadoId(), "DESCONOCIDO"),
                    postulante.getPuntaje()));
        }
        return filas;
    }

    @Override
    public Map<EstadoCodigo, Long> postulantesPorEstado() {
        Map<String, String> codigosPorId = construirMapaCodigosEstado();

        Map<EstadoCodigo, Long> conteoCrudo = postulanteRepository.findAll().stream()
                .map(p -> codigosPorId.get(p.getEstadoId()))
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(EstadoCodigo::valueOf, Collectors.counting()));

        Map<EstadoCodigo, Long> resultadoCompleto = new LinkedHashMap<>();
        for (EstadoCodigo codigo : EstadoCodigo.values()) {
            resultadoCompleto.put(codigo, conteoCrudo.getOrDefault(codigo, 0L));
        }
        return resultadoCompleto;
    }

    @Override
    public List<Map<String, Object>> ofertasTop(int limite) {
        Map<String, Long> conteoPorOferta = postulanteRepository.findAll().stream()
                .collect(Collectors.groupingBy(Postulante::getOfertaId, Collectors.counting()));

        Map<String, String> titulos = ofertaRepository.findAll().stream()
                .collect(Collectors.toMap(Oferta::getId, Oferta::getTitulo, (a, b) -> a));

        return conteoPorOferta.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(Math.max(1, limite))
                .map(entrada -> {
                    Map<String, Object> fila = new LinkedHashMap<>();
                    fila.put("ofertaId", entrada.getKey());
                    fila.put("titulo", titulos.getOrDefault(entrada.getKey(), "—"));
                    fila.put("postulaciones", entrada.getValue());
                    return fila;
                })
                .collect(Collectors.toList());
    }

    /* ============ Helpers privados ============ */

    /** Cache local del map estadoId -> codigo, evita N+1 lookups. */
    private Map<String, String> construirMapaCodigosEstado() {
        return estadoRepository.listarTodos().stream()
                .collect(Collectors.toMap(Estado::getId, Estado::getCodigo, (a, b) -> a));
    }

    private String obtenerIdEstadoPorCodigo(String codigo) {
        return estadoRepository.buscarPorCodigo(codigo).map(Estado::getId).orElse(null);
    }
}
