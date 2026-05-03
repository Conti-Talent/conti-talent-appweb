package com.conti_talent.springboot.appweb.conti_talent_web.service.impl;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.MetricasDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.response.RankingItemDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Oferta;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Postulante;
import com.conti_talent.springboot.appweb.conti_talent_web.model.enums.EstadoCodigo;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IMetricasRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IOfertaRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IPostulanteRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.service.IMetricasService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MetricasServiceImpl implements IMetricasService {

    private final IMetricasRepository metricasRepository;
    private final IPostulanteRepository postulanteRepository;
    private final IOfertaRepository ofertaRepository;

    public MetricasServiceImpl(IMetricasRepository metricasRepository,
                               IPostulanteRepository postulanteRepository,
                               IOfertaRepository ofertaRepository) {
        this.metricasRepository = metricasRepository;
        this.postulanteRepository = postulanteRepository;
        this.ofertaRepository = ofertaRepository;
    }

    @Override
    public MetricasDTO obtenerDashboard() {
        return metricasRepository.load();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RankingItemDTO> ranking(Long ofertaId, int limite) {
        List<Postulante> base = ofertaId == null
                ? postulanteRepository.findAll()
                : postulanteRepository.findByOfertaId(ofertaId);

        Map<Long, String> titulosOferta = ofertaRepository.findAll().stream()
                .collect(Collectors.toMap(Oferta::getId, Oferta::getTitulo, (a, b) -> a));

        List<Postulante> ordenados = base.stream()
                .filter(p -> p.getEstado() == null
                        || !EstadoCodigo.RECHAZADO.name().equals(p.getEstado().getCodigo()))
                .sorted(Comparator.comparingInt(Postulante::getPuntajeFinal).reversed())
                .limit(Math.max(1, limite))
                .collect(Collectors.toList());

        List<RankingItemDTO> filas = new ArrayList<>(ordenados.size());
        for (int i = 0; i < ordenados.size(); i++) {
            Postulante postulante = ordenados.get(i);
            String codigoEstado = postulante.getEstado() != null
                    ? postulante.getEstado().getCodigo()
                    : "DESCONOCIDO";
            RankingItemDTO fila = new RankingItemDTO(
                    i + 1,
                    postulante.getId(),
                    postulante.getNombre(),
                    postulante.getOfertaId(),
                    titulosOferta.getOrDefault(postulante.getOfertaId(), "—"),
                    codigoEstado,
                    postulante.getPuntajeFinal());
            fila.setPuntajeCuestionario(postulante.getPuntaje());
            fila.setPuntajeExperiencia(postulante.getPuntajeExperiencia());
            fila.setPuntajeHabilidades(postulante.getPuntajeHabilidades());
            fila.setPuntajeFinal(postulante.getPuntajeFinal());
            filas.add(fila);
        }
        return filas;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<EstadoCodigo, Long> postulantesPorEstado() {
        Map<EstadoCodigo, Long> conteoCrudo = postulanteRepository.findAll().stream()
                .filter(p -> p.getEstado() != null)
                .map(p -> p.getEstado().getCodigo())
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(EstadoCodigo::valueOf, Collectors.counting()));

        Map<EstadoCodigo, Long> resultadoCompleto = new LinkedHashMap<>();
        for (EstadoCodigo codigo : EstadoCodigo.values()) {
            resultadoCompleto.put(codigo, conteoCrudo.getOrDefault(codigo, 0L));
        }
        return resultadoCompleto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> ofertasTop(int limite) {
        Map<Long, Long> conteoPorOferta = postulanteRepository.findAll().stream()
                .filter(p -> p.getOfertaId() != null)
                .collect(Collectors.groupingBy(Postulante::getOfertaId, Collectors.counting()));

        Map<Long, String> titulos = ofertaRepository.findAll().stream()
                .collect(Collectors.toMap(Oferta::getId, Oferta::getTitulo, (a, b) -> a));

        return conteoPorOferta.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
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
}
