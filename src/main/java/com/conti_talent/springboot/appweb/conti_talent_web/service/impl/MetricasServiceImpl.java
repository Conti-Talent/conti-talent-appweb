package com.conti_talent.springboot.appweb.conti_talent_web.service.impl;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.MetricasDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.response.RankingItemDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Oferta;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Postulante;
import com.conti_talent.springboot.appweb.conti_talent_web.model.enums.EstadoPostulante;
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
    public List<RankingItemDTO> ranking(String ofertaId, int limite) {
        List<Postulante> base = (ofertaId == null || ofertaId.isBlank())
                ? postulanteRepository.findAll()
                : postulanteRepository.findByOfertaId(ofertaId);

        Map<String, String> titulos = ofertaRepository.findAll().stream()
                .collect(Collectors.toMap(Oferta::getId, Oferta::getTitulo, (a, b) -> a));

        List<Postulante> ordenados = base.stream()
                .filter(p -> p.getEstado() != EstadoPostulante.RECHAZADO)
                .sorted(Comparator.comparingInt(Postulante::getPuntaje).reversed())
                .limit(Math.max(1, limite))
                .collect(Collectors.toList());

        List<RankingItemDTO> rows = new ArrayList<>(ordenados.size());
        for (int i = 0; i < ordenados.size(); i++) {
            Postulante p = ordenados.get(i);
            rows.add(new RankingItemDTO(
                    i + 1,
                    p.getId(),
                    p.getNombre(),
                    p.getOfertaId(),
                    titulos.getOrDefault(p.getOfertaId(), "—"),
                    p.getEstado(),
                    p.getPuntaje()));
        }
        return rows;
    }

    @Override
    public Map<EstadoPostulante, Long> postulantesPorEstado() {
        Map<EstadoPostulante, Long> counts = postulanteRepository.findAll().stream()
                .collect(Collectors.groupingBy(Postulante::getEstado, Collectors.counting()));
        // Garantizamos que todos los estados existan en la respuesta (con 0 si no hay).
        Map<EstadoPostulante, Long> resultado = new LinkedHashMap<>();
        for (EstadoPostulante e : EstadoPostulante.values()) {
            resultado.put(e, counts.getOrDefault(e, 0L));
        }
        return resultado;
    }

    @Override
    public List<Map<String, Object>> ofertasTop(int limite) {
        Map<String, Long> conteo = postulanteRepository.findAll().stream()
                .collect(Collectors.groupingBy(Postulante::getOfertaId, Collectors.counting()));

        Map<String, String> titulos = ofertaRepository.findAll().stream()
                .collect(Collectors.toMap(Oferta::getId, Oferta::getTitulo, (a, b) -> a));

        return conteo.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(Math.max(1, limite))
                .map(e -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("ofertaId", e.getKey());
                    row.put("titulo", titulos.getOrDefault(e.getKey(), "—"));
                    row.put("postulaciones", e.getValue());
                    return row;
                })
                .collect(Collectors.toList());
    }
}
