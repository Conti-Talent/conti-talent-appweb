package com.conti_talent.springboot.appweb.conti_talent_web.service.impl;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.RolDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.BusinessException;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.ResourceNotFoundException;
import com.conti_talent.springboot.appweb.conti_talent_web.mapper.RolMapper;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Rol;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IRolRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.service.IRolService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RolServiceImpl implements IRolService {

    private final IRolRepository rolRepository;
    private final RolMapper rolMapper;

    public RolServiceImpl(IRolRepository rolRepository, RolMapper rolMapper) {
        this.rolRepository = rolRepository;
        this.rolMapper = rolMapper;
    }

    @Override
    public List<RolDTO> listarTodos() {
        return rolMapper.convertirALista(rolRepository.listarTodos());
    }

    @Override
    public List<RolDTO> listarSoloActivos() {
        return rolRepository.listarTodos().stream()
                .filter(Rol::isActivo)
                .map(rolMapper::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    public RolDTO obtenerPorId(String id) {
        Rol rol = rolRepository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado: " + id));
        return rolMapper.convertirADTO(rol);
    }

    @Override
    public Rol obtenerEntidadPorCodigo(String codigo) {
        return rolRepository.buscarPorCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Rol con codigo '" + codigo + "' no encontrado"));
    }

    @Override
    public RolDTO crear(RolDTO dto) {
        validarDatosBasicos(dto);
        if (rolRepository.existePorCodigo(dto.getCodigo())) {
            throw new BusinessException("Ya existe un rol con el codigo: " + dto.getCodigo());
        }
        Rol nuevoRol = new Rol();
        nuevoRol.setCodigo(dto.getCodigo().trim().toUpperCase());
        nuevoRol.setNombre(dto.getNombre().trim());
        nuevoRol.setDescripcion(dto.getDescripcion() != null ? dto.getDescripcion().trim() : "");
        nuevoRol.setActivo(true);
        nuevoRol.setCreadoEn(System.currentTimeMillis());
        return rolMapper.convertirADTO(rolRepository.guardar(nuevoRol));
    }

    @Override
    public RolDTO actualizar(String id, RolDTO dto) {
        Rol rolExistente = rolRepository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado: " + id));

        if (dto.getNombre() != null)      rolExistente.setNombre(dto.getNombre().trim());
        if (dto.getDescripcion() != null) rolExistente.setDescripcion(dto.getDescripcion().trim());
        rolExistente.setActivo(dto.isActivo());

        return rolMapper.convertirADTO(rolRepository.guardar(rolExistente));
    }

    @Override
    public void eliminar(String id) {
        if (rolRepository.buscarPorId(id).isEmpty()) {
            throw new ResourceNotFoundException("Rol no encontrado: " + id);
        }
        rolRepository.eliminarPorId(id);
    }

    /* ============ Helpers privados ============ */

    private void validarDatosBasicos(RolDTO dto) {
        if (dto == null) {
            throw new BusinessException("Datos de rol requeridos");
        }
        if (esTextoVacio(dto.getCodigo())) {
            throw new BusinessException("El codigo del rol es obligatorio");
        }
        if (esTextoVacio(dto.getNombre())) {
            throw new BusinessException("El nombre del rol es obligatorio");
        }
    }

    private static boolean esTextoVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }
}
