package com.example.Licoreria_backend.service;

import com.example.Licoreria_backend.model.Producto;
import com.example.Licoreria_backend.repository.ProductoRepository;
import com.example.Licoreria_backend.repository.TipoProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final TipoProductoRepository tipoProductoRepository;

    public ProductoService(ProductoRepository productoRepository,
                           TipoProductoRepository tipoProductoRepository) {
        this.productoRepository = productoRepository;
        this.tipoProductoRepository = tipoProductoRepository;
    }

    public Producto guardar(Producto producto) {
        // cargar el TipoProducto desde la base
        if (producto.getTipoProducto() != null && producto.getTipoProducto().getId() != null) {
            var tipoOpt = tipoProductoRepository.findById(producto.getTipoProducto().getId());
            if (tipoOpt.isPresent()) {
                producto.setTipoProducto(tipoOpt.get());
            } else {
                throw new RuntimeException("TipoProducto no encontrado con ID " + producto.getTipoProducto().getId());
            }
        } else {
            producto.setTipoProducto(null);
        }

        return productoRepository.save(producto);
    }

    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    public Optional<Producto> obtenerPorId(Long id) {
        return productoRepository.findById(id);
    }

    public void eliminar(Long id) {
        productoRepository.deleteById(id);
    }
}
