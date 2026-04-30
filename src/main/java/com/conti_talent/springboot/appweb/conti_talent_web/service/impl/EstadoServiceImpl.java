package com.conti_talent.springboot.appweb.conti_talent_web.service.impl;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.EstadoDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.ResourceNotFoundException;
import com.conti_talent.springboot.appweb.conti_talent_web.mapper.EstadoMapper;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Estado;
import com.conti_talent.springboot.appweb.conti_talent_web.model.enums.EstadoCodigo;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IEstadoRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.service.IEstadoService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EstadoServiceImpl implements IEstadoService {

    private final IEstadoRepository estadoRepository;
    private final EstadoMapper estadoMapper;

    public EstadoServiceImpl(IEstadoRepository estadoRepository, EstadoMapper estadoMapper) {
        this.estadoRepository = estadoRepository;
        this.estadoMapper = estadoMapper;
    }

    @Override
    public List<EstadoDTO> listarTodos() {
        return estadoMapper.convertirALista(estadoRepository.listarTodos());
    }

    @Override
    public List<EstadoDTO> listarSoloActivos() {
        return estadoRepository.listarTodos().stream()
                .filter(Estado::isActivo)
                .sorted((a, b) -> Integer.compare(a.getOrden(), b.getOrden()))
                .map(estadoMapper::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    public EstadoDTO obtenerPorId(String id) {
        Estado estado = estadoRepository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estado no encontrado: " + id));
        return estadoMapper.convertirADTO(estado);
    }

    @Override
    public Estado obtenerEntidadPorCodigo(String codigo) {
        return estadoRepository.buscarPorCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Estado con codigo '" + codigo + "' no encontrado"));
    }

    @Override
    public boolean puedeTransicionar(String idEstadoOrigen, String idEstadoDestino) {
        Estado origen  = estadoRepository.buscarPorId(idEstadoOrigen)
                .orElseThrow(() -> new ResourceNotFoundException("Estado origen no encontrado: " + idEstadoOrigen));
        Estado destino = estadoRepository.buscarPorId(idEstadoDestino)
                .orElseThrow(() -> new ResourceNotFoundException("Estado destino no encontrado: " + idEstadoDestino));

        EstadoCodigo codigoOrigen  = EstadoCodigo.valueOf(origen.getCodigo());
        EstadoCodigo codigoDestino = EstadoCodigo.valueOf(destino.getCodigo());
        return codigoOrigen.puedeTransicionarA(codigoDestino);
    }
}
