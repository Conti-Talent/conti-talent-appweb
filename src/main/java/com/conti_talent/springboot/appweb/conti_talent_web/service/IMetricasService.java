package com.conti_talent.springboot.appweb.conti_talent_web.service;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.MetricasDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.response.RankingItemDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.model.enums.EstadoCodigo;

import java.util.List;
import java.util.Map;

public interface IMetricasService {

    /** Series + KPIs precomputados (forma identica al seed.js del frontend). */
    MetricasDTO obtenerDashboard();

    /** Top postulantes ordenados por puntaje. ofertaId opcional. */
    List<RankingItemDTO> ranking(String ofertaId, int limite);

    /** Conteo de postulantes agrupado por codigo de estado. */
    Map<EstadoCodigo, Long> postulantesPorEstado();

    /** Top ofertas por numero de postulaciones. */
    List<Map<String, Object>> ofertasTop(int limite);
}
