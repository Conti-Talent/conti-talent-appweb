package com.conti_talent.springboot.appweb.conti_talent_web.service;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.MetricasDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.response.RankingItemDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.model.enums.EstadoPostulante;

import java.util.List;
import java.util.Map;

public interface MetricasService {

    /** Series + KPIs precomputados (mantiene la forma del seed.js). */
    MetricasDTO obtenerDashboard();

    /** Top postulantes por puntaje. ofertaId opcional. */
    List<RankingItemDTO> ranking(String ofertaId, int limite);

    /** Conteo de postulantes agrupado por estado. */
    Map<EstadoPostulante, Long> postulantesPorEstado();

    /** Top ofertas por número de postulaciones. */
    List<Map<String, Object>> ofertasTop(int limite);
}
