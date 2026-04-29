package com.conti_talent.springboot.appweb.conti_talent_web.repository;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.MetricasDTO;

/**
 * Repositorio de métricas precomputadas. Hoy expone las series de seed.js;
 * mañana podría leer de una base agregada / data-warehouse.
 */
public interface MetricasRepository {

    MetricasDTO load();

    void save(MetricasDTO metricas);
}
