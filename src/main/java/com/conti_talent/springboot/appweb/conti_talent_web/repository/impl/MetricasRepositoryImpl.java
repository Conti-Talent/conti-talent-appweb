package com.conti_talent.springboot.appweb.conti_talent_web.repository.impl;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.MetricasDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IMetricasRepository;
import org.springframework.stereotype.Repository;

@Repository
public class MetricasRepositoryImpl implements IMetricasRepository {

    /**
     * Volátil: el snapshot lo escribe el DataLoader una vez al arrancar y se
     * lee desde cualquier hilo HTTP. La escritura desde otros lugares es
     * excepcional (admin futuro) por lo que no necesitamos sincronización extra.
     */
    private volatile MetricasDTO snapshot = new MetricasDTO();

    @Override
    public MetricasDTO load() {
        return snapshot;
    }

    @Override
    public void save(MetricasDTO metricas) {
        if (metricas != null) this.snapshot = metricas;
    }
}
