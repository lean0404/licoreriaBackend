package com.example.Licoreria_backend.service;

import com.example.Licoreria_backend.model.TipoProducto;
import com.example.Licoreria_backend.repository.TipoProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TipoProductoService {

    private final TipoProductoRepository tipoProductoRepository;

    public TipoProductoService(TipoProductoRepository tipoProductoRepository) {
        this.tipoProductoRepository = tipoProductoRepository;
    }

    public List<TipoProducto> listarTodos() {
        return tipoProductoRepository.findAll();
    }

    public Optional<TipoProducto> obtenerPorId(Long id) {
        return tipoProductoRepository.findById(id);
    }

    public TipoProducto guardar(TipoProducto tipoProducto) {
        return tipoProductoRepository.save(tipoProducto);
    }

    public void eliminar(Long id) {
        tipoProductoRepository.deleteById(id);
    }
}
