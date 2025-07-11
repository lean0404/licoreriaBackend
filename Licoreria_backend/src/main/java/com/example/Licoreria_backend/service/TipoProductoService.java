package com.example.Licoreria_backend.service;

import com.example.Licoreria_backend.model.TipoProducto;
import com.example.Licoreria_backend.repository.TipoProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipoProductoService {

    private final TipoProductoRepository tipoProductoRepository;

    public TipoProductoService(TipoProductoRepository tipoProductoRepository) {
        this.tipoProductoRepository = tipoProductoRepository;
    }

    public List<TipoProducto> listarTodos() {
        return tipoProductoRepository.findAll();
    }
}
