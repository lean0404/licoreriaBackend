package com.example.Licoreria_backend.service;

import com.example.Licoreria_backend.model.Producto;
import com.example.Licoreria_backend.repository.MarcaRepository;
import com.example.Licoreria_backend.repository.ProductoRepository;
import com.example.Licoreria_backend.repository.TipoProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final TipoProductoRepository tipoProductoRepository;
    private final MarcaRepository marcaRepository;

    public ProductoService(ProductoRepository productoRepository,
                           TipoProductoRepository tipoProductoRepository,
                           MarcaRepository marcaRepository) {
        this.productoRepository = productoRepository;
        this.tipoProductoRepository = tipoProductoRepository;
        this.marcaRepository = marcaRepository;
    }

    public Producto guardar(Producto producto) {
        if (producto.getTipoProducto() != null && producto.getTipoProducto().getId() != null) {
            tipoProductoRepository.findById(producto.getTipoProducto().getId())
                    .ifPresentOrElse(producto::setTipoProducto,
                            () -> { throw new RuntimeException("TipoProducto no encontrado con ID " + producto.getTipoProducto().getId()); });
        } else {
            producto.setTipoProducto(null);
        }

        if (producto.getMarca() != null && producto.getMarca().getId() != null) {
            marcaRepository.findById(producto.getMarca().getId())
                    .ifPresentOrElse(producto::setMarca,
                            () -> { throw new RuntimeException("Marca no encontrada con ID " + producto.getMarca().getId()); });
        } else {
            producto.setMarca(null);
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
